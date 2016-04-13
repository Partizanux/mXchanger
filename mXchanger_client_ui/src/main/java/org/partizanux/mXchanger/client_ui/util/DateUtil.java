package org.partizanux.mXchanger.client_ui.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
	
	private static final String PATTERN = "HH:mm";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);
	
	private static final String ORDER_PATTERN = "dd-MM-yyyy HH:mm:ss";
	private static final DateTimeFormatter ORDER_FORMATTER = DateTimeFormatter.ofPattern(ORDER_PATTERN);
	
	public static String format(LocalDateTime time) {
		return FORMATTER.format(time);
	}
	
	public static String format(Timestamp time) {
		LocalDateTime local = time.toLocalDateTime();
		return FORMATTER.format(local);
	}
	
	public static LocalDateTime parse(String s) {
		LocalDateTime localDateTime = ORDER_FORMATTER.parse(s, LocalDateTime::from);
		Timestamp timestamp = Timestamp.valueOf(localDateTime);
		 return timestamp.toLocalDateTime();
	}

}
