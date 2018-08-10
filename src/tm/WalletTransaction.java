package tm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import um.Customer;
import utility.Constants;
import utility.DBManager;
import utility.Photo;

public class WalletTransaction {
	public int id;
	public int topUp;
	public Date transaction_date;
	public String description;
	public int amount;
	
	public WalletTransaction(int id, int topUp, Date transaction_date, String description, int amount) {
		this.id = id;
		this.topUp = topUp;
		this.transaction_date = transaction_date;
		this.description = description;
		this.amount = amount;
	}
	
	public JSONObject toJSON() {
		JSONObject profile = new JSONObject();
		profile.put("id", id);
		profile.put("topUp", topUp);
		profile.put("transactionDate", transaction_date);
		profile.put("description", description);
		profile.put("amount", amount);
		return profile;
	}
	
	public static WalletTransaction saveNewTransaction(int customerId, int topUp, 
			Date transactionDate, String description, int amount) {
		String sql = "INSERT INTO wallet_transactions "
				+ "(customer_id, top_up, transaction_date, description, amount) "
				+ "VALUE (?, ?, ?, ?, ?)";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, customerId);
			stmt.setInt(2, topUp);
			stmt.setDate(3, transactionDate);
			stmt.setString(4, description);
			stmt.setInt(5, amount);
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
			return new WalletTransaction(newId, topUp, transactionDate, description, amount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void deleteTransaction(int id) {
		String sql = "DELETE FROM wallet_transactions WHERE id=?;";
		
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
	
	
	public static WalletTransaction fetchWalletTransaction(int id) {
		
		WalletTransaction result = null;
		
		String sql = "SELECT * FROM wallet_transactions WHERE id=?";
		
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
				int topUp = rs.getInt("top_up");
				Date transactionDate = rs.getDate("transaction_date");
				String description = rs.getString("description");
				int amount = rs.getInt("amount");
				result = new WalletTransaction(id, topUp, transactionDate, description, amount);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<WalletTransaction> fetchAllTransactions(Customer customer){
		if(customer == null) {
			return new ArrayList<>();
		}
		
		List<WalletTransaction> result = new ArrayList<>();
		
		String sql = "SELECT * FROM wallet_transactions WHERE customer_id=?";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, customer.id);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				int id = rs.getInt("id");
				int topUp = rs.getInt("top_up");
				Date transactionDate = rs.getDate("transaction_date");
				String description = rs.getString("description");
				int amount = rs.getInt("amount");
				result.add(new WalletTransaction(id, topUp, transactionDate, description, amount));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
