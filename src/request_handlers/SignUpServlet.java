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
import javax.servlet.http.Part;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
		/** 
		 * There are two types of sign up request call, one is full of data and the other has just cell phone
		 */
		boolean codeVerification = false;
		if (request.getParameter(Constants.CELL_PHONE) != null) {
			codeVerification = true;
			if (request.getParameter(Constants.FIRST_NAME) != null || 
					request.getParameter(Constants.LAST_NAME) != null || 
					request.getParameter(Constants.EMAIL_ADDR) != null ||
					request.getParameter(Constants.ADS_FLAG) != null || 
					request.getParameter(Constants.PASSWORD) != null) {
				codeVerification = false;
			}
		}
		if (codeVerification) {
			// TODO: generate a random code and send it to the phone number provided
			// set a code and put parameters in session
			String password = "12345";
			String cellphone = (String) request.getParameter(Constants.CELL_PHONE);
			
			// retrieve info from DB whether this user name exists in DB or not
			boolean userExist = false;
			Connection conn = DBManager.getDBManager().getConnection();
			String sql = "";
			PreparedStatement stmt;
			try {
				sql = "SELECT * FROM users WHERE cellphone=?;";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, cellphone);
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
					stmt.setString(1, password);
					stmt.setString(2, cellphone);
					stmt.executeUpdate();
					stmt.close();
					DBManager.getDBManager().closeConnection();
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else {
				// if user does not exist, put its data in the session and wait for a signup request with full data
				request.getSession().setAttribute(Constants.CELL_PHONE, cellphone);
				request.getSession().setAttribute("verificationCode", password);
			}
			
			// return result with user existence notification
			JSONObject result = new JSONObject();
			result.put("user_exist", userExist?1:0);
			result = ResponseHelper.respondWithStatus(result, true);
			ResponseHelper.respondWithJSONObject(result, response);
			return;
		}
		
		// now I want to receive a full data sign-up request
		// with the password I have set before
		if(request.getParameter(Constants.FIRST_NAME) == null ||
				request.getParameter(Constants.LAST_NAME) == null ||
				request.getParameter(Constants.CELL_PHONE) == null ||
				request.getParameter(Constants.EMAIL_ADDR) == null ||
				request.getPart(Constants.PROFILE_IMAGE) == null || 
				request.getParameter(Constants.ADS_FLAG) == null || 
				request.getParameter(Constants.PASSWORD) == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.INPUT_INFO_INCOMPLETE, response);
			return;
		}
		
		// I know that data is complete, so first check it with parameters in the session 
		// and then if it is OK, put it in the database and set this user as signed in user in session
		String fname = (String) request.getParameter(Constants.FIRST_NAME);
		String lname = (String) request.getParameter(Constants.LAST_NAME);
		String cellphone = (String) request.getParameter(Constants.CELL_PHONE);
		String emailAddr = (String) request.getParameter(Constants.EMAIL_ADDR);
		Part profileImagePart = request.getPart(Constants.PROFILE_IMAGE);
		int adsFlag = Integer.parseInt(request.getParameter(Constants.ADS_FLAG));
		String password = (String) request.getParameter(Constants.PASSWORD);
		
		// check cell phone and password provided with the data in the session
		String preCellPhone = (String)request.getSession().getAttribute(Constants.CELL_PHONE);
		String preCode = (String) request.getSession().getAttribute("verificationCode");
		if (preCellPhone == null || preCode == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.VERIFICATION_STEP_NOT_STARTED, response);
			return;
		}
		if (cellphone.equals(preCellPhone) && password.equals(preCode)) {
			// first check if no user is currently signed in
			User customer = UserManager.getCM().getUser(request);
			if(customer != null) {
				UserManager.getCM().signOutCustomer(request);
			}
			
			// everything is fine, create new user
			
			
			ResponseHelper.respondWithJSONObject(UserManager.getCM().signUpCustomer(request,
					fname, lname, cellphone, emailAddr, profileImagePart, adsFlag, password), response);
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
