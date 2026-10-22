package org.ocpsoft.prettytime.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Objects;
import java.util.ResourceBundle;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;
import org.ocpsoft.prettytime.impl.TimeFormatProvider;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.Millennium;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

public class Resources_hr extends ListResourceBundle implements TimeFormatProvider {
   private static final Object[][] OBJECTS = new Object[][]{
      {"CenturyPattern", "%n %u"},
      {"CenturyFuturePrefix", "za "},
      {"CenturyFutureSuffix", ""},
      {"CenturyPastPrefix", ""},
      {"CenturyPastSuffix", " unatrag"},
      {"CenturySingularName", "stoljeće"},
      {"CenturyPluralName", "stoljeća"},
      {"DayPattern", "%n %u"},
      {"DayFuturePrefix", "za "},
      {"DayFutureSuffix", ""},
      {"DayPastPrefix", "prije "},
      {"DayPastSuffix", ""},
      {"DaySingularName", "dan"},
      {"DayPluralName", "dana"},
      {"DecadePattern", "%n %u"},
      {"DecadeFuturePrefix", "za "},
      {"DecadeFutureSuffix", ""},
      {"DecadePastPrefix", "prije "},
      {"DecadePastSuffix", ""},
      {"DecadeSingularName", "desetljeće"},
      {"DecadePluralName", "desetljeća"},
      {"HourPattern", "%n %u"},
      {"HourFuturePrefix", "za "},
      {"HourFutureSuffix", ""},
      {"HourPastPrefix", "prije "},
      {"HourPastSuffix", ""},
      {"HourSingularName", "sat"},
      {"HourPluralName", "sati"},
      {"JustNowPattern", "%u"},
      {"JustNowFuturePrefix", "za nekoliko trenutaka"},
      {"JustNowFutureSuffix", ""},
      {"JustNowPastPrefix", "prije nekoliko trenutaka"},
      {"JustNowPastSuffix", ""},
      {"JustNowSingularName", ""},
      {"JustNowPluralName", ""},
      {"MillenniumPattern", "%n %u"},
      {"MillenniumFuturePrefix", "za "},
      {"MillenniumFutureSuffix", ""},
      {"MillenniumPastPrefix", "prije "},
      {"MillenniumPastSuffix", ""},
      {"MillenniumSingularName", "tisućljeće"},
      {"MillenniumPluralName", "tisućljeća"},
      {"MillisecondPattern", "%n %u"},
      {"MillisecondFuturePrefix", "za "},
      {"MillisecondFutureSuffix", ""},
      {"MillisecondPastPrefix", "prije "},
      {"MillisecondPastSuffix", ""},
      {"MillisecondSingularName", "milisekunda"},
      {"MillisecondPluralName", "milisekunda"},
      {"MinutePattern", "%n %u"},
      {"MinuteFuturePrefix", "za "},
      {"MinuteFutureSuffix", ""},
      {"MinutePastPrefix", "prije "},
      {"MinutePastSuffix", ""},
      {"MinuteSingularName", "minuta"},
      {"MinutePluralName", "minuta"},
      {"MonthPattern", "%n %u"},
      {"MonthFuturePrefix", "za "},
      {"MonthFutureSuffix", ""},
      {"MonthPastPrefix", "prije "},
      {"MonthPastSuffix", ""},
      {"MonthSingularName", "mjesec"},
      {"MonthPluralName", "mjeseca"},
      {"SecondPattern", "%n %u"},
      {"SecondFuturePrefix", "za "},
      {"SecondFutureSuffix", ""},
      {"SecondPastPrefix", "prije "},
      {"SecondPastSuffix", ""},
      {"SecondSingularName", "sekunda"},
      {"SecondPluralName", "sekundi"},
      {"WeekPattern", "%n %u"},
      {"WeekFuturePrefix", "za "},
      {"WeekFutureSuffix", ""},
      {"WeekPastPrefix", "prije "},
      {"WeekPastSuffix", ""},
      {"WeekSingularName", "tjedan"},
      {"WeekPluralName", "tjedna"},
      {"YearPattern", "%n %u"},
      {"YearFuturePrefix", "za "},
      {"YearFutureSuffix", ""},
      {"YearPastPrefix", "prije "},
      {"YearPastSuffix", ""},
      {"YearSingularName", "godina"},
      {"YearPluralName", "godina"},
      {"AbstractTimeUnitPattern", ""},
      {"AbstractTimeUnitFuturePrefix", ""},
      {"AbstractTimeUnitFutureSuffix", ""},
      {"AbstractTimeUnitPastPrefix", ""},
      {"AbstractTimeUnitPastSuffix", ""},
      {"AbstractTimeUnitSingularName", ""},
      {"AbstractTimeUnitPluralName", ""}
   };

   @Override
   public Object[][] getContents() {
      return OBJECTS;
   }

