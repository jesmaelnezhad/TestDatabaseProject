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
import javax.servlet.http.Part;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import rm.parking_structure.ParkingSpotContainer;
import um.User;
import um.UserManager;
import utility.Constants;
import utility.Point;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUpServlet() {
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

		if(request.getParameter(Constants.FIRST_NAME) == null ||
				request.getParameter(Constants.LAST_NAME) == null ||
				request.getParameter(Constants.CELL_PHONE) == null ||
				request.getParameter(Constants.EMAIL_ADDR) == null ||
				request.getPart(Constants.PROFILE_IMAGE) == null || 
				request.getParameter(Constants.ADS_FLAG) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.INPUT_INFO_INCOMPLETE, response);
			return;
		}
		
		String fname = (String) request.getParameter(Constants.FIRST_NAME);
		String lname = (String) request.getParameter(Constants.LAST_NAME);
		String cellphone = (String) request.getParameter(Constants.CELL_PHONE);
		String emailAddr = (String) request.getParameter(Constants.EMAIL_ADDR);
		Part profileImagePart = request.getPart(Constants.PROFILE_IMAGE);
		int adsFlag = Integer.parseInt(request.getParameter(Constants.ADS_FLAG));
		


		
		
		ResourceManager rm = ResourceManager.getRM();
		User customer = UserManager.getCM().getUser(request);
		
		if(customer != null) {
			UserManager.getCM().signOutCustomer(request);
		}
		
	    ResponseHelper.respondWithJSONObject(UserManager.getCM().signUpCustomer(request,
				fname, lname, cellphone, emailAddr, profileImagePart, adsFlag), response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
