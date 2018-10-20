package request_handlers;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.omg.CORBA.portable.ResponseHandler;

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpotContainer;
import um.User;
import um.User.UserType;
import um.UserManager;
import utility.Constants;
import utility.Point;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SignInServlet")
public class SignInServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignInServlet() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
	}


	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String typeStr = (String) request.getParameter(Constants.LOGIN_TYPE);
		if(typeStr == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.LOGIN_TYPE_MISSING, response);
			return;
		}
		UserType type = UserType.fromString(typeStr);
		switch (type) {
		case Customer:
			
			break;
		case Police:
			
			break;
			
		case Basestation:
		{
			ResourceManager rm = ResourceManager.getRM();
			User customer = UserManager.getCM().getUser(request);
			
			if(customer != null) {
				// there is a signed in user. Sign out
				UserManager.getCM().signOutCustomer(request);
			}
			
			String username = (String) request.getParameter(Constants.USERNAME);
			String password = (String) request.getParameter(Constants.PASSWORD);
			
			ResponseHelper.respondWithJSONObject(
					UserManager.getCM().signInUser(request, username, password), response);
			break;
		}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
