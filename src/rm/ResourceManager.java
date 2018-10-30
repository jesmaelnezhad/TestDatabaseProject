package rm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import jdk.internal.util.xml.impl.Pair;
import request_handlers.ResponseConstants.ResponseCode;
import request_handlers.ResponseHelper;
import rm.PriceRate.PriceRating;
import rm.Reservation.ReservationType;
import rm.basestations.Sensor;
import rm.basestations.SensorId;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpotContainer;
import rm.parking_structure.Sector;
import rm.parking_structure.Segment;
import sun.util.locale.StringTokenIterator;
import tm.Transaction;
import tm.TransactionManager;
import tm.Wallet;
import um.User;
import utility.Constants;
import utility.DBManager;
import utility.Locker;
import utility.Logger;
import utility.Permit;
import utility.Point;

public class ResourceManager implements Locker{

	// A mapping from cities to the container of their parking spots
	private Map<City, ParkingSpotContainer> citySpots = new HashMap<City, ParkingSpotContainer>();
	private ReentrantReadWriteLock citySpotsMapLock = new ReentrantReadWriteLock();

	
	private ResourceManager() {}
	
	// returns true only if the file is actually parsed and loaded.
	public boolean loadFromFile(String filePath) {
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
				
				// 3. Make a .idx file to know this file is loaded.
				if(! indexFile.createNewFile()) {
					Logger.getLogger().log("Index file cannot be stored on disk.");
				}
				
			} catch (FileNotFoundException e) {
				Logger.getLogger().log("Cannot open the data file " + filePath);
				return false;
			} catch (ParseException e) {
				Logger.getLogger().log("Cannot parse the data from the file " + filePath);
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			Sector newSector = new Sector();
			
			if(sectorObject == null) {
				Logger.getLogger().log("Sector info not readable. Sector number " + sec + " in city " + cityName);
				continue;
			}
			newSector.id = Integer.parseInt((String)sectorObject.get("id"))	;
			String locationStr = (String) sectorObject.get("center_location");
			StringTokenizer st = new StringTokenizer(locationStr, ",");
			double sectorX = Double.parseDouble(st.nextToken());
			double sectorY = Double.parseDouble(st.nextToken());
			newSector.sectorLocation = new Point(sectorX, sectorY);
			
			JSONArray segmentsArray = (JSONArray) sectorObject.get("segments");
			JSONArray spotsArray = (JSONArray) sectorObject.get("spots");
			JSONArray priceRatesArray = (JSONArray) sectorObject.get("price_rates");
			JSONObject workingHoursObject = (JSONObject) sectorObject.get("working_hours");
			if(newSector.id == null || locationStr == null || 
					segmentsArray == null || spotsArray == null || 
					priceRatesArray == null || workingHoursObject == null) {
				Logger.getLogger().log("Sector info incomplete. Sector number " + sec + " in city " + cityName);
				continue;
			}
			newSector.parkCapacity = 0;
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
				
				st = new StringTokenizer(startLocStr, ",");
				double segmentX = Double.parseDouble(st.nextToken());
				double segmentY = Double.parseDouble(st.nextToken());
				Point segStart = new Point(segmentX, segmentY);
				st = new StringTokenizer(endLocStr, ",");
				segmentX = Double.parseDouble(st.nextToken());
				segmentY = Double.parseDouble(st.nextToken());
				Point segEnd = new Point(segmentX, segmentY);
				
				Segment segment = new Segment(segmentId, newSector.id, capacity, segStart, segEnd);
				newSector.parkCapacity  += capacity;
				newSector.getSegments().add(segment);
				segment.saveInDB();
			}
			newSector.setAvailablePark(newSector.parkCapacity);
			
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
				
				// For each spot, we should first prepare the sensor object.
				SensorId sensorId = new SensorId(id, pid, bid);
				Sensor sensor = Sensor.fetchOrInsertSensor(city.id, sensorId.toInt());

