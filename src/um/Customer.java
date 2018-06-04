/**
 * 
 */
package um;

import org.json.simple.JSONObject;

import rm.parking_structure.City;
import utility.Constants;

/**
 * @author jam
 * If this object exists in session, it means this customer is signed in.
 *
 */
public class Customer {
	public int id;
	public String username;
	public String fname, lname;
	public String cellphone_number;
	public String email_addr;
	public int ads_flag;
	
	public City selected_city = null;

	public Customer(int id,	String username, String fname, 
			String lname, String cellphone_number, String email_addr, 
			int ads_flag) {
		this.id = id;
		this.username = username;
		this.fname = fname;
		this.lname = lname;
		this.cellphone_number = cellphone_number;
		this.email_addr = email_addr;
		this.ads_flag = ads_flag;
	}
	
	public JSONObject pay(int price, int parkTransactionId) {
		// TODO: working with wallet
		// temporary code
		JSONObject walletTransaction = new JSONObject();
		walletTransaction.put("transaction_id", parkTransactionId);
		walletTransaction.put("status", "successful");
		walletTransaction.put("price", price);
		return walletTransaction;
	}
	
	public JSONObject getUserProfile() {
		JSONObject profile = new JSONObject();
		profile.put("id", id);
		profile.put(Constants.USERNAME, username);
		profile.put(Constants.FIRST_NAME, fname);
		profile.put(Constants.LAST_NAME, lname);
		profile.put(Constants.CELL_PHONE, cellphone_number);
		profile.put(Constants.EMAIL_ADDR, email_addr);
		profile.put(Constants.ADS_FLAG, ads_flag);
		return profile;
	}
	
}
