package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import rm.ResourceManager;

@WebListener
public class Server implements ServletContextListener {
	
	private static Server instance = null;
	public static Server getServer() {
		return instance;
	}
	
	private String TIME_FORMAT_STRING = "";
	public String getTimeFormatString() {
		return TIME_FORMAT_STRING;
	}
	
    public void contextInitialized(ServletContextEvent event) {
    	
    	// remember this server object
    	Server.instance = this;
    	
    	// read the config parameters
    	
    	// time format string
    	this.TIME_FORMAT_STRING = event.getServletContext().getInitParameter("timeFormatString");
    	if(this.TIME_FORMAT_STRING == null) {
    		this.TIME_FORMAT_STRING = "HH:mm:ss";
    	}
    	
    	// load data if datafile exists, load data structures, ...
    	List<String> dataFilePaths = new ArrayList<>();
    	String filePaths = event.getServletContext().getInitParameter("dataFilePaths");
    	dataFilePaths = Arrays.asList(filePaths.split(","));
    	ResourceManager rm = ResourceManager.getRM();
    	for(String dataFilePath : dataFilePaths) {
    		boolean isLoaded = rm.loadFromFile(dataFilePath);
    		// isLoaded is true only if the file is actually loaded.
    		// we may need this value later.
    	}
    	
    	
    	// load cities that are declared to be loaded beforehand at the startup of the system
    	// Other cities may be loaded by using the selectCity command
    	List<String> citiesToLoad = new ArrayList<>();
    	String cityStrs = event.getServletContext().getInitParameter("citiesToLoad");
    	citiesToLoad = Arrays.asList(cityStrs.split(","));
    	rm.loadCities(citiesToLoad);
    }
    public void contextDestroyed(ServletContextEvent event) {
        // Do your thing during webapp's shutdown.
    	// TODO: save and exit
    }
}
