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
import um.Car;
import um.Customer;
import um.CustomerManager;
import utility.Constants;
import utility.Point;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/CarsServlet")
public class CarsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CarsServlet() {
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

		String command = (String) request.getSession().getAttribute(Constants.COMMAND);
		if(command == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.COMMAND_MISSING, response);
			return;
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
		
		// search for sectors
		JSONObject result = new JSONObject();
		if(Constants.COMMAND_ADD.equals(command)) {
			String makeModel = request.getParameter(Constants.MAKE_MODEL);
			int color = Integer.parseInt(request.getParameter(Constants.COLOR));
			String plateNumber = request.getParameter(Constants.PLATE_NUMBER);
			Car newCar = CustomerManager.getCM().insertNewCar(customer, makeModel, color, plateNumber);
			if(newCar == null) {
				result = ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
			}else {
				result = newCar.getJSON();
			}
		}else if(Constants.COMMAND_EDIT.equals(command)) {
			int id = Integer.parseInt(request.getParameter("id"));
			String makeModel = request.getParameter(Constants.MAKE_MODEL);
			int color = Integer.parseInt(request.getParameter(Constants.COLOR));
			String plateNumber = request.getParameter(Constants.PLATE_NUMBER);
			//NOTE: the existance of these parameters is not checked because it's assumed 
			//      this request is only used by the app in correct format.
			
			Car car = new Car(id);
			car.makeModel = makeModel;
			car.color = color;
			car.plateNumber = plateNumber;
			Car editedCar = CustomerManager.getCM().editCar(car);
			if(editedCar == null) {
				result = ResponseHelper.respondWithMessage(false, ResponseCode.CAR_ID_NOT_FOUND);
			}else {
				result = editedCar.getJSON();
			}
		}else if(Constants.COMMAND_GET_ALL.equals(command)) {
			List<Car> cars = CustomerManager.getCM().fetchAllCars(customer);
			JSONArray results = new JSONArray();
			for(Car car : cars) {
				results.add(car.getJSON());
			}
		    
			ResponseHelper.respondWithJSONArray(results, response);
		    return;
		}else {
			result = ResponseHelper.respondWithMessage(false, ResponseCode.COMMAND_NOT_RECOGNIZED);
		}
		
	    ResponseHelper.respondWithJSONObject(result, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
