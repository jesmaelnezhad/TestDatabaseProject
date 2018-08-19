package request_handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;

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
 * Servlet implementation class UpdateSensor
 */
@WebServlet("/UpdateSensorServlet")
public class UpdateSensorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateSensorsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String [] sensorIdStrings = request.getParameterValues("id");
		String [] fullFlagStrings = request.getParameterValues("full");
		String [] lastTimeChangedStrings = request.getParameterValues("t1");
		String [] lastTimeUpdatedStrings = request.getParameterValues("t2");
		
		if(sensorIdStrings == null || fullFlagStrings == null || 
				lastTimeChangedStrings == null || lastTimeUpdatedStrings == null) {
		    ResponseHelper.respondWithMessage(false, ResponseCode.UPDATE_SENSOR_INPUT_WRONG, response);
		    return;
		}
		
		if(sensorIdStrings.length != fullFlagStrings.length ||
				sensorIdStrings.length != lastTimeChangedStrings.length ||	
				sensorIdStrings.length != lastTimeUpdatedStrings.length ) {
			ResponseHelper.respondWithMessage(false, ResponseCode.UPDATE_SENSOR_INPUT_WRONG, response);
		    return;
		}
		
		int [] sensorIds = new int[sensorIdStrings.length];
		for(int i = 0 ; i < sensorIdStrings.length; i++) {
			sensorIds[i] = Integer.parseInt(sensorIdStrings[i]);
		}
		
		boolean [] fullFlags = new boolean[fullFlagStrings.length];
		for(int i = 0 ; i < fullFlagStrings.length; i++) {
			fullFlags[i] = Boolean.parseBoolean(fullFlagStrings[i]);
		}
		
		Time [] lastChangedTimes = new Time[lastTimeChangedStrings.length];
		for(int i = 0 ; i < lastTimeChangedStrings.length; i++) {
			lastChangedTimes[i] = Time.valueOf(lastTimeChangedStrings[i]);
		}
		
		Time [] lastUpdatedTimes = new Time[lastTimeUpdatedStrings.length];
		for(int i = 0 ; i < lastTimeUpdatedStrings.length; i++) {
			lastChangedTimes[i] = Time.valueOf(lastTimeUpdatedStrings[i]);
		}
		
		
		ResourceManager rm = ResourceManager.getRM();
		User customer = UserManager.getCM().getUser(request);
		//TODO: the type of user should probably be basestation. This must be checked
		// 		when we confirm this.
		
		City city = UserManager.getCM().getCity(request);
		
	    ResponseHelper.respondWithJSONObject(
	    		rm.updateSensors(city, sensorIds, fullFlags, lastChangedTimes, lastUpdatedTimes) , response);
	    return;
		
	}

}
