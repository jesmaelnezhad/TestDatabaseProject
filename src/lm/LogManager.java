package lm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import utility.DBManager;

public class LogManager {

	private static LogManager instance = new LogManager();
	public static LogManager getLogger() { return instance;}
	
	
	/// returns -1 if inserting a new record fails
	/// otherwise, returns the id of the newly added log record.
	public int addRecord(String group, String log) {
		LogRecord record = null;
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = "INSERT INTO logs (log_group, log, time_, date_) "
				+ "VALUE (?, ?, ?, ?);";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, group);
			stmt.setString(2, log);
			Calendar currenttime = Calendar.getInstance();
		    Date sqldate = new Date((currenttime.getTime()).getTime());
		    Time sqltime = new Time(currenttime.getTimeInMillis());
			stmt.setTime(3, sqltime);
			stmt.setDate(4, sqldate);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()){
				//Retrieve by column name
				int id = rs.getInt("id");
				String logGroup = rs.getString("log_group");
				String log_ = rs.getString("log");
				Time time = rs.getTime("time_");
				Date date = rs.getDate("date_");
				record = new LogRecord(id, logGroup, log_, time, date);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		return record.getId();
	}

	/// returns the list of log records in all groups starting from position zero
	public List<LogRecord> getRecords() {
		return getRecords(null, 0);
	}
	
	/// returns the list of log records in all groups starting from position 'p'
	public List<LogRecord> getRecords(int p) {
		return getRecords(null, p);
	}
	
	/// returns the list of log records in group 'group' starting from position zero
	public List<LogRecord> getRecords(String group) {
		return getRecords(group, 0);
	}
	
	/// returns the list of log records in group 'group' starting from position 'p'
	/// if group is null, all groups are returned
	public List<LogRecord> getRecords(String group, int p) {
		List<LogRecord> records = new ArrayList<>();
		// TODO fill records with list of log records in group
		
		Connection conn = DBManager.getDBManager().getConnection();
		
		String sql = ""; 
		if(group == null) {
			sql = "SELECT * FROM logs WHERE id > ?;";
		}else {
			sql = "SELECT * FROM logs WHERE id > ? AND log_group=?;";
		}
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, p);	
			if(group != null) {
				stmt.setString(2, group);
			}
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				//Retrieve by column name
				int id = rs.getInt("id");
				String logGroup = rs.getString("log_group");
				String log = rs.getString("log");
				Time time = rs.getTime("time_");
				Date date = rs.getDate("date_");
				records.add(new LogRecord(id, logGroup, log, time, date));
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return records;
	}
}
