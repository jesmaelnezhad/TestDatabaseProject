package rm.parking_structure;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.simple.JSONObject;

public class Segment {
	
	/// lock and concurrency
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public int id;
	public Sector sector;
	
	public int parkCapacity;
	public int availablePark;
	
	// parking spots
	private List<ParkingSpot> spotList = new ArrayList<>();
	
	public Segment(int id, Sector sec) {
		this.id = id;
		sector = sec;
	}
	
	public List<ParkingSpot> getSpots() {
		if(spotList == null) {
			//TODO it's related to creation and load of the structure.
			spotList = new ArrayList<>();
		}
		return spotList;
	}
	
	public ParkingSpot getSpot(int spotId) {
		if(spotList == null) {
			//TODO it's related to creation and load of the structure.
			return null;
		}
		
		for(ParkingSpot spot : this.getSpots()) {
			if(spot.id == spotId) {
				return spot;
			}
		}
		return null;
	}
	
	public JSONObject getMinimumJSONObject() {
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("park-capacity", parkCapacity);
		result.put("available-park", availablePark);
		return result;
	}
	
	// load a Street object
	public static Segment fetchFromDB(ResultSet resultSet) {
		// TODO: read the record out of resultSet object
		
		return null;
	}
	
	// update db
	public static void updateDB(Segment s) {
		//TODO: update the db record with the content of this object
	}
}
