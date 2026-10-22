package com.cropster.util.text;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationFormat extends NumberFormat {
   private static final long serialVersionUID = -8176655309297047502L;
   private static final String FORMAT_PATTERN = "%2$02d:%3$02d";
   private static final String FORMAT_PATTERN_HOURS = "%1$d:";
   private static final String FORMAT_PATTERN_MILLIS = ".%4$03d";
   private static final String PARSE_PATTERN = "(\\d+):(?:(\\d{2}):)?(\\d{2})(?:\\.(\\d{3}))?+)(?:[^:\\.0-9].*)?";
   private static final Pattern NON_NEGATIVE_PATTERN = Pattern.compile("((\\d+):(?:(\\d{2}):)?(\\d{2})(?:\\.(\\d{3}))?+)(?:[^:\\.0-9].*)?");
   private static final Pattern PATTERN = Pattern.compile("((?:-)?(\\d+):(?:(\\d{2}):)?(\\d{2})(?:\\.(\\d{3}))?+)(?:[^:\\.0-9].*)?");
   private final boolean _allowNegative;
   private boolean _printMillis;

   public static DurationFormat getDurationInstance() {
      return new DurationFormat(false);
   }

   public static DurationFormat getDurationInstance(boolean allowNegative) {
      return new DurationFormat(allowNegative);
   }

   private DurationFormat(boolean allowNegative) {
      this._allowNegative = allowNegative;
   }

   public void setPrintMillis(boolean printMillis) {
      this._printMillis = printMillis;
   }

   public StringBuffer format(Number number, StringBuffer toAppendTo, FieldPosition pos) {
      long millis = Math.abs(number.longValue());
      long seconds = millis / 1000L;
      millis %= 1000L;
      long minutes = seconds / 60L;
      seconds %= 60L;
      long hours = minutes / 60L;
      minutes %= 60L;
      String pattern = "%2$02d:%3$02d";
      if (hours > 0L) {
         pattern = "%1$d:" + pattern;
      }

      if (this._printMillis && millis > 0L) {
         pattern = pattern + ".%4$03d";
      }

      if (number.longValue() < 0L) {
         if (!this._allowNegative) {
            throw new IllegalArgumentException("Negative values not allowed");
         }

         pattern = "-" + pattern;
      }

      String formattedString = String.format(pattern, hours, minutes, seconds, millis);
      return toAppendTo.append(formattedString);
   }

   @Override
   public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
      return this.format(Double.valueOf(number), toAppendTo, pos);
   }

   @Override
   public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
      return this.format(Long.valueOf(number), toAppendTo, pos);
   }

   @Override
   public Number parse(String source, ParsePosition parsePosition) {
      if (source != null) {
         String substring = source.substring(parsePosition.getIndex());
         Pattern pattern = this._allowNegative ? PATTERN : NON_NEGATIVE_PATTERN;
         Matcher matcher = pattern.matcher(substring);
         if (matcher.matches()) {
            long hours = 0L;
            long minutes = 0L;
            long seconds = 0L;
            long milliseconds = 0L;
            boolean negative = substring.startsWith("-");
            String minuteGroup = matcher.group(3);
            if (minuteGroup != null) {
               minutes = Long.valueOf(minuteGroup);
            }

            String leadingGroup = matcher.group(2);
            if (leadingGroup != null) {
               if (minuteGroup != null) {
                  hours = Long.valueOf(leadingGroup);
               } else {
                  minutes = Long.valueOf(leadingGroup);
               }
            }

            String secondGroup = matcher.group(4);
            if (secondGroup != null) {
               seconds = Long.valueOf(secondGroup);
            }

            String millisecondGroup = matcher.group(5);
            if (millisecondGroup != null) {
               milliseconds = Long.valueOf(millisecondGroup);
            }

            long millis = hours * 60L * 60L * 1000L + minutes * 60L * 1000L + seconds * 1000L + milliseconds;
            if (negative && this._allowNegative) {
               millis = -millis;
            }

            parsePosition.setIndex(parsePosition.getIndex() + matcher.group(1).length());
            return millis;
         }
      }

      return null;
   }
}
