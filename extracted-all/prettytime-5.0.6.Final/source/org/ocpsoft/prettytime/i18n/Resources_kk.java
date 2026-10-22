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

public class Resources_kk extends ListResourceBundle implements TimeFormatProvider {
   private static final Object[][] OBJECTS = new Object[0][0];

   @Override
   protected Object[][] getContents() {
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
                  return "дәл қазір";
               } else {
                  return duration.isInPast() ? "жана ғана" : null;
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
         return new Resources_kk.KkTimeFormat("ғасыр", "ғасырдан");
      } else if (t instanceof Day) {
         return new Resources_kk.KkTimeFormat("күн", "күннен");
      } else if (t instanceof Decade) {
         return new Resources_kk.KkTimeFormat("онжылдық", "онжылдықтан");
      } else if (t instanceof Hour) {
         return new Resources_kk.KkTimeFormat("сағат", "сағаттан");
      } else if (t instanceof Millennium) {
         return new Resources_kk.KkTimeFormat("мыңжылдық", "мыңжылдықтан");
      } else if (t instanceof Millisecond) {
         return new Resources_kk.KkTimeFormat("миллисекунд", "миллисекундтан");
      } else if (t instanceof Minute) {
         return new Resources_kk.KkTimeFormat("минут", "минуттан");
      } else if (t instanceof Month) {
         return new Resources_kk.KkTimeFormat("ай", "айдан");
      } else if (t instanceof Second) {
         return new Resources_kk.KkTimeFormat("секунд", "секундтан");
      } else if (t instanceof Week) {
         return new Resources_kk.KkTimeFormat("апта", "аптадан");
      } else {
         return t instanceof Year ? new Resources_kk.KkTimeFormat("жыл", "жылдан") : null;
      }
   }

   private static class KkTimeFormat implements TimeFormat {
      private final int tolerance = 50;
      private final String[] forms;

      public KkTimeFormat(String... plurals) {
         if (plurals.length != 2) {
            throw new IllegalArgumentException("Future and past forms must be provided for kazakh language!");
         } else {
            this.forms = plurals;
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
         StringBuilder builder = new StringBuilder();
         int formIndex = past ? 0 : 1;
         builder.append(time);
         builder.append(' ');
         builder.append(this.forms[formIndex]);
         builder.append(' ');
         if (past) {
            builder.append("бұрын");
         }

         if (future) {
            builder.append("кейін");
         }

         return builder.toString();
      }
   }
}
