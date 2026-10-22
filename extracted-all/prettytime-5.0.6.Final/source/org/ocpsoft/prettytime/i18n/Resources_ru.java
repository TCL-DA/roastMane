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

public class Resources_ru extends ListResourceBundle implements TimeFormatProvider {
   private static final Object[][] OBJECTS = new Object[0][0];
   private static final int tolerance = 50;
   private static final int russianPluralForms = 3;

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
                  return "сейчас";
               } else {
                  return duration.isInPast() ? "только что" : null;
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
         return new Resources_ru.TimeFormatAided("век", "века", "веков");
      } else if (t instanceof Day) {
         return new Resources_ru.TimeFormatAided("день", "дня", "дней");
      } else if (t instanceof Decade) {
         return new Resources_ru.TimeFormatAided("десятилетие", "десятилетия", "десятилетий");
      } else if (t instanceof Hour) {
         return new Resources_ru.TimeFormatAided("час", "часа", "часов");
      } else if (t instanceof Millennium) {
         return new Resources_ru.TimeFormatAided("тысячелетие", "тысячелетия", "тысячелетий");
      } else if (t instanceof Millisecond) {
         return new Resources_ru.TimeFormatAided("миллисекунду", "миллисекунды", "миллисекунд");
      } else if (t instanceof Minute) {
         return new Resources_ru.TimeFormatAided("минуту", "минуты", "минут");
      } else if (t instanceof Month) {
         return new Resources_ru.TimeFormatAided("месяц", "месяца", "месяцев");
      } else if (t instanceof Second) {
         return new Resources_ru.TimeFormatAided("секунду", "секунды", "секунд");
      } else if (t instanceof Week) {
         return new Resources_ru.TimeFormatAided("неделю", "недели", "недель");
      } else {
         return t instanceof Year ? new Resources_ru.TimeFormatAided("год", "года", "лет") : null;
      }
   }

   private static class TimeFormatAided implements TimeFormat {
      private final String[] pluarls;

      public TimeFormatAided(String... plurals) {
         if (plurals.length != 3) {
            throw new IllegalArgumentException("Wrong plural forms number for russian language!");
         } else {
            this.pluarls = plurals;
         }
      }

      @Override
      public String format(Duration duration) {
         long quantity = duration.getQuantityRounded(50);
         return String.valueOf(quantity);
      }

      @Override
      public String formatUnrounded(Duration duration) {
         long quantity = Math.abs(duration.getQuantity());
         return String.valueOf(quantity);
      }

      @Override
      public String decorate(Duration duration, String time) {
         return this.performDecoration(duration.isInPast(), duration.isInFuture(), duration.getQuantityRounded(50), time);
      }

      @Override
      public String decorateUnrounded(Duration duration, String time) {
         return this.performDecoration(duration.isInPast(), duration.isInFuture(), Math.abs(duration.getQuantity()), time);
      }

      private String performDecoration(boolean past, boolean future, long n, String time) {
         int pluralIdx = n % 10L == 1L && n % 100L != 11L ? 0 : (n % 10L < 2L || n % 10L > 4L || n % 100L >= 10L && n % 100L < 20L ? 2 : 1);
         if (pluralIdx > 3) {
            throw new IllegalStateException("Wrong plural index was calculated somehow for russian language");
         } else {
            StringBuilder result = new StringBuilder();
            if (future) {
               result.append("через ");
            }

            result.append(time);
            result.append(' ');
            result.append(this.pluarls[pluralIdx]);
            if (past) {
               result.append(" назад");
            }

            return result.toString();
         }
      }
   }
}
