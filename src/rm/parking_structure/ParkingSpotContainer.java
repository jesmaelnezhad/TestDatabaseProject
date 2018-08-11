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
import rm.basestations.Sensor;
import rm.basestations.SensorId;
import utility.Constants;
import utility.ParkingSpotStatus;
import utility.Point;

/**
 * @author jam
 *
 */
public class ParkingSpotContainer {
	// sector id to sector location
	public Map<Integer, Point> sectorLocationIndex = new HashMap<>();
	// TODO: these maps may need a lock themselves
	public Map<Integer, Sector> sectorIndex = new HashMap<>();
	
	public Map<SensorId, Sensor> citySensors = new HashMap<>();
	
	public ParkingSpot getSpotById(int spotId) {
		// TODO
		return null;
	}
	
	// returns the list of all parking spots 
	public JSONArray searchByProximity(Point center, double radius){
		
		JSONArray result = new JSONArray();
		
		
		for(Map.Entry<Integer, Point> sectorEntry : sectorLocationIndex.entrySet()) {
			if(Point.distance(sectorEntry.getValue(), center) <= radius) {
				Sector sector = sectorIndex.get(sectorEntry.getKey());
				// lock S on sector
				ReadLock sectorLock = sector.lock.readLock();
				///////// sector is safe for read ////////
				// read sector info
				JSONObject sectorJSONObj = sector.getMinimumJSONObject();
				// read segments' info
				List<Segment> segments = sector.getSegments();
				JSONArray sectorSegments = new JSONArray();
				for(Segment segment : segments) {
					ReadLock segmentLock = segment.lock.readLock();
					///////// segment is safe for read ////////
					JSONObject segmentJSONObj = segment.getMinimumJSONObject();
					sectorSegments.add(segmentJSONObj);
					/////////
					// unlock segment
					segmentLock.unlock();
				}
				sectorJSONObj.put(Constants.SEGMENTS, sectorSegments);
				result.add(sectorJSONObj);
				/////////
				// unlock sector
				sectorLock.unlock();
			}
		}
		return result;
	}
	
	// Get the information of a sector, segment, and|or a spot
	public JSONObject getInfo(int sectorId, int segmentId, int spotId) {
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
		// read segment info
		if(segmentId == -1) {
			/////////
			// unlock sector
			sectorLock.unlock();
			sectorJSONObj = ResponseHelper.respondWithMessage(sectorJSONObj, false, ResponseCode.SEGMENT_ID_INVALID);
			return sectorJSONObj;
		}
		// read segments' info
		Segment segment = sector.getSegment(segmentId);
		ReadLock segmentLock = segment.lock.readLock();
		///////// segment is safe for read ////////
		JSONObject segmentJSONObj = segment.getMinimumJSONObject();
		sectorJSONObj.put(Constants.SEGMENT, segmentJSONObj);
		// read spot info
		if(spotId == -1) {
			/////////
			// unlock segment
			segmentLock.unlock();
			// unlock sector
			sectorLock.unlock();
			sectorJSONObj = ResponseHelper.respondWithMessage(sectorJSONObj, false, ResponseCode.SPOT_ID_INVALID);
			return sectorJSONObj;
		}
		// read segments' info
		ParkingSpot spot = segment.getSpot(spotId);
		ReadLock spotLock = spot.lock.readLock();
		///////// segment is safe for read ////////
		JSONObject spotJSONObj = spot.getMinimumJSONObject();
		sectorJSONObj.put(Constants.SPOT, spotJSONObj);
		/////////
		// unlock segment
		spotLock.unlock();
		/////////
		// unlock segment
		segmentLock.unlock();
		/////////
		// unlock sector
		sectorLock.unlock();
		
		return null;
	}

	// Get the information of a sector, segment, and|or a spot
	public JSONObject calculatePrice(int sectorId, int segmentId, int priceRateId, int time) {

		JSONObject status = new JSONObject();
		
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			// TODO: there is something wrong. sectorId isn't valid.
			status.put("status", "unsuccessful");
			status.put("message", "sector id invalid.");
			return status;
		}

		// lock S on sector
		ReadLock sectorLock = sector.lock.readLock();
		///////// sector is safe for read ////////
		// read sector info
		JSONObject sectorJSONObj = sector.getMinimumJSONObject();
		// read price rates
		List<PriceRate> priceRates = sector.getPriceRates();
		// read segment info
		// read segments' info
		Segment segment = sector.getSegment(segmentId);
		if(segment == null) {
			/////////
			// unlock sector
			sectorLock.unlock();
			status.put("status", "unsuccessful");
			status.put("message", "segment id invalid.");
			return status;
		}
		ReadLock segmentLock = segment.lock.readLock();
		///////// segment is safe for read ////////
		JSONObject segmentJSONObj = segment.getMinimumJSONObject();
		sectorJSONObj.put(Constants.SEGMENT, segmentJSONObj);
		// check if there is an empty spot
		List<ParkingSpot> spots = segment.getSpots();
		boolean emptySpotExists = false;
		for(ParkingSpot spot : spots) {
			///////// spot is safe for read ////////
			ReadLock lock = spot.lock.readLock();
			// act on the first empty spot
			if(spot.status == ParkingSpotStatus.EMPTY) {
				emptySpotExists = true;
			}
			lock.unlock();
			if(emptySpotExists) {
				break;
			}
		}


