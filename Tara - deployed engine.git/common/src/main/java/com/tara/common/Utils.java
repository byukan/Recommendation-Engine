package com.tara.common;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Utils {

	public static Long getCurrentUtcTimeInMillis() {
		DateTime time = new DateTime(DateTimeZone.UTC);
		return time.getMillis();
	}

}
