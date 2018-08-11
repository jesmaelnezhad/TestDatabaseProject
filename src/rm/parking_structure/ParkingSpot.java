/**
 * 
 */
package rm.parking_structure;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.simple.JSONObject;

import rm.PriceRate;
import rm.ResourceManager;
import utility.DBManager;
import utility.ParkingSpotStatus;

/**
 * @author jam
 *
 */
public class ParkingSpot {
	
	/// lock and concurrency
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	// id and location in paking structure
	public int id;
	public int base_station_id;
	public int parkometer_id;
	public int sensor_id;
	public Segment segment;
	
	// info
	public ParkingSpotStatus status;
	
	public JSONObject getMinimumJSONObject() {
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("base_station_id", base_station_id);
		result.put("parkometer_id", parkometer_id);
		result.put("sensor_id", sensor_id);
		result.put("status", (status == ParkingSpotStatus.EMPTY)? "EMPTY" : "FULL");
		
		return result;
	}
	
	public String getJSONShort() {
		String result = "{\"id\":" + id + 
				"\"base_station_id\":" + base_station_id +
				"\"parkometer_id\":" + parkometer_id +
				"\"sensor_id\":" + sensor_id +
				"\"status\":" + (status==ParkingSpotStatus.EMPTY?"EMPTY":"FULL") ;
		return result;
	}
	
	public String getJSONLong() {
		String result = "{\"id\":" + id + 
				"\"base_station_id\":" + base_station_id + "," +
				"\"parkometer_id\":" + parkometer_id + "," +
				"\"sensor_id\":" + sensor_id + "," +
				"\"status\":" + (status==ParkingSpotStatus.EMPTY?"\"EMPTY\"":"\"FULL\"") ;
		List<PriceRate> rates = segment.sector.getPriceRates();
		result += ", \"price_rates\":[";
		for(int i = 0 ; i < rates.size(); ++i) {
			result += rates.get(i).getJSON();
			if(i < rates.size()-1) {
				result += ",";
			}
		}
		result +="]";
		return result;
	}
	
	
	// load a Street object
	public static ParkingSpot fetchFromDB(ResultSet resultSet) {
		// TODO: read the record out of resultSet object
		
		return null;
	}
	
	// update db
	public static void updateDB(ParkingSpot s) {
		//update the db record with the content of this object
		Connection conn = DBManager.getDBManager().getConnection();
		try {
			String sql = "UPDATE parking_spots SET status = '"
					+(s.status == ParkingSpotStatus.EMPTY?"EMPTY":"FULL")+"' WHERE id=?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s.id);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
