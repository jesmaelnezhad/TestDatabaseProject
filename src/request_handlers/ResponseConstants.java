/**
 * 
 */
package request_handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jam
 *
 */
public class ResponseConstants {
	
	private static ResponseConstants rc = null;
	
	
	private Map<ResponseCode, String> messages = new HashMap<>();
	
	public enum ResponseCode{
		SECTOR_ID_MISSING,
		SEGMENT_ID_MISSING,
		CAR_ID_MISSING,
		RATE_ID_MISSING,
		PARK_TIME_MISSING,
		SENSOR_ID_MISSING,
		AMOUNT_MISSING,
		SENSOR_ID_INVALID,
		SECTOR_ID_INVALID,
		SEGMENT_ID_INVALID,
		SPOT_ID_INVALID,
		TOPUP_VALUE_INVALID,
		CUSTOMER_NOT_SIGNED_IN,
		CITY_NOT_FOUND,
		WALLET_NOT_FOUND,
		COMMAND_MISSING,
		NOT_POSSIBLE,
		CAR_ID_NOT_FOUND,
		COMMAND_NOT_RECOGNIZED,
		SEGMENT_CAPACITY_FULL,
		WALLET_NOT_WORKING,
		WALLET_INFO_NOT_CONSISTENT,
		WALLET_BALANCE_NOT_ENOUGH,
		PAYMENT_NOT_SUCCESSFUL,
		INPUT_INFO_INCOMPLETE,
		CUSTOMER_EXISTS,
		UPDATE_SENSOR_INPUT_WRONG
	}

	private ResponseConstants() {
		messages.put(ResponseCode.SECTOR_ID_MISSING, "شناسه ی سکتور داده نشده است.");
		messages.put(ResponseCode.SEGMENT_ID_MISSING, "شناسه ی سگمنت داده نشده است.");
		messages.put(ResponseCode.CAR_ID_MISSING, "شناسه ی ماشین داده نشده است.");
		messages.put(ResponseCode.RATE_ID_MISSING, "شناسه ی نرخ پارک داده نشده است.");
		messages.put(ResponseCode.PARK_TIME_MISSING, "زمان پارک داده نشده است.");
		messages.put(ResponseCode.SENSOR_ID_MISSING, "شناسه ی سنسور داده نشده است.");
		messages.put(ResponseCode.AMOUNT_MISSING, "مقدار داده نشده است.");
		messages.put(ResponseCode.SENSOR_ID_INVALID, "شناسه ی سنسور داده شده وجود ندارد.");
		messages.put(ResponseCode.SECTOR_ID_INVALID, "شناسه ی سکتور داده شده وجود ندارد.");
		messages.put(ResponseCode.SEGMENT_ID_INVALID, "شناسه ی سگمنت داده شده وجود ندارد.");
		messages.put(ResponseCode.SPOT_ID_INVALID, "شناسه ی اسپات داده شده وجود ندارد.");
		messages.put(ResponseCode.TOPUP_VALUE_INVALID, "مقدار داده شده صحیح نمیباشد.");
		messages.put(ResponseCode.CUSTOMER_NOT_SIGNED_IN, "مشتری وارد نشده است.");
		messages.put(ResponseCode.CITY_NOT_FOUND, "شهر مورد نظر مشخص نیست.");
		messages.put(ResponseCode.WALLET_NOT_FOUND, "کیف پول مورد نظر مشخص نیست.");
		messages.put(ResponseCode.COMMAND_MISSING, "دستور مورد نظر داده نشده است.");
		messages.put(ResponseCode.NOT_POSSIBLE, "امکان پذیر نمیباشد.");
		messages.put(ResponseCode.CAR_ID_NOT_FOUND, "ماشین مورد نظر پیدا نشد.");
		messages.put(ResponseCode.COMMAND_NOT_RECOGNIZED, "دستور درخواست شده وجود ندارد.");
		messages.put(ResponseCode.SEGMENT_CAPACITY_FULL, "ظرفیت سگمنت پر شده است.");
		messages.put(ResponseCode.WALLET_NOT_WORKING, "کیف پول مورد نظر کار نمیکند.");
		messages.put(ResponseCode.WALLET_INFO_NOT_CONSISTENT, "اطلاعات کیف پول صحیح نمیباشد.");
		messages.put(ResponseCode.WALLET_BALANCE_NOT_ENOUGH, "بودجه ی موجود کافی نمی‌باشد.");
		messages.put(ResponseCode.PAYMENT_NOT_SUCCESSFUL, "پرداخت با موفقیت انجام نشد.");
		messages.put(ResponseCode.INPUT_INFO_INCOMPLETE, "اطلاعات ورودی کافی نیست.");
		messages.put(ResponseCode.CUSTOMER_EXISTS, "مشتری با این مشخصات قبلا در سیستم ثبت شده است.");
		messages.put(ResponseCode.UPDATE_SENSOR_INPUT_WRONG, "Four parallel arrays of id, fullFlag, lastTimeUpdated, and lastTimeChanged values must be given.");
	}
	
	public String getMessage(ResponseCode code) {
		if(messages.containsKey(code)) {
			return messages.get(code);
		}
		return "";
	}
	
	public static ResponseConstants getRC() {
		if(rc == null) {
			rc = new ResponseConstants();
		}
		return rc;
	}
}