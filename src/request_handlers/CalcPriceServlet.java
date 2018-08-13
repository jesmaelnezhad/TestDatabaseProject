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
@WebServlet("/CalcPriceServlet")
public class CalcPriceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CalcPriceServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getAttribute(Constants.SECTOR_ID) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_MISSING, response);
			return;
		}
		if(request.getAttribute(Constants.SEGMENT_ID) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.SEGMENT_ID_MISSING, response);
			return;
		}
		if(request.getAttribute(Constants.RATE_ID) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.RATE_ID_MISSING, response);
			return;
		}
		if(request.getAttribute(Constants.PARK_TIME) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.PARK_TIME_MISSING, response);
			return;
		}

		int sectorId = Integer.parseInt(request.getParameter(Constants.SECTOR_ID));
		int segmentId = Integer.parseInt(request.getParameter(Constants.SEGMENT_ID));
		int rateId = Integer.parseInt(request.getParameter(Constants.RATE_ID));
		int parkTime = Integer.parseInt(request.getParameter(Constants.PARK_TIME));

		ResourceManager rm = ResourceManager.getRM();
		Customer customer = CustomerManager.getCM().getCustomer(request);

		City city = CustomerManager.getCM().getCity(request);

		ResponseHelper.respondWithJSONObject(
				rm.calculatePrice(city, sectorId, segmentId, rateId, parkTime), response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
