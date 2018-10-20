package rm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import rm.Reservation.ReservationType;
import rm.basestations.SensorId;
import rm.parking_structure.ParkingSpotContainer;
import rm.parking_structure.Sector;
import utility.DBManager;

public class Spot {
	public Sector sector;
	public int localSpotId;
	public SensorId sensorId;
	
	private Spot(Sector sector, int localSpotId, SensorId sensorId) {
		this.sector = sector;
		this.localSpotId = localSpotId;
		this.sensorId = sensorId;
	}
	
	private Spot(Sector sector, int localSpotId) {
		this.sector = sector;
		this.localSpotId = localSpotId;
		this.sensorId = null;
	}
	
	private Spot(Sector sector, SensorId sensorId) {
		this.sector = sector;
		this.localSpotId = -1;
		this.sensorId = sensorId;
	}
	
	public static Spot fetchSpotByLocalSpotId(ParkingSpotContainer container, int localSpotId) {
		if(container == null) {
			return null;
		}
		
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
				Sector sector = container.getSectorById(sectorId);
				SensorId sensorId = null;
				int sensorId_ = rs.getInt("sensor_id");
				if(! rs.wasNull()) {
					sensorId = SensorId.toSensorId(sensorId_);
				}
				
				
				result = new Spot(sector, localSpotId, sensorId);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Spot fetchSpotBySensorId(ParkingSpotContainer container, SensorId sensorId) {
		if(container == null || sensorId == null) {
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
				Sector sector = container.getSectorById(sectorId);
				int localSpotId = -1;
				int localSpotId_ = rs.getInt("local_spot_id");
				if(! rs.wasNull()) {
					localSpotId = localSpotId_;
				}
				result = new Spot(sector, localSpotId, sensorId);
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
