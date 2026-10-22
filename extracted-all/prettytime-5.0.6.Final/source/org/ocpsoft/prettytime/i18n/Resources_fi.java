package org.ocpsoft.prettytime.i18n;

import java.util.ListResourceBundle;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;
import org.ocpsoft.prettytime.impl.TimeFormatProvider;
import org.ocpsoft.prettytime.units.Day;

public class Resources_fi extends ListResourceBundle implements TimeFormatProvider {
   private static final int tolerance = 50;
   private static Object[][] CONTENTS = new Object[][]{
      {"JustNowPattern", "%u"},
      {"JustNowPastSingularName", "hetki"},
      {"JustNowFutureSingularName", "hetken"},
      {"JustNowPastSuffix", "sitten"},
      {"JustNowFutureSuffix", "päästä"},
      {"MillisecondPattern", "%u"},
      {"MillisecondPluralPattern", "%n %u"},
      {"MillisecondPastSingularName", "millisekunti"},
      {"MillisecondPastPluralName", "millisekuntia"},
      {"MillisecondFutureSingularName", "millisekunnin"},
      {"MillisecondPastSuffix", "sitten"},
      {"MillisecondFutureSuffix", "päästä"},
      {"SecondPattern", "%u"},
      {"SecondPluralPattern", "%n %u"},
      {"SecondPastSingularName", "sekunti"},
      {"SecondPastPluralName", "sekuntia"},
      {"SecondFutureSingularName", "sekunnin"},
      {"SecondPastSuffix", "sitten"},
      {"SecondFutureSuffix", "päästä"},
      {"MinutePattern", "%u"},
      {"MinutePluralPattern", "%n %u"},
      {"MinutePastSingularName", "minuutti"},
      {"MinutePastPluralName", "minuuttia"},
      {"MinuteFutureSingularName", "minuutin"},
      {"MinutePastSuffix", "sitten"},
      {"MinuteFutureSuffix", "päästä"},
      {"HourPattern", "%u"},
      {"HourPluralPattern", "%n %u"},
      {"HourPastSingularName", "tunti"},
      {"HourPastPluralName", "tuntia"},
      {"HourFutureSingularName", "tunnin"},
      {"HourPastSuffix", "sitten"},
      {"HourFutureSuffix", "päästä"},
      {"DayPattern", "%u"},
      {"DayPluralPattern", "%n %u"},
      {"DayPastSingularName", "eilen"},
      {"DayPastPluralName", "päivää"},
      {"DayFutureSingularName", "huomenna"},
      {"DayFuturePluralName", "päivän"},
      {"DayPastSuffix", "sitten"},
      {"DayFutureSuffix", "päästä"},
      {"WeekPattern", "%u"},
      {"WeekPluralPattern", "%n %u"},
      {"WeekPastSingularName", "viikko"},
      {"WeekPastPluralName", "viikkoa"},
      {"WeekFutureSingularName", "viikon"},
      {"WeekFuturePluralName", "viikon"},
      {"WeekPastSuffix", "sitten"},
      {"WeekFutureSuffix", "päästä"},
      {"MonthPattern", "%u"},
      {"MonthPluralPattern", "%n %u"},
      {"MonthPastSingularName", "kuukausi"},
      {"MonthPastPluralName", "kuukautta"},
      {"MonthFutureSingularName", "kuukauden"},
      {"MonthPastSuffix", "sitten"},
      {"MonthFutureSuffix", "päästä"},
      {"YearPattern", "%u"},
      {"YearPluralPattern", "%n %u"},
      {"YearPastSingularName", "vuosi"},
      {"YearPastPluralName", "vuotta"},
      {"YearFutureSingularName", "vuoden"},
      {"YearPastSuffix", "sitten"},
      {"YearFutureSuffix", "päästä"},
      {"DecadePattern", "%u"},
      {"DecadePluralPattern", "%n %u"},
      {"DecadePastSingularName", "vuosikymmen"},
      {"DecadePastPluralName", "vuosikymmentä"},
      {"DecadeFutureSingularName", "vuosikymmenen"},
      {"DecadePastSuffix", "sitten"},
      {"DecadeFutureSuffix", "päästä"},
      {"CenturyPattern", "%u"},
      {"CenturyPluralPattern", "%n %u"},
      {"CenturyPastSingularName", "vuosisata"},
      {"CenturyPastPluralName", "vuosisataa"},
      {"CenturyFutureSingularName", "vuosisadan"},
      {"CenturyPastSuffix", "sitten"},
      {"CenturyFutureSuffix", "päästä"},
      {"MillenniumPattern", "%u"},
      {"MillenniumPluralPattern", "%n %u"},
      {"MillenniumPastSingularName", "vuosituhat"},
      {"MillenniumPastPluralName", "vuosituhatta"},
      {"MillenniumFutureSingularName", "vuosituhannen"},
      {"MillenniumPastSuffix", "sitten"},
      {"MillenniumFutureSuffix", "päästä"}
   };
   private final Map<TimeUnit, TimeFormat> formatMap = new ConcurrentHashMap<>();

