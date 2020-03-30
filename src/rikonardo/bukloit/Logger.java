package rikonardo.bukloit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
	public static void info(String textIn) {
		System.out.println(buildString(LineType.INFO, textIn));
	}
	public static void warn(String textIn) {
		System.out.println(buildString(LineType.WARN, textIn));
	}
	public static void error(String textIn) {
		System.out.println(buildString(LineType.ERROR, textIn));
	}
	
	private static String buildString(LineType typeIn, String textIn) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
	    LocalDateTime now = LocalDateTime.now();
		return "[" + dtf.format(now) + " " + ((typeIn == LineType.INFO) ? "INFO" : (typeIn == LineType.WARN) ? "WARN" : (typeIn == LineType.ERROR) ? "ERROR" : null) + "]: " + textIn;
	}
	
	private enum LineType {
		INFO,
		WARN,
		ERROR
	}
}
