package request_handlers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

import lm.LogManager;
import lm.LogRecord;
import request_handlers.ResponseConstants.ResponseCode;
import utility.Constants;

/**
 * Servlet implementation class ReservationDBFetchServlet
 */
@WebServlet("/ReservationDBFetchServlet")
public class ReservationDBFetchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReservationDBFetchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int position = -1;
		if(request.getSession().getAttribute(Constants.POSITION) != null) {
			try {
				position = Integer.parseInt(
						(String)request.getSession().getAttribute(Constants.POSITION)) ;
			} catch (NumberFormatException e) {
				ResponseHelper.respondWithMessage(false, ResponseCode.INVALID_POSITION, response);
				return;
			}
		}
		String group = (String) request.getSession().getAttribute(Constants.GROUP);;
		JSONArray logRecords = new JSONArray();
		List<LogRecord> logs = LogManager.getLogger().getRecords(group, position);
		for(LogRecord lr: logs) {
			logRecords.add(lr.toJSON());
		}
		ResponseHelper.respondWithJSONArray(logRecords, response);
		return;
	}

}
