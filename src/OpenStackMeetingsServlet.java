
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OpenStackMeetingsServlet extends HttpServlet {
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException
    {
		String session = request.getParameter("session");
		boolean startSession = false;
		if(session != null) {
			startSession = session.equals("start");
		}
		response.getWriter().println("Start session " + startSession);
		
		response.getWriter().println("OpenStackMeetingsServlet");
    }

}
