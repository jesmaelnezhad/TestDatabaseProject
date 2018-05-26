package rm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import rm.parking_structure.ParkingSpotContainer;
import rm.parking_structure.Street;
import utility.Point;

public class ResourceManager {

	// A mapping from cities to the container of their parking spots
	public Map<City, ParkingSpotContainer> citySpots = new HashMap<>();
	
	
	public ParkingSpot getSpot(City city, int spotId) {
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			return null;
		}
		return container.getSpotById(spotId);
	}
	// Search for ParkingSpots
	public List<ParkingSpot> searchByProximity(Point center, double radius){
		// TODO Logically, city should be determined from the center point,
		// but it could be stored in other ways too (such as running the server 
		// environment for one city in each tomcat so the city is always the same,
		// but the database has several city records in its tables)
		City city = null;
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			return null;
		}
		return container.searchByProximity(center, radius);
	}
	
	public boolean checkSpot() {
		return true;
	}
	
	public static String getJSON(List<ParkingSpot> spotsList) {
		
		// create a JSON list
		String result = "[";
		for(int i = 0 ; i < spotsList.size(); ++i) {
			result += spotsList.get(i).getJSONShort();
			if(i != spotsList.size() - 1) {
				result += ",";
			}
		}
		result += "]";
		return result;
	}
	
	
	
	// methods
	public static List<PriceRate> fetchStreetRates(Street s){
		List<PriceRate> result = new ArrayList<>();
		//TODO
		List<Integer> rate_ids = new ArrayList<>();
		//TODO: fill out rate_ids using s.id
		
		for(Integer rate_id : rate_ids) {
			PriceRate rate = PriceRate.getRate(rate_id);
			result.add(rate);
		}
		return result;
	}
}
