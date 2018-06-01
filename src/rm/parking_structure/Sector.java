package rm.parking_structure;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import rm.PriceRate;
import rm.ResourceManager;

public class Sector {
	
	/// lock and concurrency
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public int id;
	public City city;
	
	private List<PriceRate> priceRates = null;
	
	private List<Segment> segmentList = new ArrayList<>();
	
	public List<PriceRate> getPriceRates() {
		if(priceRates == null) {
			priceRates = ResourceManager.fetchSectorRates(this);
		}
		return priceRates;
	}
	
	public Sector() {
		id = 0;
		city = null;
	}
	
	// load a Street object
	public static Sector fetchFromDB(ResultSet resultSet) {
		// TODO: read the record out of resultSet object
		
		return null;
	}
	
	// update db
	public static void updateDB(Sector s) {
		//TODO: update the db record with the content of this object
	}
}
