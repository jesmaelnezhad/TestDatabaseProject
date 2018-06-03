package tm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rm.parking_structure.ParkingSpot;
import um.Car;
import um.Customer;
import utility.Constants;
import utility.DBManager;

public class TransactionManager {

	private static TransactionManager tm = null;
	private TransactionManager() {
		
	}
	
	public static TransactionManager getTM() {
		if(tm == null) {
			tm = new TransactionManager();
		}
		return tm;
	}
	
	
	public ParkTransaction recordNewParkTransaction(Customer customer, 
			int carId, JSONObject parkSpotInfo, int timeLength, int rateId) {
		int sectorId = (Integer)parkSpotInfo.get(Constants.SECTOR_ID);
		int segmentId = (Integer)parkSpotInfo.get(Constants.SEGMENT_ID);
		int spotId = (Integer)parkSpotInfo.get(Constants.SPOT_ID);
		
		Time startTime = new Time(Calendar.getInstance().getTimeInMillis());
		
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "INSERT INTO park_transactions "
				+ "(status, sector_id, segment_id, spot_id, customer_id, " + 
				"car_id, price_rate_id, start_time, time_length) " + 
				"VALUE ('open', ?, ?, ?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, sectorId);
			stmt.setInt(2, segmentId);
			stmt.setInt(3, spotId);
			stmt.setInt(4, customer.id);
			stmt.setInt(5, carId);
			stmt.setInt(6, rateId);
			stmt.setTime(7, startTime);
			stmt.setInt(8, timeLength);
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
			return new ParkTransaction(newId, customer, carId, sectorId, 
					segmentId, spotId, startTime, timeLength, rateId, 
					ParkTransactionStatus.OPEN);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public JSONArray getCurrentParkTransactions(Customer customer){
		
		List<ParkTransaction> transactions = new ArrayList<>();
		JSONArray results = new JSONArray();
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "SELECT * FROM park_transactions WHERE customer_id=? AND status='open';";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, customer.id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				//Retrieve by column name
				int id = rs.getInt("id");
				ParkTransactionStatus status = ((String)rs.getString("status")).equals("open")?
						ParkTransactionStatus.OPEN:ParkTransactionStatus.CLOSE;
				int sectorId = rs.getInt("sector_id");
				int segmentId = rs.getInt("segment_id");
				int spotId = rs.getInt("spot_id");
				int carId = rs.getInt("car_id");
				Car car = new Car(carId);
				car.fetchInfoFromDB();
				int rateId = rs.getInt("price_rate_id");
				Time startTime = rs.getTime("start_time");
				int timeLength = rs.getInt("time_length");
				ParkTransaction newTransaction = new ParkTransaction(id, customer, car, 
						sectorId, segmentId, spotId, startTime, timeLength, rateId, status);
				transactions.add(newTransaction);
				results.add(newTransaction.getJSON());
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	public JSONArray getAllParkTransactions(Customer customer){
		
		List<ParkTransaction> transactions = new ArrayList<>();
		JSONArray results = new JSONArray();
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "SELECT * FROM park_transactions WHERE customer_id=?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, customer.id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				//Retrieve by column name
				int id = rs.getInt("id");
				ParkTransactionStatus status = ((String)rs.getString("status")).equals("open")?
						ParkTransactionStatus.OPEN:ParkTransactionStatus.CLOSE;
				int sectorId = rs.getInt("sector_id");
				int segmentId = rs.getInt("segment_id");
				int spotId = rs.getInt("spot_id");
				int carId = rs.getInt("car_id");
				Car car = new Car(carId);
				car.fetchInfoFromDB();
				int rateId = rs.getInt("price_rate_id");
				Time startTime = rs.getTime("start_time");
				int timeLength = rs.getInt("time_length");
				ParkTransaction newTransaction = new ParkTransaction(id, customer, car, 
						sectorId, segmentId, spotId, startTime, timeLength, rateId, status);
				transactions.add(newTransaction);
				results.add(newTransaction.getJSON());
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	public void deleteParkTransaction(int transactionId) {
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "DELETE FROM park_transactions WHERE id=?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, transactionId);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double calcPrice(ParkingSpot spot, int rateId, int time) {
		
		// TODO
		return 0.0;
	}
}
