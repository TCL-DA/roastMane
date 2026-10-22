package com.segment.analytics.gson;

import com.google.gson.JsonParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

final class Iso8601Utils {
   static final String GMT_ID = "GMT";
   static final TimeZone TIMEZONE_Z = TimeZone.getTimeZone("GMT");

   static String format(Date date) {
      Calendar calendar = new GregorianCalendar(TIMEZONE_Z, Locale.US);
      calendar.setTime(date);
      int capacity = "yyyy-MM-ddThh:mm:ss.sssZ".length();
      StringBuilder formatted = new StringBuilder(capacity);
      padInt(formatted, calendar.get(1), "yyyy".length());
      formatted.append('-');
      padInt(formatted, calendar.get(2) + 1, "MM".length());
      formatted.append('-');
      padInt(formatted, calendar.get(5), "dd".length());
      formatted.append('T');
      padInt(formatted, calendar.get(11), "hh".length());
      formatted.append(':');
      padInt(formatted, calendar.get(12), "mm".length());
      formatted.append(':');
      padInt(formatted, calendar.get(13), "ss".length());
      formatted.append('.');
      padInt(formatted, calendar.get(14), "sss".length());
      formatted.append('Z');
      return formatted.toString();
   }

   static Date parse(String date) throws JsonParseException {
      try {
         int offset = 0;
         int var17 = offset + 4;
         int year = parseInt(date, offset, var17);
         if (checkOffset(date, var17, '-')) {
            var17++;
         }

         offset = var17 + 2;
         int month = parseInt(date, var17, offset);
         if (checkOffset(date, offset, '-')) {
            offset++;
         }

         int var19 = offset + 2;
         int day = parseInt(date, offset, var19);
         int hour = 0;
         int minutes = 0;
         int seconds = 0;
         int milliseconds = 0;
         boolean hasT = checkOffset(date, var19, 'T');
         if (!hasT && date.length() <= var19) {
            Calendar calendar = new GregorianCalendar(year, month - 1, day);
            return calendar.getTime();
         } else {
            if (hasT) {
               int var31 = ++var19;
               offset = var19 + 2;
               hour = parseInt(date, var31, offset);
               if (checkOffset(date, offset, ':')) {
                  offset++;
               }

               var19 = offset + 2;
               minutes = parseInt(date, offset, var19);
               if (checkOffset(date, var19, ':')) {
                  var19++;
               }

               if (date.length() > var19) {
                  char c = date.charAt(var19);
                  if (c != 'Z' && c != '+' && c != '-') {
                     var31 = var19;
                     var19 += 2;
                     seconds = parseInt(date, var31, var19);
                     if (seconds > 59 && seconds < 63) {
                        seconds = 59;
                     }

                     if (checkOffset(date, var19, '.')) {
                        int endOffset = indexOfNonDigit(date, ++var19 + 1);
                        int parseEndOffset = Math.min(endOffset, var19 + 3);
                        int fraction = parseInt(date, var19, parseEndOffset);
                        milliseconds = (int)(Math.pow(10.0, (double)(3 - (parseEndOffset - var19))) * (double)fraction);
                        var19 = endOffset;
                     }
                  }
               }
            }

            if (date.length() <= var19) {
               throw new IllegalArgumentException("No time zone indicator");
            } else {
               char timezoneIndicator = date.charAt(var19);
               TimeZone timezone;
               if (timezoneIndicator == 'Z') {
                  timezone = TIMEZONE_Z;
               } else {
                  if (timezoneIndicator != '+' && timezoneIndicator != '-') {
                     throw new IndexOutOfBoundsException("Invalid time zone indicator '" + timezoneIndicator + "'");
                  }

                  String timezoneOffset = date.substring(var19);
                  if (!"+0000".equals(timezoneOffset) && !"+00:00".equals(timezoneOffset)) {
                     String timezoneId = "GMT" + timezoneOffset;
                     timezone = TimeZone.getTimeZone(timezoneId);
                     String act = timezone.getID();
                     if (!act.equals(timezoneId)) {
                        String cleaned = act.replace(":", "");
                        if (!cleaned.equals(timezoneId)) {
                           throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID());
                        }
                     }
                  } else {
                     timezone = TIMEZONE_Z;
                  }
               }

               Calendar calendar = new GregorianCalendar(timezone);
               calendar.setLenient(false);
               calendar.set(1, year);
               calendar.set(2, month - 1);
               calendar.set(5, day);
               calendar.set(11, hour);
               calendar.set(12, minutes);
               calendar.set(13, seconds);
               calendar.set(14, milliseconds);
               return calendar.getTime();
            }
         }
      } catch (IllegalArgumentException | IndexOutOfBoundsException var16) {
         throw new JsonParseException("Not an RFC 3339 date: " + date);
      }
   }

   private static boolean checkOffset(String value, int offset, char expected) {
      return offset < value.length() && value.charAt(offset) == expected;
   }

   private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
      if (beginIndex >= 0 && endIndex <= value.length() && beginIndex <= endIndex) {
         int i = beginIndex;
         int result = 0;
         if (beginIndex < endIndex) {
            i = beginIndex + 1;
            int digit = Character.digit(value.charAt(beginIndex), 10);
            if (digit < 0) {
               throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }

            result = -digit;
         }

         while (i < endIndex) {
            int digit = Character.digit(value.charAt(i++), 10);
            if (digit < 0) {
               throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }

            result *= 10;
            result -= digit;
         }

         return -result;
      } else {
         throw new NumberFormatException(value);
      }
   }

   private static void padInt(StringBuilder buffer, int value, int length) {
      String strValue = Integer.toString(value);

      for (int i = length - strValue.length(); i > 0; i--) {
         buffer.append('0');
      }

      buffer.append(strValue);
   }

   private static int indexOfNonDigit(String string, int offset) {
      for (int i = offset; i < string.length(); i++) {
         char c = string.charAt(i);
         if (c < '0' || c > '9') {
            return i;
         }
      }

      return string.length();
   }
}
