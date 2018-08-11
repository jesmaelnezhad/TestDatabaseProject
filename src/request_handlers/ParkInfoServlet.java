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

import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import tm.ParkTransaction;
import tm.TransactionManager;
import um.Customer;
import um.CustomerManager;
import utility.Constants;

/**
 * Servlet implementation class SpotInfoServlet
 */
@WebServlet("/ParkInfoServlet")
public class ParkInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ParkInfoServlet() {
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


		boolean ongoing = true;
		if(request.getAttribute(Constants.ON_GOING) != null) {
			ongoing = (Boolean)request.getAttribute(Constants.ON_GOING);
		}
		
		TransactionManager tm = TransactionManager.getTM();
		Customer customer = CustomerManager.getCM().getCustomer(request);
		
		if(customer == null) {
			// customer must be known to fetch current park transactions
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "customer not signed in.");
		      // Set response content type
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    // TODO : could be redirected to sign in
		    return;
		}

		JSONArray transactions = null;
		if(ongoing) {
			transactions = tm.getCurrentParkTransactions(customer);
		}else {
			transactions = tm.getAllParkTransactions(customer);
		}
		
	      // Set response content type
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
		
	    out.println(transactions.toJSONString());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
