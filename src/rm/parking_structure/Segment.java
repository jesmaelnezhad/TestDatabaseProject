package rm.parking_structure;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.simple.JSONObject;

import rm.Reservation;
import rm.Reservation.ReservationType;
import tm.Transaction;
import tm.Transaction.TransactionType;
import utility.DBManager;
import utility.Point;

public class Segment {
	
	public int id;
	public int sectorId;
	
	public int parkCapacity;

	public Point start;
	public Point end;
	
	public Segment(int id, int sec, int cap, Point start, Point end) {
		this.id = id;
		this.sectorId = sec;
		this.parkCapacity = cap;
		this.start = start;
		this.end = end;
	}

	
	public JSONObject getMinimumJSONObject() {
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("park-capacity", parkCapacity);
		result.put("start", start.getJSON());
		result.put("end", end.getJSON());
		return result;
	}
	
	public void saveInDB() {
		// check if this record exists in DB remove it
		// then, insert it.
		Segment seg = fetchSegmentById(this.id);
		if(seg != null) {
			deleteSegmentById(this.id);
		}
		String sql = "INSERT INTO sector_segments "
				+ "(id, sector_id, capacity, start_x, start_y, end_x, end_y) "
				+ "VALUE (?, ?, ?, ?, ?, ?, ?)";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, this.id);
			stmt.setInt(2, this.sectorId);
			stmt.setInt(3, this.parkCapacity);
			stmt.setDouble(4, this.start.x);
			stmt.setDouble(5, this.start.y);
			stmt.setDouble(6, this.end.x);
			stmt.setDouble(7, this.end.y);
			stmt.executeUpdate();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ;
	}
	
	public Segment fetchSegmentById(int id) {
		
		Segment result = null;
		
		String sql = "SELECT * FROM sector_segments WHERE id=?";
		
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
			if(rs.next()){

				int sector_id = rs.getInt("sector_id");
				int capacity = rs.getInt("capacity");
				double startX = rs.getDouble("start_x");
				double startY = rs.getDouble("start_y");
				double endX = rs.getDouble("end_x");
				double endY = rs.getDouble("end_y");
				result = new Segment(id, sector_id, capacity, 
						new Point(startX, startY), new Point(endX, endY));
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void deleteSegmentById(int id) {
		String sql = "DELETE FROM sector_segments WHERE id=?;";
		
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
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
}
