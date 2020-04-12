package ee.ttu.ofbizpublisher.services.jersey.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class QueryParamStringConverter {

	public static Object convert(String string, String className) {
		switch (className) {
			case "blob":
				// Blob
			case "byte-array":
				// byte[]
			case "object":
				// Object
				// Not possible to make it work.
				return string;
			case "date-time":
				// java.sql.Timestamp
				return new Timestamp(Long.parseLong(string));
			case "date":
				// java.sql.Date
				return new Date(Long.parseLong(string));
			case "time":
				// java.sql.Time
				return new Time(Long.parseLong(string));
			case "currency-amount":
				// java.math.BigDecimal
			case "currency-precise":
				// java.math.BigDecimal
			case "fixed-point":
				// java.math.BigDecimal
				return BigDecimal.valueOf(Double.parseDouble(string));
			case "floating-point":
				// Double
				return Double.parseDouble(string);
			case "numeric":
				// Long
				return Long.parseLong(string);
			default:
				// "tel-number", "url", "email", "credit-card-date", "credit-card-number", "value", "name",
				// "description", "comment", "very-long", "long-varchar", "short-varchar", "very-short",
				// "indicator", "id-vlong", "id-long", "id", "" and whatever else
				return string;
		}
	}
}
