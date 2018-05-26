package request_handlers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import tm.TransactionManager;
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
		int spotId = (Integer) request.getAttribute(Constants.SPOT_ID);
		int rateId = (Integer) request.getAttribute(Constants.RATE_ID);
		int parkTime = (Integer) request.getAttribute(Constants.PARK_TIME);
		ResourceManager rm = 
				(ResourceManager)request.getSession().getAttribute(Constants.RESOURCE_MANAGER);
		TransactionManager tm = 
				(TransactionManager)request.getSession().getAttribute(Constants.TRANSACTION_MANAGER);
		
		// TODO: city could be fetched from the session object
		City city = null;
		ParkingSpot spot = rm.getSpot(city, spotId);
		double price = TransactionManager.calcPrice(spot, rateId, parkTime);
		
	      // Set response content type
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
		
	    out.println("{\"status\":\"success\", \"price\":"+price+"}");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
