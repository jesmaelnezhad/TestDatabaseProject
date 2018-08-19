package tm;

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

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelAttribute;

import utility.DBManager;

public class Transaction {
	public enum TransactionType{
		TopUp,
		PaymentByWallet,
		PaymentByRFCard;
		public static String toString(TransactionType type) {
			switch (type) {
			case TopUp:
				return "topUp";
			case PaymentByWallet:
				return "paymentByWallet";
			case PaymentByRFCard:
				return "paymentByRFCARD";
			}
			return "";
		}
		public static TransactionType fromString(String str) {
			if("topUp".equals(str)) {
				return TransactionType.TopUp;
			}else if("paymentByWallet".equals(str)) {
				return TransactionType.PaymentByWallet;
			}else if("paymentByRFCARD".equals(str)) {
				return TransactionType.PaymentByRFCard;
			}
			return TransactionType.TopUp;
		}
	}
	
	public int id;
	public TransactionType type;
	public String payerId;//either contains the wallet_id or the rf_id
	public int reservationId = -1;
	public Date transactionDate;
	public Time transactionTime;
	public String description;
	public int price;
	
	public Transaction(int id, int walletId,
			Date transaction_date, Time transaction_time, String description, int price) {
		this.id = id;
		this.type = TransactionType.TopUp;
		this.payerId = "" + walletId;
		this.reservationId = -1;
		this.transactionDate = transaction_date;
		this.transactionTime = transaction_time;
		this.description = description;
		this.price = price;
	}
	
	public Transaction(int id, int walletId, int reservationId,
			Date transaction_date, Time transaction_time, String description, int price) {
		this.id = id;
		this.type = TransactionType.PaymentByWallet;
		this.payerId = "" + walletId;
		this.reservationId = reservationId;
		this.transactionDate = transaction_date;
		this.transactionTime = transaction_time;
		this.description = description;
		this.price = price;
	}
	
	public Transaction(int id, String rfId, int reservationId, 
			Date transaction_date, Time transaction_time, String description, int price) {
		this.id = id;
		this.type = TransactionType.PaymentByRFCard;
		this.payerId = rfId;
		this.reservationId = reservationId;
		this.transactionDate = transaction_date;
		this.transactionTime = transaction_time;
		this.description = description;
		this.price = price;
	}
	
	public JSONObject toJSON() {
		JSONObject profile = new JSONObject();
		profile.put("id", id);
		profile.put("type", TransactionType.toString(type));
		switch (type) {
		case TopUp:
			profile.put("wallet_id", payerId);
			break;
		case PaymentByWallet:
			profile.put("wallet_id", payerId);
			break;
		case PaymentByRFCard:
			profile.put("rf_id", payerId);
			break;
		}
		if(type != TransactionType.TopUp) {
			profile.put("reservation_id", reservationId);
		}
		profile.put("transaction_date", transactionDate.toString());
		profile.put("transaction_time", transactionTime.toString());
		profile.put("description", description);
		profile.put("price", price);
		return profile;
	}
	
	public static Transaction saveNewTopUp(int walletId, Date transactionDate, 
			Time transactionTime, String description, int price) {
		String sql = "INSERT INTO transactions "
				+ "(type, payer_id, price, transaction_date, transaction_time, description) "
				+ "VALUE (?, ?, ?, ?, ?, ?)";
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, TransactionType.toString(TransactionType.TopUp));
			stmt.setString(2, "" + walletId);
			stmt.setInt(3, price);
			stmt.setDate(4, transactionDate);
			stmt.setTime(5, transactionTime);
			stmt.setString(6, description);
			
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
			return new Transaction(newId, walletId,
					transactionDate, transactionTime, description, price);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Transaction saveNewRFCardTransaction(String rfId, int reservationId, Date transactionDate, 
			Time transactionTime, String description, int price) {
		String sql = "INSERT INTO transactions "
				+ "(type, payer_id, reservation_id, price, transaction_date, transaction_time, description) "
				+ "VALUE (?, ?, ?, ?, ?, ?, ?)";
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, TransactionType.toString(TransactionType.PaymentByRFCard));
			stmt.setString(2, rfId);
			stmt.setInt(3, reservationId);
			stmt.setInt(4, price);
			stmt.setDate(5, transactionDate);
			stmt.setTime(6, transactionTime);
			stmt.setString(7, description);
			
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
			return new Transaction(newId, rfId, reservationId,
					transactionDate, transactionTime, description, price);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Transaction saveNewWalletTransaction(int walletId, int reservationId, Date transactionDate, 
			Time transactionTime, String description, int price) {
		String sql = "INSERT INTO transactions "
				+ "(type, payer_id, reservation_id, price, transaction_date, transaction_time, description) "
				+ "VALUE (?, ?, ?, ?, ?, ?, ?)";
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, TransactionType.toString(TransactionType.PaymentByWallet));
			stmt.setString(2, "" + walletId);
			stmt.setInt(3, reservationId);
			stmt.setInt(4, price);
			stmt.setDate(5, transactionDate);
			stmt.setTime(6, transactionTime);
			stmt.setString(7, description);
			
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
			return new Transaction(newId, walletId, reservationId,
					transactionDate, transactionTime, description, price);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void deleteTransaction(int id) {
		String sql = "DELETE FROM transactions WHERE id=?;";
		
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
	
	
	public static Transaction fetchTransaction(int id) {
		
		Transaction result = null;
		
		String sql = "SELECT * FROM transactions WHERE id=?";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
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
					result = new Transaction(id,walletId, 
							transactionDate, transactionTime, description, price);
					break;
				case PaymentByWallet:
					result = new Transaction(id,walletId, reservationId, 
							transactionDate, transactionTime, description, price);
					break;
				case PaymentByRFCard:
					result = new Transaction(id,payerId, reservationId, 
							transactionDate, transactionTime, description, price);
					break;
				}
				
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Transaction> fetchAllTransactions(Wallet wallet){
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
	}
}
