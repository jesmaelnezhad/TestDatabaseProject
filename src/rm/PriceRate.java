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
	public int id;
	public String description;
	public int price;
	
	private PriceRate(int id, String description, int price){
		this.id = id;
		this.description = description;
		this.price = price;
	}
	
	
	public String getJSON() {
		String result = "{\"id\":"+id+", \"description\":\""+description+"\", \"price\":"+price+"}";
		return result;
	}
	public JSONObject getJSONObject() {
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("description", description);
		result.put("price", price);
		return result;
	}	
	
	
	public static ReentrantReadWriteLock all_ratesLock = new ReentrantReadWriteLock();
	public static Map<Integer, PriceRate> all_rates = new HashMap<>();
	
	public static PriceRate getRate(ResultSet rs) throws SQLException {
		WriteLock lock = all_ratesLock.writeLock();
		int id  = rs.getInt("id");
		String description = rs.getString("description");
		int price = rs.getInt("price");
		PriceRate rate = all_rates.get(id);
		if(rate == null) {
			rate = new PriceRate(id, description, price);
			all_rates.put(id, rate);
		}
		
		lock.unlock();
		return rate;
	}
	
	
}
