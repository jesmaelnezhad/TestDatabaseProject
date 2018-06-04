/**
 * 
 */
package um;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

import utility.Constants;
import utility.DBManager;


/**
 * @author jam
 *
 */
public class CustomerManager {

	private static CustomerManager cm = null;
	private CustomerManager() {
		
	}
	
	public static CustomerManager getCM() {
		if(cm == null) {
			cm = new CustomerManager();
		}
		return cm;
	}
	public Customer getCustomer(HttpServletRequest request) {
		Customer customer = (Customer) request.getAttribute(Constants.SIGNED_IN_CUSTOMER);
		if(customer == null) {
			// TODO: user is not signed in
			return null;
		}
		return customer;
	}
	public void signOutCustomer(HttpServletRequest request) {
		Customer customer = (Customer) request.getAttribute(Constants.SIGNED_IN_CUSTOMER);
		if(customer == null) {
			return ;
		}
		request.removeAttribute(Constants.SIGNED_IN_CUSTOMER);
	}
	
	public JSONObject signUpCustomer(HttpServletRequest request, String username, String password, 
			String fname, String lname, String cellphone, String emailAddr, int adsFlag) {
		
		
		Customer newCustomer = insertNewCustomer(username, password, fname, 
				lname, cellphone, emailAddr, adsFlag);
		if(newCustomer == null) {
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "Customer exists.");
			return result;
		}
		request.setAttribute(Constants.SIGNED_IN_CUSTOMER, newCustomer);
		return newCustomer.getUserProfile();
	}
	
	// returns null if customer exists
	private Customer insertNewCustomer(String username, String password, 
			String fname, String lname, String cellphone, String emailAddr, int adsFlag) {
		Connection conn = DBManager.getDBManager().getConnection();
		String sql = "SELECT id FROM customers WHERE username=?;";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				int id = rs.getInt("id");
				// customer exists
				return null;
			}
			rs.close();
			stmt.close();
			sql = "INSERT INTO customers (username, password, fname, lname, cellphone, email_addr, ads_flag)"
					+ "VALUE (?, MD5(?), ?, ?, ?, ?, ?);";
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.setString(3, fname);
			stmt.setString(4, lname);
			stmt.setString(5, cellphone);
			stmt.setString(6, emailAddr);
			stmt.setInt(7, adsFlag);
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				int id = rs.getInt("id");
				Customer newCustomer = 
						new Customer(id, username, fname, lname, cellphone, emailAddr, adsFlag);
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
		Customer newCustomer = fetchCustomer(username, password);
		if(newCustomer == null) {
			result.put(Constants.STATUS, "unsuccessful");
			result.put(Constants.MESSAGE, "User/Password don't match.");
			return result;
		}
		request.setAttribute(Constants.SIGNED_IN_CUSTOMER, newCustomer);
		return newCustomer.getUserProfile();
	}
	// returns null if username and password don't match
	private Customer fetchCustomer(String username, String password) {
		Connection conn = DBManager.getDBManager().getConnection();
		PreparedStatement stmt;
		try {
			String sql = "SELECT id FROM customers WHERE username=? AND password=MD5(?);";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(1, password);

			Customer newCustomer = null;
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				int id = rs.getInt("id");
				String fname = rs.getString("fname");
				String lname = rs.getString("lname");
				String cellphone = rs.getString("cellphone");
				String emailAddr = rs.getString("email_addr");
				int adsFlag = rs.getInt("ads_flag");
				newCustomer = 
						new Customer(id, username, fname, lname, cellphone, emailAddr, adsFlag);
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
	
	public Car insertNewCar(Customer customer, String makeModel, int color) {
		Car newCar = null;
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "INSERT INTO cars (customer_id, make_model, color) "
				+ "VALUE (?, ?, ?);";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, customer.id);
			stmt.setString(2, makeModel);
			stmt.setInt(3, color);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()){
				//Retrieve by column name
				int id = rs.getInt("id");
				newCar = new Car(id);
				newCar.makeModel = makeModel;
				newCar.color = color;
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
	
	public List<Car> fetchAllCars(Customer customer){
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
