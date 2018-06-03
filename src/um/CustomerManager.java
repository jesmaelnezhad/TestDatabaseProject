/**
 * 
 */
package um;

import javax.servlet.http.HttpServletRequest;

import utility.Constants;


/**
 * @author jam
 *
 */
public class CustomerManager {

	public static Customer getCustomer(HttpServletRequest request) {
		Customer customer = (Customer) request.getAttribute(Constants.SIGNED_IN_CUSTOMER);
		if(customer == null) {
			// TODO: user is not signed in
			return null;
		}
		return customer;
	}
}
