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
		USERNAME_PASSWORD_NOT_MATCHING,
		PARAMETERS_NOT_COMPLETE,
		REQUEST_NOT_SUPPORTED,
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
		CITY_ID_NOT_GIVEN,
		CITY_NOT_SELECTED,
		SPOT_NOT_FOUND,
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
		UPDATE_SENSOR_INPUT_WRONG,
		SECTOR_CAPACITY_FULL,
		RESERVATION_TYPE_MISSING,
		RESERVATION_TIME_MISSING,
		RESERVATION_TYPE_UNDEFINED,
		LOCAL_SPOT_ID_MISSING,
		LOCAL_SPOT_ID_INVALID,
		LOGIN_TYPE_MISSING,
		REQUEST_FORMAT_NOT_JSON,
		USERNAME_MISSING,
		PASSWORD_MISSING,
		VERIFICATION_STEP_NOT_STARTED,
		INCORRECT_VERIFICATION_CODE,
		PHOTO_TIME_MISSING,
		TIME_FORMAT_INVALID,
		WRONG_INPUT_FOR_VERIFICATION,
		VERIFIED_SUCCESSFULLY,
		VERIFIED_UNSUCCESSFULLY,
		SUCCESSFULLY_PROCESSED_PHOTO,
		INVALID_POSITION
	}

	private ResponseConstants() {
		messages.put(ResponseCode.USERNAME_PASSWORD_NOT_MATCHING, "Username and password do not match.");
		messages.put(ResponseCode.PARAMETERS_NOT_COMPLETE, "پارامتر های ورودی کامل نیستند.");
		messages.put(ResponseCode.REQUEST_NOT_SUPPORTED, "Request is not supported for this type of user.");
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
		messages.put(ResponseCode.CITY_ID_NOT_GIVEN, "City ID is not given.");
		messages.put(ResponseCode.CITY_NOT_SELECTED, "City is not selected.");
		messages.put(ResponseCode.SPOT_NOT_FOUND, "محل مورد نظر یافت نشد.");
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
		messages.put(ResponseCode.SECTOR_CAPACITY_FULL, "ظرفیت این سکتور پر است.");
		messages.put(ResponseCode.RESERVATION_TYPE_MISSING, "نوع رزرو مشخص نشده است.");
		messages.put(ResponseCode.RESERVATION_TIME_MISSING, "زمان رزرو مشخص نشده است.");
		messages.put(ResponseCode.RESERVATION_TYPE_UNDEFINED, "نوع رزرو اشتباه مشخص شده است.");
		messages.put(ResponseCode.LOCAL_SPOT_ID_MISSING, "شماره مکان پارک داده نشده است.");
		messages.put(ResponseCode.LOCAL_SPOT_ID_INVALID, "Local spot id does not exist.");
		messages.put(ResponseCode.LOGIN_TYPE_MISSING, "login_type parameter is missing. It should be one of the following three values: customer, police, basestation");
		messages.put(ResponseCode.REQUEST_FORMAT_NOT_JSON, "Request data is expected to be in JSON format.");
		messages.put(ResponseCode.USERNAME_MISSING, "username parameter missing.");
		messages.put(ResponseCode.PASSWORD_MISSING, "password parameter missing.");
		messages.put(ResponseCode.VERIFICATION_STEP_NOT_STARTED, "مراحل تایید کاربر از طریق پیام کوتاه انجام نشده است.");
		messages.put(ResponseCode.INCORRECT_VERIFICATION_CODE, "کد تایید وارد شده صحیح نمی باشد.");
		messages.put(ResponseCode.PHOTO_TIME_MISSING, "Time of taking photo is missing.");
		messages.put(ResponseCode.TIME_FORMAT_INVALID, "Time format is invalid.");
		messages.put(ResponseCode.WRONG_INPUT_FOR_VERIFICATION, "Wrong input for verification.");
		messages.put(ResponseCode.VERIFIED_SUCCESSFULLY, "Verified successfully.");
		messages.put(ResponseCode.VERIFIED_UNSUCCESSFULLY, "Verification failed. Should issue ticket.");
		messages.put(ResponseCode.SUCCESSFULLY_PROCESSED_PHOTO, "Photo processed successfully.");
		messages.put(ResponseCode.INVALID_POSITION, "Invalid position.");
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
