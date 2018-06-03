/**
 * 
 */
package tm;

import java.sql.Time;

import org.json.simple.JSONObject;

import um.Car;
import um.Customer;
import utility.Constants;

/**
 * @author jam
 *
 */
public class ParkTransaction {
	public int id;
	public Customer customer;
	public Car car;
	public int sectorId, segmentId, spotId;
	public Time startTime;
	public int timeLength;
	public int rateId;
	public ParkTransactionStatus status;
	
	
	public ParkTransaction(int id, Customer customer, int carId, 
			int sectorId, int segmentId, int spotId, Time startTime, 
			int timeLength, int rateId, ParkTransactionStatus status) {
		this.id= id;
		this.customer= customer;
		this.car = new Car(carId);
		this.sectorId = sectorId; 
		this.segmentId = segmentId; 
		this.spotId = spotId;
		this.startTime= startTime;
		this.timeLength= timeLength;
		this.rateId = rateId;
		this.status= status;
	}
	
	public ParkTransaction(int id, Customer customer, Car car, 
			int sectorId, int segmentId, int spotId, Time startTime, 
			int timeLength, int rateId, ParkTransactionStatus status) {
		this.id= id;
		this.customer= customer;
		this.car = car;
		this.sectorId = sectorId; 
		this.segmentId = segmentId; 
		this.spotId = spotId;
		this.startTime= startTime;
		this.timeLength= timeLength;
		this.rateId = rateId;
		this.status= status;
	}
	
	public JSONObject getJSON() {
		JSONObject result= new JSONObject();
		result.put("id", id);
		result.put("car", car.getJSON());
		result.put(Constants.SECTOR_ID, sectorId);
		result.put(Constants.SEGMENT_ID, segmentId);
		result.put(Constants.SPOT_ID, spotId);
		result.put(Constants.START_TIME, startTime.toString());
		result.put(Constants.TIME_LENGTH, timeLength);
		result.put(Constants.RATE_ID, rateId);
		result.put(Constants.STATUS, status==ParkTransactionStatus.OPEN? "open":"close");
		return result;
	}
}
