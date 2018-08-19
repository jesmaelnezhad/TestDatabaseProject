/**
 * 
 */
package rm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.json.simple.JSONObject;

/**
 * @author jam
 *
 */
public class PriceRate {
	private int id;
	private int from;
	private int to;
	private int price;
	
	private PriceRate(int id, int from, int to, int price){
		this.id = id;
		this.from = from;
		this.to = to;
		this.price = price;
	}
	
	public int getFromInMinutes() {
		return from * 30;
	}
	
	public int getToInMinutes() {
		return to * 30;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getId() {
		return id;
	}
	
	
	public String getJSON() {
		String result = "{\"from\":\""+getFromInMinutes()+"\", \"to\":\""+getToInMinutes()+"\" \"price\":"+price+"}";
		return result;
	}
	
	public static PriceRate readJSON(int id, JSONObject jsonObject) {
		int from = (Integer)jsonObject.get("from");
		int to = (Integer)jsonObject.get("to");
		int price = (Integer)jsonObject.get("price");
		return new PriceRate(id, from, to, price);
	}
	public JSONObject getJSONObject() {
		JSONObject result = new JSONObject();
		result.put("from", getFromInMinutes());
		result.put("to", getToInMinutes());
		result.put("price", price);
		return result;
	}	
}
