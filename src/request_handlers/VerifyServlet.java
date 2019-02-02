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
		if(RequestHelper.getRequestParameter(request, Constants.PLATE_NUMBER) == null) {
			// in this case, the police is just informing us about an empty spot.
		    plateNumber = null;
		}else {
			plateNumber = RequestHelper.getRequestParameter(request, Constants.PLATE_NUMBER);
		}
		
		String localSpotIdStr = "";
		Integer localSpotId;
		if(RequestHelper.getRequestParameter(request, Constants.LOCAL_SPOT_ID) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.LOCAL_SPOT_ID_MISSING, response);
			return;
		}else {
			localSpotIdStr = RequestHelper.getRequestParameter(request, Constants.LOCAL_SPOT_ID);
			localSpotId = Integer.parseInt(localSpotIdStr);
		}
		
		String photoTimeStr = "";
		if(RequestHelper.getRequestParameter(request, Constants.PHOTO_TIME) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.PHOTO_TIME_MISSING, response);
			return;
		}else {
			photoTimeStr = RequestHelper.getRequestParameter(request, Constants.PHOTO_TIME);
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
			ResponseHelper.respondWithJSONObject(rm.verify(policeUser, city, 
					plateNumber, localSpotId, photoTime), response);
			return;
		}
		
		// TODO : we should use the given information to improve our database 
		// by possibly adding local_spot_id
	}

}
