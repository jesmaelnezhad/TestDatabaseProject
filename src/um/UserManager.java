/**
 * 
 */
package um;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.json.simple.JSONObject;

import request_handlers.ResponseConstants.ResponseCode;
import rm.parking_structure.City;
import request_handlers.ResponseHelper;
import utility.Constants;
import utility.DBManager;
import utility.Photo;


/**
 * @author jam
 *
 */
public class UserManager {

	private static UserManager cm = null;
	private UserManager() {
		
	}
	
	public static UserManager getCM() {
		if(cm == null) {
			cm = new UserManager();
		}
		return cm;
	}
	public User getUser(HttpServletRequest request) {
		User customer = (User) request.getSession().getAttribute(Constants.SIGNED_IN_CUSTOMER);
		return customer;
	}
	
	public City getCity(HttpServletRequest request) {
		return (City) request.getSession().getAttribute(Constants.CITY_IN_USE);
	}
	
	public void setCity(HttpServletRequest request, City city) {
		request.getSession().setAttribute(Constants.CITY_IN_USE, city);
	}
	
	public void signOutCustomer(HttpServletRequest request) {
		User customer = (User) request.getSession().getAttribute(Constants.SIGNED_IN_CUSTOMER);
		if(customer == null) {
			return ;
		}
		request.getSession().removeAttribute(Constants.SIGNED_IN_CUSTOMER);
	}
	
	public JSONObject signUpCustomer(HttpServletRequest request, 
			String fname, String lname, String cellphone, String emailAddr, byte[] profileImage, int adsFlag, 
			String password) {
		
		
		User newCustomer = insertNewCustomer(fname, 
				lname, cellphone, emailAddr, profileImage, adsFlag, password);
		if(newCustomer == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CUSTOMER_EXISTS);
		}
		
		/* try {
			Photo.savePhoto(Constants.USER_PROFILE_IMAGES, newCustomer.id, profileImage, request.getServletContext());
		}catch(IOException e) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		} */
		
		request.getSession().setAttribute(Constants.SIGNED_IN_CUSTOMER, newCustomer);
		return newCustomer.getUserProfile();
	}
	
	// returns null if customer exists
	private User insertNewCustomer(String fname, String lname, String cellphone, 
			String emailAddr, byte[] profileImage, int adsFlag, String password) {
		Connection conn = DBManager.getDBManager().getConnection();
		String sql = "";
		PreparedStatement stmt;
		try {
			sql = "INSERT INTO users (fname, lname, cellphone, email_addr, profile_image, ads_flag, username, password)"
					+ "VALUE (?, ?, ?, ?, ?, ?, ?, mD5(?));";
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, fname);
			stmt.setString(2, lname);
			stmt.setString(3, cellphone);
			stmt.setString(4, emailAddr);
			stmt.setBlob(5, new ByteArrayInputStream(profileImage), profileImage.length);
			stmt.setInt(6, adsFlag);
			stmt.setString(7, cellphone);
			stmt.setString(8, password);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			User newCustomer = null;
			if(rs.next()) {
				int id = rs.getInt("id");
				newCustomer = 
						new User(id, cellphone, password, fname, lname, cellphone, emailAddr, profileImage, adsFlag);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
			return newCustomer;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject signInUser(HttpServletRequest request, String username, String password) {
		User newCustomer = fetchUser(username, password);
		if(newCustomer == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.USERNAME_PASSWORD_NOT_MATCHING);
		}
		request.getSession().setAttribute(Constants.SIGNED_IN_CUSTOMER, newCustomer);
		return newCustomer.getUserProfile();
	}
	// returns null if username and password don't match
	private User fetchUser(String username, String password) {
		Connection conn = DBManager.getDBManager().getConnection();
		PreparedStatement stmt;
		try {
			String sql = "SELECT * FROM users WHERE username=? AND password=MD5(?);";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, password);

			User newCustomer = null;
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				int id = rs.getInt("id");
				String fname = rs.getString("fname");
				String lname = rs.getString("lname");
				String cellphone = rs.getString("cellphone");
				String emailAddr = rs.getString("email_addr");
				Blob imageBlob = rs.getBlob("profile_image");
				byte[] profileImage = imageBlob.getBytes(1, (int)imageBlob.length());
				int adsFlag = rs.getInt("ads_flag");
				newCustomer = 
						new User(id, username, password, fname, lname, cellphone, emailAddr, profileImage, adsFlag);
			}else {
				// username password don't match
				newCustomer = null;
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
			return newCustomer;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}
