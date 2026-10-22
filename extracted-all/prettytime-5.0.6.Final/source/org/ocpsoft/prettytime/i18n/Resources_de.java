package org.ocpsoft.prettytime.i18n;

import java.util.ListResourceBundle;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;
import org.ocpsoft.prettytime.impl.TimeFormatProvider;

public class Resources_de extends ListResourceBundle implements TimeFormatProvider {
   private static final Object[][] OBJECTS = new Object[][]{
      {"CenturyPattern", "%n %u"},
      {"CenturyFuturePrefix", "in "},
      {"CenturyFutureSuffix", ""},
      {"CenturyPastPrefix", "vor "},
      {"CenturyPastSuffix", ""},
      {"CenturySingularName", "Jahrhundert"},
      {"CenturyPluralName", "Jahrhunderte"},
      {"DayPattern", "%n %u"},
      {"DayFuturePrefix", "in "},
      {"DayFutureSuffix", ""},
      {"DayPastPrefix", "vor "},
      {"DayPastSuffix", ""},
      {"DaySingularName", "Tag"},
      {"DayPluralName", "Tage"},
      {"DecadePattern", "%n %u"},
      {"DecadeFuturePrefix", "in "},
      {"DecadeFutureSuffix", ""},
      {"DecadePastPrefix", "vor "},
      {"DecadePastSuffix", ""},
      {"DecadeSingularName", "Jahrzehnt"},
      {"DecadePluralName", "Jahrzehnte"},
      {"HourPattern", "%n %u"},
      {"HourFuturePrefix", "in "},
      {"HourFutureSuffix", ""},
      {"HourPastPrefix", "vor "},
      {"HourPastSuffix", ""},
      {"HourSingularName", "Stunde"},
      {"HourPluralName", "Stunden"},
      {"JustNowPattern", "%u"},
      {"JustNowFuturePrefix", "Jetzt"},
      {"JustNowFutureSuffix", ""},
      {"JustNowPastPrefix", "gerade eben"},
      {"JustNowPastSuffix", ""},
      {"JustNowSingularName", ""},
      {"JustNowPluralName", ""},
      {"MillenniumPattern", "%n %u"},
      {"MillenniumFuturePrefix", "in "},
      {"MillenniumFutureSuffix", ""},
      {"MillenniumPastPrefix", "vor "},
      {"MillenniumPastSuffix", ""},
      {"MillenniumSingularName", "Jahrtausend"},
      {"MillenniumPluralName", "Jahrtausende"},
      {"MillisecondPattern", "%n %u"},
      {"MillisecondFuturePrefix", "in "},
      {"MillisecondFutureSuffix", ""},
      {"MillisecondPastPrefix", "vor "},
      {"MillisecondPastSuffix", ""},
      {"MillisecondSingularName", "Millisekunde"},
      {"MillisecondPluralName", "Millisekunden"},
      {"MinutePattern", "%n %u"},
      {"MinuteFuturePrefix", "in "},
      {"MinuteFutureSuffix", ""},
      {"MinutePastPrefix", "vor "},
      {"MinutePastSuffix", ""},
      {"MinuteSingularName", "Minute"},
      {"MinutePluralName", "Minuten"},
      {"MonthPattern", "%n %u"},
      {"MonthFuturePrefix", "in "},
      {"MonthFutureSuffix", ""},
      {"MonthPastPrefix", "vor "},
      {"MonthPastSuffix", ""},
      {"MonthSingularName", "Monat"},
      {"MonthPluralName", "Monate"},
      {"SecondPattern", "%n %u"},
      {"SecondFuturePrefix", "in "},
      {"SecondFutureSuffix", ""},
      {"SecondPastPrefix", "vor "},
      {"SecondPastSuffix", ""},
      {"SecondSingularName", "Sekunde"},
      {"SecondPluralName", "Sekunden"},
      {"WeekPattern", "%n %u"},
      {"WeekFuturePrefix", "in "},
      {"WeekFutureSuffix", ""},
      {"WeekPastPrefix", "vor "},
      {"WeekPastSuffix", ""},
      {"WeekSingularName", "Woche"},
      {"WeekPluralName", "Wochen"},
      {"YearPattern", "%n %u"},
      {"YearFuturePrefix", "in "},
      {"YearFutureSuffix", ""},
      {"YearPastPrefix", "vor "},
      {"YearPastSuffix", ""},
      {"YearSingularName", "Jahr"},
      {"YearPluralName", "Jahre"},
      {"AbstractTimeUnitPattern", ""},
      {"AbstractTimeUnitFuturePrefix", ""},
      {"AbstractTimeUnitFutureSuffix", ""},
      {"AbstractTimeUnitPastPrefix", ""},
      {"AbstractTimeUnitPastSuffix", ""},
      {"AbstractTimeUnitSingularName", ""},
      {"AbstractTimeUnitPluralName", ""}
   };

   @Override
   protected Object[][] getContents() {
      return OBJECTS;
   }

   @Override
   public TimeFormat getFormatFor(TimeUnit t) {
      return new Resources_de.DeTimeFormat(this, t);
   }

   private static class DeTimeFormat extends SimpleTimeFormat {
      private static final Map<String, String> unitsToAdjust = Stream.of("Jahrtausende", "Jahrhunderte", "Jahrzehnte", "Jahre", "Monate", "Tage")
         .collect(Collectors.toMap(Function.identity(), s -> s + "n"));
      private static final Pattern grammerReplacementPattern = Pattern.compile("\\b(" + String.join("|", unitsToAdjust.keySet()) + ")\\b");

      public DeTimeFormat(ResourceBundle bundle, TimeUnit unit) {
         String prefix = unit.getClass().getSimpleName();
         this.setPattern(bundle.getString(prefix + "Pattern"));
         this.setFuturePrefix(bundle.getString(prefix + "FuturePrefix"));
         this.setFutureSuffix(bundle.getString(prefix + "FutureSuffix"));
         this.setPastPrefix(bundle.getString(prefix + "PastPrefix"));
         this.setPastSuffix(bundle.getString(prefix + "PastSuffix"));
         this.setSingularName(bundle.getString(prefix + "SingularName"));
         this.setPluralName(bundle.getString(prefix + "PluralName"));
      }

      @Override
      public String decorate(Duration duration, String time) {
         return super.decorate(duration, this.adjustGrammar(time));
      }

      @Override
      public String decorateUnrounded(Duration duration, String time) {
         return super.decorateUnrounded(duration, this.adjustGrammar(time));
      }

      private String adjustGrammar(String time) {
         Matcher matcher = grammerReplacementPattern.matcher(time);
         StringBuffer sb = new StringBuffer();

         while (matcher.find()) {
            matcher.appendReplacement(sb, unitsToAdjust.get(matcher.group(1)));
         }

         matcher.appendTail(sb);
         return sb.toString();
      }
   }
}
