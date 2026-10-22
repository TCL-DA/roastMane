package org.glassfish.jersey.message.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class HttpDateFormat {
   private static final String RFC1123_DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
   private static final String RFC1036_DATE_FORMAT_PATTERN = "EEEE, dd-MMM-yy HH:mm:ss zzz";
   private static final String ANSI_C_ASCTIME_DATE_FORMAT_PATTERN = "EEE MMM d HH:mm:ss yyyy";
   private static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");
   private static final ThreadLocal<List<SimpleDateFormat>> dateFormats = new ThreadLocal<List<SimpleDateFormat>>() {
      protected synchronized List<SimpleDateFormat> initialValue() {
         return HttpDateFormat.createDateFormats();
      }
   };

   private HttpDateFormat() {
   }

   private static List<SimpleDateFormat> createDateFormats() {
      SimpleDateFormat[] formats = new SimpleDateFormat[]{
         new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
         new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
         new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.US)
      };
      formats[0].setTimeZone(GMT_TIME_ZONE);
      formats[1].setTimeZone(GMT_TIME_ZONE);
      formats[2].setTimeZone(GMT_TIME_ZONE);
      return Collections.unmodifiableList(Arrays.asList(formats));
   }

   private static List<SimpleDateFormat> getDateFormats() {
      return dateFormats.get();
   }

   public static SimpleDateFormat getPreferredDateFormat() {
      return (SimpleDateFormat)dateFormats.get().get(0).clone();
   }

   public static Date readDate(String date) throws ParseException {
      ParseException pe = null;

      for (SimpleDateFormat f : getDateFormats()) {
         try {
            Date result = f.parse(date);
            f.setTimeZone(GMT_TIME_ZONE);
            return result;
         } catch (ParseException var5) {
            pe = pe == null ? var5 : pe;
         }
      }

      throw pe;
   }
}
