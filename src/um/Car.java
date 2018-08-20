/**
 * 
 */
package um;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

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
	
	public void fetchInfoFromDB() {
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "SELECT * FROM cars WHERE id=?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, this.id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				//Retrieve by column name
				this.makeModel = rs.getString("make_model");
				this.color = rs.getInt("color");
				this.plateNumber = rs.getString("plate_number");
				return;
			}
			rs.close();
			stmt.close();
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
}
