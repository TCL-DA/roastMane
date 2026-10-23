package org.cropster.roastingintelligence.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {
   private static final String DATE_TIMEZONE_PATTERN = "yyyyMMdd_HHmmss_SSS_Z";

   public static String formatDateTimeZone(Date date) {
      return new SimpleDateFormat("yyyyMMdd_HHmmss_SSS_Z").format(date);
   }

   public static Date parseDateTimeZone(String text) throws ParseException {
      return new SimpleDateFormat("yyyyMMdd_HHmmss_SSS_Z").parse(text);
   }

   public static Date truncate(Date date, long precision) {
      long factor = (long)Math.pow(10.0, (double)precision);
      long millis = date.getTime() / factor * factor;
      return new Date(millis);
   }

   public static Date newDateTruncated(long precision) {
      return truncate(new Date(NanoTime.currentTimeMillis()), precision);
   }

   private DateUtil() {
   }
}
