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
import um.User;
import um.UserManager;
import utility.Constants;

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
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get command type
		String command = request.getParameter(Constants.COMMAND);
		
		// check if command type has value
		if (command == null) {
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, Constants.STATUS_UNSUCCESSFUL);
			result.put(Constants.MESSAGE, Constants.MSG_COMMAND_NOT_GIVEN);
			response.setContentType(Constants.TYPE_HTML);
			PrintWriter out = response.getWriter();
			out.println(result.toJSONString());
			return;
		}
		
		// get transaction manager and customer objects
		TransactionManager tm = TransactionManager.getTM(); // It is a unique object, remember
		User customer = UserManager.getCM().getUser(request);
		
		// set the city for this transaction request
		City city = null;
		if (customer != null) {
			city = customer.selected_city; 
			// TODO: Not implemented in customer object yet 
		} else {
			// TODO: if customer is null city will not be known. (jamshid this is implemented below, right?)
			// TODO: redirect to use authentication
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, Constants.MSG_CUSTOMER_NOT_SIGHNED_IN);
			result.put(Constants.STATUS, Constants.STATUS_UNSUCCESSFUL);
			response.setContentType(Constants.TYPE_HTML);
			PrintWriter out = response.getWriter();
			out.println(result.toJSONString());
		}
		
		// perform actions based on command type
		if (command.equals(Constants.COMMAND_SAVE)) {
			// save command
			// get parameters from request
			String sensorIdString = request.getParameter(Constants.SENSOR_ID);
			String priceString = request.getParameter(Constants.PRICE);
			String timeString = request.getParameter(Constants.TIME);
			
			// check if these parameters are exist, if is OK, then convert the parameter to its right type 
			// sensor ID
			if (sensorIdString == null) {
				JSONObject result = new JSONObject();
				result.put(Constants.STATUS, Constants.STATUS_UNSUCCESSFUL);
				result.put(Constants.MESSAGE, Constants.MSG_SENSOR_ID_NOT_GIVEN);
				response.setContentType(Constants.TYPE_HTML);
				PrintWriter out = response.getWriter();
				out.println(result.toJSONString());
				return;
			}
			int sensorId = Integer.parseInt(sensorIdString);
			
			// price
			if (priceString == null) {
				JSONObject result = new JSONObject();
				result.put(Constants.STATUS, Constants.STATUS_UNSUCCESSFUL);
				result.put(Constants.MESSAGE, Constants.MSG_PRICE_NOT_GIVEN);
				response.setContentType(Constants.TYPE_HTML);
				PrintWriter out = response.getWriter();
				out.println(result.toJSONString());
				return;
			}
			int price = Integer.parseInt(priceString);
			
			// time
			if (timeString == null) {
				JSONObject result = new JSONObject();
				result.put(Constants.STATUS, Constants.STATUS_UNSUCCESSFUL);
				result.put(Constants.MESSAGE, Constants.MSG_TIME_NOT_GIVEN);
				response.setContentType(Constants.TYPE_HTML);
				PrintWriter out = response.getWriter();
				out.println(result.toJSONString());
				return;
			}
			Time time = Time.valueOf(timeString);
			
			// every input is correct, so save this transaction to the database
			JSONObject result = tm.saveSensorTransaction(sensorId, price, time); // TODO: complete this function
			response.setContentType(Constants.TYPE_HTML);
			PrintWriter out = response.getWriter();
			out.println(result.toJSONString());
			return;
		}
		else if (command.equals(Constants.COMMAND_CALC_PRICE)) {
			// get the input parameters as string
			String sensorIdString = request.getParameter(Constants.SENSOR_ID);
			String timeLengthString = request.getParameter(Constants.TIME_LENGTH);
			
			// check if these parameters are exist, if is OK, then convert the parameter to its right type 
			// sensor ID
			if (sensorIdString == null) {
				JSONObject result = new JSONObject();
				result.put(Constants.STATUS, Constants.STATUS_UNSUCCESSFUL);
				result.put(Constants.MESSAGE, Constants.MSG_SENSOR_ID_NOT_GIVEN);
				response.setContentType(Constants.TYPE_HTML);
				PrintWriter out = response.getWriter();
				out.println(result.toJSONString());
				return;
			}
			int sensorId = Integer.parseInt(sensorIdString);
			
			// Time Length
			if (timeLengthString == null) {
				JSONObject result = new JSONObject();
				result.put(Constants.STATUS, Constants.STATUS_UNSUCCESSFUL);
				result.put(Constants.MESSAGE, Constants.MSG_TIME_NOT_GIVEN);
				response.setContentType(Constants.TYPE_HTML);
				PrintWriter out = response.getWriter();
				out.println(result.toJSONString());
				return;
			}
			int timeLength = Integer.parseInt(timeLengthString);
			
			// every input is correct, calculate price and send it back as response
			JSONObject result = tm.calcSensorPrice(sensorId, timeLength); // TODO: complete this function
			response.setContentType(Constants.TYPE_HTML);
			PrintWriter out = response.getWriter();
			out.println(result.toJSONString());
			return;
		}		
	}
}
