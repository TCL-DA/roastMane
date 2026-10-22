package org.eclipse.jetty.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateCache {
   public static final String DEFAULT_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
   private final String _formatString;
   private final DateTimeFormatter _tzFormat1;
   private final DateTimeFormatter _tzFormat2;
   private final ZoneId _zoneId;
   private volatile DateCache.TickHolder _tickHolder;

   public DateCache() {
      this("EEE MMM dd HH:mm:ss zzz yyyy");
   }

   public DateCache(String format) {
      this(format, null, TimeZone.getDefault());
   }

   public DateCache(String format, Locale l) {
      this(format, l, TimeZone.getDefault());
   }

   public DateCache(String format, Locale l, String tz) {
      this(format, l, TimeZone.getTimeZone(tz));
   }

   public DateCache(String format, Locale l, TimeZone tz) {
      this(format, l, tz, true);
   }

   public DateCache(String format, Locale l, TimeZone tz, boolean subSecondPrecision) {
      format = format.replaceFirst("S+", "SSS");
      this._formatString = format;
      this._zoneId = tz.toZoneId();
      String format1 = format;
      String format2 = null;
      boolean subSecond;
      if (subSecondPrecision) {
         int msIndex = format.indexOf("SSS");
         subSecond = msIndex >= 0;
         if (subSecond) {
            format1 = format.substring(0, msIndex);
            format2 = format.substring(msIndex + 3);
         }
      } else {
         subSecond = false;
         format1 = format.replace("SSS", "000");
      }

      this._tzFormat1 = this.createFormatter(format1, l, this._zoneId);
      this._tzFormat2 = subSecond ? this.createFormatter(format2, l, this._zoneId) : null;
   }

   private DateTimeFormatter createFormatter(String format, Locale locale, ZoneId zoneId) {
      return locale == null ? DateTimeFormatter.ofPattern(format).withZone(zoneId) : DateTimeFormatter.ofPattern(format, locale).withZone(zoneId);
   }

   public TimeZone getTimeZone() {
      return TimeZone.getTimeZone(this._zoneId);
   }

   public String format(Date inDate) {
      return this.format(inDate.getTime());
   }

   public String format(long inDate) {
      return this.formatTick(inDate).format(inDate);
   }

   protected String doFormat(long inDate, DateTimeFormatter formatter) {
      return formatter == null ? null : formatter.format(Instant.ofEpochMilli(inDate));
   }

   protected DateCache.Tick formatTick(long inDate) {
      long seconds = inDate / 1000L;
      DateCache.TickHolder holder = this._tickHolder;
      if (holder != null) {
         if (holder.tick1 != null && holder.tick1.getSeconds() == seconds) {
            return holder.tick1;
         }

         if (holder.tick2 != null && holder.tick2.getSeconds() == seconds) {
            return holder.tick2;
         }
      }

      String prefix = this.doFormat(inDate, this._tzFormat1);
      String suffix = this.doFormat(inDate, this._tzFormat2);
      DateCache.Tick tick = new DateCache.Tick(seconds, prefix, suffix);
      this._tickHolder = new DateCache.TickHolder(tick, holder == null ? null : holder.tick1);
      return tick;
   }

   public String getFormatString() {
      return this._formatString;
   }

   public static class Tick {
      private final long _seconds;
      private final String _prefix;
      private final String _suffix;

      public Tick(long seconds, String prefix, String suffix) {
         this._seconds = seconds;
         this._prefix = prefix;
         this._suffix = suffix;
      }

      public long getSeconds() {
         return this._seconds;
      }

      public String format(long inDate) {
         if (this._suffix == null) {
            return this._prefix;
         } else {
            long ms = inDate % 1000L;
            StringBuilder sb = new StringBuilder();
            sb.append(this._prefix);
            if (ms < 10L) {
               sb.append("00").append(ms);
            } else if (ms < 100L) {
               sb.append('0').append(ms);
            } else {
               sb.append(ms);
            }

            sb.append(this._suffix);
            return sb.toString();
         }
      }
   }

   private static class TickHolder {
      final DateCache.Tick tick1;
      final DateCache.Tick tick2;

      public TickHolder(DateCache.Tick t1, DateCache.Tick t2) {
         this.tick1 = t1;
         this.tick2 = t2;
      }
   }
}
