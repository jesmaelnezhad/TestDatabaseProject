package request_handlers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import request_handlers.ResponseConstants.ResponseCode;
import utility.Constants;

public class ResponseHelper {
	
	public static void respondWithMessage(boolean status, ResponseCode messageCode, HttpServletResponse response) throws IOException {
		respondWithJSONObject(respondWithMessage(status, messageCode), response);
	}
	
	public static void respondWithJSONObject(JSONObject result, HttpServletResponse response) throws IOException {
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	    out.println(result.toJSONString());
	}
	public static void respondWithJSONArray(JSONArray result, HttpServletResponse response) throws IOException {
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	    out.println(result.toJSONString());
	}
	
	public static JSONObject respondWithMessage(JSONObject result, 
			boolean status, ResponseCode messageCode){
		result.put(Constants.STATUS, status?"unsuccessful":"successful");
		result.put(Constants.MESSAGE, ResponseConstants.getRC().getMessage(messageCode));
		result.put(Constants.MESSAGE_CODE, messageCode.ordinal());
		return result;
	}
	public static JSONObject respondWithStatus(JSONObject result, boolean status){
		result.put(Constants.STATUS, status?"unsuccessful":"successful");
		return result;
	}
	
	public static JSONObject respondWithMessage(boolean status, ResponseCode messageCode){
		JSONObject result = new JSONObject();
		result.put(Constants.STATUS, status?"unsuccessful":"successful");
		result.put(Constants.MESSAGE, ResponseConstants.getRC().getMessage(messageCode));
		result.put(Constants.MESSAGE_CODE, messageCode.ordinal());
		return result;
	}
}
