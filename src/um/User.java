/**
 * 
 */
package um;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import request_handlers.ResponseHelper;
import request_handlers.ResponseConstants.ResponseCode;
import rm.parking_structure.City;
import tm.Wallet;
import tm.Transaction;
import tm.Transaction.TransactionType;
import utility.Constants;
import utility.Photo;

/**
 * @author jam
 * If this object exists in session, it means this customer is signed in.
 *
 */
public class User {
	public enum UserType{
		Customer,
		Police,
		Basestation;
		public static String toString(UserType type) {
			switch (type) {
			case Customer:
				return "customer";
			case Police:
				return "police";
			case Basestation:
				return "basestation";
			}
			return "";
		}
		public static UserType fromString(String str) {
			if("customer".equals(str)) {
				return UserType.Customer;
			}else if("police".equals(str)) {
				return UserType.Police;
			}else if("basestation".equals(str)) {
				return UserType.Basestation;
			}
			return UserType.Customer;
		}
	}
	
	public int id;
	public UserType type;
	public String fname, lname;
	public String username, password;
	public String cellphone_number;
	public String email_addr;
	public String profileImage;
	public int ads_flag;
	
	public User(int id, String fname, 
			String lname, String cellphone_number, String email_addr, String profileImage,
			int ads_flag) {
		this.id = id;
		this.username = this.password = "";
		this.type = UserType.Customer;
		this.fname = fname;
		this.lname = lname;
		this.cellphone_number = cellphone_number;
		this.email_addr = email_addr;
		this.ads_flag = ads_flag;
		this.profileImage = profileImage;
	}
	
	public User(int id, String username, String password, String fname, 
			String lname, String cellphone_number, String email_addr, String profileImage,
			int ads_flag) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.type = UserType.Customer;
		this.fname = fname;
		this.lname = lname;
		this.cellphone_number = cellphone_number;
		this.email_addr = email_addr;
		this.ads_flag = ads_flag;
		this.profileImage = profileImage;
	}
	
	public User(int id, UserType type, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.type = type;
		assert(type == UserType.Police || type == UserType.Basestation);
		this.fname = "";
		this.lname = "";
		this.cellphone_number = "";
		this.email_addr = "";
		this.ads_flag = 0;
		this.profileImage = "";
	}

	public JSONObject getWalletInfo() {
		Wallet wallet = Wallet.fetchWallet(this);
		if(wallet == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_NOT_FOUND);			
		}	
		JSONObject walletTransaction = new JSONObject();
		walletTransaction.put(Constants.BALANCE, wallet.balance);
		return walletTransaction;
	}
	
	public JSONArray getTransactionHistory() {
		JSONArray result = new JSONArray();
		Wallet wallet = Wallet.fetchWallet(this);
		if(wallet == null) {
			 result.add(ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_NOT_FOUND));
			 return result;
		}
		List<Transaction> transactions = Transaction.fetchAllTransactions(wallet);
		for(Transaction t: transactions) {
			//TODO: description of the transaction should be prepared for output.
//			if(! t.description.equals("topup")) {
//				// we must retrieve the description of a park transaction
//				t.description = 
//						ParkTransaction.getParkTransactionDescription(Integer.parseInt(t.description));
//			}
			result.add(t.toJSON());
		}
		return result;
	}
	
	public JSONObject topUp(int price) {
		if(price <= 0) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.TOPUP_VALUE_INVALID);
		}		
		
		Wallet wallet = Wallet.fetchWallet(this);
		if(wallet == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_NOT_FOUND);		
		}
		
		// 1. add money to the wallet and update the wallet
		wallet.balance += price;
		boolean result = wallet.save();
		if(! result) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_NOT_WORKING);
		}
		// 2. record a wallet transaction
		Calendar currenttime = Calendar.getInstance();
	    Date now = new Date((currenttime.getTime()).getTime());
	    Time nowTime = new Time(currenttime.getTime().getTime());
		Transaction transaction = 
				Transaction.saveNewTopUp(wallet.id, now, nowTime, "", price);
		if(transaction == null) {
			wallet.balance -= price;
			result = wallet.save();
			if(! result) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_INFO_NOT_CONSISTENT);	
			}
			return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
		}
		// 3. report the result out of the function
		JSONObject walletTransaction = new JSONObject();
		walletTransaction.put("transaction_id", transaction.id);
		walletTransaction.put(Constants.STATUS, "successful");
		walletTransaction.put(Constants.PRICE, price);
		walletTransaction.put(Constants.BALANCE, wallet.balance);
		return walletTransaction;
	}
	
	public JSONObject pay(int price, int reservationId) {
		
		Wallet wallet = Wallet.fetchWallet(this);
		if(wallet == null) {
			return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_NOT_FOUND);
		}
		
		if(wallet.balance >= price) {
			// 1. take money from the wallet and update the wallet
			wallet.balance -= price;
			boolean result = wallet.save();
			if(! result) {
				return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_NOT_WORKING);
			}
			// 2. record a wallet transaction
			Calendar currenttime = Calendar.getInstance();
		    Date now = new Date((currenttime.getTime()).getTime());
		    Time nowTime = new Time(currenttime.getTime().getTime());
			Transaction transaction = 
					Transaction.saveNewWalletTransaction(wallet.id, reservationId, now, nowTime, "", price);
			if(transaction == null) {
				wallet.balance += price;
				result = wallet.save();
				if(! result) {
					return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_INFO_NOT_CONSISTENT);
				}
				return ResponseHelper.respondWithMessage(false, ResponseCode.NOT_POSSIBLE);
			}
			// 3. report the result out of the function
			JSONObject walletTransaction = new JSONObject();
			walletTransaction.put("transaction_id", transaction.id);
			walletTransaction.put(Constants.STATUS, "successful");
			walletTransaction.put(Constants.PRICE, price);
			return walletTransaction;
			
		}else {
			return ResponseHelper.respondWithMessage(false, ResponseCode.WALLET_BALANCE_NOT_ENOUGH);
		}
	}
	
	public JSONObject getUserProfile() {
		JSONObject profile = new JSONObject();
		profile.put("id", id);
		profile.put(Constants.USERNAME, username);
		profile.put(Constants.FIRST_NAME, fname);
		profile.put(Constants.LAST_NAME, lname);
		profile.put(Constants.CELL_PHONE, cellphone_number);
		profile.put(Constants.EMAIL_ADDR, email_addr);
		profile.put(Constants.PROFILE_IMAGE, Photo.getPhotoPath(Constants.USER_PROFILE_IMAGES, id, profileImage));
		profile.put(Constants.ADS_FLAG, ads_flag);
		return profile;
	}
	
}
