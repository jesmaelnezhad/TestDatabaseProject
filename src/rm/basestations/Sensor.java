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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.json.simple.JSONObject;

import rm.PriceRate;
import rm.WorkingHour;
import rm.parking_structure.City;
import rm.parking_structure.Sector;
import um.Car;
import um.User;
import utility.DBManager;
import utility.Locker;
import utility.Permit;
import utility.Point;

/**
 * @author jam
 *
 */
public class Sensor implements Locker{
	ReentrantReadWriteLock sensorMutex = new ReentrantReadWriteLock();
	private boolean fullFlag = false;//full if true
	private int cityId = -1;
	private Time lastTimeChanged ;
	private Time lastTimeUpdated;
	
	Sensor(boolean fullFlag, int cityId, Time lastTimeChanged, Time lastTimeUpdated){
		this.fullFlag = fullFlag;
		this.cityId = cityId;
		this.lastTimeChanged = lastTimeChanged;
		this.lastTimeUpdated = lastTimeUpdated;
	}

	//
//	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean updatedSinceFlush = false;
	
	public class SensorSnapshot{
		public boolean fullFlag;
		public int cityId;
		public Time lastTimeChanged;
		public Time lastTimeUpdated;
		boolean updatedSinceFlush ;
		public JSONObject toJSON() {
			JSONObject result = new JSONObject();
			result.put("full", fullFlag);
			result.put("cityId", cityId);
			result.put("lastTimeChanged", lastTimeChanged.toString());
			result.put("lastTimeUpdated", lastTimeUpdated.toString());
			return result;
		}
	}
	
	// returns true if vacancy status changes
	public boolean update(boolean fullFlag, Time lastTimeChanged, Time lastTimeUpdated) {
		Permit writeLock = null;
		try {
			writeLock = this.getWritePermit();
			boolean result = this.fullFlag != fullFlag;
			
			this.fullFlag = fullFlag;
			this.lastTimeChanged = lastTimeChanged;
			this.lastTimeUpdated = lastTimeUpdated;
			
			updatedSinceFlush = true;
			return result;
		}finally {
			writeLock.unlock();
		}
	}
	
	public SensorSnapshot read() {
		
		Permit readLock = null;
		try {
			readLock = this.getReadPermit();
			SensorSnapshot snapshot = new SensorSnapshot();
			snapshot.fullFlag = this.fullFlag;
			snapshot.cityId = this.cityId;
			snapshot.lastTimeChanged = this.lastTimeChanged;
			snapshot.lastTimeUpdated = this.lastTimeUpdated;
			snapshot.updatedSinceFlush = updatedSinceFlush;
			
			return snapshot;
		}finally {
			readLock.unlock();
		}

	}
	
	public boolean isChanged() {
		Permit readLock = null;
		try {
			readLock = this.getReadPermit();
			return updatedSinceFlush;
		}finally {
			readLock.unlock();
		}
	}
	
	public void flush() {
		Permit writeLock = null;
		try {
			writeLock = this.getWritePermit();
			updatedSinceFlush = false;
		}finally {
			writeLock.unlock();
		}
	}
	
	// fetch from database or insert a new default record
	public static Sensor fetchOrInsertSensor(int cityId, int id) {
		Sensor sensor = fetchSensorById(id);
		if(sensor == null) {
			java.util.Date today = new java.util.Date();
			sensor = insertSensor(id, false, cityId, new Time(today.getTime()), new Time(today.getTime()));
		}
		return sensor;
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
				int cityId = rs.getInt("city_id");
				int fullFlagInteger = rs.getInt("full_flag");
				Time lastChanged = rs.getTime("last_changed");
				Time lastUpdated = rs.getTime("last_updated");
				
				sensor = new Sensor(fullFlagInteger==1?true:false, cityId, lastChanged, lastUpdated);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sensor;
	}
	
	public static Map<SensorId, Sensor> fetchByCityId(int cityId){
		Map<SensorId, Sensor> sensorIndex = new HashMap<>();
		
		String sql = "select * from sensors WHERE city_id=?;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return sensorIndex;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, cityId);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				int id = rs.getInt("id");
				boolean fullFlag = rs.getBoolean("full_flag");
				Time lastChanged = rs.getTime("last_changed");
				Time lastUpdated = rs.getTime("last_updated");
				Sensor newSensor = new Sensor(fullFlag, cityId, lastChanged, lastUpdated);
				sensorIndex.put(SensorId.toSensorId(id), newSensor);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sensorIndex;
	}
	
	public static Sensor insertSensor(int sensorId, boolean fullFlag, int cityId, Time lastChanged, Time lastUpdated) {
		Connection conn = DBManager.getDBManager().getConnection();
		String sql = "";
		PreparedStatement stmt;
		try {
			sql = "INSERT INTO sensors (id, full_flag, city_id, last_changed, last_updated)"
					+ "VALUE (?, ?, ?, ?, ?);";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, sensorId);
			stmt.setInt(2, fullFlag?1:0);
			stmt.setInt(3, cityId);
			stmt.setTime(4, lastChanged);
			stmt.setTime(5, lastUpdated);
			stmt.executeUpdate();
			stmt.close();
			DBManager.getDBManager().closeConnection();
			return new Sensor(fullFlag, cityId, lastChanged, lastUpdated);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void updateSensor(int id, Sensor sensor) {
		Connection conn = DBManager.getDBManager().getConnection();
		String sql = "";
		PreparedStatement stmt;
		try {
			sql = "UPDATE sensors SET full_flag=?, city_id=?, last_changed=?, last_updated=? WHERE id=?;";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setInt(2, sensor.fullFlag?1:0);
			stmt.setInt(3, sensor.cityId);
			stmt.setTime(4, sensor.lastTimeChanged);
			stmt.setTime(5, sensor.lastTimeUpdated);
			stmt.executeUpdate();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON() {
		Permit readLock = null;
		try {
			readLock = this.getReadPermit();
			JSONObject result = new JSONObject();
			result.put("full", fullFlag);
			result.put("city_id", cityId);
			result.put("last_changed", lastTimeChanged.toString());
			result.put("last_updated", lastTimeUpdated.toString());
			return result;
		}finally {
			readLock.unlock();
		}
	}

	@Override
	public Permit getReadPermit() {
		return new Permit(sensorMutex.readLock());
	}

	@Override
	public Permit getWritePermit() {
		return new Permit(sensorMutex.writeLock());
	}
	
	
	
	
	
}
