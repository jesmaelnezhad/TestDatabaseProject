/**
 * 
 */
package um;

import java.io.IOException;
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
		User customer = (User) request.getAttribute(Constants.SIGNED_IN_CUSTOMER);
		if(customer == null) {
			// TODO: user is not signed in
			return null;
		}
		return customer;
	}
	
	public City getCity(HttpServletRequest request) {
		return (City) request.getAttribute(Constants.CITY_IN_USE);
	}
	
	public void setCity(HttpServletRequest request, City city) {
		request.setAttribute(Constants.CITY_IN_USE, city);
	}
	
	public void signOutCustomer(HttpServletRequest request) {
		User customer = (User) request.getAttribute(Constants.SIGNED_IN_CUSTOMER);
		if(customer == null) {
			return ;
		}
		request.removeAttribute(Constants.SIGNED_IN_CUSTOMER);
	}
	
	public JSONObject signUpCustomer(HttpServletRequest request, 
			String fname, String lname, String cellphone, String emailAddr, Part profileImage, int adsFlag) {
		
		
		User newCustomer = insertNewCustomer(fname, 
				lname, cellphone, emailAddr, profileImage.getName(), adsFlag);
		if(newCustomer == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CUSTOMER_EXISTS);
		}
		
		try {
			Photo.savePhoto(Constants.USER_PROFILE_IMAGES, newCustomer.id, profileImage, request.getServletContext());
		}catch(IOException e) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		}
		
		request.setAttribute(Constants.SIGNED_IN_CUSTOMER, newCustomer);
		return newCustomer.getUserProfile();
	}
	
	// returns null if customer exists
	private User insertNewCustomer(String fname, String lname, String cellphone, 
			String emailAddr, String profileImage, int adsFlag) {
		Connection conn = DBManager.getDBManager().getConnection();
		String sql = "";
		PreparedStatement stmt;
		try {
			sql = "INSERT INTO customers (fname, lname, cellphone, email_addr, profile_image, ads_flag)"
					+ "VALUE (?, ?, ?, ?, ?, ?);";
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, fname);
			stmt.setString(2, lname);
			stmt.setString(3, cellphone);
			stmt.setString(4, emailAddr);
			stmt.setString(5, profileImage);
			stmt.setInt(6, adsFlag);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				int id = rs.getInt("id");
				User newCustomer = 
						new User(id, fname, lname, cellphone, emailAddr, profileImage, adsFlag);
				return newCustomer;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject signInCustomer(HttpServletRequest request, String username, String password) {
		JSONObject result = new JSONObject();
		User newCustomer = fetchCustomer(username, password);
		if(newCustomer == null) {
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "User/Password don't match.");
			return result;
		}
		request.setAttribute(Constants.SIGNED_IN_CUSTOMER, newCustomer);
		return newCustomer.getUserProfile();
	}
	// returns null if username and password don't match
	private User fetchCustomer(String username, String password) {
		Connection conn = DBManager.getDBManager().getConnection();
		PreparedStatement stmt;
		try {
			String sql = "SELECT id FROM customers WHERE username=? AND password=MD5(?);";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(1, password);

			User newCustomer = null;
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				int id = rs.getInt("id");
				String fname = rs.getString("fname");
				String lname = rs.getString("lname");
				String cellphone = rs.getString("cellphone");
				String emailAddr = rs.getString("email_addr");
				String profileImage = rs.getString("profile_image");
				int adsFlag = rs.getInt("ads_flag");
				newCustomer = 
						new User(id, fname, lname, cellphone, emailAddr, profileImage, adsFlag);
			}else {
				// username password don't match
				newCustomer = null;
			}
			rs.close();
			stmt.close();
			return newCustomer;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Car insertNewCar(User customer, String makeModel, int color, String plateNumber) {
		Car newCar = null;
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "INSERT INTO cars (customer_id, make_model, color, plateNumber) "
				+ "VALUE (?, ?, ?, ?);";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, customer.id);
			stmt.setString(2, makeModel);
			stmt.setInt(3, color);
			stmt.setString(4, plateNumber);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()){
				//Retrieve by column name
				int id = rs.getInt("id");
				newCar = new Car(id);
				newCar.makeModel = makeModel;
				newCar.color = color;
				newCar.plateNumber = plateNumber;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newCar;
	}
	
	// returns null if car with this id doesn't exist
	public Car editCar(Car car) {
		Car editedCar = null;
		
		// TODO: check if carId exists, if so, update the record.
		return editedCar;
	}
	
	public List<Car> fetchAllCars(User customer){
		List<Car> cars = new ArrayList<>();
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "SELECT * FROM cars WHERE customer_id=?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, customer.id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				//Retrieve by column name
				int id = rs.getInt("id");
				Car newCar = new Car(id);
				newCar.makeModel = rs.getString("make_model");
				newCar.color = rs.getInt("color");
				newCar.plateNumber = rs.getString("plate_number");
				cars.add(newCar);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cars;
	}
}
