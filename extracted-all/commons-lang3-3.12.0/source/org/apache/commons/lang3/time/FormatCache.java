package org.apache.commons.lang3.time;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.Validate;

abstract class FormatCache<F extends Format> {
   static final int NONE = -1;
   private final ConcurrentMap<FormatCache.ArrayKey, F> cInstanceCache = new ConcurrentHashMap<>(7);
   private static final ConcurrentMap<FormatCache.ArrayKey, String> cDateTimeInstanceCache = new ConcurrentHashMap<>(7);

   public F getInstance() {
      return this.getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
   }

   public F getInstance(String pattern, TimeZone timeZone, Locale locale) {
      Validate.notNull(pattern, "pattern");
      if (timeZone == null) {
         timeZone = TimeZone.getDefault();
      }

      locale = LocaleUtils.toLocale(locale);
      FormatCache.ArrayKey key = new FormatCache.ArrayKey(pattern, timeZone, locale);
      F format = this.cInstanceCache.get(key);
      if (format == null) {
         format = this.createInstance(pattern, timeZone, locale);
         F previousValue = this.cInstanceCache.putIfAbsent(key, format);
         if (previousValue != null) {
            format = previousValue;
         }
      }

      return format;
   }

   protected abstract F createInstance(String var1, TimeZone var2, Locale var3);

   private F getDateTimeInstance(Integer dateStyle, Integer timeStyle, TimeZone timeZone, Locale locale) {
      locale = LocaleUtils.toLocale(locale);
      String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
      return this.getInstance(pattern, timeZone, locale);
   }

   F getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
      return this.getDateTimeInstance(dateStyle, Integer.valueOf(timeStyle), timeZone, locale);
   }

   F getDateInstance(int dateStyle, TimeZone timeZone, Locale locale) {
      return this.getDateTimeInstance(dateStyle, null, timeZone, locale);
   }

   F getTimeInstance(int timeStyle, TimeZone timeZone, Locale locale) {
      return this.getDateTimeInstance(null, timeStyle, timeZone, locale);
   }

   static String getPatternForStyle(Integer dateStyle, Integer timeStyle, Locale locale) {
      Locale safeLocale = LocaleUtils.toLocale(locale);
      FormatCache.ArrayKey key = new FormatCache.ArrayKey(dateStyle, timeStyle, safeLocale);
      String pattern = cDateTimeInstanceCache.get(key);
      if (pattern == null) {
         try {
            DateFormat formatter;
            if (dateStyle == null) {
               formatter = DateFormat.getTimeInstance(timeStyle, safeLocale);
            } else if (timeStyle == null) {
               formatter = DateFormat.getDateInstance(dateStyle, safeLocale);
            } else {
               formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, safeLocale);
            }

            pattern = ((SimpleDateFormat)formatter).toPattern();
            String previous = cDateTimeInstanceCache.putIfAbsent(key, pattern);
            if (previous != null) {
               pattern = previous;
            }
         } catch (ClassCastException var8) {
            throw new IllegalArgumentException("No date time pattern for locale: " + safeLocale);
         }
      }

      return pattern;
   }

   private static final class ArrayKey {
      private final Object[] keys;
      private final int hashCode;

      private static int computeHashCode(Object[] keys) {
         int prime = 31;
         int result = 1;
         return 31 * result + Arrays.hashCode(keys);
      }

      ArrayKey(Object... keys) {
         this.keys = keys;
         this.hashCode = computeHashCode(keys);
      }

      @Override
      public int hashCode() {
         return this.hashCode;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            FormatCache.ArrayKey other = (FormatCache.ArrayKey)obj;
            return Arrays.deepEquals(this.keys, other.keys);
         }
      }
   }
}
