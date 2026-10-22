package org.ocpsoft.prettytime.i18n;

import java.util.ListResourceBundle;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.impl.TimeFormatProvider;
import org.ocpsoft.prettytime.units.Decade;
import org.ocpsoft.prettytime.units.Millennium;

public class Resources_ja extends ListResourceBundle implements TimeFormatProvider {
   private static final Object[][] OBJECTS = new Object[][]{
      {"CenturyPattern", "%n%u"},
      {"CenturyFuturePrefix", "今から"},
      {"CenturyFutureSuffix", "後"},
      {"CenturyPastPrefix", ""},
      {"CenturyPastSuffix", "前"},
      {"CenturySingularName", "世紀"},
      {"CenturyPluralName", "世紀"},
      {"DayPattern", "%n%u"},
      {"DayFuturePrefix", "今から"},
      {"DayFutureSuffix", "後"},
      {"DayPastPrefix", ""},
      {"DayPastSuffix", "前"},
      {"DaySingularName", "日"},
      {"DayPluralName", "日"},
      {"DecadePattern", "%n%u"},
      {"DecadeFuturePrefix", "今から"},
      {"DecadeFutureSuffix", "後"},
      {"DecadePastPrefix", ""},
      {"DecadePastSuffix", "前"},
      {"DecadeSingularName", "年"},
      {"DecadePluralName", "年"},
      {"HourPattern", "%n%u"},
      {"HourFuturePrefix", "今から"},
      {"HourFutureSuffix", "後"},
      {"HourPastPrefix", ""},
      {"HourPastSuffix", "前"},
      {"HourSingularName", "時間"},
      {"HourPluralName", "時間"},
      {"JustNowPattern", "%u"},
      {"JustNowFuturePrefix", "今から"},
      {"JustNowFutureSuffix", "すぐ"},
      {"JustNowPastPrefix", ""},
      {"JustNowPastSuffix", "たった今"},
      {"JustNowSingularName", ""},
      {"JustNowPluralName", ""},
      {"MillenniumPattern", "%n%u"},
      {"MillenniumFuturePrefix", "今から"},
      {"MillenniumFutureSuffix", "後"},
      {"MillenniumPastPrefix", ""},
      {"MillenniumPastSuffix", "前"},
      {"MillenniumSingularName", "年"},
      {"MillenniumPluralName", "年"},
      {"MillisecondPattern", "%n%u"},
      {"MillisecondFuturePrefix", "今から"},
      {"MillisecondFutureSuffix", "後"},
      {"MillisecondPastPrefix", ""},
      {"MillisecondPastSuffix", "前"},
      {"MillisecondSingularName", "ミリ秒"},
      {"MillisecondPluralName", "ミリ秒"},
      {"MinutePattern", "%n%u"},
      {"MinuteFuturePrefix", "今から"},
      {"MinuteFutureSuffix", "後"},
      {"MinutePastPrefix", ""},
      {"MinutePastSuffix", "前"},
      {"MinuteSingularName", "分"},
      {"MinutePluralName", "分"},
      {"MonthPattern", "%n%u"},
      {"MonthFuturePrefix", "今から"},
      {"MonthFutureSuffix", "後"},
      {"MonthPastPrefix", ""},
      {"MonthPastSuffix", "前"},
      {"MonthSingularName", "ヶ月"},
      {"MonthPluralName", "ヶ月"},
      {"SecondPattern", "%n%u"},
      {"SecondFuturePrefix", "今から"},
      {"SecondFutureSuffix", "後"},
      {"SecondPastPrefix", ""},
      {"SecondPastSuffix", "前"},
      {"SecondSingularName", "秒"},
      {"SecondPluralName", "秒"},
      {"WeekPattern", "%n%u"},
      {"WeekFuturePrefix", "今から"},
      {"WeekFutureSuffix", "後"},
      {"WeekPastPrefix", ""},
      {"WeekPastSuffix", "前"},
      {"WeekSingularName", "週間"},
      {"WeekPluralName", "週間"},
      {"YearPattern", "%n%u"},
      {"YearFuturePrefix", "今から"},
      {"YearFutureSuffix", "後"},
      {"YearPastPrefix", ""},
      {"YearPastSuffix", "前"},
      {"YearSingularName", "年"},
      {"YearPluralName", "年"},
      {"AbstractTimeUnitPattern", ""},
      {"AbstractTimeUnitFuturePrefix", ""},
      {"AbstractTimeUnitFutureSuffix", ""},
      {"AbstractTimeUnitPastPrefix", ""},
      {"AbstractTimeUnitPastSuffix", ""},
      {"AbstractTimeUnitSingularName", ""},
      {"AbstractTimeUnitPluralName", ""}
   };
   private final Map<TimeUnit, TimeFormat> formatMap = new ConcurrentHashMap<>();

