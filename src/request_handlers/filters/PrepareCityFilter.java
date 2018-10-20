package request_handlers.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import request_handlers.ResponseHelper;
import request_handlers.ResponseConstants.ResponseCode;
import rm.parking_structure.City;
import um.UserManager;

/**
 * Servlet Filter implementation class PrepareCityFilter
 */
//@WebFilter("/PrepareCityFilter")
public class PrepareCityFilter implements Filter {

    private List<String> excludedUrls;
    /**
     * Default constructor. 
     */
    public PrepareCityFilter() {

    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
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

    	// TODO: we should check here and see if the request is searchArea
    	//       we must find the city from the request.
        
        // for all other requests ...
        if(isCitySelected(req, res)) {
        	chain.doFilter(request, response);
        }else {
        	ResponseHelper.respondWithMessage(false, ResponseCode.CITY_NOT_SELECTED, res);
        }
	}
	
	public static boolean isCitySelected(HttpServletRequest req, HttpServletResponse res) {
		City city = UserManager.getCM().getCity(req);
		if(city != null) {
			return true;
		}
		return false;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
        String excludePattern = fConfig.getInitParameter("excludedUrls");
        excludedUrls = Arrays.asList(excludePattern.split(","));	
    }

}
