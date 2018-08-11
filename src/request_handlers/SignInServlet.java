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

import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import rm.parking_structure.ParkingSpotContainer;
import um.Customer;
import um.CustomerManager;
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
//		String username = (String) request.getAttribute(Constants.USERNAME);
//		String password = (String) request.getAttribute(Constants.PASSWORD);
		String username = "";
		String password = "";
		
		
		ResourceManager rm = ResourceManager.getRM();
		Customer customer = CustomerManager.getCM().getCustomer(request);
		
		if(customer != null) {
			// there is a signed in user. Sign out
			CustomerManager.getCM().signOutCustomer(request);
		}
		ResponseHelper.respondWithJSONObject(
				CustomerManager.getCM().signInCustomer(request, username, password), response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
