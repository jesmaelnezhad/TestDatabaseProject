package rm.parking_structure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mysql.jdbc.Statement;

import rm.PriceRate;
import rm.ResourceManager;
import rm.WorkingHour;
import utility.DBManager;
import utility.Locker;
import utility.Permit;
import utility.Point;

public class Sector implements Locker {
	
	/// lock and concurrency
	
	public Integer id = null;
	private int priceRatesId;
	private int workingHourId;
	public Point sectorLocation;
	public City city;
	
	public int parkCapacity;
	ReentrantReadWriteLock availableParkMutex = new ReentrantReadWriteLock();
	private int availablePark;
	public int getAvailablePark() {
		Permit readLock = null;
		try {
			readLock = this.getReadPermit();
			return availablePark;
		}finally {
			readLock.unlock();
		}
	}
	
	public void setAvailablePark(int park) {
		Permit writeLock = null;
		try {
			writeLock = this.getWritePermit();
			availablePark = park;
		}finally {
			writeLock.unlock();
		}
	}
	
	private List<PriceRate> priceRates = null;
	private WorkingHour workingHour = null;
	
	private List<Segment> segmentList = null;

	public void setPriceRates(int id, List<PriceRate> rates) {
		this.priceRates = rates;
		this.priceRatesId = id;
	}
	
	public List<PriceRate> getPriceRates() {
		if(priceRates == null) {
			priceRates = this.fetchPriceRates();
		}
		return priceRates;
	}

	public void setWorkingHour(WorkingHour hour) {
		this.workingHour = hour;
		this.workingHourId = this.workingHour.id;
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
		this.availablePark = this.parkCapacity;
	}
	
	public Sector() {
		id = 0;
		city = null;
		this.parkCapacity = 0;
		this.availablePark = this.parkCapacity;
	}
	
	public JSONObject getMinimumJSONObject() {
		Permit readLock = null;
		try {
			readLock = this.getReadPermit();
			JSONObject result = new JSONObject();
			result.put("id", id);
			result.put("location_x", sectorLocation.x);
			result.put("location_y", sectorLocation.y);
			result.put("park_capacity", parkCapacity);
			result.put("available_park", availablePark);
			return result;
		}finally {
			readLock.unlock();
		}
	}
	
	public boolean save() {
		Permit readLock = null;
		try {
			readLock = this.getReadPermit();
			Sector sector = Sector.fetchById(this.id);
			String sql = "";
			if(sector != null) {
				// update the existing record
				sql = "UPDATE sectors SET capacity=?, city_id=?, rep_x=?, rep_y=?, price_rates_id=?, working_hour_id=? WHERE id=?";
			}else {
				sql = "INSERT INTO sectors "
						+ "(id, capacity, city_id, rep_x, rep_y, price_rates_id, working_hour_id) "
						+ "VALUE (?, ?, ?, ?, ?, ?, ?);";
			}
			Connection conn = DBManager.getDBManager().getConnection();
			if (conn == null) {
				return false;
			}
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(sql);
				if(sector != null) {
					stmt.setInt(1, this.parkCapacity);
					stmt.setInt(2, this.city.id);
					stmt.setDouble(3, this.sectorLocation.x);
					stmt.setDouble(4, this.sectorLocation.y);
					stmt.setInt(5, this.priceRatesId);
					stmt.setInt(6, this.workingHourId);				
					stmt.setInt(7, this.id);
				}else {
					stmt.setInt(1, this.id);
					stmt.setInt(2, this.parkCapacity);
					stmt.setInt(3, this.city.id);
					stmt.setDouble(4, this.sectorLocation.x);
					stmt.setDouble(5, this.sectorLocation.y);
					stmt.setInt(6, this.priceRatesId);
					stmt.setInt(7, this.workingHourId);
				}
				stmt.executeUpdate();
				stmt.close();
				DBManager.getDBManager().closeConnection();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}finally {
			readLock.unlock();
		}
	}
	
	public static void deleteById(int id) {
		String sql = "DELETE FROM sectors WHERE id=?;";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return ;
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
	}
	
	public static Map<Integer, Sector> fetchByCityId(int cityId){
		Map<Integer, Sector> sectorIndex = new HashMap<>();
		
		String sql = "select * from sectors WHERE city_id=?;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return sectorIndex;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, cityId);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				int id = rs.getInt("id");
				int cap = rs.getInt("capacity");
				City city = City.fetchCityById(rs.getInt("city_id"));
				Point rep = new Point(rs.getDouble("rep_x"), rs.getDouble("rep_y"));
				PriceRate.PriceRating priceRates = PriceRate.fetchPriceRatesById(rs.getInt("price_rates_id"));
				WorkingHour wh = WorkingHour.fetchById(rs.getInt("working_hour_id"));
				Sector newSector = new Sector();
				newSector.id = id;
				newSector.parkCapacity = newSector.availablePark = cap;
				newSector.city = city;
				newSector.sectorLocation = rep;
				newSector.setPriceRates(priceRates.id, priceRates.priceRates);
				newSector.setWorkingHour(wh);
				sectorIndex.put(id, newSector);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sectorIndex;
	}
	
	public static Sector fetchById(int id) {
		Sector result = null;
		String sql = "select * from sectors WHERE id=?;";
		
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
				City city = City.fetchCityById(rs.getInt("city_id"));
				Point rep = new Point(rs.getDouble("rep_x"), rs.getDouble("rep_y"));
				PriceRate.PriceRating priceRates = PriceRate.fetchPriceRatesById(rs.getInt("price_rates_id"));
				WorkingHour wh = WorkingHour.fetchById(rs.getInt("working_hour_id"));
				result = new Sector();
				result.id = id;
				result.parkCapacity = result.availablePark = cap;
				result.city = city;
				result.setPriceRates(priceRates.id, priceRates.priceRates);
				result.setWorkingHour(wh);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
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
					result.add(PriceRate.readJSON(jsonObject));
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
				result.add(new Segment(id,  this.id,  cap, start, end));
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Permit getReadPermit() {
		return new Permit(availableParkMutex.readLock());
	}

	@Override
	public Permit getWritePermit() {
		return new Permit(availableParkMutex.writeLock());
	}
	
	
}
