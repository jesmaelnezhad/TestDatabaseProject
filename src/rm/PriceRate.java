/**
 * 
 */
package rm;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jam
 *
 */
public class PriceRate {
	public int id;
	public String description;
	public float price;
	
	public String getJSON() {
		String result = "{\"id\":"+id+", \"description\":\""+description+"\", \"price\":"+price+"}";
		return result;
	}
	
	public static Map<Integer, PriceRate> all_rates = new HashMap<>();
	
	public static PriceRate getRate(int id) {
		PriceRate rate = all_rates.get(id);
		if(rate == null) {
			// TODO: try to fetch this rate from the database
		}
		return rate;
	}
}
