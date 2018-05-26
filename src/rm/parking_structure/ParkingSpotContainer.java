/**
 * 
 */
package rm.parking_structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utility.Point;

/**
 * @author jam
 *
 */
public class ParkingSpotContainer {
	// spot id to spot
	public Map<Integer, ParkingSpot> spots = new HashMap<>();
	
	public ParkingSpot getSpotById(int spotId) {
		ParkingSpot spot = spots.get(spotId);
		return spot;
	}
	
	// returns the list of all parking spots 
	public List<ParkingSpot> searchByProximity(Point center, double radius){
		
		// TODO
		return null;
	}
	
	// load the parking spots of a city into memory
	public static ParkingSpotContainer loadCity(City city) {
		
		// TODO
		return null;
	}
}
