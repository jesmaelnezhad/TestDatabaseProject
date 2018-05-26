package rm.parking_structure;

import java.sql.ResultSet;

public class Street {
	public int id;
	public String name;
	public City city;
	
	public Street() {
		id = 0;
		name = null;
		city = null;
	}
	
	// load a Street object
	public static Street fetchFromDB(ResultSet resultSet) {
		// TODO: read the record out of resultSet object
		
		return null;
	}
	
	// update db
	public static void updateDB(Street s) {
		//TODO: update the db record with the content of this object
	}
}
