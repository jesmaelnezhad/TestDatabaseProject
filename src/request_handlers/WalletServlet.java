package request_handlers;


import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import request_handlers.ResponseConstants.ResponseCode;
import rm.ResourceManager;
import um.User;
import um.UserManager;
import um.User.UserType;
import utility.Constants;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/WalletServlet")
public class WalletServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WalletServlet() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("command");
		if(command == null) {
			ResponseHelper.respondWithMessage(false, ResponseCode.COMMAND_MISSING, response);
			return;
		}

		ResourceManager rm = ResourceManager.getRM();
		User customer = UserManager.getCM().getUser(request);
		if(customer.type != UserType.Customer) {
			ResponseHelper.respondWithMessage(false, ResponseCode.REQUEST_NOT_SUPPORTED, response);
			return;
		}
		
		if(command.equals(Constants.COMMAND_TOPUP)) {

			if(request.getParameter("amount") == null) {
				ResponseHelper.respondWithMessage(false, ResponseCode.AMOUNT_MISSING, response);
				return;
			}
			int amount = Integer.parseInt(request.getParameter("amount"));

			ResponseHelper.respondWithJSONObject(customer.topUp(amount), response);
			return;
		}else if(command.equals(Constants.COMMAND_WALLET_INFO)) {
			ResponseHelper.respondWithJSONObject(customer.getWalletInfo(), response);
			return;
		}else if(command.equals(Constants.COMMAND_WALLET_TRANSACTIONS)) {
			ResponseHelper.respondWithJSONArray(customer.getTransactionHistory(), response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
