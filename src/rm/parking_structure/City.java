/**
 * 
 */
package rm.parking_structure;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jam
 *
 */
public class City {
	int id;
	String name;
	
	//street_id to Street object
	Map<Integer, Sector> streets = new HashMap<Integer, Sector>();
	
	// load a City object
	public static City fetchFromDB(int city_id) {
		// TODO: fetch record with id=city_id and make an object (also fetch streets of this city);
		
		return null;
	}
	
	// update db
	public static void updateDB(City c) {
		//TODO: update the db record with the content of this object
	}
}
