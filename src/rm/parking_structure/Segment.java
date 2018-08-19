package rm.parking_structure;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.simple.JSONObject;

import utility.Point;

public class Segment {
	
	public int id;
	public Sector sector;
	
	public int parkCapacity;

	public Point start;
	public Point end;
	
	public Segment(int id, Sector sec, int cap, Point start, Point end) {
		this.id = id;
		this.sector = sec;
		this.parkCapacity = cap;
		this.start = start;
		this.end = end;
	}

	
	public JSONObject getMinimumJSONObject() {
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("park-capacity", parkCapacity);
		result.put("start", start.getJSON());
		result.put("end", end.getJSON());
		return result;
	}
}
