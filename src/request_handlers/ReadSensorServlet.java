package request_handlers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import um.Customer;
import um.CustomerManager;
import utility.Constants;

/**
 * Servlet implementation class ReadSensor
 */
@WebServlet("/ReadSensorServlet")
public class ReadSensorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadSensorServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sensorIdString = request.getParameter("id");
		if(sensorIdString == null) {
		    ResponseHelper.respondWithMessage(false, ResponseCode.SENSOR_ID_MISSING, response);
		    return;
		}
		int sensorId = 0;
		try {
			sensorId = Integer.parseInt(sensorIdString);
		}catch(NumberFormatException e) {
		    ResponseHelper.respondWithMessage(false, ResponseCode.SENSOR_ID_INVALID, response);
		    return;
		}
		
		ResourceManager rm = ResourceManager.getRM();
		Customer customer = CustomerManager.getCM().getCustomer(request);
		
		City city = CustomerManager.getCM().getCity(request);
		
		ResponseHelper.respondWithJSONObject(rm.readSensor(city, sensorId), response);
	    return;
	}

}
