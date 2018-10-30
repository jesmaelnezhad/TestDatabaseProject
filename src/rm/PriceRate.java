/**
 * 
 */
package rm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mysql.jdbc.Statement;

import utility.DBManager;

/**
 * @author jam
 *
 */
public class PriceRate {
	private int from;
	private int to;
	private int price;
	
	public PriceRate(int from, int to, int price){
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
	
	public static class PriceRating{
		public int id;
		public List<PriceRate> priceRates = null;
		public PriceRating(int id, List<PriceRate> priceRates) {
			this.id = id;
			this.priceRates = priceRates;
		}
		public PriceRating(int id, String priceRatesStr) throws ParseException {
			List<PriceRate> priceRates = new ArrayList<>();
			JSONParser parser = new JSONParser();
			JSONArray priceRatesObj = (JSONArray) parser.parse(priceRatesStr);
			for(int i = 0 ; i < priceRatesObj.size(); i++) {
				JSONObject pr = (JSONObject) priceRatesObj.get(i);
				priceRates.add(PriceRate.readJSON(pr));
			}
			this.id = id;
			this.priceRates = priceRates;
		}
	};
	
	public static PriceRating savePriceRates(List<PriceRate> priceRates) {
		if(priceRates == null) {
			return null;
		}
		JSONArray priceRatesJSON = new JSONArray();
		for(PriceRate pr: priceRates) {
			priceRatesJSON.add(pr.getJSONObject());
		}
		
		int priceRatingId = existsInDB(priceRatesJSON);
		if(priceRatingId >= 0) {
			return new PriceRating(priceRatingId, priceRates);
		}
		// it doesn't exist in DB. Insert it here.
		// insert
		String sql = "INSERT INTO price_rates "
				+ "(pricing) "
				+ "VALUE (?);";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, priceRatesJSON.toJSONString());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				priceRatingId = rs.getInt("id");
			}
			stmt.close();
			DBManager.getDBManager().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(priceRatingId == -1) {
			return null;
		}
		return new PriceRating(priceRatingId, priceRates);
	}
	
	public static int existsInDB(JSONArray priceRates) {
		if(priceRates == null) {
			return -1;
		}
		String result = null;
		int id = -1;
		String sql = "SELECT * FROM price_rates WHERE pricing=?;";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return -1;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, priceRates.toJSONString());
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			if (rs.next()) {
				result = rs.getString("pricing");
				id = rs.getInt("id");
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public static PriceRating fetchPriceRatesById(int id) {
		PriceRating result = null;
		String sql = "SELECT * FROM price_rates WHERE id=?;";
		Connection conn = DBManager.getDBManager().getConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			// Extract data from result set
			if (rs.next()) {
				result = new PriceRating(rs.getInt("id"), rs.getString("pricing"));
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getJSON() {
		String result = "{\"from\":\""+getFromInMinutes()+"\", \"to\":\""+getToInMinutes()+"\" \"price\":"+price+"}";
		return result;
	}
	
	public static PriceRate readJSON(JSONObject jsonObject) {
		int from = ((Long)jsonObject.get("from")).intValue();
		int to = ((Long)jsonObject.get("to")).intValue();
		int price = ((Long)jsonObject.get("price")).intValue();
		return new PriceRate(from, to, price);
	}
	public JSONObject getJSONObject() {
		JSONObject result = new JSONObject();
		result.put("from", getFromInMinutes());
		result.put("to", getToInMinutes());
		result.put("price", price);
		return result;
	}	
}
