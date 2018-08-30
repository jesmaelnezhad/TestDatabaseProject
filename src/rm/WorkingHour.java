package rm;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import org.json.simple.JSONObject;

public class WorkingHour {
	public int id;
	public Time startTime;
	public Time endTime;
	//TODO: this class must be completed.
	public WorkingHour(int id, Time startTime, Time endTime) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public JSONObject getJSON() {
		JSONObject obj = new JSONObject();
		obj.put("start", startTime.toString());
		obj.put("end", endTime.toString());
		return obj;
	}
	
	public boolean isWorkingNow() {
		Calendar currenttime = Calendar.getInstance();
	    Time nowTime = new Time(currenttime.getTime().getTime());
	    return nowTime.equals(startTime) || nowTime.equals(endTime) || (nowTime.after(startTime) && nowTime.before(endTime));
	}
	
	// time left in minutes
	public int getTimeLengthLeftWorking() {
		Calendar currenttime = Calendar.getInstance();
	    Time nowTime = new Time(currenttime.getTime().getTime());
	    if(! endTime.after(nowTime)) {
	    	return 0;
	    }
	    return (int)((endTime.getTime() - nowTime.getTime())/60);
	}
}
