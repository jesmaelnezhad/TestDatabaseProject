package request_handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import request_handlers.ResponseConstants.ResponseCode;
import rm.Reservation.ReservationType;
import rm.ResourceManager;
import rm.parking_structure.City;
import tm.TransactionManager;
import um.User;
import um.UserManager;
import utility.Constants;

/**
 * Servlet implementation class ReserveServlet
 */
@WebServlet("/ReserveServlet")
public class ReserveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReserveServlet() {
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
		int type = -1;
		if(request.getParameter(Constants.TYPE) == null) {
		    ResponseHelper.respondWithMessage(false, ResponseCode.RESERVATION_TYPE_MISSING, response);
		    return;
		}else {
			try {
				type = Integer.parseInt(request.getParameter(Constants.TYPE));
			} catch (NumberFormatException e) {
			    ResponseHelper.respondWithMessage(false, ResponseCode.RESERVATION_TYPE_MISSING, response);
			    return; 
			}
		}
		
		int timeLength = -1;
		if(request.getParameter(Constants.TIME_LENGTH) == null) {
		    ResponseHelper.respondWithMessage(false, ResponseCode.RESERVATION_TIME_MISSING, response);
		    return;
		}else {
			timeLength = Integer.parseInt(request.getParameter(Constants.TIME_LENGTH));
		}
		Calendar currenttime = Calendar.getInstance();
	    Date now = new Date((currenttime.getTime()).getTime());
	    Time nowTime = new Time(currenttime.getTime().getTime());
		
		ResourceManager rm = ResourceManager.getRM();
		User customer = UserManager.getCM().getUser(request);
		City city = UserManager.getCM().getCity(request);
		
		switch (type) {
		case 1:{
			if(request.getParameter(Constants.LOCAL_SPOT_ID) == null) {
			    ResponseHelper.respondWithMessage(false, ResponseCode.LOCAL_SPOT_ID_MISSING, response);
			    return;
			}
			if(request.getParameter(Constants.CAR_ID) == null) {
				ResponseHelper.respondWithMessage(false, ResponseCode.CAR_ID_MISSING, response);
			    return;
			}
			int localSpotId = Integer.parseInt(request.getParameter(Constants.LOCAL_SPOT_ID));
			int carId = Integer.parseInt(request.getParameter(Constants.CAR_ID));
			ResponseHelper.respondWithJSONObject(
		    		rm.reserve(customer, city, ReservationType.LocalSpotId, 
		    				localSpotId, carId, nowTime, timeLength), response);
			break;
		}
		case 2:{
			if(request.getParameter(Constants.CAR_ID) == null) {
				ResponseHelper.respondWithMessage(false, ResponseCode.CAR_ID_MISSING, response);
			    return;
			}
			int carId = Integer.parseInt(request.getParameter(Constants.CAR_ID));
			ResponseHelper.respondWithJSONObject(
		    		rm.reserve(customer, city, carId, nowTime, timeLength), response);
			break;
		}
		case 3:{
			if(request.getParameter(Constants.SECTOR_ID) == null) {
			    ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_MISSING, response);
			    return;
			}
			if(request.getParameter(Constants.CAR_ID) == null) {
				ResponseHelper.respondWithMessage(false, ResponseCode.CAR_ID_MISSING, response);
			    return;
			}
			int sectorId = Integer.parseInt(request.getParameter(Constants.SECTOR_ID));
			int carId = Integer.parseInt(request.getParameter(Constants.CAR_ID));
			ResponseHelper.respondWithJSONObject(
		    		rm.reserve(customer, city, ReservationType.SectorId, 
		    				sectorId, carId, nowTime, timeLength), response);
			break;
		}
		case 4:{
			if(request.getParameter(Constants.SENSOR_ID) == null) {
			    ResponseHelper.respondWithMessage(false, ResponseCode.SENSOR_ID_MISSING, response);
			    return;
			}
			int sensorId = Integer.parseInt(request.getParameter(Constants.SENSOR_ID));
			ResponseHelper.respondWithJSONObject(
		    		rm.reserve(customer, city, ReservationType.SensorId, 
		    				sensorId, -1, nowTime, timeLength), response);
			break;
		}
		default:
		    ResponseHelper.respondWithMessage(false, ResponseCode.RESERVATION_TYPE_UNDEFINED, response);
		    return; 
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
