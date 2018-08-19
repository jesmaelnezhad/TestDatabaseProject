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

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import um.User;
import um.UserManager;
import um.User.UserType;
import utility.Constants;

/**
 * Servlet implementation class SpotInfoServlet
 */
@WebServlet("/SpotInfoServlet")
public class SectorInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SectorInfoServlet() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if(request.getParameter(Constants.SECTOR_ID) == null) {
		    ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_MISSING, response);
		    return;
		}
		int sectorId = Integer.parseInt(request.getParameter(Constants.SECTOR_ID));
		
		ResourceManager rm = ResourceManager.getRM();
		User customer = UserManager.getCM().getUser(request);
		if(customer.type != UserType.Customer) {
			ResponseHelper.respondWithMessage(false, ResponseCode.REQUEST_NOT_SUPPORTED, response);
			return;
		}
		
		City city = UserManager.getCM().getCity(request);

	    ResponseHelper.respondWithJSONObject(rm.getInfo(city, sectorId) , response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
