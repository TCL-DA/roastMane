package org.ocpsoft.prettytime.i18n;

import java.util.ListResourceBundle;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.impl.TimeFormatProvider;
import org.ocpsoft.prettytime.units.Century;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Decade;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millennium;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Second;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

public class Resources_uk extends ListResourceBundle implements TimeFormatProvider {
   private static final Object[][] OBJECTS = new Object[0][0];
   private static final int tolerance = 50;
   private static final int slavicPluralForms = 3;

   @Override
   public Object[][] getContents() {
      return OBJECTS;
   }

   @Override
   public TimeFormat getFormatFor(TimeUnit t) {
      if (t instanceof JustNow) {
         return new TimeFormat() {
            @Override
            public String format(Duration duration) {
               return this.performFormat(duration);
            }

            @Override
            public String formatUnrounded(Duration duration) {
               return this.performFormat(duration);
            }

            private String performFormat(Duration duration) {
               if (duration.isInFuture()) {
                  return "зараз";
               } else {
                  return duration.isInPast() ? "щойно" : null;
               }
            }

            @Override
            public String decorate(Duration duration, String time) {
               return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
               return time;
            }
         };
      } else if (t instanceof Century) {
         return new Resources_uk.TimeFormatAided("століття", "століття", "столітть");
      } else if (t instanceof Day) {
         return new Resources_uk.TimeFormatAided("день", "дні", "днів");
      } else if (t instanceof Decade) {
         return new Resources_uk.TimeFormatAided("десятиліття", "десятиліття", "десятиліть");
      } else if (t instanceof Hour) {
         return new Resources_uk.TimeFormatAided("годину", "години", "годин");
      } else if (t instanceof Millennium) {
         return new Resources_uk.TimeFormatAided("тисячоліття", "тисячоліття", "тисячоліть");
      } else if (t instanceof Millisecond) {
         return new Resources_uk.TimeFormatAided("мілісекунду", "мілісекунди", "мілісекунд");
      } else if (t instanceof Minute) {
         return new Resources_uk.TimeFormatAided("хвилину", "хвилини", "хвилин");
      } else if (t instanceof Month) {
         return new Resources_uk.TimeFormatAided("місяць", "місяці", "місяців");
      } else if (t instanceof Second) {
         return new Resources_uk.TimeFormatAided("секунду", "секунди", "секунд");
      } else if (t instanceof Week) {
         return new Resources_uk.TimeFormatAided("тиждень", "тижні", "тижнів");
      } else {
         return t instanceof Year ? new Resources_uk.TimeFormatAided("рік", "роки", "років") : null;
      }
   }

   private static class TimeFormatAided implements TimeFormat {
      private final String[] pluarls;

      public TimeFormatAided(String... plurals) {
         if (plurals.length != 3) {
            throw new IllegalArgumentException("Wrong plural forms number for slavic language!");
         } else {
            this.pluarls = plurals;
         }
      }

      @Override
      public String format(Duration duration) {
         long quantity = duration.getQuantityRounded(50);
         StringBuilder result = new StringBuilder();
         result.append(quantity);
         return result.toString();
      }

      @Override
      public String formatUnrounded(Duration duration) {
         long quantity = duration.getQuantity();
         StringBuilder result = new StringBuilder();
         result.append(quantity);
         return result.toString();
      }

      @Override
      public String decorate(Duration duration, String time) {
         return this.performDecoration(duration.isInPast(), duration.isInFuture(), duration.getQuantityRounded(50), time);
      }

      @Override
      public String decorateUnrounded(Duration duration, String time) {
         return this.performDecoration(duration.isInPast(), duration.isInFuture(), duration.getQuantity(), time);
      }

      private String performDecoration(boolean past, boolean future, long n, String time) {
         int pluralIdx = n % 10L == 1L && n % 100L != 11L ? 0 : (n % 10L < 2L || n % 10L > 4L || n % 100L >= 10L && n % 100L < 20L ? 2 : 1);
         if (pluralIdx > 3) {
            throw new IllegalStateException("Wrong plural index was calculated somehow for slavic language");
         } else {
            StringBuilder result = new StringBuilder();
            if (future) {
               result.append("через ");
            }

            result.append(time);
            result.append(' ');
            result.append(this.pluarls[pluralIdx]);
            if (past) {
               result.append(" тому");
            }

            return result.toString();
         }
      }
   }
}