				Spot newSpot = new Spot(newSector.id, (int)localSpotId, sensorId);
				newSpot.save();
			}
			
			List<PriceRate> priceRates = new ArrayList<>();
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
				PriceRate priceRate = new PriceRate(from, to, price);
				priceRates.add(priceRate);
			}
			PriceRating priceRating = PriceRate.savePriceRates(priceRates);
			if(priceRating == null) {
				continue;
			}
			newSector.setPriceRates(priceRating.id, priceRates);
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String workingHourStartStr = (String) workingHoursObject.get("start");
			long startTimeMS;
			try {
				startTimeMS = sdf.parse(workingHourStartStr).getTime();
			} catch (java.text.ParseException e) {
				startTimeMS = 0;
			}
			Time startTime = new Time(startTimeMS);
			String workingHourEndStr = (String) workingHoursObject.get("end");
			long endTimeMS;
			try {
				endTimeMS = sdf.parse(workingHourEndStr).getTime();
			} catch (java.text.ParseException e) {
				endTimeMS = 0;
			}
			Time endTime = new Time(endTimeMS);
			if(workingHourStartStr == null || workingHourEndStr == null) {
				Logger.getLogger().log("Working hour info incomplete. Sector " + sec + " in city " + cityName);
				continue;
			}
			WorkingHour workingHour = WorkingHour.saveWorkingHour(startTime, endTime);
			newSector.setWorkingHour(workingHour);
			
			newSector.city = city;
			// save the sector in DB;
			newSector.save();
		}
		return false;
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
		
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getWritePermit();
			
			for(City city : citySpots.keySet()) {
				if(city.id == cityId) {
					return city;
				}
			}
			City newCity = City.fetchCityById(cityId);
			return loadCityNoLock(newCity);
			
		}finally {
			citySpotsPermit.unlock();
		}

	}
	
	public City loadCity(City city_) {
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getWritePermit();
			return loadCityNoLock(city_);

		}finally {
			citySpotsPermit.unlock();
		}
	}

	private City loadCityNoLock(City city_) {
		for(City city : citySpots.keySet()) {
			if(city.id == city_.id) {
				return city;
			}
		}
		
		// create new data container for this new city
		// 1. Fetch all sectors of a city from the database and create the index
		Map<Integer, Sector> sectorIndex = Sector.fetchByCityId(city_.id);
		// 2. Create the container object
		ParkingSpotContainer container = new ParkingSpotContainer(sectorIndex);
		citySpots.put(city_, container);
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
		
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getReadPermit();

			ParkingSpotContainer container = citySpots.get(city);
			if(container == null) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
			}
			// 0. calculate price
			int price = 0;
			if(type == ReservationType.LocalSpotId) {
				
				Spot spot = Spot.fetchSpotByLocalSpotId(localSpotIdOrSectorIdOrSensorId);
				if(spot == null) {
					return ResponseHelper.respondWithMessage(false, ResponseCode.SPOT_NOT_FOUND);
				}
				
				price = container.calcPrice(container.getSectorById(spot.sectorId), timeLength);
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

		}finally {
			citySpotsPermit.unlock();
		}
	}
	
	public JSONObject reserve(User customer, City city,
			int sensorId, String rfId, Time startTime, int timeLength) {
		
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getReadPermit();
			
			ParkingSpotContainer container = citySpots.get(city);
			if(container == null) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
			}
			// 0. calculate price
			Spot spot = Spot.fetchSpotBySensorId(SensorId.toSensorId(sensorId));
			if(spot == null) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.SPOT_NOT_FOUND);
			}
			int price = container.calcPrice(container.getSectorById(spot.sectorId), timeLength);
			
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
		}finally {
			citySpotsPermit.unlock();
		}
		
	}
	
	public JSONObject reserve(User customer, City city,
			int carId, Time startTime, int timeLength) {
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getReadPermit();
			
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

		}finally {
			citySpotsPermit.unlock();
		}

	}

	

	public JSONObject calculatePrice(City city, int sectorId, int time) {
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getReadPermit();
			ParkingSpotContainer container = citySpots.get(city);
			if(container == null) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
			}
			return container.calculatePrice(sectorId, time);
		}finally {
			citySpotsPermit.unlock();
		}
	}
	
	public JSONObject getInfo(City city, int sectorId) {
		
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getReadPermit();
			
			JSONObject result = new JSONObject();
			
			ParkingSpotContainer container = citySpots.get(city);
			if(container == null) {
				return result;
			}
			return container.getInfo(sectorId);
		}finally {
			citySpotsPermit.unlock();
		}
		
	}
	// Search for sectors
	// Returns a JSONArray which is an array of JSONObjects which are sectors
	public JSONArray searchByRange(City city, Point topLeft, Point bottomRight){
		
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getReadPermit();
			
			JSONArray result = new JSONArray();
			
			ParkingSpotContainer container = citySpots.get(city);
			if(container == null) {
				return result;
			}
			return container.searchByRange(topLeft, bottomRight);

		}finally {
			citySpotsPermit.unlock();
		}

	}
	
	public boolean checkSpot() {
		return true;
	}
	
	
	public JSONObject updateSensors(City city, int[] sensorIds, boolean[] fullFlags, 
			Time[] lastTimeChanged, Time[] lastTimeUpdated) {
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getReadPermit();
			
			ParkingSpotContainer container = citySpots.get(city);
			if(container == null) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
			}
			return container.updateSensors(city.id, sensorIds, fullFlags, lastTimeChanged, lastTimeUpdated);
		}finally {
			citySpotsPermit.unlock();
		}
	}
	
	public JSONObject readSensor(City city, int sensorId) {
		Permit citySpotsPermit = null;
		try {
			citySpotsPermit = this.getReadPermit();
			
			ParkingSpotContainer container = citySpots.get(city);
			if(container == null) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND);
			}
			return container.readSensor(sensorId);
		}finally {
			citySpotsPermit.unlock();
		}
	}
	
//	public JSONObject readSensor(int sensorId) {
//		Sensor sensor = Sensor.fetchSensorById(sensorId);
//		if(sensor == null) {
//			return ResponseHelper.respondWithMessage(false, ResponseCode.SENSOR_ID_INVALID);
//		}
//		JSONObject sensorObj = sensor.toJSON();
//		sensorObj.put("id", sensorId);
//		return sensorObj;
//	}

	@Override
	public Permit getReadPermit() {
		return new Permit(citySpotsMapLock.readLock());
	}
	
	@Override
	public Permit getWritePermit() {
		return new Permit(citySpotsMapLock.writeLock());
	}
	
	

}
