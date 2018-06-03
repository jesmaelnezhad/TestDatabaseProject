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
import um.Customer;
import um.CustomerManager;
import utility.Constants;

/**
 * Servlet implementation class SpotInfoServlet
 */
@WebServlet("/SpotInfoServlet")
public class SpotInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SpotInfoServlet() {
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
		int sectorId = (Integer) request.getAttribute(Constants.SECTOR_ID);
		int segmentId = -1;
		if(request.getAttribute(Constants.SEGMENT_ID) != null) {
			segmentId = (Integer) request.getAttribute(Constants.SEGMENT_ID);
		}
		int spotId = -1;
		if(request.getAttribute(Constants.SPOT_ID) != null) {
			spotId = (Integer) request.getAttribute(Constants.SPOT_ID);
		}
		ResourceManager rm = ResourceManager.getRM();
		Customer customer = CustomerManager.getCustomer(request);
		
		City city = null;
		if(customer != null) {
			city = customer.selected_city;
		}else {
			// TODO if customer is null city will not be known.
			// TODO : redirect to use authentication
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "customer not signed in.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
		}

		JSONObject result = rm.getInfo(city, sectorId, segmentId, spotId);
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
