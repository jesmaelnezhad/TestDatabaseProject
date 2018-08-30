package tm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import um.Car;
import um.User;
import utility.Constants;
import utility.DBManager;

public class TransactionManager {

	private static TransactionManager tm = null;
	private TransactionManager() {
		
	}
	
	public static TransactionManager getTM() {
		if(tm == null) {
			tm = new TransactionManager();
		}
		return tm;
	}
	
// TODO: these methods must be replaced with the needed methods for reservations
//	public ParkTransaction recordNewParkTransaction(Customer customer, 
//	public JSONArray getCurrentParkTransactions(Customer customer){
//	public JSONArray getAllParkTransactions(Customer customer){
//	public void deleteParkTransaction(int transactionId) {

	
	// TODO: saves the event of somebody paying for a spot at a parkometer.
	// saves it in data base.
	public JSONObject saveSensorTransaction(int sensorId, int price, Time time) {
		// TODO
		return null;
	}
	
	// TODO: calculates the price of parking at a spot (given with sensor) for a given timeLength.
	// saves it in data base.
	public JSONObject calcSensorPrice(int sensorId, int timeLength) {
		// TODO
		return null;
	}
	
}
