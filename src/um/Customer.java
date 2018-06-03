/**
 * 
 */
package um;

import org.json.simple.JSONObject;

import rm.parking_structure.City;

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
	public boolean ads_flag;
	
	public City selected_city = null;

	public Customer(int id,	String username, String fname, 
			String lname, String cellphone_number, String email_addr, 
			boolean ads_flag) {
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
		walletTransaction.put("status", "successful");
		return walletTransaction;
	}
	
	
	public static Customer loadCustomer(String username, String password) {
		
		// returns null if username does not exist or password doesn't work
		return null;
	}
}
