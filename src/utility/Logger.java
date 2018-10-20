package utility;

public class Logger {

	private static Logger logger = new Logger();
	
	public void log(String message) {
		System.out.println("TODO:time/date" + "\t" + message);
	}
	
	public static Logger getLogger() {
		return logger;
	}
}