   @Override
   public Object[][] getContents() {
      return OBJECTS;
   }

   @Override
   public TimeFormat getFormatFor(TimeUnit t) {
      return this.formatMap.computeIfAbsent(t, unit -> new Resources_ja.JaTimeFormat(this, unit));
   }

   private static class JaTimeFormat implements TimeFormat {
      private static final String NEGATIVE = "-";
      public static final String SIGN = "%s";
      public static final String QUANTITY = "%n";
      public static final String UNIT = "%u";
      private String singularName = "";
      private String pluralName = "";
      private String futureSingularName = "";
      private String futurePluralName = "";
      private String pastSingularName = "";
      private String pastPluralName = "";
      private String pattern = "";
      private String futurePrefix = "";
      private String futureSuffix = "";
      private String pastPrefix = "";
      private String pastSuffix = "";
      private int roundingTolerance = 50;

      public JaTimeFormat(ResourceBundle bundle, TimeUnit unit) {
         this.setPattern(bundle.getString(this.getUnitName(unit) + "Pattern"));
         this.setFuturePrefix(bundle.getString(this.getUnitName(unit) + "FuturePrefix"));
         this.setFutureSuffix(bundle.getString(this.getUnitName(unit) + "FutureSuffix"));
         this.setPastPrefix(bundle.getString(this.getUnitName(unit) + "PastPrefix"));
         this.setPastSuffix(bundle.getString(this.getUnitName(unit) + "PastSuffix"));
         this.setSingularName(bundle.getString(this.getUnitName(unit) + "SingularName"));
         this.setPluralName(bundle.getString(this.getUnitName(unit) + "PluralName"));

         try {
            this.setFuturePluralName(bundle.getString(this.getUnitName(unit) + "FuturePluralName"));
         } catch (Exception var7) {
         }

         try {
            this.setFutureSingularName(bundle.getString(this.getUnitName(unit) + "FutureSingularName"));
         } catch (Exception var6) {
         }

         try {
            this.setPastPluralName(bundle.getString(this.getUnitName(unit) + "PastPluralName"));
         } catch (Exception var5) {
         }

         try {
            this.setPastSingularName(bundle.getString(this.getUnitName(unit) + "PastSingularName"));
         } catch (Exception var4) {
         }
      }

      private String getUnitName(TimeUnit unit) {
         return unit.getClass().getSimpleName();
      }

      @Override
      public String format(Duration duration) {
         return this.format(duration, true);
      }

      @Override
      public String formatUnrounded(Duration duration) {
         return this.format(duration, false);
      }

      private String format(Duration duration, boolean round) {
         String sign = this.getSign(duration);
         String unit = this.getGramaticallyCorrectName(duration, round);
         long quantity = this.getQuantity(duration, round);
         if (duration.getUnit() instanceof Decade) {
            quantity *= 10L;
         }

         if (duration.getUnit() instanceof Millennium) {
            quantity *= 1000L;
         }

         return this.applyPattern(sign, unit, quantity);
      }

      private String applyPattern(String sign, String unit, long quantity) {
         String result = this.getPattern(quantity).replaceAll("%s", sign);
         result = result.replaceAll("%n", String.valueOf(quantity));
         return result.replaceAll("%u", unit);
      }

