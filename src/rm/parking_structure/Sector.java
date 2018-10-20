package rm.parking_structure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rm.PriceRate;
import rm.ResourceManager;
import rm.WorkingHour;
import utility.DBManager;
import utility.Point;

public class Sector {
	
	/// lock and concurrency
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public int id;
	private int priceRatesId;
	private int workingHourId;
	public Point sectorLocation;
	public City city;
	
	public final int parkCapacity;
	public int availablePark;
	
	private List<PriceRate> priceRates = null;
	private WorkingHour workingHour = null;
	
	private List<Segment> segmentList = null;
	
	public List<PriceRate> getPriceRates() {
		if(priceRates == null) {
			priceRates = this.fetchPriceRates();
		}
		return priceRates;
	}
	
	public WorkingHour getWorkingHour() {
		if(workingHour == null) {
			workingHour = this.fetchWorkingHour();
		}
		return workingHour;
	}
	
	public List<Segment> getSegments() {
		if(segmentList == null) {
			//TODO it's related to creation and load of the structure.
			segmentList = new ArrayList<>();
		}
		return segmentList;
	}
	public Segment getSegment(int segmentId) {
		if(segmentList == null) {
			segmentList = getSegments();
		}
		
		for(Segment segment : this.getSegments()) {
			if(segment.id == segmentId) {
				return segment;
			}
		}
		return null;
	}
	
	public Sector(int parkCapacity) {
		id = 0;
		city = null;
		this.parkCapacity = parkCapacity;
	}
	
	public JSONObject getMinimumJSONObject() {
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("location_x", sectorLocation.x);
		result.put("location_y", sectorLocation.y);
		result.put("park_capacity", parkCapacity);
		result.put("available_park", availablePark);
		return result;
	}
	
	// load a Street object
	public static Sector fetchFromDB(ResultSet resultSet) {
		// TODO: read the record out of resultSet object
		
		return null;
	}
	
	// update db
	public static void updateDB(Sector s) {
		//TODO: update the db record with the content of this object
	}
	
	public List<PriceRate> fetchPriceRates(){
		List<PriceRate> result = new ArrayList<>();
		//get price rates using s.id
		String sql = "select pricing from price_rates WHERE id=?;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return result;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, priceRatesId);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				String pricing = rs.getString("pricing");
				JSONParser parser = new JSONParser();
				JSONArray priceRatesJSONObjs;
				try {
					priceRatesJSONObjs = (JSONArray)parser.parse(pricing);
				} catch (ParseException e) {
					rs.close();
					stmt.close();
					DBManager.getDBManager().closeConnection();
					return null;
				}
				for(int i = 0 ; i < priceRatesJSONObjs.size(); i++) {
					JSONObject jsonObject = (JSONObject) priceRatesJSONObjs.get(i);
					result.add(PriceRate.readJSON(id, jsonObject));
				}
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public WorkingHour fetchWorkingHour(){
		WorkingHour result = null;
		//get price rates using s.id
		String sql = "select * from working_hours WHERE id=?;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return result;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, workingHourId);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				Time startTime = rs.getTime("start_time");
				Time endTime = rs.getTime("end_time");
				result = new WorkingHour(id, startTime, endTime);
				break;
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Segment> fetchSegments(){
		List<Segment> result = new ArrayList<>();
		//get price rates using s.id
		String sql = "select * from sector_segments WHERE sector_id=?;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return result;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				int cap = rs.getInt("capacity");
				Point start = new Point(rs.getInt("start_x"),rs.getInt("start_y"));
				Point end = new Point(rs.getInt("end_x"),rs.getInt("end_y"));
				result.add(new Segment(id,  this,  cap, start, end));
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}