		if(emptySpotExists) {
			PriceRate selectedRate = null;
			for(PriceRate pr : priceRates) {
				if(pr.id == priceRateId) {
					selectedRate = pr;
				}
			}
			int calculatedPrice = 0;
			/*
			 * 
			 * 
			 * TODO: calculate price using selectedRate and time 
			 * and append the price to sectorJSONObj
			 * 
			 * 
			 */
			sectorJSONObj.put("price", calculatedPrice);
		}else {
			/////////
			// unlock segment
			segmentLock.unlock();
			/////////
			// unlock sector
			sectorLock.unlock();
			status.put("status", "unsuccessful");
			status.put("message", "Segment capacity is full.");
			return status;
			
		}
		/////////
		// unlock segment
		segmentLock.unlock();
		/////////
		// unlock sector
		sectorLock.unlock();
		
		return sectorJSONObj;
	}

	// Get the information of a sector, segment, and|or a spot
	public JSONObject rentSpot(int sectorId, int segmentId) {
		
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			// TODO: there is something wrong. sectorId isn't valid.
			return ResponseHelper.respondWithMessage(false, ResponseCode.SECTOR_ID_INVALID);
		}

		JSONObject transactionJSONObj = new JSONObject();

		// lock S on sector
		WriteLock sectorLock = sector.lock.writeLock();
		///////// sector is safe for read ////////
		// read sector info
		transactionJSONObj.put(Constants.SECTOR_ID, sector.id);
		// read segment info
		// read segments' info
		Segment segment = sector.getSegment(segmentId);
		if(segment == null) {
			/////////
			// unlock sector
			sectorLock.unlock();
			return ResponseHelper.respondWithMessage(transactionJSONObj, 
					false, ResponseCode.SEGMENT_ID_INVALID);
		}
		WriteLock segmentLock = segment.lock.writeLock();
		///////// segment is safe for read ////////
		transactionJSONObj.put(Constants.SEGMENT_ID, segment.id);
		// check if there is an empty spot
		List<ParkingSpot> spots = segment.getSpots();
		for(ParkingSpot spot : spots) {
			///////// spot is safe for read ////////
			WriteLock lock = spot.lock.writeLock();
			// act on the first empty spot
			if(spot.status == ParkingSpotStatus.EMPTY) {
				transactionJSONObj.put(Constants.SPOT_ID, spot.id);
				// modifications for rent
				spot.status = ParkingSpotStatus.FULL;
				segment.availablePark --;
				sector.availablePark --;
				// record the change in DB
				ParkingSpot.updateDB(spot);
				
				transactionJSONObj = ResponseHelper.respondWithStatus(transactionJSONObj, true);
				/////////
				// unlock the spot
				lock.unlock();
				/////////
				// unlock segment
				segmentLock.unlock();
				/////////
				// unlock sector
				sectorLock.unlock();
				
				return transactionJSONObj;
			}
			lock.unlock();
		}

		/////////
		// unlock segment
		segmentLock.unlock();
		/////////
		// unlock sector
		sectorLock.unlock();
		return ResponseHelper.respondWithMessage(transactionJSONObj, 
				false, ResponseCode.SEGMENT_CAPACITY_FULL);
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
	public JSONObject freeSpot(int sectorId, int segmentId, int spotId) {

		JSONObject transactionJSONObj = new JSONObject();
		
		Sector sector = sectorIndex.get(sectorId);
		if(sector == null) { 
			// TODO: there is something wrong. sectorId isn't valid.
			transactionJSONObj.put("status", "unsuccessful");
			transactionJSONObj.put("message", "sector id invalid.");
			return transactionJSONObj;
		}

		// lock S on sector
		WriteLock sectorLock = sector.lock.writeLock();
		///////// sector is safe for read ////////
		// read sector info
		transactionJSONObj.put(Constants.SECTOR_ID, sector.id);
		// read price rates
		List<PriceRate> priceRates = sector.getPriceRates();
		// read segment info
		// read segments' info
		Segment segment = sector.getSegment(segmentId);
		if(segment == null) {
			/////////
			// unlock sector
			sectorLock.unlock();
			transactionJSONObj.put("status", "unsuccessful");
			transactionJSONObj.put("message", "segment id invalid.");
			return transactionJSONObj;
		}
		WriteLock segmentLock = segment.lock.writeLock();
		///////// segment is safe for read ////////
		transactionJSONObj.put(Constants.SEGMENT_ID, segment.id);
		// check if there is an empty spot
		List<ParkingSpot> spots = segment.getSpots();
		for(ParkingSpot spot : spots) {
			///////// spot is safe for read ////////
			WriteLock lock = spot.lock.writeLock();
			// act on the first empty spot
			if(spot.id == spotId) {
				transactionJSONObj.put(Constants.SPOT_ID, spot.id);

				// modifications for rent
				spot.status = ParkingSpotStatus.EMPTY;
				segment.availablePark ++;
				sector.availablePark ++;
				// record the change in DB
				ParkingSpot.updateDB(spot);
				
				transactionJSONObj.put("status", "successful");
				/////////
				// unlock the spot
				lock.unlock();
				/////////
				// unlock segment
				segmentLock.unlock();
				/////////
				// unlock sector
				sectorLock.unlock();
				
				return transactionJSONObj;
			}
			lock.unlock();
		}

		/////////
		// unlock segment
		segmentLock.unlock();
		/////////
		// unlock sector
		sectorLock.unlock();
		transactionJSONObj.put("status", "unsuccessful");
		transactionJSONObj.put("message", "Parking spot not found.");
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
