package rm.parking_structure;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.json.simple.JSONObject;

import rm.PriceRate;
import rm.ResourceManager;
import utility.Point;

public class Sector {
	
	/// lock and concurrency
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public int id;
	public Point sectorLocation = null;//TODO
	public City city;
	
	public int parkCapacity;
	public int availablePark;
	
	private List<PriceRate> priceRates = null;
	
	private List<Segment> segmentList = null;
	
	public List<PriceRate> getPriceRates() {
		if(priceRates == null) {
			priceRates = ResourceManager.fetchSectorRates(this);
		}
		return priceRates;
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
			//TODO it's related to creation and load of the structure.
			return null;
		}
		
		for(Segment segment : this.getSegments()) {
			if(segment.id == segmentId) {
				return segment;
			}
		}
		return null;
	}
	
	public Sector() {
		id = 0;
		city = null;
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
	
	
}
