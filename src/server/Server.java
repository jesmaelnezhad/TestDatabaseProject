package server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Server implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        // Do your thing during webapp's startup.
    	// TODO: load data if datafile exists, load data structures, ...
    }
    public void contextDestroyed(ServletContextEvent event) {
        // Do your thing during webapp's shutdown.
    	// TODO: save and exit
    }
}
