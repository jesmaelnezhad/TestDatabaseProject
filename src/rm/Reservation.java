package rm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;


import utility.DBManager;

public class Reservation {
	public enum ReservationType{
		None,
		LocalSpotId,
		SectorId,
		SensorId;
		public static String toString(ReservationType type) {
			switch (type) {
			case None:
				return "none";
			case LocalSpotId:
				return "localSpotId";
			case SectorId:
				return "sectorId";
			case SensorId:
				return "sensorId";
			}
			return "";
		}
		public static ReservationType fromString(String str) {
			if("none".equals(str)) {
				return ReservationType.None;
			}else if("localSpotId".equals(str)) {
				return ReservationType.LocalSpotId;
			}else if("sectorId".equals(str)) {
				return ReservationType.SectorId;
			}else if("sensorId".equals(str)) {
				return ReservationType.SensorId;
			}
			return ReservationType.None;
		}
	}
	
	public int id;
	public ReservationType type;
	public int locationId; // could be local_spot_id, sector_id, sensor_id or none of them
	public int carId;
	public Time startTime;
	public int timeLength;
	
	
	
	public Reservation(int id, int carId, int locationId, Boolean getLocalSpotId, Time startTime, int timeLength) {
		this.id = id;
		this.carId = carId;
		this.type = ReservationType.SectorId;
		if (getLocalSpotId) {this.type = ReservationType.LocalSpotId;}
		this.locationId = locationId;
		this.startTime = startTime;
		this.timeLength = timeLength;
	}
	
	public Reservation(int id, int carSensorId, Boolean getCarId, Time startTime, int timeLength) {
		this.id = id;
		this.type = ReservationType.SensorId;
		this.locationId = carSensorId; 
		this.carId = -1;// as non-sense value
		if (getCarId) {
			this.type = ReservationType.None;
			this.locationId = -1;
			this.carId = carSensorId;
			this.startTime = startTime;
			this.timeLength = timeLength;
		}
	}
		
	private Reservation(int id, ReservationType type, int locationId, int carId, Time startTime, int timeLength) {
		this.id = id;
		this.type = type;
		this.locationId = locationId;
		this.carId = carId;
		this.startTime = startTime;
		this.timeLength = timeLength;
	}
	
	
	public JSONObject toJSON() {
		JSONObject profile = new JSONObject();
		profile.put("id", id);
		profile.put("type", ReservationType.toString(type));
		if (type != ReservationType.SensorId)
			profile.put("car_id", carId);
		if (type != ReservationType.None)
			profile.put("location_id", locationId);
		profile.put("start_time", startTime.toString());
		profile.put("time_length", timeLength);
		return profile;
	}
	
	
	public static Reservation saveNewReservation(int carId, int locationId, boolean getLocalSpotId, 
			Time startTime, int timeLength) {
		String sql = "INSERT INTO reservations "
				+ "(type, location_id, car_id, start_time, time_length) "
				+ "VALUE (?, ?, ?, ?, ?)";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ReservationType tempType = ReservationType.SectorId;
			if (getLocalSpotId) {
				tempType = ReservationType.LocalSpotId;
			}
			stmt.setString(1, ReservationType.toString(tempType));
			stmt.setInt(2, locationId);
			stmt.setInt(3, carId);
			stmt.setTime(4, startTime);
			stmt.setInt(5, timeLength);
			
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			int newId = 0;
			if (rs.next()) {
				newId = rs.getInt(1);
			}else {
				return null;
			}
			rs.close();
			stmt.close();
			return new Reservation(newId, carId, locationId, getLocalSpotId, startTime, timeLength);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Reservation saveNewReservation(int carSensorId, boolean getCarId, Time startTime, 
			int timeLength) {
		String sql = "INSERT INTO reservations "
				+ "(type, location_id, car_id, start_time, time_length) "
				+ "VALUE (?, ?, ?, ?, ?)";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ReservationType tempType = ReservationType.SensorId;
			int tempCarId = -1;
			int tempLocationId = carSensorId;
			if (getCarId) {
				tempType = ReservationType.None;
				tempLocationId = -1;
				tempCarId = carSensorId;
			}
			stmt.setString(1, ReservationType.toString(tempType));
			stmt.setInt(2, tempLocationId);
			stmt.setInt(3, tempCarId);
			stmt.setTime(4, startTime);
			stmt.setInt(5, timeLength);
			
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			int newId = 0;
			if (rs.next()) {
				newId = rs.getInt(1);
			}else {
				return null;
			}
			rs.close();
			stmt.close();
			return new Reservation(newId, carSensorId, getCarId, startTime, timeLength);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void deleteReservation(int id) {
		String sql = "DELETE FROM reservation WHERE id=?;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	
	public static Reservation fetchReservation(int id) {
		Reservation result = null;
		String sql = "SELECT * FROM reservations WHERE id=?";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while (rs.next()) {
				String typeStr = rs.getString("type");
				ReservationType type = ReservationType.fromString(typeStr);
				int locationId = rs.getInt("location_id");
				int carId = rs.getInt("car_id");
				Time startTime = rs.getTime("start_time");
				int timeLength = rs.getInt("time_length");
				result = new Reservation(id, type, locationId, carId, startTime, timeLength);
				rs.close();
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 
	 * @param carId
	 * @param sensorId
	 * @return list of all matching reservations in the database. Empty list if there are none.
	 */
	public static List<Reservation> findReservation(int carId, int sensorId) {
		List<Reservation> results = new ArrayList<>();
		// TODO: find if there are any reservations with this carId or this sensorId
		//       and add them to 'results'
		return results;
	}
		

	
	/* public static List<Transaction> fetchAllTransactions(Wallet wallet){
		if(wallet == null) {
			return new ArrayList<>();
		}
		
		List<Transaction> result = new ArrayList<>();
		
		String sql = "SELECT * FROM transactions WHERE payer_id=?";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "" + wallet.id);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				int id = rs.getInt("id");
				String typeStr = rs.getString("type");
				TransactionType type = TransactionType.fromString(typeStr);
				String payerId = rs.getString("payer_id");
				int reservationId = -1;
				if(type != TransactionType.TopUp) {
					reservationId = rs.getInt("reservation_id");
				}
				int walletId = -1;
				if(type != TransactionType.PaymentByRFCard) {
					walletId = Integer.parseInt(payerId);
				}
				int price = rs.getInt("price");
				Date transactionDate = rs.getDate("transaction_date");
				Time transactionTime = rs.getTime("transaction_time");
				String description = rs.getString("description");
				switch (type) {
				case TopUp:
					result.add(new Transaction(id,walletId, 
							transactionDate, transactionTime, description, price));
					break;
				case PaymentByWallet:
					result.add(new Transaction(id,walletId, reservationId, 
							transactionDate, transactionTime, description, price));
					break;
				case PaymentByRFCard:
					result.add(new Transaction(id,payerId, reservationId, 
							transactionDate, transactionTime, description, price));
					break;
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	} */
}
