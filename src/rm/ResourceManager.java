package rm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import request_handlers.ResponseConstants.ResponseCode;
import request_handlers.ResponseHelper;
import rm.Reservation.ReservationType;
import rm.basestations.Sensor;
import rm.basestations.SensorId;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpotContainer;
import rm.parking_structure.Sector;
import tm.Transaction;
import tm.TransactionManager;
import tm.Wallet;
import um.User;
import utility.Constants;
import utility.DBManager;
import utility.Logger;
import utility.Point;

public class ResourceManager {

	// A mapping from cities to the container of their parking spots
	public Map<City, ParkingSpotContainer> citySpots = new HashMap<City, ParkingSpotContainer>();
	
	private ResourceManager() {
		//TODO: initialization
	}
	
	// returns true only if the file is actually parsed and loaded.
	public boolean loadFromFile(String filePath) {
		// TODO: 1. we must check for the existence of file filePath+'.idx'
		//          if this file doesn't exist, 
		// 									we must parse and load the file
		// 									and save a file filePath+'.idx'
		
		
		return false;
	}
	
	public void loadFromDB() {
		//TODO: initialization
	}
	
	public void loadCities(List<String> cityNames) {
		for(String cityName : cityNames) {
			City cityObj = City.fetchCityByName(cityName);
			if(cityObj != null) {
				loadCity(cityObj);
			}else {
				Logger.getLogger().log("City " + cityName + " could not be loaded. It does not exist in the database.");
			}
		}
	}
	
	public City loadCity(int cityId) {
		for(City city : citySpots.keySet()) {
			if(city.id == cityId) {
				return city;
			}
		}
		City newCity = City.fetchCityById(cityId);
		return loadCity(newCity);
	}
	
	public City loadCity(City city_) {
		for(City city : citySpots.keySet()) {
			if(city.id == city_.id) {
				return city;
			}
		}

		// TODO: load a city from database
		return city_;
	}
	
	private static ResourceManager rm = null;
	public static ResourceManager getRM() {
		if(rm == null) {
			rm = new ResourceManager();
		}
		return rm;
	}
	
	
	// must be replaced with several overloads of a new method called 'reserve'.
	
