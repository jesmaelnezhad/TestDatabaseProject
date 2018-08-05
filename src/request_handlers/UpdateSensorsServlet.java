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

import rm.ResourceManager;
import rm.parking_structure.City;
import um.Customer;
import um.CustomerManager;
import utility.Constants;

/**
 * Servlet implementation class UpdateSensor
 */
@WebServlet("/UpdateSensor")
public class UpdateSensorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateSensorsServlet() {
        super();
        // TODO Auto-generated constructor stub
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
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "Four parallel arrays of id, fullFlag, lastTimeUpdated, and lastTimeChanged values must be given.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
		}
		
		if(sensorIdStrings.length != fullFlagStrings.length ||
				sensorIdStrings.length != lastTimeChangedStrings.length ||	
				sensorIdStrings.length != lastTimeUpdatedStrings.length ) {
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "Arrays must have the same size.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
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
		Customer customer = CustomerManager.getCM().getCustomer(request);
		
		City city = null;
		if(customer != null) {
			city = customer.selected_city;
		}else {
			// TODO if customer is null city will not be known.
			// TODO : redirect to use authentication
			JSONObject result = new JSONObject();
			// TODO if customer is null city will not be known.
			// TODO : redirect to use authentication
			result.put("status", "unsuccessful");
			result.put("message", "customer not signed in.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
		}
		
		JSONObject result = 
				rm.updateSensors(city, sensorIds, fullFlags, lastChangedTimes, lastUpdatedTimes);
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
		
	    out.println(result.toJSONString());
	    return;
		
	}

}
