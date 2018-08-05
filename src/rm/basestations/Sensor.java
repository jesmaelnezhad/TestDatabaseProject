/**
 * 
 */
package rm.basestations;

import java.sql.Time;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.json.simple.JSONObject;

/**
 * @author jam
 *
 */
public class Sensor {
	private boolean fullFlag = false;//full if true
	private Time lastTimeChanged ;
	private Time lastTimeUpdated;
	//
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	public boolean updatedSinceFlush = false;
	
	public class SensorSnapshot{
		public boolean fullFlag;
		public Time lastTimeChanged;
		public Time lastTimeUpdated;
		boolean updatedSinceFlush ;
		public JSONObject toJSON() {
			JSONObject result = new JSONObject();
			result.put("full", fullFlag);
			result.put("lastTimeChanged", lastTimeChanged.toString());
			result.put("lastTimeUpdated", lastTimeUpdated).toString();
			return result;
		}
	}
	
	// returns true if vacancy status changes
	public boolean update(boolean fullFlag, Time lastTimeChanged, Time lastTimeUpdated) {
		WriteLock lockObj = lock.writeLock();
		
		boolean result = this.fullFlag != fullFlag;
		
		this.fullFlag = fullFlag;
		this.lastTimeChanged = lastTimeChanged;
		this.lastTimeUpdated = lastTimeUpdated;
		
		updatedSinceFlush = true;
		lockObj.unlock();
		return result;
	}
	
	public SensorSnapshot read() {
		ReadLock lockObj = lock.readLock();
		
		SensorSnapshot snapshot = new SensorSnapshot();
		snapshot.fullFlag = this.fullFlag;
		snapshot.lastTimeChanged = this.lastTimeChanged;
		snapshot.lastTimeUpdated = this.lastTimeUpdated;
		snapshot.updatedSinceFlush = updatedSinceFlush;
		lockObj.unlock();
		
		return snapshot;
	}
	
	public void flush() {
		updatedSinceFlush = false;
	}
}
