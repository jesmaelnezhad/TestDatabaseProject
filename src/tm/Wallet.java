package tm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import um.Customer;
import utility.DBManager;

public class Wallet {
	public int customerId;
	public int balance;
	
	public Wallet(int customerId, int balance) {
		this.customerId = customerId;
		this.balance = balance;
	}
	
	public static Wallet fetchWallet(Customer customer) {
		if(customer == null) {
			return null;
		}
		Wallet wallet = fetchWallet(customer.id);
		if(wallet == null) {
			// no wallet for this customer. Add a new wallet.
			String sql = "INSERT INTO customer_wallets (customer_id, balance) VALUE (?, ?);";
			
			Connection conn = DBManager.getDBManager().getConnection();
			if(conn == null) {
				return null;
			}
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, customer.id);
				stmt.setInt(2, 0);
				stmt.executeUpdate();
				stmt.close();
				return new Wallet(customer.id , 0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		return wallet;
	}
	
	public boolean save() {
		String sql = "UPDATE customer_wallets SET balance=? WHERE customer_id=?;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return false;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, balance);
			stmt.setInt(2, customerId);
			stmt.executeUpdate();
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Wallet fetchWallet(int customerId) {
		
		Wallet result = null;
		
		String sql = "SELECT * FROM customer_wallets WHERE customer_id=?";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, customerId);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				int balance = rs.getInt("balance");
				result = new Wallet(customerId, balance);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
