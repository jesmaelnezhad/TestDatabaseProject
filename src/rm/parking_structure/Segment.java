package rm.parking_structure;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Segment {
	public int id;
	public Sector sector;
	
	// parking spots
	private List<ParkingSpot> spotList = new ArrayList<>();
	
	public Segment(int id, Sector sec) {
		this.id = id;
		sector = sec;
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