	public JSONObject reserve(User customer, City city,
			ReservationType type, int localSpotIdOrSectorIdOrSensorId, int carId, Time startTime, int timeLength) {
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
		}
		// 0. calculate price
		int price = 0;
		if(type == ReservationType.LocalSpotId) {
			
			Spot spot = Spot.fetchSpotByLocalSpotId(container, localSpotIdOrSectorIdOrSensorId);
			if(spot == null) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.SPOT_NOT_FOUND);
			}
			price = container.calcPrice(spot.sector, timeLength);
		}else if(type == ReservationType.SectorId) {
			Sector sector = container.getSectorById(localSpotIdOrSectorIdOrSensorId);
			price = container.calcPrice(sector, timeLength);			
		}
		
		// 1. save a reservation record
		Reservation newReservation = null;
		
		if(type == ReservationType.LocalSpotId) {
			newReservation =
					Reservation.saveNewReservation(carId, localSpotIdOrSectorIdOrSensorId, 
							true, startTime, timeLength);
		}else if(type == ReservationType.SectorId) {
			newReservation =
					Reservation.saveNewReservation(carId, localSpotIdOrSectorIdOrSensorId, 
							false, startTime, timeLength);
		}

		if(newReservation == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		}
		
		// 2. pay with wallet
		JSONObject result = new JSONObject();
		result.put("transaction", customer.pay(price, newReservation.id));
		Wallet wallet = Wallet.fetchWallet(customer); 
		if(wallet.balance < price) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_BALANCE_NOT_ENOUGH);
		}
		wallet.balance -= price;
		if(! wallet.save()) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		}
		result.put("reservation", newReservation.toJSON());
		// 3. return reservation results;
		return result;
		
	}
	
	public JSONObject reserve(User customer, City city,
			int sensorId, String rfId, Time startTime, int timeLength) {
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
		}
		// 0. calculate price
		Spot spot = Spot.fetchSpotBySensorId(container, SensorId.toSensorId(sensorId));
		if(spot == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.SPOT_NOT_FOUND);
		}
		int price = container.calcPrice(spot.sector, timeLength);
		
		// 1. save a reservation record
		Reservation newReservation =
				Reservation.saveNewReservation(sensorId, 
						false, startTime, timeLength);

		if(newReservation == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		}
		
		// 2. pay with wallet
		JSONObject result = new JSONObject();
		Calendar currenttime = Calendar.getInstance();
	    Date now = new Date((currenttime.getTime()).getTime());
	    Time nowTime = new Time(currenttime.getTime().getTime());
		Transaction transaction = Transaction.saveNewRFCardTransaction(rfId, newReservation.id, 
				now, nowTime, "", price);
		if(transaction == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		}
		result.put("transaction", transaction.toJSON());
		result.put("reservation", newReservation.toJSON());
		// 3. return reservation results;
		return result;
		
	}
	
	public JSONObject reserve(User customer, City city,
			int carId, Time startTime, int timeLength) {
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
		}
		// 0. calculate price
		int price = container.calcPrice(null, timeLength);
		
		// 1. save a reservation record
		Reservation newReservation = 
				Reservation.saveNewReservation(carId, true, startTime, timeLength);

		if(newReservation == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		}
		
		// 2. pay with wallet
		JSONObject result = new JSONObject();
		result.put("transaction", customer.pay(price, newReservation.id));
		Wallet wallet = Wallet.fetchWallet(customer); 
		if(wallet.balance < price) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_BALANCE_NOT_ENOUGH);
		}
		wallet.balance -= price;
		if(! wallet.save()) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		}
		result.put("reservation", newReservation.toJSON());
		// 3. return reservation results;
		return result;
	}

	

	public JSONObject calculatePrice(City city, int sectorId, int time) {
		
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
		}
		return container.calculatePrice(sectorId, time);
	}
	
	public JSONObject getInfo(City city, int sectorId) {
		
		JSONObject result = new JSONObject();
		
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
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
			return result;
		}
		return container.searchByRange(topLeft, bottomRight);
	}
	
	public boolean checkSpot() {
		return true;
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
	
	public JSONObject updateSensors(int[] sensorIds, boolean[] fullFlags, 
			Time[] lastTimeChanged, Time[] lastTimeUpdated) {
		JSONObject result = new JSONObject();
		JSONArray updatedSensors = new JSONArray();
		
		for(int i = 0 ; i < sensorIds.length; i++) {
			int sensorIdInt = sensorIds[i];
			boolean fullFlag = fullFlags[i];
			Time t1 = lastTimeChanged[i];
			Time t2 = lastTimeUpdated[i];
			
			Sensor sensor = Sensor.fetchSensorById(sensorIdInt);
			if(sensor == null) {
				sensor = Sensor.insertSensor(sensorIdInt, fullFlag, t1, t2);
				if(sensor == null) {
					return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
				}
			}else {
				sensor.update(fullFlag, t1, t2);
				Sensor.updateSensor(sensorIdInt, sensor);
			}
			updatedSensors.add(sensorIdInt);
		}
		result.put("updated_sensors", updatedSensors);
		return result;
	}
	
	public JSONObject readSensor(City city, int sensorId) {
		ParkingSpotContainer container = citySpots.get(city);
		if(container == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
		}
		return container.readSensor(sensorId);
	}
	
	public JSONObject readSensor(int sensorId) {
		Sensor sensor = Sensor.fetchSensorById(sensorId);
		if(sensor == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.SENSOR_ID_INVALID);
		}
		JSONObject sensorObj = sensor.toJSON();
		sensorObj.put("id", sensorId);
		return sensorObj;
	}
	
	

}
