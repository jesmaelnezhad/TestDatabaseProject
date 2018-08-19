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

import rm.parking_structure.City;
import um.UserManager;

/**
 * Servlet Filter implementation class PrepareCityFilter
 */
@WebFilter("/PrepareCityFilter")
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

        if(prepareCity(req, res)) {
        	chain.doFilter(request, response);
        }
	}
	
	public boolean prepareCity(HttpServletRequest req, HttpServletResponse res) {
		//TODO: find out what the city is and load the data structure if needed.
		City city = null;//TODO
		UserManager.getCM().setCity(req, city);
		return true;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
        String excludePattern = fConfig.getInitParameter("excludedUrls");
        excludedUrls = Arrays.asList(excludePattern.split(","));	
    }

}
