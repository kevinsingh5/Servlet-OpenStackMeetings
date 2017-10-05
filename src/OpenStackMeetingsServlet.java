/* 
 * Author: Kevin Singh
 *   Date: 09/26/2018
 *   
 * A basic servlet application to handle sessions, query parameters and 
 *   	parsing using jsoup
 *   
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup; 	
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OpenStackMeetingsServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<String> history = new ArrayList<String>();
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException
    {
		PrintWriter writer = response.getWriter();
		String session = request.getParameter("session");
		String project = request.getParameter("project");
		String year = request.getParameter("year");
		boolean startSession = false;
		boolean endSession = false;
		
		// parse query parameters
		if(session != null) {
			startSession = session.equalsIgnoreCase("start");
			endSession = session.equalsIgnoreCase("end");
		}
		if(project != null && year == null) 
			writer.println("Required parameter <year> missing");	
		else if	(project == null && year != null)
			writer.println("Required parameter <project> missing");
		else
			writer.println("History");

		if(startSession) {
			history.add(request.getRequestURL() + "?" + request.getQueryString());
		}
		
		// check for existing session, if indicated, start new session
		Cookie cookies[] = request.getCookies();
        boolean activeSession = false;
        for(int i=0; cookies != null && i<cookies.length; i++) {
                Cookie ck = cookies[i];
                String cookieName = ck.getName();
                String cookieValue = ck.getValue();
                if ((cookieName != null && cookieName.equals("active-session")) 
                                && cookieValue != null && cookieValue.equals("true")) {
                	if(endSession) {	// end the session, clear history
                		ck.setDomain("localhost");
                		ck.setPath("/assignment1" + request.getServletPath());
                		ck.setHttpOnly(true);
                		ck.setMaxAge(0);
                		ck.setValue("");
                		response.addCookie(ck);
                		history.clear();
                	} else {
                		Iterator<String> itr = history.listIterator();	// print history
                		while(itr.hasNext()) {
                			String nxt = (String) itr.next();
                			writer.println(nxt);
                		}
                        activeSession = true;
                	}
                }
        }
        if (!activeSession && startSession) {	// start new session
                Cookie cookie = new Cookie("active-session","true");
                cookie.setDomain("localhost");
                cookie.setPath("/assignment1" + request.getServletPath());
                cookie.setHttpOnly(true);
                cookie.setMaxAge(1000);
                response.addCookie(cookie);
        }
		
        writer.println();
		writer.println("Data");
		
		Elements data = null;
		if(project != null && year != null) {
			data = load_doc(project, year, writer);
		}
		// iterate through the logs and print them out
	    if (data != null) {
	    	if(activeSession)
	    		history.add(request.getRequestURL() + "?" + request.getQueryString());
		    ListIterator<Element> iterator = data.listIterator();		    	
		    while(iterator.hasNext()) {
	    			Element e = (Element) iterator.next();
	    			String s = e.html();
	    			if (s.equalsIgnoreCase("Parent Directory"))
	    				continue;
	    			else if (s != null) {
	    				writer.println(s);		    			
	    			}
		    }	    
	    } 
    }

	
	
	// function to load the HTTP document given the project and year URL
	protected Elements load_doc(String project, String year, PrintWriter writer) {
		Document doc = null;
		// try to load the project page
		try {
			doc = Jsoup.connect("http://eavesdrop.openstack.org/meetings" + "/" + project.toLowerCase()).get();
		} catch (IOException e) {
			writer.println("Project with name " + project + " not found");
			e.printStackTrace();
			return null;
		}
		// try to load the year page
		try {
			doc = Jsoup.connect("http://eavesdrop.openstack.org/meetings" + "/" + 
					project.toLowerCase() + "/" + year).get();
		} catch (IOException e) {
			writer.println("Invalid year " + year + " for project " + project);
			e.printStackTrace();
			return null;
		}
		if (doc != null) {
			Elements logs = doc.select("tr td a[href]");
		    return logs;			
		}
		else {
			return null;
		}
	}
	
}









