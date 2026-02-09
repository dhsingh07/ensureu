package com.book.ensureu.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String OUTPUT_DATE_PAPER = "yyyy-MM-dd";
	static DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
	DateTimeFormatter formatDate = DateTimeFormatter.ofPattern(OUTPUT_DATE_PAPER);
	private static final transient Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

	public static void main(String args[]) {
	//	getDateStringFromDate();

	}

	/*private static void getDateStringFromDate() {

	}*/

	public static LocalDate getLocalDateStringFromString(String date) {
		LocalDate localDate = null;
		try {
			localDate = LocalDate.parse(date);
		} catch (Exception e) {
			LOGGER.error("Failed date conversion", e);
		}

		return localDate;
	}

}
