package lm;

import java.sql.Date;
import java.sql.Time;

import org.json.simple.JSONObject;

public class LogRecord {
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private String group;
	private String log;
	private Time time_ = null;
	private Date date_ = null;
	public LogRecord(int id, String group, String log){
		this.id = id;
		this.group = group;
		this.log = log;
	}
	public LogRecord(int id, String group, String log, Time t, Date d){
		this.id = id;
		this.group = group;
		this.log = log;
		this.time_ = t;
		this.date_ = d;
	}
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("group", group);
		result.put("log", log);
		if(time_ != null) {
			result.put("time", time_.toString());
		}
		if(date_ != null) {
			result.put("date", date_.toString());
		}
		return result;
	}
}