   @Override
   public TimeFormat getFormatFor(TimeUnit t) {
      return this.formatMap.computeIfAbsent(t, unit -> new Resources_fi.FiTimeFormat(this, unit));
   }

   @Override
   protected Object[][] getContents() {
      return CONTENTS;
   }

   private static class FiTimeFormat extends SimpleTimeFormat {
      private final ResourceBundle bundle;
      private String pastName = "";
      private String futureName = "";
      private String pastPluralName = "";
      private String futurePluralName = "";
      private String pluralPattern = "";

      public FiTimeFormat(ResourceBundle rb, TimeUnit unit) {
         this.bundle = rb;
         if (this.bundle.containsKey(this.getUnitName(unit) + "PastSingularName")) {
            this.setPastName(this.bundle.getString(this.getUnitName(unit) + "PastSingularName"))
               .setFutureName(this.bundle.getString(this.getUnitName(unit) + "FutureSingularName"))
               .setPastPluralName(this.bundle.getString(this.getUnitName(unit) + "PastSingularName"))
               .setFuturePluralName(this.bundle.getString(this.getUnitName(unit) + "FutureSingularName"))
               .setPluralPattern(this.bundle.getString(this.getUnitName(unit) + "Pattern"));
            if (this.bundle.containsKey(this.getUnitName(unit) + "PastPluralName")) {
               this.setPastPluralName(this.bundle.getString(this.getUnitName(unit) + "PastPluralName"));
            }

            if (this.bundle.containsKey(this.getUnitName(unit) + "FuturePluralName")) {
               this.setFuturePluralName(this.bundle.getString(this.getUnitName(unit) + "FuturePluralName"));
            }

            if (this.bundle.containsKey(this.getUnitName(unit) + "PluralPattern")) {
               this.setPluralPattern(this.bundle.getString(this.getUnitName(unit) + "PluralPattern"));
            }

            this.setPattern(this.bundle.getString(this.getUnitName(unit) + "Pattern"))
               .setPastSuffix(this.bundle.getString(this.getUnitName(unit) + "PastSuffix"))
               .setFutureSuffix(this.bundle.getString(this.getUnitName(unit) + "FutureSuffix"))
               .setFuturePrefix("")
               .setPastPrefix("")
               .setSingularName("")
               .setPluralName("");
         }
      }

      public String getPastName() {
         return this.pastName;
      }

      public String getFutureName() {
         return this.futureName;
      }

      public String getPastPluralName() {
         return this.pastPluralName;
      }

      public String getFuturePluralName() {
         return this.futurePluralName;
      }

      public String getPluralPattern() {
         return this.pluralPattern;
      }

      public Resources_fi.FiTimeFormat setPastName(String pastName) {
         this.pastName = pastName;
         return this;
      }

      public Resources_fi.FiTimeFormat setFutureName(String futureName) {
         this.futureName = futureName;
         return this;
      }

      public Resources_fi.FiTimeFormat setPastPluralName(String pastName) {
         this.pastPluralName = pastName;
         return this;
      }

      public Resources_fi.FiTimeFormat setFuturePluralName(String futureName) {
         this.futurePluralName = futureName;
         return this;
      }

      public Resources_fi.FiTimeFormat setPluralPattern(String pattern) {
         this.pluralPattern = pattern;
         return this;
      }

      @Override
      protected String getGramaticallyCorrectName(Duration d, boolean round) {
         String result = d.isInPast() ? this.getPastName() : this.getFutureName();
         if (Math.abs(this.getQuantity(d, round)) == 0L || Math.abs(this.getQuantity(d, round)) > 1L) {
            result = d.isInPast() ? this.getPastPluralName() : this.getFuturePluralName();
         }

         return result;
      }

      @Override
      protected String getPattern(long quantity) {
         return Math.abs(quantity) == 1L ? this.getPattern() : this.getPluralPattern();
      }

      @Override
      public String decorate(Duration duration, String time) {
         String result = "";
         if (duration.getUnit() instanceof Day && Math.abs(duration.getQuantityRounded(50)) == 1L) {
            result = time;
         } else {
            result = super.decorate(duration, time);
         }

         return result;
      }

      private String getUnitName(TimeUnit unit) {
         return unit.getClass().getSimpleName();
      }
   }
}
