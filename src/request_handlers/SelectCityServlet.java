package request_handlers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.portable.ResponseHandler;

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import rm.parking_structure.City;
import rm.parking_structure.ParkingSpotContainer;
import um.User;
import um.User;
import um.UserManager;
import utility.Constants;
import utility.Point;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SelectCityServlet")
public class SelectCityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SelectCityServlet() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
		JSONObject requestParams = null;
		try {
			requestParams = (JSONObject) new JSONParser().parse(requestParametersStr);
		} catch (ParseException e) {
			// TODO: handle exception
			// ERROR: request format should be JSON
			ResponseHelper.respondWithMessage(false, ResponseCode.REQUEST_FORMAT_NOT_JSON, response);
			return;
		}
	
		
		// int cityId = Integer.parseInt(request.getParameter(Constants.CITY_ID));
		int cityId = Integer.parseInt((String)requestParams.get(Constants.CITY_ID));
		
		
		ResourceManager rm = ResourceManager.getRM();
		User customer = UserManager.getCM().getUser(request);
		
		City city = rm.loadCity(cityId);
		if(city == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_FOUND, response);
		    return;
		}else {
			
			UserManager.getCM().setCity(request, city);
			
			JSONObject result = new JSONObject();
			result.put(Constants.STATUS, "successful");
			result.put(Constants.MESSAGE, "شهر "+city.name+" انتخاب شده است.");
		    response.setContentType("text/html");
		    response.setCharacterEncoding("UTF-8");
		    PrintWriter out = response.getWriter();
		    out.println(result.toJSONString());
		    return;
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
