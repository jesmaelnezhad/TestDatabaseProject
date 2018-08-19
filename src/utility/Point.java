/**
 * 
 */
package utility;

import org.json.simple.JSONObject;

/**
 * @author jam
 * Every street is made of three points
 *
 */
public class Point {
	public int x;
	public int y;
	
	public Point(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	public static double distance(Point a, Point b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
	
	public JSONObject getJSON() {
		JSONObject object = new JSONObject();
		object.put("x", x);
		object.put("y", y);
		return object;
		
	}
}
