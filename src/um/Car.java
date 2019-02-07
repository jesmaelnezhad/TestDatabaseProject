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

import org.json.simple.JSONObject;

import lm.LogManager;
import utility.Constants;
import utility.DBManager;

/**
 * @author jam
 *
 */
public class Car {
	public int id;
	// info
	public String makeModel = "";
	public int color = 0;
	public String plateNumber;
	public Car(int id){
		this.id = id;
	}
	
	public String toLogString() {
		return id + "\t" + makeModel + "\t" + color + "\t" + plateNumber;
	}
	
	public void fetchInfoFromDB() {
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "SELECT * FROM cars WHERE id=?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, this.id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				//Retrieve by column name
				this.makeModel = rs.getString("make_model");
				this.color = rs.getInt("color");
				this.plateNumber = rs.getString("plate_number");
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject getJSON() {
		JSONObject result= new JSONObject();
		result.put("id", id);
		result.put("make_model", makeModel);
		result.put("color", color);
		result.put("plate_number", plateNumber);
		return result;
	}
	
	
	/**
	 * 
	 * @param plateNumber
	 * @return the matching Car object or null of no such record is found.
	 */
	public static Car findCarByPlateNumber(String plateNumber) {
		if(plateNumber == null) {
			return null;
		}
		// TODO: find and return the Car object whose plateNumber equals to the given argument.
		return null;
	}

	public static Car insertNewCar(User customer, String makeModel, int color, String plateNumber) {
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
				
				/*
				 * Log the new car in the logger for police.
				 */
				String logString = newCar.toLogString();
				int logId = LogManager.getLogger().addRecord(LogManager.LOG_GROUP_CARS, logString);
				if(logId == LogManager.LOG_ID_FAILED) {
					//TODO: log manager failed to save the record
					// TODO
					System.err.println("Failed to save the log record for car.");
				}
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newCar;
	}
	
	// returns null if car with this id doesn't exist
	public static Car editCar(Car car) {
		Car editedCar = null;
		
		// TODO: check if carId exists, if so, update the record.
		return editedCar;
	}
	
	public static List<Car> fetchAllCars(User customer){
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
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cars;
	}

}
