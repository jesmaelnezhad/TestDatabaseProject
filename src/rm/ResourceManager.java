package rm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import rm.parking_structure.ParkingSpotContainer;
import rm.parking_structure.Sector;
import tm.ParkTransaction;
import tm.TransactionManager;
import um.Customer;
import utility.Constants;
import utility.DBManager;
import utility.Point;

public class ResourceManager {

	// A mapping from cities to the container of their parking spots
	public Map<City, ParkingSpotContainer> citySpots = new HashMap<>();
	
	private ResourceManager() {
		//TODO: initialization
	}
	public void loadFromDB() {
		//TODO: initialization
	}
	
	private static ResourceManager rm = null;
	public static ResourceManager getRM() {
		if(rm == null) {
			rm = new ResourceManager();
		}
		return rm;
	}
	
	public JSONObject rentSpot(Customer customer, City city, int sectorId, int segmentId, int carId, int rateId, int time) {
		
		JSONObject result = new JSONObject();
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			// TODO: city must be loaded from database or city is wrong
			// currently, we just return an empty result
			// prepare output
			result.put("status", "unsuccessful");
			result.put("message", "City not found.");
			return result;
		}
		JSONObject spotInfoJSONObj = container.rentSpot(sectorId, segmentId);
		if(spotInfoJSONObj == null || 
				((String)spotInfoJSONObj.get("status")).equals("unsuccessful")) {
			result.put("status", "unsuccessful");
			result.put("message", "City not found.");
			return result;
		}
		// price
		// read price rates
		List<PriceRate> priceRates = container.getSectorPriceRates(sectorId);
		PriceRate selectedRate = null;
		for(PriceRate pr : priceRates) {
			if(pr.id == rateId) {
				selectedRate = pr;
			}
		}
		int calculatedPrice = 0;
		/*
		 * 
		 * 
		 * TODO: calculate price using selectedRate and time
		 * 
		 * 
		 */
		spotInfoJSONObj.put("price", calculatedPrice);
		
		// record parkTransaction
		TransactionManager tm = TransactionManager.getTM();
		ParkTransaction transaction = 
				tm.recordNewParkTransaction(customer, carId, spotInfoJSONObj, time, rateId);
		if(transaction == null) {
			// release the spot
			int spotId = (Integer)spotInfoJSONObj.get(Constants.SPOT_ID);
			container.freeSpot(sectorId, segmentId, spotId);
			// prepare output
			result.put("status", "unsuccessful");
			result.put("message", "");
			return result;
		}
		
		JSONObject walletTransaction = customer.pay(calculatedPrice, transaction.id);
		
		if(walletTransaction == null || 
				((String)walletTransaction.get("status")).equals("unsuccessful")) {
			// delete park transaction
			tm.deleteParkTransaction(transaction.id);
			// release the spot
			int spotId = (Integer)spotInfoJSONObj.get(Constants.SPOT_ID);
			container.freeSpot(sectorId, segmentId, spotId);
			// prepare output
			result.put("status", "unsuccessful");
			result.put("message", "Payment not successful.");
			return result;
		}
		
		result.put("status", "successful");
		result.put("resource", spotInfoJSONObj);
		result.put("payment", walletTransaction);
		
		return result;
	}
	
	public JSONObject calculatePrice(City city, int sectorId, int segmentId, int rateId, int time) {
		
		JSONObject result = new JSONObject();
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			// TODO: city must be loaded from database or city is wrong
			// currently, we just return an empty result
			result.put("status", "unsuccessful");
			result.put("message", "City not found.");
			return result;
		}
		return container.calculatePrice(sectorId, segmentId, rateId, time);
	}
	
	public JSONObject getInfo(City city, int sectorId, int segmentId, int spotId) {
		
		JSONObject result = new JSONObject();
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			// TODO: city must be loaded from database or city is wrong
			// currently, we just return an empty result
			return result;
		}
		return container.getInfo(sectorId, segmentId, spotId);
	}
	// Search for sectors
	// Returns a JSONArray which is an array of JSONObjects which are sectors
	public JSONArray searchByProximity(City city, Point center, double radius){
		
		JSONArray result = new JSONArray();
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			// TODO: city must be loaded from database or city is wrong
			// currently, we just return an empty list
			return result;
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
	public static List<PriceRate> fetchSectorRates(Sector s){
		List<PriceRate> result = new ArrayList<>();
		//get price rates using s.id
		String sql = "select price_rates.id, price_rates.description, price_rates.price "
				+ "from available_rates JOIN price_rates "
				+ "WHERE available_rates.sector_id=? AND available_rates.price_rate_id=price_rates.id;";
		
		Connection conn = DBManager.getDBManager().getConnection();
		if(conn == null) {
			return result;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s.id);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			while(rs.next()){
				PriceRate rate = PriceRate.getRate(rs);
				result.add(rate);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
