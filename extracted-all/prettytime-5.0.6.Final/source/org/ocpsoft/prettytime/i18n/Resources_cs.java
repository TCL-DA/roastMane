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
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

public class Resources_cs extends ListResourceBundle implements TimeFormatProvider {
   private static final Object[][] OBJECTS = new Object[][]{
      {"CenturyPattern", "%n %u"},
      {"CenturyFuturePrefix", "za "},
      {"CenturyFutureSuffix", ""},
      {"CenturyPastPrefix", "před "},
      {"CenturyPastSuffix", ""},
      {"CenturySingularName", "století"},
      {"CenturyPluralName", "století"},
      {"CenturyPastSingularName", "stoletím"},
      {"CenturyPastPluralName", "stoletími"},
      {"CenturyFutureSingularName", "století"},
      {"CenturyFuturePluralName", "století"},
      {"DayPattern", "%n %u"},
      {"DayFuturePrefix", "za "},
      {"DayFutureSuffix", ""},
      {"DayPastPrefix", "před "},
      {"DayPastSuffix", ""},
      {"DaySingularName", "den"},
      {"DayPluralName", "dny"},
      {"DecadePattern", "%n %u"},
      {"DecadeFuturePrefix", "za "},
      {"DecadeFutureSuffix", ""},
      {"DecadePastPrefix", "před "},
      {"DecadePastSuffix", ""},
      {"DecadeSingularName", "desetiletí"},
      {"DecadePluralName", "desetiletí"},
      {"DecadePastSingularName", "desetiletím"},
      {"DecadePastPluralName", "desetiletími"},
      {"DecadeFutureSingularName", "desetiletí"},
      {"DecadeFuturePluralName", "desetiletí"},
      {"HourPattern", "%n %u"},
      {"HourFuturePrefix", "za "},
      {"HourFutureSuffix", ""},
      {"HourPastPrefix", "před"},
      {"HourPastSuffix", ""},
      {"HourSingularName", "hodina"},
      {"HourPluralName", "hodiny"},
      {"JustNowPattern", "%u"},
      {"JustNowFuturePrefix", ""},
      {"JustNowFutureSuffix", "za chvíli"},
      {"JustNowPastPrefix", "před chvílí"},
      {"JustNowPastSuffix", ""},
      {"JustNowSingularName", ""},
      {"JustNowPluralName", ""},
      {"MillenniumPattern", "%n %u"},
      {"MillenniumFuturePrefix", "za "},
      {"MillenniumFutureSuffix", ""},
      {"MillenniumPastPrefix", "před "},
      {"MillenniumPastSuffix", ""},
      {"MillenniumSingularName", "tisíciletí"},
      {"MillenniumPluralName", "tisíciletí"},
      {"MillisecondPattern", "%n %u"},
      {"MillisecondFuturePrefix", "za "},
      {"MillisecondFutureSuffix", ""},
      {"MillisecondPastPrefix", "před "},
      {"MillisecondPastSuffix", ""},
      {"MillisecondSingularName", "milisekunda"},
      {"MillisecondPluralName", "milisekundy"},
      {"MillisecondPastSingularName", "milisekundou"},
      {"MillisecondPastPluralName", "milisekundami"},
      {"MillisecondFutureSingularName", "milisekundu"},
      {"MillisecondFuturePluralName", "milisekund"},
      {"MinutePattern", "%n %u"},
      {"MinuteFuturePrefix", "za "},
      {"MinuteFutureSuffix", ""},
      {"MinutePastPrefix", "před "},
      {"MinutePastSuffix", ""},
      {"MinuteSingularName", "minuta"},
      {"MinutePluralName", "minuty"},
      {"MonthPattern", "%n %u"},
      {"MonthFuturePrefix", "za "},
      {"MonthFutureSuffix", ""},
      {"MonthPastPrefix", "před "},
      {"MonthPastSuffix", ""},
      {"MonthSingularName", "měsíc"},
      {"MonthPluralName", "měsíce"},
      {"SecondPattern", "%n %u"},
      {"SecondFuturePrefix", "za "},
      {"SecondFutureSuffix", ""},
      {"SecondPastPrefix", "před "},
      {"SecondPastSuffix", ""},
      {"SecondSingularName", "sekunda"},
      {"SecondPluralName", "sekundy"},
      {"WeekPattern", "%n %u"},
      {"WeekFuturePrefix", "za "},
      {"WeekFutureSuffix", ""},
      {"WeekPastPrefix", "před "},
      {"WeekPastSuffix", ""},
      {"WeekSingularName", "týden"},
      {"WeekPluralName", "týdny"},
      {"YearPattern", "%n %u"},
      {"YearFuturePrefix", "za "},
      {"YearFutureSuffix", ""},
      {"YearPastPrefix", "před "},
      {"YearPastSuffix", ""},
      {"YearSingularName", "rok"},
      {"YearPluralName", "roky"},
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
         return new Resources_cs.CsTimeFormatBuilder("Minute")
            .addFutureName("minutu", 1L)
            .addFutureName("minuty", 4L)
            .addFutureName("minut", Long.MAX_VALUE)
            .addPastName("minutou", 1L)
            .addPastName("minutami", Long.MAX_VALUE)
            .build(this);
      } else if (t instanceof Hour) {
         return new Resources_cs.CsTimeFormatBuilder("Hour")
            .addFutureName("hodinu", 1L)
            .addFutureName("hodiny", 4L)
            .addFutureName("hodin", Long.MAX_VALUE)
            .addPastName("hodinou", 1L)
            .addPastName("hodinami", Long.MAX_VALUE)
            .build(this);
      } else if (t instanceof Day) {
         return new Resources_cs.CsTimeFormatBuilder("Day")
            .addFutureName("den", 1L)
            .addFutureName("dny", 4L)
            .addFutureName("dní", Long.MAX_VALUE)
            .addPastName("dnem", 1L)
            .addPastName("dny", Long.MAX_VALUE)
            .build(this);
      } else if (t instanceof Week) {
         return new Resources_cs.CsTimeFormatBuilder("Week")
            .addFutureName("týden", 1L)
            .addFutureName("týdny", 4L)
            .addFutureName("týdnů", Long.MAX_VALUE)
            .addPastName("týdnem", 1L)
            .addPastName("týdny", Long.MAX_VALUE)
            .build(this);
      } else if (t instanceof Month) {
         return new Resources_cs.CsTimeFormatBuilder("Month")
            .addFutureName("měsíc", 1L)
            .addFutureName("měsíce", 4L)
            .addFutureName("měsíců", Long.MAX_VALUE)
            .addPastName("měsícem", 1L)
            .addPastName("měsíci", Long.MAX_VALUE)
            .build(this);
      } else {
         return t instanceof Year
            ? new Resources_cs.CsTimeFormatBuilder("Year")
               .addFutureName("rok", 1L)
               .addFutureName("roky", 4L)
               .addFutureName("let", Long.MAX_VALUE)
               .addPastName("rokem", 1L)
               .addPastName("roky", Long.MAX_VALUE)
               .build(this)
            : null;
      }
   }

   private static class CsName implements Comparable<Resources_cs.CsName> {
      private final boolean isFuture;
      private final String value;
      private final Long threshold;

      public CsName(boolean isFuture, String value, Long threshold) {
         this.isFuture = isFuture;
         this.value = value;
         this.threshold = threshold;
      }

      public boolean isFuture() {
         return this.isFuture;
      }

      public String get() {
         return this.value;
      }

      public long getThreshold() {
         return this.threshold;
      }

      public int compareTo(Resources_cs.CsName o) {
         return this.threshold.compareTo(o.getThreshold());
      }
   }

   private static class CsTimeFormat extends SimpleTimeFormat {
      private final List<Resources_cs.CsName> futureNames = new ArrayList<>();
      private final List<Resources_cs.CsName> pastNames = new ArrayList<>();

      public CsTimeFormat(String resourceKeyPrefix, ResourceBundle bundle, Collection<Resources_cs.CsName> names) {
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

         for (Resources_cs.CsName name : names) {
            if (name.isFuture()) {
               this.futureNames.add(name);
            } else {
               this.pastNames.add(name);
            }
         }

         Collections.sort(this.futureNames);
         Collections.sort(this.pastNames);
      }

      @Override
      protected String getGramaticallyCorrectName(Duration d, boolean round) {
         long quantity = Math.abs(this.getQuantity(d, round));
         return d.isInFuture() ? this.getGramaticallyCorrectName(quantity, this.futureNames) : this.getGramaticallyCorrectName(quantity, this.pastNames);
      }

      private String getGramaticallyCorrectName(long quantity, List<Resources_cs.CsName> names) {
         for (Resources_cs.CsName name : names) {
            if (name.getThreshold() >= quantity) {
               return name.get();
            }
         }

         throw new IllegalStateException("Invalid resource bundle configuration");
      }
   }

   private static class CsTimeFormatBuilder {
      private final List<Resources_cs.CsName> names = new ArrayList<>();
      private final String resourceKeyPrefix;

      CsTimeFormatBuilder(String resourceKeyPrefix) {
         this.resourceKeyPrefix = resourceKeyPrefix;
      }

      Resources_cs.CsTimeFormatBuilder addFutureName(String name, long limit) {
         return this.addName(true, name, limit);
      }

      Resources_cs.CsTimeFormatBuilder addPastName(String name, long limit) {
         return this.addName(false, name, limit);
      }

      private Resources_cs.CsTimeFormatBuilder addName(boolean isFuture, String name, long limit) {
         this.names.add(new Resources_cs.CsName(isFuture, Objects.requireNonNull(name), limit));
         return this;
      }

      Resources_cs.CsTimeFormat build(ResourceBundle bundle) {
         return new Resources_cs.CsTimeFormat(this.resourceKeyPrefix, bundle, this.names);
      }
   }
}
