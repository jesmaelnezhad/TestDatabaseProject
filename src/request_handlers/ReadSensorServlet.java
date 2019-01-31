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
import um.User;
import um.UserManager;
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
		String sensorIdString = RequestHelper.getRequestParameter(request, Constants.ID);
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
		User customer = UserManager.getCM().getUser(request);
		//TODO: the type of user should probably be basestation. This must be checked
		// 		when we confirm this.
		
		City city = UserManager.getCM().getCity(request);
		
//		ResponseHelper.respondWithJSONObject(rm.readSensor(city, sensorId), response);
		ResponseHelper.respondWithJSONObject(rm.readSensor(city, sensorId), response);
	    return;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
