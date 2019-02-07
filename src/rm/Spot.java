package rm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import lm.LogManager;
import rm.Reservation.ReservationType;
import rm.basestations.SensorId;
import rm.parking_structure.ParkingSpotContainer;
import rm.parking_structure.Sector;
import utility.Constants;
import utility.DBManager;

public class Spot {
	public int	sectorId;
	public int localSpotId = -1;
	public SensorId sensorId = null;
	
	public Spot(int sectorId, int localSpotId, SensorId sensorId) {
		this.sectorId = sectorId;
		this.localSpotId = localSpotId;
		this.sensorId = sensorId;
	}
	
	public Spot(int sectorId, int localSpotId) {
		this.sectorId = sectorId;
		this.localSpotId = localSpotId;
		this.sensorId = null;
	}
	
	public Spot(int sectorId, SensorId sensorId) {
		this.sectorId = sectorId;
		this.localSpotId = -1;
		this.sensorId = sensorId;
	}
	
	public String toLogString() {
		return this.sectorId + "\t" + this.localSpotId + "\t" + this.sensorId;
	}
	
	//save object in DB or update the existing record
	public void save() {
		Spot spot = fetchSpot(this.sectorId, this.localSpotId, this.sensorId);
		if(spot != null) {
			return;
		}
		/**
		 * Save the new spot in logger as well: police will fetch info from logger.
		 */
		String logString = this.toLogString();
		LogManager logger = LogManager.getLogger();
		int logId = logger.addRecord(LogManager.LOG_GROUP_SPOTS, logString);
		if(logId == LogManager.LOG_ID_FAILED) {
			// TODO: logger failed. DO SOMETHING.
			return;
		}
		
		// insert
		String sql = "INSERT INTO spots "
				+ "(sector_id, local_spot_id, sensor_id) "
				+ "VALUE (?, ?, ?);";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, this.sectorId);
			stmt.setInt(2, this.localSpotId);
			stmt.setInt(3, this.sensorId==null?null:this.sensorId.toInt());
			stmt.executeUpdate();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ;
	}
	
	public static Spot fetchSpot(int sectorId, int localSpotId, SensorId sensorId) {
		Spot result = null;
		String sql = "SELECT * FROM spots WHERE sector_id=? AND local_spot_id=? AND sensor_id=?;";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, sectorId);
			stmt.setInt(2, localSpotId);
			stmt.setInt(3, sensorId==null?null:sensorId.toInt());
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			if (rs.next()) {
				result = new Spot(sectorId, localSpotId, sensorId);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Spot fetchSpotByLocalSpotId(int localSpotId) {
		Spot result = null;
		String sql = "SELECT * FROM spots WHERE local_spot_id=?";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, localSpotId);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			if (rs.next()) {
				int sectorId = rs.getInt("sector_id");
				SensorId sensorId = null;
				int sensorId_ = rs.getInt("sensor_id");
				if(! rs.wasNull()) {
					sensorId = SensorId.toSensorId(sensorId_);
				}
				
				
				result = new Spot(sectorId, localSpotId, sensorId);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Spot fetchSpotBySensorId(SensorId sensorId) {
		if(sensorId == null) {
			return null;
		}
		
		Spot result = null;
		String sql = "SELECT * FROM spots WHERE sensor_id=?";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, sensorId.toInt());
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			if (rs.next()) {
				int sectorId = rs.getInt("sector_id");
				int localSpotId = -1;
				int localSpotId_ = rs.getInt("local_spot_id");
				if(! rs.wasNull()) {
					localSpotId = localSpotId_;
				}
				result = new Spot(sectorId, localSpotId, sensorId);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
