/**
 * 
 */
package rm.parking_structure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import um.User;
import utility.DBManager;

/**
 * @author jam
 *
 */
public class City {
	public int id;
	public String name;
	
	
	public City(int id, String name) {
		this.id = id;
		this.name = name;
	}
	//street_id to Street object
	Map<Integer, Sector> streets = new HashMap<Integer, Sector>();
	
	// load a City object
	public static City fetchFromDB(int city_id) {
		// TODO: fetch record with id=city_id and make an object (also fetch streets of this city);
		
		return null;
	}
	
	public static City fetchCityById(int id){
		Connection conn = DBManager.getDBManager().getConnection();
		PreparedStatement stmt;
		try {
			String sql = "SELECT * FROM cities where id=?;";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			City city = null;
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				String name = rs.getString("name");
				city = new City(id, name);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
			return city;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static City fetchCityByName(String name){
		Connection conn = DBManager.getDBManager().getConnection();
		PreparedStatement stmt;
		try {
			String sql = "SELECT * FROM cities where name=?;";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			City city = null;
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				int id = rs.getInt("id");
				city = new City(id, name);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
			return city;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<City> fetchAllCities(){
		List<City> results = new ArrayList<>();
		Connection conn = DBManager.getDBManager().getConnection();
		PreparedStatement stmt;
		try {
			String sql = "SELECT * FROM cities;";
			stmt = conn.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				int id = rs.getInt("id");
				String name = rs.getString("name");
				City city = new City(id, name);
				results.add(city);
			}
			rs.close();
			stmt.close();
			DBManager.getDBManager().closeConnection();
			return results;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	// update db
	public static void updateDB(City c) {
		//TODO: update the db record with the content of this object
	}
}
