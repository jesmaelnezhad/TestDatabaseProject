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

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import request_handlers.ResponseConstants.ResponseCode;
import rm.parking_structure.City;
import um.User;
import um.UserManager;
import request_handlers.RequestHelper;
import request_handlers.ResponseHelper;

/**
 * RequestBlockingFilter class to block requests to MovieHut.com.
 */
//@WebFilter("/AuthenticationFilter")
public class RequestParseFilter implements Filter {

    private ServletContext context;
    private List<String> excludedUrls;

    public void init(FilterConfig fConfig) throws ServletException {
        this.context = fConfig.getServletContext();
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

        try {
			JSONObject params = RequestHelper.bodyToJSON(req);
			req.setAttribute(RequestHelper.REQUEST_PARAMS_ATTR_NAME, params);
			chain.doFilter(request, response);
		} catch (ParseException e) {
			ResponseHelper.respondWithMessage(false, ResponseCode.REQUEST_FORMAT_NOT_JSON, res);
			return;
		};
    }
    
    public void destroy() {
        //we can close resources here
    }

}