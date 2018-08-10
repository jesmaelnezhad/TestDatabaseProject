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
import rm.parking_structure.ParkingSpotContainer;
import tm.WalletTransaction;
import um.Customer;
import um.CustomerManager;
import utility.Constants;
import utility.Point;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/WalletServlet")
public class WalletServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WalletServlet() {
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

//	/**
//	 * @see Servlet#getServletConfig()
//	 */
//	public ServletConfig getServletConfig() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/**
//	 * @see Servlet#getServletInfo()
//	 */
//	public String getServletInfo() {
//		// TODO Auto-generated method stub
//		return null; 
//	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: just check if customer is signed in. If so, return customer profile info.
		String command = request.getParameter("command");
		if(command == null) {
			JSONObject result = new JSONObject();
			result.put("status", "unsuccessful");
			result.put("message", "Command not given.");
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
		    out.println(result.toJSONString());
		    return;
		}
		
		ResourceManager rm = ResourceManager.getRM();
		Customer customer = CustomerManager.getCM().getCustomer(request);
		
		if(customer != null) {
			JSONObject result = new JSONObject();;
			if(command.equals(Constants.COMMAND_TOPUP)) {
				
				int amount = Integer.parseInt(request.getParameter("amount"));
				if(command == null) {
					result.put("status", "unsuccessful");
					result.put("message", "Amount not given.");
				    response.setContentType("text/html");
				    PrintWriter out = response.getWriter();
				    out.println(result.toJSONString());
				    return;
				}
				
				result = customer.topUp(amount);
			}else if(command.equals(Constants.COMMAND_WALLET_INFO)) {
				result = customer.getWalletInfo();
			}else if(command.equals(Constants.COMMAND_WALLET_TRANSACTIONS)) {
				JSONArray results = customer.getTransactionHistory();
			    response.setContentType("text/html");
			    PrintWriter out = response.getWriter();
				
			    out.println(results.toJSONString());
			    return;
			}
			
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
			
		    out.println(result.toJSONString());
		    return;
		}else {
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

//	/**
//	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
//	 */
//	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//	}
//
//	/**
//	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
//	 */
//	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//	}
//
//	/**
//	 * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse)
//	 */
//	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//	}
//
//	/**
//	 * @see HttpServlet#doOptions(HttpServletRequest, HttpServletResponse)
//	 */
//	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//	}
//
//	/**
//	 * @see HttpServlet#doTrace(HttpServletRequest, HttpServletResponse)
//	 */
//	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//	}

}
