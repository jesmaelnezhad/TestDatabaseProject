package request_handlers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.print.attribute.standard.RequestingUserName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpotContainer;
import um.User;
import um.UserManager;
import utility.Constants;
import utility.DBManager;
import utility.Point;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUpServlet() {
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
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// There are two types of sign up request call, one is full of data and the other has just cell phone
		 
		boolean codeVerification = false;
		String cellPhone = RequestHelper.getRequestParameter(request, Constants.CELL_PHONE);	
		String fName = RequestHelper.getRequestParameter(request, Constants.FIRST_NAME);
		String lName = RequestHelper.getRequestParameter(request, Constants.LAST_NAME);
		String email = RequestHelper.getRequestParameter(request, Constants.EMAIL_ADDR);
		String ads = RequestHelper.getRequestParameter(request, Constants.ADS_FLAG);
		String pass = RequestHelper.getRequestParameter(request, Constants.PASSWORD);
		if (cellPhone != null) {
			codeVerification = true;
			if (fName != null || lName != null || email != null || ads != null || pass != null) {
				codeVerification = false;
			}
		}
		else {
			ResponseHelper.respondWithMessage(false, ResponseCode.PARAMETERS_NOT_COMPLETE, response);
			return;
		}
		if (codeVerification) {
			// TODO: generate a random code and send it to the phone number provided
			// set a code and put parameters in session
			String newPassword = "12345";
			
			// retrieve info from DB whether this user name exists in DB or not
			boolean userExist = false;
			Connection conn = DBManager.getDBManager().getConnection();
			String sql = "";
			PreparedStatement stmt;
			try {
				sql = "SELECT * FROM users WHERE cellphone=?;";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, cellPhone);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					userExist = true;
				}
				rs.close();
				stmt.close();
				DBManager.getDBManager().closeConnection();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			// if user exist, I have just to set its new password
			if (userExist) {
				conn = DBManager.getDBManager().getConnection();
				try {
					sql = "UPDATE users SET password=MD5(?) WHERE cellphone=?;";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, newPassword);
					stmt.setString(2, cellPhone);
					stmt.executeUpdate();
					stmt.close();
					DBManager.getDBManager().closeConnection();
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else {
				// if user does not exist, put its data in the session and wait for a signup request with full data
				request.getSession().setAttribute(Constants.CELL_PHONE, cellPhone);
				request.getSession().setAttribute(Constants.VERIFICATION_CODE, newPassword);
			}
			
			// return result with user existence notification
			JSONObject result = new JSONObject();
			result.put(Constants.USER_EXIST, userExist?"TRUE":"FALSE");
			result = ResponseHelper.respondWithStatus(result, true);
			ResponseHelper.respondWithJSONObject(result, response);
			return;
		}
		
		// now I want to receive a full data sign-up request
		// with the password I have set before
		byte[] profileImage = RequestHelper.getRequestByteArrayParameter(request, Constants.PROFILE_IMAGE);
		if(fName == null || lName == null || cellPhone == null || email == null || profileImage == null || pass == null || ads == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.INPUT_INFO_INCOMPLETE, response);
			return;
		}
		
		// check cell phone and password provided with the data in the session
		String preCellPhone = (String)request.getSession().getAttribute(Constants.CELL_PHONE);
		String preCode = (String) request.getSession().getAttribute(Constants.VERIFICATION_CODE);
		if (preCellPhone == null || preCode == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.VERIFICATION_STEP_NOT_STARTED, response);
			return;
		}
		if (cellPhone.equals(preCellPhone) && pass.equals(preCode)) {
			// first check if no user is currently signed in
			User customer = UserManager.getCM().getUser(request);
			if(customer != null) {
				UserManager.getCM().signOutCustomer(request);
			}
			
			// everything is fine, create new user
			ResponseHelper.respondWithJSONObject(UserManager.getCM().signUpCustomer(request,
					fName, lName, cellPhone, email, profileImage, Integer.parseInt(ads), pass), response);
			return;
		} else {
			ResponseHelper.respondWithMessage(false, ResponseCode.INCORRECT_VERIFICATION_CODE, response);
			return;
		}   
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
