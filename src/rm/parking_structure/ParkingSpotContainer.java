/**
 * 
 */
package rm.parking_structure;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import request_handlers.ResponseHelper;
import request_handlers.ResponseConstants.ResponseCode;
import rm.PriceRate;
import rm.SensorInfoMaterializer;
import rm.WorkingHour;
import rm.basestations.Sensor;
import rm.basestations.SensorId;
import utility.Constants;
import utility.KDTree;
import utility.ParkingSpotStatus;
import utility.Point;

/**
 * @author jam
 *
 */
public class ParkingSpotContainer {
	// sector id to sector location
	public KDTree<Sector> sectorsKDTree = new KDTree<>(2);
	// TODO: these maps may need a lock themselves
	public Map<Integer, Sector> sectorIndex = new HashMap<>();
	
	public Map<SensorId, Sensor> citySensors = new HashMap<>();
	
	private SensorInfoMaterializer materializer = null;
	
	public ParkingSpotContainer() {
		this.materializer = new SensorInfoMaterializer(this);
		this.materializer.start();
	}
	
	public ParkingSpot getSpotById(int spotId) {
		// TODO
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
			// lock S on sector
			ReadLock sectorLock = sector.lock.readLock();
			///////// sector is safe for read ////////
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
			/////////
			// unlock sector
			sectorLock.unlock();
		}
		
		return result;
	}
	
	// Get the information of a sector, segment, and|or a spot
	public JSONObject getInfo(int sectorId) {
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_INVALID);
		}

		// lock S on sector
		ReadLock sectorLock = sector.lock.readLock();
		///////// sector is safe for read ////////
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
		/////////
		// unlock sector
		sectorLock.unlock();
		
		return null;
	}

	// Get the price of parking 'time' minutes in the given sector
	public JSONObject calculatePrice(int sectorId, int time) {

		JSONObject status = new JSONObject();
		
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			// there is something wrong. sectorId isn't valid.
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_INVALID);
		}

		// lock S on sector
		ReadLock sectorLock = sector.lock.readLock();
		///////// sector is safe for read ////////
		boolean emptySpotExists = sector.availablePark < sector.parkCapacity;
		if(! emptySpotExists) {
			sectorLock.unlock();
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_CAPACITY_FULL);
		}
		// read sector info
		JSONObject sectorJSONObj = sector.getMinimumJSONObject();
		// read the working hour and check if any price applies to the current time
		WorkingHour wh = sector.getWorkingHour();
		
		if(wh.isWorkingNow()) {
			// read price rates
			List<PriceRate> priceRates = sector.getPriceRates();
			
			PriceRate selectedRate = priceRates.get(priceRates.size()-1);
			for(PriceRate pr : priceRates) {
				if(pr.getFromInMinutes() <= time && pr.getToInMinutes() > time) {
					selectedRate = pr;
					break;
				}
			}
			int calculatedPrice = selectedRate.getPrice() * (time / 30);
			
			sectorJSONObj.put("price", calculatedPrice);
		}else {
			sectorJSONObj.put("price", 0);
		}
		/////////
		// unlock sector
		sectorLock.unlock();
		
		return sectorJSONObj;
	}

	// Get the information of a sector, segment, and|or a spot
	public JSONObject rentSpot(int sectorId) {
		
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			// there is something wrong. sectorId isn't valid.
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_INVALID);
		}

		JSONObject transactionJSONObj = new JSONObject();

		// lock S on sector
		WriteLock sectorLock = sector.lock.writeLock();
		///////// sector is safe for read ////////
		// read sector info
		transactionJSONObj.put(Constants.SECTOR_ID, sector.id);
		sector.availablePark --;
		
		transactionJSONObj = ResponseHelper.respondWithStatus(transactionJSONObj, true);

		// unlock sector
		sectorLock.unlock();
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

		// lock S on sector
		WriteLock sectorLock = sector.lock.writeLock();
		///////// sector is safe for read ////////
		// read sector info
		transactionJSONObj.put(Constants.SECTOR_ID, sector.id);

		sector.availablePark ++;
		
		transactionJSONObj = ResponseHelper.respondWithStatus(transactionJSONObj, true);

		// unlock sector
		sectorLock.unlock();
		return transactionJSONObj;
	}
	
	// load the parking spots of a city into memory
	public static ParkingSpotContainer loadCity(City city) {
		
		// TODO
		return null;
	}
	
	public JSONObject updateSensors(int[] sensorIds, boolean[] fullFlags, 
			Time[] lastTimeChanged, Time[] lastTimeUpdated) {
		
		JSONObject result = new JSONObject();
		for(int i = 0 ; i < sensorIds.length; i++) {
			int sensorIdInt = sensorIds[i];
			boolean fullFlag = fullFlags[i];
			Time t1 = lastTimeChanged[i];
			Time t2 = lastTimeUpdated[i];
			SensorId id = SensorId.toSensorId(sensorIdInt);
			Sensor s = citySensors.get(id);
			if(s == null) {
				if(! result.containsKey("status")) {
					result.put("status", "unsuccessful");
				}
				result.put("sensor_not_found", sensorIdInt);
				continue;
			}
			s.update(fullFlag, t1, t2);
			result.put("sensor_updated", sensorIdInt);
		}
		return result;
	}
	
	public JSONObject readSensor(int sensorId) {
		JSONObject result = new JSONObject();
		SensorId id = SensorId.toSensorId(sensorId);
		Sensor s = citySensors.get(id);
		if(s == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.SENSOR_ID_INVALID);
		}
		return s.read().toJSON();
	}
	
	public Sensor getSensorById(int sensorId) {
		SensorId id = SensorId.toSensorId(sensorId);
		Sensor s = citySensors.get(id);
		if(s == null) {
			return null;
		}
		return s;		
	}
}