   @Override
   public TimeFormat getFormatFor(TimeUnit t) {
      if (t instanceof Minute) {
         return new Resources_hr.HrTimeFormatBuilder("Minute").addNames("minutu", 1L).addNames("minute", 4L).addNames("minuta", Long.MAX_VALUE).build(this);
      } else if (t instanceof Hour) {
         return new Resources_hr.HrTimeFormatBuilder("Hour").addNames("sat", 1L).addNames("sata", 4L).addNames("sati", Long.MAX_VALUE).build(this);
      } else if (t instanceof Day) {
         return new Resources_hr.HrTimeFormatBuilder("Day").addNames("dan", 1L).addNames("dana", 4L).addNames("dana", Long.MAX_VALUE).build(this);
      } else if (t instanceof Week) {
         return new Resources_hr.HrTimeFormatBuilder("Week").addNames("tjedan", 1L).addNames("tjedna", 4L).addNames("tjedana", Long.MAX_VALUE).build(this);
      } else if (t instanceof Month) {
         return new Resources_hr.HrTimeFormatBuilder("Month").addNames("mjesec", 1L).addNames("mjeseca", 4L).addNames("mjeseci", Long.MAX_VALUE).build(this);
      } else if (t instanceof Year) {
         return new Resources_hr.HrTimeFormatBuilder("Year").addNames("godinu", 1L).addNames("godine", 4L).addNames("godina", Long.MAX_VALUE).build(this);
      } else {
         return t instanceof Millennium
            ? new Resources_hr.HrTimeFormatBuilder("Millennium").addNames("tisućljeće", 1L).addNames("tisućljeća", Long.MAX_VALUE).build(this)
            : null;
      }
   }

   private static class HrName implements Comparable<Resources_hr.HrName> {
      private final boolean isFuture;
      private final Long threshold;
      private final String value;

      public HrName(boolean isFuture, String value, Long threshold) {
         this.isFuture = isFuture;
         this.value = value;
         this.threshold = threshold;
      }

      public int compareTo(Resources_hr.HrName o) {
         return this.threshold.compareTo(o.getThreshold());
      }

      public String get() {
         return this.value;
      }

      public long getThreshold() {
         return this.threshold;
      }

      public boolean isFuture() {
         return this.isFuture;
      }
   }

   private static class HrTimeFormat extends SimpleTimeFormat {
      private final List<Resources_hr.HrName> futureNames = new ArrayList<>();
      private final List<Resources_hr.HrName> pastNames = new ArrayList<>();

      public HrTimeFormat(String resourceKeyPrefix, ResourceBundle bundle, Collection<Resources_hr.HrName> names) {
         this.setPattern(bundle.getString(resourceKeyPrefix + "Pattern"));
         this.setFuturePrefix(bundle.getString(resourceKeyPrefix + "FuturePrefix"));
         this.setFutureSuffix(bundle.getString(resourceKeyPrefix + "FutureSuffix"));
         this.setPastPrefix(bundle.getString(resourceKeyPrefix + "PastPrefix"));
         this.setPastSuffix(bundle.getString(resourceKeyPrefix + "PastSuffix"));
         this.setSingularName(bundle.getString(resourceKeyPrefix + "SingularName"));
         this.setPluralName(bundle.getString(resourceKeyPrefix + "PluralName"));

         try {
            this.setFuturePluralName(bundle.getString(resourceKeyPrefix + "FuturePluralName"));
         } catch (Exception var9) {
         }

         try {
            this.setFutureSingularName(bundle.getString(resourceKeyPrefix + "FutureSingularName"));
         } catch (Exception var8) {
         }

         try {
            this.setPastPluralName(bundle.getString(resourceKeyPrefix + "PastPluralName"));
         } catch (Exception var7) {
         }

         try {
            this.setPastSingularName(bundle.getString(resourceKeyPrefix + "PastSingularName"));
         } catch (Exception var6) {
         }

         for (Resources_hr.HrName name : names) {
            if (name.isFuture()) {
               this.futureNames.add(name);
            } else {
               this.pastNames.add(name);
            }
         }

         Collections.sort(this.futureNames);
         Collections.sort(this.pastNames);
      }

      private String getGramaticallyCorrectName(long quantity, List<Resources_hr.HrName> names) {
         for (Resources_hr.HrName name : names) {
            if (name.getThreshold() >= quantity) {
               return name.get();
            }
         }

         throw new IllegalStateException("Invalid resource bundle configuration");
      }

      @Override
      protected String getGramaticallyCorrectName(Duration d, boolean round) {
         long quantity = Math.abs(this.getQuantity(d, round));
         return d.isInFuture() ? this.getGramaticallyCorrectName(quantity, this.futureNames) : this.getGramaticallyCorrectName(quantity, this.pastNames);
      }
   }

   private static class HrTimeFormatBuilder {
      private final List<Resources_hr.HrName> names = new ArrayList<>();
      private final String resourceKeyPrefix;

      HrTimeFormatBuilder(String resourceKeyPrefix) {
         this.resourceKeyPrefix = resourceKeyPrefix;
      }

      private Resources_hr.HrTimeFormatBuilder addName(boolean isFuture, String name, long limit) {
         this.names.add(new Resources_hr.HrName(isFuture, Objects.requireNonNull(name), limit));
         return this;
      }

      Resources_hr.HrTimeFormatBuilder addNames(String name, long limit) {
         return this.addName(true, name, limit).addName(false, name, limit);
      }

      Resources_hr.HrTimeFormat build(ResourceBundle bundle) {
         return new Resources_hr.HrTimeFormat(this.resourceKeyPrefix, bundle, this.names);
      }
   }
}
