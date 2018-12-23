package request_handlers;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import server.Server;
import um.User;
import um.UserManager;
import utility.Constants;

/**
 * Servlet implementation class VerifyServlet
 */
@WebServlet("/VerifyServlet")
public class VerifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		// 1. Read parameters
		// (plate, localSpotId, time_of_taking_photo)
		String plateNumber = "";
		if(request.getParameter(Constants.PLATE_NUMBER) == null) {
			// in this case, the police is just informing us about an empty spot.
		    plateNumber = null;
		}else {
			plateNumber = request.getParameter(Constants.PLATE_NUMBER);
		}
		
		String localSpotId = "";
		if(request.getParameter(Constants.PLATE_NUMBER) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.LOCAL_SPOT_ID_MISSING, response);
			return;
		}else {
			localSpotId = request.getParameter(Constants.LOCAL_SPOT_ID);
		}
		
		String photoTimeStr = "";
		if(request.getParameter(Constants.PHOTO_TIME) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.PHOTO_TIME_MISSING, response);
			return;
		}else {
			photoTimeStr = request.getParameter(Constants.PHOTO_TIME);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(Server.getServer().getTimeFormatString());
		long ms;
		try {
			ms = sdf.parse(photoTimeStr).getTime();
		} catch (ParseException e) {
			ResponseHelper.respondWithMessage(false, ResponseCode.TIME_FORMAT_INVALID, response);
			return;
		}
		Time photoTime = new Time(ms);
		
		// 2. Now, answer the parameters
		ResourceManager rm = ResourceManager.getRM();
		User policeUser = UserManager.getCM().getUser(request);
		City city = UserManager.getCM().getCity(request);
		
		if(plateNumber != null) {
			// we should verify the parked car
			
		}
		
		// we should use the given information to improve our database 
		// by possibly adding local_spot_id
		
				
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		//  SELECT R.id,R.type,R.location_id,C.plate_number,R.start_time,R.time_length FROM reservations AS R INNER JOIN cars AS C ON R.car_id=C.id;
		
	}

}
