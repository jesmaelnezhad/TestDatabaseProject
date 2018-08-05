package request_handlers;

import java.io.IOException;
import java.io.PrintWriter;

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
 * Servlet implementation class ReadSensor
 */
@WebServlet("/ReadSensor")
public class ReadSensorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadSensorServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String sensorIdString = request.getParameter("id");
		if(sensorIdString == null) {
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "Sensor id is not given.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
		}
		int sensorId = 0;
		try {
			sensorId = Integer.parseInt(sensorIdString);
		}catch(NumberFormatException e) {
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "Sensor id is invalid.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
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
				rm.readSensor(city, sensorId);
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
		
	    out.println(result.toJSONString());
	    return;
	}

}
