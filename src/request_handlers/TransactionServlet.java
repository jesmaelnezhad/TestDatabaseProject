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
import tm.TransactionManager;
import um.Customer;
import um.CustomerManager;

/**
 * Servlet implementation class TransactionServlet
 */
@WebServlet("/TransactionServlet")
public class TransactionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TransactionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("command");
		if(command == null) {
			JSONObject result = new JSONObject();
			result.put("status", "unsuccessful");
			result.put("message", "command not given.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
		    out.println(result.toJSONString());
		    return;
		}
		///
		TransactionManager tm = TransactionManager.getTM();
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
		if(command.equals("save")) {
			String sensorIdString = request.getParameter("sensorId");
			String priceString = request.getParameter("price");
			String timeString = request.getParameter("time");

			if(sensorIdString == null) {
				JSONObject result = new JSONObject();
				result.put("status", "unsuccessful");
				result.put("message", "sensorId not given.");
			    response.setContentType("text/html");
			    PrintWriter out = response.getWriter();
			    out.println(result.toJSONString());
			    return;
			}
			int sensorId = Integer.parseInt(sensorIdString);
			if(priceString == null) {
				JSONObject result = new JSONObject();
				result.put("status", "unsuccessful");
				result.put("message", "price not given.");
			    response.setContentType("text/html");
			    PrintWriter out = response.getWriter();
			    out.println(result.toJSONString());
			    return;
			}
			int price = Integer.parseInt(priceString);
			if(timeString == null) {
				JSONObject result = new JSONObject();
				result.put("status", "unsuccessful");
				result.put("message", "time not given.");
			    response.setContentType("text/html");
			    PrintWriter out = response.getWriter();
			    out.println(result.toJSONString());
			    return;
			}
			Time time = Time.valueOf(timeString);
			JSONObject result = tm.saveSensorTransaction(sensorId, price, time);
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
		}else if(command.equals("calcprice")) {
			String sensorIdString = request.getParameter("sensorId");
			String timeLengthString = request.getParameter("timeLength");

			if(sensorIdString == null) {
				JSONObject result = new JSONObject();
				result.put("status", "unsuccessful");
				result.put("message", "sensorId not given.");
			    response.setContentType("text/html");
			    PrintWriter out = response.getWriter();
			    out.println(result.toJSONString());
			    return;
			}
			int sensorId = Integer.parseInt(sensorIdString);
			if(timeLengthString == null) {
				JSONObject result = new JSONObject();
				result.put("status", "unsuccessful");
				result.put("message", "price not given.");
			    response.setContentType("text/html");
			    PrintWriter out = response.getWriter();
			    out.println(result.toJSONString());
			    return;
			}
			int timeLength = Integer.parseInt(timeLengthString);
			JSONObject result = tm.calcSensorPrice(sensorId, timeLength);
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
		    out.println(result.toJSONString());
		    return;
		}
	}

}
