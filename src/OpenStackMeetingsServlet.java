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
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException
    {
		PrintWriter writer = response.getWriter();
		String session = request.getParameter("session");
		String project = request.getParameter("project");
		String year = request.getParameter("year");
		boolean startSession = false;
		
		// parse query parameters
		if(session != null)
			startSession = session.equalsIgnoreCase("start");
		if(project != null && year == null) 
			writer.println("Required parameter <year> missing");	
		else if	(project == null && year != null)
			writer.println("Required parameter <project> missing");
			
		// initialize a session
		if(startSession) {
			Cookie cookies[] = request.getCookies();
            boolean activeSession = false;
            for(int i=0; cookies != null && i<cookies.length; i++) {
                    Cookie ck = cookies[i];
                    String cookieName = ck.getName();
                    String cookieValue = ck.getValue();
                    if ((cookieName != null && cookieName.equals("active-session")) 
                                    && cookieValue != null && cookieValue.equals("true")) {
                            writer.println("Session Active. Welcome! :)");
                            activeSession = true;
                    }
            }
            if (!activeSession) {
                    Cookie cookie = new Cookie("active-session","true");
                    cookie.setDomain("localhost");
                    cookie.setPath("/assignment1" + request.getServletPath());
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(60);
                    response.addCookie(cookie);
                    writer.println("No active session");
            }
		} // end initialize session
		
		// load document
		Elements data = null;
		if(project != null && year != null)
			data = load_doc(project, year, writer);
		
	    if (data != null) {
		    ListIterator<Element> iterator = data.listIterator();		    	
		    while(iterator.hasNext()) {
	    			Element e = (Element) iterator.next();
	    			String s = e.html();
	    			if (s != null) {
	    				writer.println(s);		    			
	    			}
		    }	    
	    } else {
    		writer.println("Project with " + project + " not found");
	    }
		
		writer.println("Start session " + startSession);
		writer.println("");
		writer.println("OpenStackMeetingsServlet" + request.getQueryString());
    }

	
	protected Elements load_doc(String project, String year, PrintWriter writer) {
		Document doc = null;
		try {
			doc = Jsoup.connect("http://eavesdrop.openstack.org/meetings" + "/" + project).get();
		} catch (IOException e) {
			writer.println("Project with " + project + " not found");
			e.printStackTrace();
			return null;
		}
		try {
			doc = Jsoup.connect("http://eavesdrop.openstack.org/meetings" + "/" + project + "/" + year).get();
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









