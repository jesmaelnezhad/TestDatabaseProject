package request_handlers;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestHelper {
	
	public static final String REQUEST_PARAMS_ATTR_NAME = "request_json_params";
	public static String getRequestParameter(HttpServletRequest request, String paramName) {
		JSONObject params = (JSONObject) request.getAttribute(REQUEST_PARAMS_ATTR_NAME);
		if(params == null) {
			if(request.getParameter(paramName) != null) {
				return request.getParameter(paramName);
			}
			return null;
		}
		return (String) params.get(paramName);
	}
	
	public static String [] getRequestParameters(HttpServletRequest request, String paramName) {
		JSONObject params = (JSONObject) request.getAttribute(REQUEST_PARAMS_ATTR_NAME);
		if(params == null || params.get(paramName) == null) {
			if(request.getParameter(paramName) != null) {
				return request.getParameterValues(paramName);
			}
			return null;
		}
		JSONArray values =  (JSONArray) params.get(paramName);
		String [] result = new String[values.size()];
		for(int i = 0 ; i < values.size(); i++) {
			result[i] = (String) values.get(i);
		}
		return result;
	}
	/**
	 * 
	 * @param request the HTTPServletRequest object being processed in the servlets or JSP files
	 * @return the request body in the form of a JSON object.
	 * @throws IOException if request stream has a problem
	 * @throws ParseException if body is not in the form of a JSON object
	 */
	public static JSONObject bodyToJSON(HttpServletRequest request) throws IOException, ParseException {
		// get parameters as json object
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		
		String requestParametersStr = sb.toString();
		if(requestParametersStr == null || requestParametersStr.equals("")){
			return null;
		}
		JSONObject requestParams = null;
		requestParams = (JSONObject) new JSONParser().parse(requestParametersStr);
		return requestParams;
	}
}
