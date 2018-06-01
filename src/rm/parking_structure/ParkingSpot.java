/**
 * 
 */
package rm.parking_structure;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import rm.PriceRate;
import rm.ResourceManager;
import utility.ParkingSpotStatus;

/**
 * @author jam
 *
 */
public class ParkingSpot {
	// id and location in paking structure
	public int id;
	public int base_station_id;
	public int parkometer_id;
	public int sensor_id;
	public City city;
	public Segment segment;
	
	// info
	public ParkingSpotStatus status;
	
	public String getJSONShort() {
		String result = "{\"id\":" + id + 
				"\"base_station_id\":" + base_station_id +
				"\"parkometer_id\":" + parkometer_id +
				"\"sensor_id\":" + sensor_id +
				"\"city\":" + city.name +
				"\"status\":" + (status==ParkingSpotStatus.EMPTY?"EMPTY":"FULL") ;
		return result;
	}
	
	public String getJSONLong() {
		String result = "{\"id\":" + id + 
				"\"base_station_id\":" + base_station_id + "," +
				"\"parkometer_id\":" + parkometer_id + "," +
				"\"sensor_id\":" + sensor_id + "," +
				"\"city\":" + city.name + "," +
				"\"status\":" + (status==ParkingSpotStatus.EMPTY?"\"EMPTY\"":"\"FULL\"") ;
		List<PriceRate> rates = segment.sector.getPriceRates();
		result += ", \"price_rates\":[";
		for(int i = 0 ; i < rates.size(); ++i) {
			result += rates.get(i).getJSON();
			if(i < rates.size()-1) {
				result += ",";
			}
		}
		result +="]";
		return result;
	}
	
	
	// load a Street object
	public static ParkingSpot fetchFromDB(ResultSet resultSet) {
		// TODO: read the record out of resultSet object
		
		return null;
	}
	
	// update db
	public static void updateDB(ParkingSpot s) {
		//TODO: update the db record with the content of this object
	}
	

}
