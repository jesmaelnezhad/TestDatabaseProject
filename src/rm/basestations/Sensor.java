/**
 * 
 */
package rm.basestations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.json.simple.JSONObject;

import um.Car;
import um.User;
import utility.DBManager;

/**
 * @author jam
 *
 */
public class Sensor {
	private boolean fullFlag = false;//full if true
	private Time lastTimeChanged ;
	private Time lastTimeUpdated;
	
	Sensor(boolean fullFlag, Time lastTimeChanged, Time lastTimeUpdated){
		this.fullFlag = fullFlag;
		this.lastTimeChanged = lastTimeChanged;
		this.lastTimeUpdated = lastTimeUpdated;
	}

	//
//	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	public boolean updatedSinceFlush = false;
	
	public class SensorSnapshot{
		public boolean fullFlag;
		public Time lastTimeChanged;
		public Time lastTimeUpdated;
		boolean updatedSinceFlush ;
		public JSONObject toJSON() {
			JSONObject result = new JSONObject();
			result.put("full", fullFlag);
			result.put("lastTimeChanged", lastTimeChanged.toString());
			result.put("lastTimeUpdated", lastTimeUpdated.toString());
			return result;
		}
	}
	
	// returns true if vacancy status changes
	public boolean update(boolean fullFlag, Time lastTimeChanged, Time lastTimeUpdated) {
		
		boolean result = this.fullFlag != fullFlag;
		
		this.fullFlag = fullFlag;
		this.lastTimeChanged = lastTimeChanged;
		this.lastTimeUpdated = lastTimeUpdated;
		
		updatedSinceFlush = true;
		return result;
	}
	
	public SensorSnapshot read() {
		
		SensorSnapshot snapshot = new SensorSnapshot();
		snapshot.fullFlag = this.fullFlag;
		snapshot.lastTimeChanged = this.lastTimeChanged;
		snapshot.lastTimeUpdated = this.lastTimeUpdated;
		snapshot.updatedSinceFlush = updatedSinceFlush;
		
		return snapshot;
	}
	
	public boolean isChanged() {
		boolean result = false;
		result = updatedSinceFlush;
		return result;
	}
	
	public void flush() {
		updatedSinceFlush = false;
	}
	
	public static Sensor fetchSensorById(int id) {
		Sensor sensor = null;
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "SELECT * FROM sensors WHERE id=?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				//Retrieve by column name
				int fullFlagInteger = rs.getInt("full_flag");
				Time lastChanged = rs.getTime("last_changed");
				Time lastUpdated = rs.getTime("last_updated");
				
				sensor = new Sensor(fullFlagInteger==1?true:false, lastChanged, lastUpdated);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sensor;
	}
	
	public static Sensor insertSensor(int sensorId, boolean fullFlag, Time lastChanged, Time lastUpdated) {
		Connection conn = DBManager.getDBManager().getConnection();
		String sql = "";
		PreparedStatement stmt;
		try {
			sql = "INSERT INTO sensors (id, full_flag, last_changed, last_updated)"
					+ "VALUE (?, ?, ?, ?);";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, sensorId);
			stmt.setInt(2, fullFlag?1:0);
			stmt.setTime(3, lastChanged);
			stmt.setTime(4, lastUpdated);
			stmt.executeUpdate();
			stmt.close();
			DBManager.getDBManager().closeConnection();
			return new Sensor(fullFlag, lastChanged, lastUpdated);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void updateSensor(int id, Sensor sensor) {
		Connection conn = DBManager.getDBManager().getConnection();
		String sql = "";
		PreparedStatement stmt;
		try {
			sql = "UPDATE sensors SET full_flag=?, last_changed=?, last_updated=? WHERE id=?;";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setInt(2, sensor.fullFlag?1:0);
			stmt.setTime(3, sensor.lastTimeChanged);
			stmt.setTime(4, sensor.lastTimeUpdated);
			stmt.executeUpdate();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		result.put("full", fullFlag);
		result.put("last_changed", lastTimeChanged.toString());
		result.put("last_updated", lastTimeUpdated.toString());
		return result;
	}
	
	
	
	
	
}
