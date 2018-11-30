/**
 * 
 */
package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author jam
 *
 *	Singleton class used for maintaining db connection
 */
public class DBManager {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL="jdbc:mysql://localhost:3306/parking_system";
	private static final String USER = "parking_admin";
	private static final String PASS = "parkingadminpassword";
	//
	private Connection conn = null;
	private int connectionUsersCount = 0;
	//
	private DBManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Open a connection
			conn = null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection createNewConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Open a connection
			Connection conn =  DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeQuery("SET NAMES utf8");
			stmt.close();
			return conn;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Connection getConnection() {
		if(conn == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				// Open a connection
				conn = DBManager.createNewConnection();
				connectionUsersCount = 1;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			connectionUsersCount ++;
		}
		return conn;
	}
	
	
	private static DBManager manager = null;;
	public static DBManager getDBManager() {
		if(manager == null) {
			manager = new DBManager();
		}
		return manager;
	}
	
	public void closeConnection() {
		if(conn != null) {
			if(connectionUsersCount > 1) {
				connectionUsersCount --;
			}else {
				try {
					conn.close();
					conn = null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					conn = null;
				}
			}
		}
	}
	
	
}
