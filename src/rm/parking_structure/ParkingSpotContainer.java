/**
 * 
 */
package rm.parking_structure;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import request_handlers.ResponseHelper;
import request_handlers.ResponseConstants.ResponseCode;
import rm.PriceRate;
import rm.SensorInfoMaterializer;
import rm.SpotPhoto;
import rm.WorkingHour;
import rm.basestations.Sensor;
import rm.basestations.SensorId;
import utility.Constants;
import utility.KDTree;
import utility.Locker;
import utility.ParkingSpotStatus;
import utility.Permit;
import utility.Point;

/**
 * @author jam
 *
 */
public class ParkingSpotContainer implements Locker{
	
	// This KDTree and the map of sectors are created in the time of system boot
	// and there won't be any add/remove to the structures after that.
	// Therefore, the structures don't need locks but each Sector itself has a lock.
	public KDTree<Sector> sectorsKDTree = new KDTree<>(2);
	public Map<Integer, Sector> sectorIndex = new HashMap<>();
	
	public Map<Integer, SpotPhoto> pastPhotos = new HashMap<>();
	
	// This structure may change while it's being used. It may be changed 
	// and materialized simultaneously. 
	public Map<SensorId, Sensor> citySensors = new HashMap<>();
	private ReentrantReadWriteLock citySensortsMapMutex = new ReentrantReadWriteLock();
	
	private SensorInfoMaterializer materializer = null;
	
	
	public ParkingSpotContainer(Map<Integer, Sector> sectorIndex) {
		this.materializer = new SensorInfoMaterializer(this);
		this.sectorIndex = sectorIndex;
		for(Integer i : sectorIndex.keySet()) {
			Sector sec = sectorIndex.get(i);
			double [] loc = new double[2];
			loc[0] = sec.sectorLocation.x;
			loc[0] = sec.sectorLocation.y;
			sectorsKDTree.add(loc, sec);
		}
		System.out.println("Number of sectors in this container : " + this.sectorIndex.size());
		
		this.materializer.start();
	}
	
	public Sector getSectorById(int sectorId) {
		if(sectorIndex.containsKey(sectorId)) {
			return sectorIndex.get(sectorId);
		}
		return null;
	}
	
	// returns the list of all parking spots 
	public JSONArray searchByRange(Point topLeft, Point bottomRight){
		
		JSONArray result = new JSONArray();
		
		double [] lows = new double[2];
		double [] highs = new double[2];
		
		lows[0] = (double)topLeft.x;
		lows[1] = (double)topLeft.y;
		
		highs[0] = (double)bottomRight.x;
		highs[1] = (double)bottomRight.y;
		
		List<Sector> inRangeSectors = sectorsKDTree.getRange(lows, highs);
		for(Sector sector : inRangeSectors) {
			// read sector info
			JSONObject sectorJSONObj = sector.getMinimumJSONObject();
			// read segments' info
			List<Segment> segments = sector.getSegments();
			JSONArray sectorSegments = new JSONArray();
			for(Segment segment : segments) {
				JSONObject segmentJSONObj = segment.getMinimumJSONObject();
				sectorSegments.add(segmentJSONObj);
			}
			sectorJSONObj.put(Constants.SEGMENTS, sectorSegments);
			result.add(sectorJSONObj);
		}
		
		return result;
	}
	
