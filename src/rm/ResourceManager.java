package rm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import request_handlers.ResponseConstants.ResponseCode;
import request_handlers.ResponseHelper;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpot;
import rm.parking_structure.ParkingSpotContainer;
import rm.parking_structure.Sector;
import tm.TransactionManager;
import um.User;
import utility.Constants;
import utility.DBManager;
import utility.Point;

public class ResourceManager {

	// A mapping from cities to the container of their parking spots
	public Map<City, ParkingSpotContainer> citySpots = new HashMap<City, ParkingSpotContainer>();
	
	private ResourceManager() {
		//TODO: initialization
	}
	public void loadFromDB() {
		//TODO: initialization
	}
	
	public City loadCity(int cityId) {
		for(City city : citySpots.keySet()) {
			if(city.id == cityId) {
				return city;
			}
		}
		City newCity = null;
		// TODO: load a city from database
		return newCity;
	}
	
	private static ResourceManager rm = null;
	public static ResourceManager getRM() {
		if(rm == null) {
			rm = new ResourceManager();
		}
		return rm;
	}
	
	
	// TODO: must be replaced with several overloads of a new method called 'reserve'.
//	public JSONObject rentSpot(User customer, City city, int sectorId, int segmentId, int carId, int rateId, int time) {
//		
//		
//		ParkingSpotContainer container = citySpots.get(city);
//		if(container == null) {
//			// TODO: city must be loaded from database or city is wrong
//			// currently, we just return an empty result
//			// prepare output
//			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
//		}
//		JSONObject spotInfoJSONObj = container.rentSpot(sectorId, segmentId);
//		if(spotInfoJSONObj == null || 
//				((String)spotInfoJSONObj.get(Constants.STATUS)).equals("unsuccessful")) {
//			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
//		}
//		// price
//		// read price rates
//		List<PriceRate> priceRates = container.getSectorPriceRates(sectorId);
//		PriceRate selectedRate = null;
//		for(PriceRate pr : priceRates) {
//			if(pr.id == rateId) {
//				selectedRate = pr;
//			}
//		}
//		int calculatedPrice = 0;
//		/*
//		 * 
//		 * 
//		 * TODO: calculate price using selectedRate and time
//		 * 
//		 * 
//		 */
//		
//		// record parkTransaction
//		TransactionManager tm = TransactionManager.getTM();
//		ParkTransaction transaction = 
//				tm.recordNewParkTransaction(customer, carId, spotInfoJSONObj, time, rateId);
//		if(transaction == null) {
//			// release the spot
//			int spotId = (Integer)spotInfoJSONObj.get(Constants.SPOT_ID);
//			container.freeSpot(sectorId, segmentId, spotId);
//
//			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
//		}
//		
//		JSONObject walletTransaction = customer.pay(calculatedPrice, transaction.id);
//		
//		if(walletTransaction == null || 
//				((String)walletTransaction.get(Constants.STATUS)).equals("unsuccessful")) {
//			// delete park transaction
//			tm.deleteParkTransaction(transaction.id);
//			// release the spot
//			int spotId = (Integer)spotInfoJSONObj.get(Constants.SPOT_ID);
//			container.freeSpot(sectorId, segmentId, spotId);
//			// prepare output
//			if(walletTransaction == null) {
//				return ResponseHelper.respondWithMessage(false, ResponseCode.PAYMENT_NOT_SUCCESSFUL);
//			}else {
//				return walletTransaction;
//			}
//		}
//		
//		JSONObject result = new JSONObject();
//		result.put(Constants.RESOURCE, spotInfoJSONObj);
//		result.put(Constants.PAYMENT, walletTransaction);
//		
//		return result;
//	}
	
	public JSONObject calculatePrice(City city, int sectorId, int time) {
		
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			// TODO: city must be loaded from database or city is wrong
			// currently, we just return an empty result
			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
		}
		return container.calculatePrice(sectorId, time);
	}
	
	public JSONObject getInfo(City city, int sectorId) {
		
		JSONObject result = new JSONObject();
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			// TODO: city must be loaded from database or city is wrong
			// currently, we just return an empty result
			return result;
		}
		return container.getInfo(sectorId);
	}
	// Search for sectors
	// Returns a JSONArray which is an array of JSONObjects which are sectors
	public JSONArray searchByRange(City city, Point topLeft, Point bottomRight){
		
		JSONArray result = new JSONArray();
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			// TODO: city must be loaded from database or city is wrong
			// currently, we just return an empty list
			return result;
		}
		return container.searchByRange(topLeft, bottomRight);
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
	
	public JSONObject updateSensors(City city, 
			int[] sensorIds, boolean[] fullFlags, 
			Time[] lastTimeChanged, Time[] lastTimeUpdated) {
		ParkingSpotContainer container = citySpots.get(city);
		
		if(container == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
		}
		
		return container.updateSensors(sensorIds, fullFlags, lastTimeChanged, lastTimeUpdated);
	}
	
	public JSONObject readSensor(City city, int sensorId) {
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
		}
		return container.readSensor(sensorId);
	}

}
