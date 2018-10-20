package request_handlers.filters;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import request_handlers.ResponseConstants.ResponseCode;
import rm.parking_structure.City;
import um.User;
import um.UserManager;
import request_handlers.ResponseHelper;

/**
 * RequestBlockingFilter class to block requests to MovieHut.com.
 */
//@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {

    private ServletContext context;
    private List<String> excludedUrls;

    public void init(FilterConfig fConfig) throws ServletException {
        this.context = fConfig.getServletContext();
        this.context.log("Authentication initialized");
        String excludePattern = fConfig.getInitParameter("excludedUrls");
        excludedUrls = Arrays.asList(excludePattern.split(","));
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String path = ((HttpServletRequest) request).getServletPath();
        
        if(excludedUrls.contains(path))
        {
            // this filter shouldn't be applied on this request.
        	chain.doFilter(request, response);
        	return;
        }
    	
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if(authenticate(req, res)) {
        	chain.doFilter(request, response);
        }else {
        	ResponseHelper.respondWithMessage(false, ResponseCode.CUSTOMER_NOT_SIGNED_IN, res);
        }
    }
    
    public static boolean authenticate(HttpServletRequest req, HttpServletResponse res) throws IOException {
		User customer = UserManager.getCM().getUser(req);

		if(customer == null) {
			// TODO if customer is null city will not be known.
			// TODO : redirect to use authentication
			// TODO if customer is null city will not be known.
			// TODO : redirect to use authentication
			return false;
		}
    	return true;
    }

    public void destroy() {
        //we can close resources here
    }

}