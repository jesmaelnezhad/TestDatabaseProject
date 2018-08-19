package tm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import um.User;
import um.User.UserType;
import utility.DBManager;

public class Wallet {
	public int id;
	public int customerId;
	public int balance;
	
	public Wallet(int id, int customerId, int balance) {
		this.id = id;
		this.customerId = customerId;
		this.balance = balance;
	}
	
	public static Wallet fetchWallet(User customer) {
		if(customer == null || customer.type != UserType.Customer) {
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
				stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, customer.id);
				stmt.setInt(2, 0);
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
				return new Wallet(newId, customer.id , 0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		return wallet;
	}
	
	public boolean save() {
		String sql = "UPDATE customer_wallets SET balance=? WHERE id=?;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return false;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, balance);
			stmt.setInt(2, id);
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
				int id = rs.getInt("id");
				int balance = rs.getInt("balance");
				result = new Wallet(id, customerId, balance);
				break;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
