/* 
 * Author: Kevin Singh
 *   Date: 09/26/2018
 *   
 * A basic servlet application to handle sessions, query parameters and 
 *   	parsing using jsoup
 *   
 */
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class OpenStackMeetingsServlet extends HttpServlet {
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException
    {
		String session = request.getParameter("session");
		boolean startSession = false;
		if(session != null) {
			startSession = session.equalsIgnoreCase("start");
		}
		
		if(startSession) {
			Cookie cookies[] = request.getCookies();
            boolean activeSession = false;
            for(int i=0; cookies != null && i<cookies.length; i++) {
                    Cookie ck = cookies[i];
                    String cookieName = ck.getName();
                    String cookieValue = ck.getValue();
                    if ((cookieName != null && cookieName.equals("active-session")) 
                                    && cookieValue != null && cookieValue.equals("true")) {
                            response.getWriter().println("Session Active. Welcome! :)");
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
                    response.getWriter().println("No active session");
            }
			
		}
		
		Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
		Elements newsHeadlines = doc.select("#mp-itn b a");
		
		response.getWriter().println("Start session " + startSession);
		
		response.getWriter().println("OpenStackMeetingsServlet");
    }

}









