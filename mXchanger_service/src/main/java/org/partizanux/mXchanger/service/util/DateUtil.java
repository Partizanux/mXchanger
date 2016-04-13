package org.partizanux.mXchanger.service.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
	private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	
	public static Timestamp parse(String s) {
		LocalDateTime localDateTime = FORMATTER.parse(s, LocalDateTime::from);
		return Timestamp.valueOf(localDateTime);
	}
	
	public static String format(Timestamp timestamp) {
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		return FORMATTER.format(localDateTime);
	}
}
