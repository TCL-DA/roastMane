package org.eclipse.jetty.http;

import java.time.Instant;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.eclipse.jetty.util.StringUtil;

public class DateGenerator {
   private static final TimeZone __GMT = TimeZone.getTimeZone("GMT");
   static final String[] DAYS = new String[]{"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
   static final String[] MONTHS = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan"};
   private static final ThreadLocal<DateGenerator> __dateGenerator = new ThreadLocal<DateGenerator>() {
      protected DateGenerator initialValue() {
         return new DateGenerator();
      }
   };
   public static final String __01Jan1970 = formatDate(0L);
   private final StringBuilder buf = new StringBuilder(32);
   private final GregorianCalendar gc = new GregorianCalendar(__GMT);

   public static String formatDate(long date) {
      return __dateGenerator.get().doFormatDate(date);
   }

   public static String formatDate(Instant instant) {
      return formatDate(instant.toEpochMilli());
   }

   public String doFormatDate(long date) {
      this.buf.setLength(0);
      this.gc.setTimeInMillis(date);
      int dayOfWeek = this.gc.get(7);
      int dayOfMonth = this.gc.get(5);
      int month = this.gc.get(2);
      int fullYear = this.gc.get(1);
      int century = fullYear / 100;
      int year = fullYear % 100;
      int hours = this.gc.get(11);
      int minutes = this.gc.get(12);
      int seconds = this.gc.get(13);
      this.buf.append(DAYS[dayOfWeek]);
      this.buf.append(',');
      this.buf.append(' ');
      StringUtil.append2digits(this.buf, dayOfMonth);
      this.buf.append(' ');
      this.buf.append(MONTHS[month]);
      this.buf.append(' ');
      StringUtil.append2digits(this.buf, century);
      StringUtil.append2digits(this.buf, year);
      this.buf.append(' ');
      StringUtil.append2digits(this.buf, hours);
      this.buf.append(':');
      StringUtil.append2digits(this.buf, minutes);
      this.buf.append(':');
      StringUtil.append2digits(this.buf, seconds);
      this.buf.append(" GMT");
      return this.buf.toString();
   }

   public void doFormatCookieDate(StringBuilder buf, long date) {
      this.gc.setTimeInMillis(date);
      int dayOfWeek = this.gc.get(7);
      int dayOfMonth = this.gc.get(5);
      int month = this.gc.get(2);
      int fullYear = this.gc.get(1);
      int year = fullYear % 10000;
      int epochSec = (int)(date / 1000L % 86400L);
      int seconds = epochSec % 60;
      int epoch = epochSec / 60;
      int minutes = epoch % 60;
      int hours = epoch / 60;
      buf.append(DAYS[dayOfWeek]);
      buf.append(',');
      buf.append(' ');
      StringUtil.append2digits(buf, dayOfMonth);
      buf.append('-');
      buf.append(MONTHS[month]);
      buf.append('-');
      StringUtil.append2digits(buf, year / 100);
      StringUtil.append2digits(buf, year % 100);
      buf.append(' ');
      StringUtil.append2digits(buf, hours);
      buf.append(':');
      StringUtil.append2digits(buf, minutes);
      buf.append(':');
      StringUtil.append2digits(buf, seconds);
      buf.append(" GMT");
   }

   static {
      __GMT.setID("GMT");
   }
}
