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

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import rm.parking_structure.ParkingSpotContainer;
import um.User;
import um.UserManager;
import um.User.UserType;
import utility.Constants;
import utility.Point;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SearchAreaServlet")
public class SearchAreaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchAreaServlet() {
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
		if(request.getParameter(Constants.TOP_LEFT_X) == null
				|| request.getParameter(Constants.TOP_LEFT_Y) == null
				|| request.getParameter(Constants.BOTTOM_RIGHT_Y) == null
				|| request.getParameter(Constants.BOTTOM_RIGHT_Y) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.PARAMETERS_NOT_COMPLETE, response);
			return;
		}
		
		
		int topLeftX = Integer.parseInt(request.getParameter(Constants.TOP_LEFT_X));
		int topLeftY = Integer.parseInt(request.getParameter(Constants.TOP_LEFT_Y));
		int bottomRightX = Integer.parseInt(request.getParameter(Constants.BOTTOM_RIGHT_X));
		int bottomRightY = Integer.parseInt(request.getParameter(Constants.BOTTOM_RIGHT_Y));
		Point topLeft = new Point(topLeftX, topLeftY);
		Point bottomRight = new Point(bottomRightX, bottomRightY);
		
		
		ResourceManager rm = ResourceManager.getRM();
		User customer = UserManager.getCM().getUser(request);
		if(customer.type != UserType.Customer) {
			ResponseHelper.respondWithMessage(false, ResponseCode.REQUEST_NOT_SUPPORTED, response);
			return;
		}
		
		City city = UserManager.getCM().getCity(request);
		
	    ResponseHelper.respondWithJSONArray(
	    		rm.searchByRange(city, topLeft, bottomRight), response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