	// Get the information of a sector, segment, and|or a spot
	public JSONObject getInfo(int sectorId) {
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_INVALID);
		}

		// read sector info
		JSONObject sectorJSONObj = sector.getMinimumJSONObject();
		// read price rates
		JSONArray priceRatesJSONArray = new JSONArray();
		List<PriceRate> priceRates = sector.getPriceRates();
		for(PriceRate pr: priceRates) {
			priceRatesJSONArray.add(pr.getJSONObject());
		}
		sectorJSONObj.put(Constants.PRICE_RATES, priceRatesJSONArray);
		
		WorkingHour wh = sector.getWorkingHour();
		sectorJSONObj.put(Constants.WORKING_HOUR, wh.getJSON());
		
		List<Segment> segments = sector.getSegments();
		JSONArray segmentsJSONArray = new JSONArray();
		for(Segment segment : segments) {
			segmentsJSONArray.add(segment.getMinimumJSONObject());
		}
		sectorJSONObj.put(Constants.SEGMENTS, segmentsJSONArray);
		
		return null;
	}

	// Get the price of parking 'time' minutes in the given sector
	public JSONObject calculatePrice(int sectorId, int time) {

		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			// there is something wrong. sectorId isn't valid.
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_INVALID);
		}


		boolean emptySpotExists = sector.getAvailablePark() < sector.parkCapacity;
		if(! emptySpotExists) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_CAPACITY_FULL);
		}
		// read sector info
		JSONObject sectorJSONObj = sector.getMinimumJSONObject();
		// read the working hour and check if any price applies to the current time
		int price = calcPrice(sector, time);
		sectorJSONObj.put("price", price);
		
		return sectorJSONObj;
	}

	public int calcPrice(Sector sector, int timeLength) {
		if(sector == null) {
			// TODO use highest price rate
			return 0;
		}
		
		WorkingHour wh = sector.getWorkingHour();
		
		if(wh.isWorkingNow()) {
			int timeLeft = wh.getTimeLengthLeftWorking();
			if(timeLeft < timeLength) {
				timeLength = timeLeft;
			}
			// read price rates
			List<PriceRate> priceRates = sector.getPriceRates();
			
			PriceRate selectedRate = priceRates.get(priceRates.size()-1);
			for(PriceRate pr : priceRates) {
				if(pr.getFromInMinutes() <= timeLength && pr.getToInMinutes() > timeLength) {
					selectedRate = pr;
					break;
				}
			}
			int calculatedPrice = selectedRate.getPrice() * (timeLength / 30);
			
			return calculatedPrice;
		}else {
			return 0;
		}
	}
	// Get the information of a sector, segment, and|or a spot
	public JSONObject rentSpot(int sectorId) {
		
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			// there is something wrong. sectorId isn't valid.
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_INVALID);
		}

		JSONObject transactionJSONObj = new JSONObject();

		// read sector info
		transactionJSONObj.put(Constants.SECTOR_ID, sector.id);
		sector.setAvailablePark(sector.getAvailablePark() - 1);
		
		transactionJSONObj = ResponseHelper.respondWithStatus(transactionJSONObj, true);
		return transactionJSONObj;
	}
	
	// returns null if sectorId is not found
	public List<PriceRate> getSectorPriceRates(int sectorId){
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) {
			// sector not found
			return null;
		}
		return sector.getPriceRates();
	}
	
	// Get the information of a sector, segment, and|or a spot
	public JSONObject freeSpot(int sectorId) {

		JSONObject transactionJSONObj = new JSONObject();
		
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			// there is something wrong. sectorId isn't valid.
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_INVALID);
		}


		// read sector info
		transactionJSONObj.put(Constants.SECTOR_ID, sector.id);

		sector.setAvailablePark(sector.getAvailablePark() + 1);
		
		transactionJSONObj = ResponseHelper.respondWithStatus(transactionJSONObj, true);

		return transactionJSONObj;
	}
	
	public JSONObject updateSensors(int cityId, int[] sensorIds, boolean[] fullFlags, 
			Time[] lastTimeChanged, Time[] lastTimeUpdated) {
		JSONObject result = new JSONObject();
		JSONArray updatedSensors = new JSONArray();
		
		for(int i = 0 ; i < sensorIds.length; i++) {
			int sensorIdInt = sensorIds[i];
			boolean fullFlag = fullFlags[i];
			Time t1 = lastTimeChanged[i];
			Time t2 = lastTimeUpdated[i];
			
			// first try to get the existing sensor object
			Sensor sensor = this.getSensorById(sensorIdInt);
			if(sensor == null) {
				// doesn't exist. Insert it in DB and in the map.
				sensor = Sensor.insertSensor(sensorIdInt, fullFlag, cityId, t1, t2);
				if(sensor == null) {
					return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
				}
				Permit citySensorsWritePermit = null;
		        try{
		        	citySensorsWritePermit = getWritePermit();
		        	citySensors.put(SensorId.toSensorId(sensorIdInt), sensor);
				}finally {
					citySensorsWritePermit.unlock();
				}

			}else {
				sensor.update(fullFlag, t1, t2);
			}
			updatedSensors.add(sensorIdInt);
		}
		result.put("updated_sensors", updatedSensors);
		return result;
	}
	
	public JSONObject readSensor(int sensorId) {
		Sensor s = this.getSensorById(sensorId);
		if(s == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.SENSOR_ID_INVALID);
		}
		return s.read().toJSON();
	}
	
	public Sensor getSensorById(int sensorId) {
		SensorId id = SensorId.toSensorId(sensorId);
		// 1. Read lock on citySensors map
		Permit citySensorsReadPermit = null;
        try{
        	citySensorsReadPermit = getReadPermit();
    		Sensor s = citySensors.get(id);
    		if(s != null) {
    			return s;
    		}
    		// if it doesn't exist in the map, try to load it from the database.
    		Sensor sensor = Sensor.fetchSensorById(sensorId);
    		return sensor;
        }finally {
        	citySensorsReadPermit.unlock();
        }
	}

	@Override
	public Permit getReadPermit() {
		return new Permit(citySensortsMapMutex.readLock());
	}

	@Override
	public Permit getWritePermit() {
		return new Permit(citySensortsMapMutex.writeLock());
	}
}
