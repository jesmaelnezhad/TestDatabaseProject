package request_handlers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import um.Customer;
import um.CustomerManager;
import utility.Constants;

/**
 * Servlet implementation class SpotInfoServlet
 */
@WebServlet("/SpotInfoServlet")
public class SpotInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SpotInfoServlet() {
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
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if(request.getParameter(Constants.SECTOR_ID) == null) {
		    ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_MISSING, response);
		    return;
		}
		int sectorId = Integer.parseInt(request.getParameter(Constants.SECTOR_ID));
		int segmentId = -1;
		if(request.getParameter(Constants.SEGMENT_ID) != null) {
			segmentId = Integer.parseInt(request.getParameter(Constants.SEGMENT_ID));
		}
		int spotId = -1;
		if(request.getParameter(Constants.SPOT_ID) != null) {
			spotId = Integer.parseInt(request.getParameter(Constants.SPOT_ID));
		}
		ResourceManager rm = ResourceManager.getRM();
		Customer customer = CustomerManager.getCM().getCustomer(request);
		
		City city = null;
		if(customer != null) {
			city = customer.selected_city;
		}else {
		    ResponseHelper.respondWithMessage(false, ResponseCode.CUSTOMER_NOT_SIGNED_IN, response);
		    return;
		}

	    ResponseHelper.respondWithJSONObject(rm.getInfo(city, sectorId, segmentId, spotId) , response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
