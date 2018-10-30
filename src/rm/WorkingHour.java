package rm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import um.User;
import utility.DBManager;

public class WorkingHour {
	public int id;
	public Time startTime;
	public Time endTime;
	//TODO: this class must be completed.
	public WorkingHour(int id, Time startTime, Time endTime) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public JSONObject getJSON() {
		JSONObject obj = new JSONObject();
		obj.put("start", startTime.toString());
		obj.put("end", endTime.toString());
		return obj;
	}
	
	public static WorkingHour saveWorkingHour(Time startTime, Time endTime) {
		if(startTime == null || endTime == null) {
			return null;
		}
		Integer id = existsInDB(startTime, endTime);
		if(id != null) {
			return new WorkingHour(id, startTime, endTime);
		}
		// insert it here
		WorkingHour wh = null;
		String sql = "INSERT INTO working_hours "
				+ "(start_time, end_time) "
				+ "VALUE (?, ?);";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setTime(1,startTime);
			stmt.setTime(2,endTime);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				id = rs.getInt(1);
				wh = new WorkingHour(id, startTime, endTime);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wh;
	}
	
	
	// either returns the ID of the existing record or null
	public static Integer existsInDB(Time start, Time end) {
		if(start == null || end == null) {
			return null;
		}
		Integer resultId = null;
		String sql = "SELECT * FROM working_hours WHERE start_time=? AND end_time=?;";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setTime(1, start);
			stmt.setTime(2, end);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			if (rs.next()) {
				resultId = rs.getInt("id");
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultId;
	}
	
	public static WorkingHour fetchById(int id) {
		WorkingHour result = null;
		String sql = "SELECT * FROM working_hours WHERE id=?;";
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
			if (rs.next()) {
				Time start = rs.getTime("start_time");
				Time end = rs.getTime("end_time");
				result = new WorkingHour(id, start, end);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean isWorkingNow() {
		Calendar currenttime = Calendar.getInstance();
	    Time nowTime = new Time(currenttime.getTime().getTime());
	    return nowTime.equals(startTime) || nowTime.equals(endTime) || (nowTime.after(startTime) && nowTime.before(endTime));
	}
	
	// time left in minutes
	public int getTimeLengthLeftWorking() {
		Calendar currenttime = Calendar.getInstance();
	    Time nowTime = new Time(currenttime.getTime().getTime());
	    if(! endTime.after(nowTime)) {
	    	return 0;
	    }
	    return (int)((endTime.getTime() - nowTime.getTime())/60);
	}
}
