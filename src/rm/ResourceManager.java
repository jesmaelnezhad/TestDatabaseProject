package rm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
		// TODO: We must check for the existence of file filePath+'.idx'
		//          if this file doesn't exist, 
		// 									we must parse and load the file
		// 									and save a file filePath+'.idx'
		
		
		// 1. look for the index file
		File indexFile = new File(filePath + ".idx");
		if(! indexFile.exists()) {
			try {
				// 2. parse and load the content of this file into the database
				Scanner dataFileScanner = new Scanner(new FileInputStream(filePath));
				String dataContent = "";
				while(dataFileScanner.hasNext()) {
					dataContent += dataFileScanner.nextLine();
				}
				dataFileScanner.close();
				JSONArray cityObjects = (JSONArray) new JSONParser().parse(dataContent);
				for(int c = 0 ; c < cityObjects.size(); c++) {
					JSONObject cityObject = (JSONObject) cityObjects.get(c);
					boolean result = parseLoadCityJSONObject(cityObject);
					if(! result) {
						String cityName = (String) cityObject.get("city");
						Logger.getLogger().log("Cannot load the city " + cityName + " from file " + filePath);
					}
				}
				
			} catch (FileNotFoundException e) {
				Logger.getLogger().log("Cannot open the data file " + filePath);
				return false;
			} catch (ParseException e) {
				Logger.getLogger().log("Cannot parse the data from the file " + filePath);
				return false;
			}
		}//else{
			// this file is parsed and loaded before.
		//}
		
		return false;
	}
	
	public boolean parseLoadCityJSONObject(JSONObject cityObject) {
		String cityName = (String) cityObject.get("city");
		if(cityName == null) {
			return false;
		}
		// 1. Find the city object from the database, and if it doesn't exist insert a new one
		City city = City.insertNewCity(cityName);
		
		// 2. Iterate over the sector objects and add them to the database or update the existing
		//    record in the database.
		JSONArray sectorsArray = (JSONArray) cityObject.get("sectors");
		if(sectorsArray == null) {
			return false;
		}
		for(int sec = 0 ; sec < sectorsArray.size(); sec++) {
			JSONObject sectorObject = (JSONObject) sectorsArray.get(sec);
			if(sectorObject == null) {
				Logger.getLogger().log("Sector info not readable. Sector number " + sec + " in city " + cityName);
				continue;
			}
			Integer sectorId = Integer.parseInt((String)sectorObject.get("id"))	;
			String locationStr = (String) sectorObject.get("center_location");
			JSONArray segmentsArray = (JSONArray) sectorObject.get("segments");
			JSONArray spotsArray = (JSONArray) sectorObject.get("spots");
			JSONArray priceRatesArray = (JSONArray) sectorObject.get("price_rates");
			JSONObject workingHoursObject = (JSONObject) sectorObject.get("working_hours");
			if(sectorId == null || locationStr == null || 
					segmentsArray == null || spotsArray == null || 
					priceRatesArray == null || workingHoursObject == null) {
				Logger.getLogger().log("Sector info incomplete. Sector number " + sec + " in city " + cityName);
				continue;
			}
			
			// TODO : iterating on all segments
			for(int seg = 0 ; seg < segmentsArray.size(); seg++) {
				JSONObject segmentObject = (JSONObject) segmentsArray.get(seg);
				if(segmentObject == null) {
					Logger.getLogger().log("Segment info not readable. Segment number " + 
												seg + " in sector " + sec + " in city " + cityName);
					continue;
				}
				Integer segmentId = Integer.parseInt((String)segmentObject.get("id"));
				String startLocStr = (String) segmentObject.get("start_location");
				String endLocStr = (String) segmentObject.get("end_location");
				Integer capacity = Integer.parseInt((String)segmentObject.get("capacity"));
				if(segmentId == null || startLocStr == null || endLocStr == null || capacity == null) {
					Logger.getLogger().log("Segment info incomplete. Segment number " + 
							seg + " in sector " + sec + " in city " + cityName);
					continue;					
				}
				
				// TODO : use segment info here ....
			}
			
			// TODO : iterating on all spots
			for(int sp = 0 ; sp < spotsArray.size(); sp++) {
				JSONObject spotObject = (JSONObject) spotsArray.get(sp);
				if(spotObject == null) {
					Logger.getLogger().log("Spot info not readable. Spot number " + 
												sp + " in sector " + sec + " in city " + cityName);
					continue;
				}
				Integer id = Integer.parseInt((String)spotObject.get("id"));
				Integer pid = Integer.parseInt((String)spotObject.get("pid"));
				Integer bid = Integer.parseInt((String)spotObject.get("bid"));
				Integer localSpotId = Integer.parseInt((String)spotObject.get("local_spot_id"));
				if(id == null || pid == null || bid == null || localSpotId == null) {
					Logger.getLogger().log("Spot info incomplete. Spot number " + 
							sp + " in sector " + sec + " in city " + cityName);
					continue;
				}
				
				// TODO : use spot info here ....
				
			}
			
			// TODO : iterating on all price rates
			for(int pr = 0 ; pr < priceRatesArray.size(); pr++) {
				JSONObject priceRateObject = (JSONObject) priceRatesArray.get(pr);
				if(priceRateObject == null) {
					Logger.getLogger().log("Price rate info not readable. Price rate number " + 
												pr + " in sector " + sec + " in city " + cityName);
					continue;
				}
				Integer from = Integer.parseInt((String)priceRateObject.get("from"));
				Integer to = Integer.parseInt((String)priceRateObject.get("to"));
				Integer price = Integer.parseInt((String)priceRateObject.get("price"));
				if(from == null || to == null || price == null) {
					Logger.getLogger().log("Price rate info incomplete. Price rate number " + 
							pr + " in sector " + sec + " in city " + cityName);
					continue;
				}
				
				// TODO : use price rate info here ....
			}
			
			// TODO: use working hour info
			String workingHourStartStr = (String) workingHoursObject.get("start");
			String workingHourEndStr = (String) workingHoursObject.get("end");
			if(workingHourStartStr == null || workingHourEndStr == null) {
				Logger.getLogger().log("Working hour info incomplete. Sector " + sec + " in city " + cityName);
				continue;
			}
			
		}
		
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
