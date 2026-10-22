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

public class Resources_sk extends ListResourceBundle implements TimeFormatProvider {
   private static final Object[][] OBJECTS = new Object[][]{
      {"CenturyPattern", "%n %u"},
      {"CenturyFuturePrefix", "o "},
      {"CenturyFutureSuffix", ""},
      {"CenturyPastPrefix", "pred "},
      {"CenturyPastSuffix", ""},
      {"CenturySingularName", "storočie"},
      {"CenturyPluralName", "storočia"},
      {"CenturyPastSingularName", "storočím"},
      {"CenturyPastPluralName", "storočiami"},
      {"CenturyFutureSingularName", "storočí"},
      {"CenturyFuturePluralName", "storočia"},
      {"DayPattern", "%n %u"},
      {"DayFuturePrefix", "o "},
      {"DayFutureSuffix", ""},
      {"DayPastPrefix", "pred "},
      {"DayPastSuffix", ""},
      {"DaySingularName", "deň"},
      {"DayPluralName", "dni"},
      {"DecadePattern", "%n %u"},
      {"DecadeFuturePrefix", "o "},
      {"DecadeFutureSuffix", ""},
      {"DecadePastPrefix", "pred "},
      {"DecadePastSuffix", ""},
      {"DecadeSingularName", "desaťročie"},
      {"DecadePluralName", "desaťročia"},
      {"DecadePastSingularName", "desaťročím"},
      {"DecadePastPluralName", "desaťročiami"},
      {"DecadeFutureSingularName", "desaťročí"},
      {"DecadeFuturePluralName", "desaťročia"},
      {"HourPattern", "%n %u"},
      {"HourFuturePrefix", "o "},
      {"HourFutureSuffix", ""},
      {"HourPastPrefix", "pred"},
      {"HourPastSuffix", ""},
      {"HourSingularName", "hodina"},
      {"HourPluralName", "hodiny"},
      {"JustNowPattern", "%u"},
      {"JustNowFuturePrefix", ""},
      {"JustNowFutureSuffix", "o chvíľu"},
      {"JustNowPastPrefix", "pred chvíľou"},
      {"JustNowPastSuffix", ""},
      {"JustNowSingularName", ""},
      {"JustNowPluralName", ""},
      {"MillenniumPattern", "%n %u"},
      {"MillenniumFuturePrefix", "o "},
      {"MillenniumFutureSuffix", ""},
      {"MillenniumPastPrefix", "pred "},
      {"MillenniumPastSuffix", ""},
      {"MillenniumSingularName", "tísícročie"},
      {"MillenniumPluralName", "tisícročia"},
      {"MillisecondPattern", "%n %u"},
      {"MillisecondFuturePrefix", "o "},
      {"MillisecondFutureSuffix", ""},
      {"MillisecondPastPrefix", "pred "},
      {"MillisecondPastSuffix", ""},
      {"MillisecondSingularName", "milisekunda"},
      {"MillisecondPluralName", "milisekundy"},
      {"MillisecondPastSingularName", "milisekundou"},
      {"MillisecondPastPluralName", "milisekundami"},
      {"MillisecondFutureSingularName", "milisekundu"},
      {"MillisecondFuturePluralName", "milisekúnd"},
      {"MinutePattern", "%n %u"},
      {"MinuteFuturePrefix", "o "},
      {"MinuteFutureSuffix", ""},
      {"MinutePastPrefix", "pred "},
      {"MinutePastSuffix", ""},
      {"MinuteSingularName", "minúta"},
      {"MinutePluralName", "minúty"},
      {"MonthPattern", "%n %u"},
      {"MonthFuturePrefix", "o "},
      {"MonthFutureSuffix", ""},
      {"MonthPastPrefix", "pred "},
      {"MonthPastSuffix", ""},
      {"MonthSingularName", "mesiac"},
      {"MonthPluralName", "mesiace"},
      {"SecondPattern", "%n %u"},
      {"SecondFuturePrefix", "o "},
      {"SecondFutureSuffix", ""},
      {"SecondPastPrefix", "pred "},
      {"SecondPastSuffix", ""},
      {"SecondSingularName", "sekunda"},
      {"SecondPluralName", "sekundy"},
      {"WeekPattern", "%n %u"},
      {"WeekFuturePrefix", "o "},
      {"WeekFutureSuffix", ""},
      {"WeekPastPrefix", "pred "},
      {"WeekPastSuffix", ""},
      {"WeekSingularName", "týždeň"},
      {"WeekPluralName", "týždne"},
      {"YearPattern", "%n %u"},
      {"YearFuturePrefix", "o "},
      {"YearFutureSuffix", ""},
      {"YearPastPrefix", "pred "},
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
         return new Resources_sk.CsTimeFormatBuilder("Minute")
            .addFutureName("minútu", 1L)
            .addFutureName("minúty", 4L)
            .addFutureName("minút", Long.MAX_VALUE)
            .addPastName("minútou", 1L)
            .addPastName("minútami", Long.MAX_VALUE)
            .build(this);
      } else if (t instanceof Hour) {
         return new Resources_sk.CsTimeFormatBuilder("Hour")
            .addFutureName("hodinu", 1L)
            .addFutureName("hodiny", 4L)
            .addFutureName("hodín", Long.MAX_VALUE)
            .addPastName("hodinou", 1L)
            .addPastName("hodinami", Long.MAX_VALUE)
            .build(this);
      } else if (t instanceof Day) {
         return new Resources_sk.CsTimeFormatBuilder("Day")
            .addFutureName("deň", 1L)
            .addFutureName("dni", 4L)
            .addFutureName("dní", Long.MAX_VALUE)
            .addPastName("dňom", 1L)
            .addPastName("dňami", Long.MAX_VALUE)
            .build(this);
      } else if (t instanceof Week) {
         return new Resources_sk.CsTimeFormatBuilder("Week")
            .addFutureName("týždeň", 1L)
            .addFutureName("týždne", 4L)
            .addFutureName("týždňov", Long.MAX_VALUE)
            .addPastName("týždňom", 1L)
            .addPastName("týždňami", Long.MAX_VALUE)
            .build(this);
      } else if (t instanceof Month) {
         return new Resources_sk.CsTimeFormatBuilder("Month")
            .addFutureName("mesiac", 1L)
            .addFutureName("mesiace", 4L)
            .addFutureName("mesiacov", Long.MAX_VALUE)
            .addPastName("mesiacom", 1L)
            .addPastName("mesiacmi", Long.MAX_VALUE)
            .build(this);
      } else {
         return t instanceof Year
            ? new Resources_sk.CsTimeFormatBuilder("Year")
               .addFutureName("rok", 1L)
               .addFutureName("roky", 4L)
               .addFutureName("rokov", Long.MAX_VALUE)
               .addPastName("rokom", 1L)
               .addPastName("rokmi", Long.MAX_VALUE)
               .build(this)
            : null;
      }
   }

   private static class CsName implements Comparable<Resources_sk.CsName> {
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

      public int compareTo(Resources_sk.CsName o) {
         return this.threshold.compareTo(o.getThreshold());
      }
   }

   private static class CsTimeFormat extends SimpleTimeFormat {
      private final List<Resources_sk.CsName> futureNames = new ArrayList<>();
      private final List<Resources_sk.CsName> pastNames = new ArrayList<>();

      public CsTimeFormat(String resourceKeyPrefix, ResourceBundle bundle, Collection<Resources_sk.CsName> names) {
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

         for (Resources_sk.CsName name : names) {
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

      private String getGramaticallyCorrectName(long quantity, List<Resources_sk.CsName> names) {
         for (Resources_sk.CsName name : names) {
            if (name.getThreshold() >= quantity) {
               return name.get();
            }
         }

         throw new IllegalStateException("Invalid resource bundle configuration");
      }
   }

   private static class CsTimeFormatBuilder {
      private List<Resources_sk.CsName> names = new ArrayList<>();
      private String resourceKeyPrefix;

      CsTimeFormatBuilder(String resourceKeyPrefix) {
         this.resourceKeyPrefix = resourceKeyPrefix;
      }

      Resources_sk.CsTimeFormatBuilder addFutureName(String name, long limit) {
         return this.addName(true, name, limit);
      }

      Resources_sk.CsTimeFormatBuilder addPastName(String name, long limit) {
         return this.addName(false, name, limit);
      }

      private Resources_sk.CsTimeFormatBuilder addName(boolean isFuture, String name, long limit) {
         this.names.add(new Resources_sk.CsName(isFuture, Objects.requireNonNull(name), limit));
         return this;
      }

      Resources_sk.CsTimeFormat build(ResourceBundle bundle) {
         return new Resources_sk.CsTimeFormat(this.resourceKeyPrefix, bundle, this.names);
      }
   }
}
