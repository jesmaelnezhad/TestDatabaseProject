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
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "sector id must be given.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
		}
		if(request.getAttribute(Constants.SEGMENT_ID) == null) {
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "segment id must be given.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
		}
		
		int sectorId = (Integer) request.getAttribute(Constants.SECTOR_ID);
		int segmentId = (Integer) request.getAttribute(Constants.SEGMENT_ID);
		int rateId = (Integer) request.getAttribute(Constants.RATE_ID);
		int parkTime = (Integer) request.getAttribute(Constants.PARK_TIME);
		
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

		JSONObject result = rm.calculatePrice(city, sectorId, segmentId, rateId, parkTime);
	      // Set response content type
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
		
	    out.println(result.toJSONString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
