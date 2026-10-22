package org.ocpsoft.prettytime.format;

import java.util.Locale;
import java.util.regex.Pattern;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.LocaleAware;
import org.ocpsoft.prettytime.TimeFormat;

public class SimpleTimeFormat implements TimeFormat, LocaleAware<SimpleTimeFormat> {
   private static final String NEGATIVE = "-";
   public static final String SIGN = "%s";
   public static final String QUANTITY = "%n";
   public static final String UNIT = "%u";
   private static final Pattern PATTERN_MULTIPLE_WHITESPACES = Pattern.compile("\\s{2,}");
   private Locale locale;
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

   public SimpleTimeFormat setLocale(Locale locale) {
      this.locale = locale;
      return this;
   }

   @Override
   public String format(Duration duration) {
      return this.format(duration, true);
   }

   @Override
   public String formatUnrounded(Duration duration) {
      return this.format(duration, false);
   }

   @Override
   public String decorate(Duration duration, String time) {
      StringBuilder result = new StringBuilder();
      if (duration.isInPast()) {
         result.append(this.pastPrefix).append(" ").append(time).append(" ").append(this.pastSuffix);
      } else {
         result.append(this.futurePrefix).append(" ").append(time).append(" ").append(this.futureSuffix);
      }

      return PATTERN_MULTIPLE_WHITESPACES.matcher(result).replaceAll(" ").trim();
   }

   @Override
   public String decorateUnrounded(Duration duration, String time) {
      return this.decorate(duration, time);
   }

   private String format(Duration duration, boolean round) {
      String sign = this.getSign(duration);
      String unit = this.getGramaticallyCorrectName(duration, round);
      long quantity = this.getQuantity(duration, round);
      return this.applyPattern(sign, unit, quantity);
   }

   private String applyPattern(String sign, String unit, long quantity) {
      String result = this.getPattern(quantity).replace("%s", sign);
      String formatted = this.locale != null ? String.format(this.locale, "%d", quantity) : String.format("%d", quantity);
      result = result.replace("%n", formatted);
      return result.replace("%u", unit);
   }

   protected String getPattern(long quantity) {
      return this.pattern;
   }

   public String getPattern() {
      return this.pattern;
   }

   protected long getQuantity(Duration duration, boolean round) {
      return Math.abs(round ? duration.getQuantityRounded(this.roundingTolerance) : duration.getQuantity());
   }

   protected String getGramaticallyCorrectName(Duration d, boolean round) {
      String result = this.getSingularName(d);
      if (this.isPlural(d, round)) {
         result = this.getPluralName(d);
      }

      return result;
   }

   protected boolean isPlural(Duration d, boolean round) {
      long quantity = Math.abs(this.getQuantity(d, round));
      return quantity == 0L || quantity > 1L;
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

   public SimpleTimeFormat setPattern(String pattern) {
      this.pattern = pattern;
      return this;
   }

   public SimpleTimeFormat setFuturePrefix(String futurePrefix) {
      this.futurePrefix = futurePrefix.trim();
      return this;
   }

   public SimpleTimeFormat setFutureSuffix(String futureSuffix) {
      this.futureSuffix = futureSuffix.trim();
      return this;
   }

   public SimpleTimeFormat setPastPrefix(String pastPrefix) {
      this.pastPrefix = pastPrefix.trim();
      return this;
   }

   public SimpleTimeFormat setPastSuffix(String pastSuffix) {
      this.pastSuffix = pastSuffix.trim();
      return this;
   }

   public SimpleTimeFormat setRoundingTolerance(int roundingTolerance) {
      this.roundingTolerance = roundingTolerance;
      return this;
   }

   public SimpleTimeFormat setSingularName(String name) {
      this.singularName = name;
      return this;
   }

   public SimpleTimeFormat setPluralName(String pluralName) {
      this.pluralName = pluralName;
      return this;
   }

   public SimpleTimeFormat setFutureSingularName(String futureSingularName) {
      this.futureSingularName = futureSingularName;
      return this;
   }

   public SimpleTimeFormat setFuturePluralName(String futurePluralName) {
      this.futurePluralName = futurePluralName;
      return this;
   }

   public SimpleTimeFormat setPastSingularName(String pastSingularName) {
      this.pastSingularName = pastSingularName;
      return this;
   }

   public SimpleTimeFormat setPastPluralName(String pastPluralName) {
      this.pastPluralName = pastPluralName;
      return this;
   }

   @Override
   public String toString() {
      return "SimpleTimeFormat [pattern="
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