      protected String getPattern(long quantity) {
         return this.pattern;
      }

      protected long getQuantity(Duration duration, boolean round) {
         return Math.abs(round ? duration.getQuantityRounded(this.roundingTolerance) : duration.getQuantity());
      }

      protected String getGramaticallyCorrectName(Duration d, boolean round) {
         String result = this.getSingularName(d);
         if (Math.abs(this.getQuantity(d, round)) == 0L || Math.abs(this.getQuantity(d, round)) > 1L) {
            result = this.getPluralName(d);
         }

         return result;
      }

      private String getSign(Duration d) {
         return d.getQuantity() < 0L ? "-" : "";
      }

      private String getSingularName(Duration duration) {
         if (duration.isInFuture() && this.futureSingularName != null && this.futureSingularName.length() > 0) {
            return this.futureSingularName;
         } else {
            return duration.isInPast() && this.pastSingularName != null && this.pastSingularName.length() > 0 ? this.pastSingularName : this.singularName;
         }
      }

      private String getPluralName(Duration duration) {
         if (duration.isInFuture() && this.futurePluralName != null && this.futureSingularName.length() > 0) {
            return this.futurePluralName;
         } else {
            return duration.isInPast() && this.pastPluralName != null && this.pastSingularName.length() > 0 ? this.pastPluralName : this.pluralName;
         }
      }

      @Override
      public String decorate(Duration duration, String time) {
         StringBuilder result = new StringBuilder();
         if (duration.isInPast()) {
            result.append(this.pastPrefix).append(time).append(this.pastSuffix);
         } else {
            result.append(this.futurePrefix).append(time).append(this.futureSuffix);
         }

         return result.toString().replaceAll("\\s+", " ").trim();
      }

      @Override
      public String decorateUnrounded(Duration duration, String time) {
         return this.decorate(duration, time);
      }

      public Resources_ja.JaTimeFormat setPattern(String pattern) {
         this.pattern = pattern;
         return this;
      }

      public Resources_ja.JaTimeFormat setFuturePrefix(String futurePrefix) {
         this.futurePrefix = futurePrefix.trim();
         return this;
      }

      public Resources_ja.JaTimeFormat setFutureSuffix(String futureSuffix) {
         this.futureSuffix = futureSuffix.trim();
         return this;
      }

      public Resources_ja.JaTimeFormat setPastPrefix(String pastPrefix) {
         this.pastPrefix = pastPrefix.trim();
         return this;
      }

      public Resources_ja.JaTimeFormat setPastSuffix(String pastSuffix) {
         this.pastSuffix = pastSuffix.trim();
         return this;
      }

      public Resources_ja.JaTimeFormat setSingularName(String name) {
         this.singularName = name;
         return this;
      }

      public Resources_ja.JaTimeFormat setPluralName(String pluralName) {
         this.pluralName = pluralName;
         return this;
      }

      public Resources_ja.JaTimeFormat setFutureSingularName(String futureSingularName) {
         this.futureSingularName = futureSingularName;
         return this;
      }

      public Resources_ja.JaTimeFormat setFuturePluralName(String futurePluralName) {
         this.futurePluralName = futurePluralName;
         return this;
      }

      public Resources_ja.JaTimeFormat setPastSingularName(String pastSingularName) {
         this.pastSingularName = pastSingularName;
         return this;
      }

      public Resources_ja.JaTimeFormat setPastPluralName(String pastPluralName) {
         this.pastPluralName = pastPluralName;
         return this;
      }

      @Override
      public String toString() {
         return "JaTimeFormat [pattern="
            + this.pattern
            + ", futurePrefix="
            + this.futurePrefix
            + ", futureSuffix="
            + this.futureSuffix
            + ", pastPrefix="
            + this.pastPrefix
            + ", pastSuffix="
            + this.pastSuffix
            + ", roundingTolerance="
            + this.roundingTolerance
            + "]";
      }
   }
}
