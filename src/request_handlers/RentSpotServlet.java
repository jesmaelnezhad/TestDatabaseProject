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
import tm.TransactionManager;
import um.Customer;
import um.CustomerManager;
import utility.Constants;

/**
 * Servlet implementation class CalcPriceServlet
 */
@WebServlet("/RentSpotServlet")
public class RentSpotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RentSpotServlet() {
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
		if(request.getParameter(Constants.SEGMENT_ID) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.SEGMENT_ID_MISSING, response);
		    return;
		}
		if(request.getParameter(Constants.CAR_ID) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.CAR_ID_MISSING, response);
		    return;
		}
		if(request.getParameter(Constants.RATE_ID) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.RATE_ID_MISSING, response);
		    return;
		}
		if(request.getParameter(Constants.PARK_TIME) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.PARK_TIME_MISSING, response);
		    return;
		}
		
		int sectorId = Integer.parseInt(request.getParameter(Constants.SECTOR_ID));
		int segmentId = Integer.parseInt(request.getParameter(Constants.SEGMENT_ID));
		int carId = Integer.parseInt(request.getParameter(Constants.CAR_ID));
		int rateId = Integer.parseInt(request.getParameter(Constants.RATE_ID));
		int parkTime = Integer.parseInt(request.getParameter(Constants.PARK_TIME));
		
		ResourceManager rm = ResourceManager.getRM();
		Customer customer = CustomerManager.getCM().getCustomer(request);
		
		City city = null;
		if(customer != null) {
			city = customer.selected_city;
		}else {
			ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND, response);
		    return;
		}

	    ResponseHelper.respondWithJSONObject(
	    		rm.rentSpot(customer, city, sectorId, segmentId, carId, rateId, parkTime), response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
