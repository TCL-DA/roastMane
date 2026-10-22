package org.apache.logging.log4j.core.pattern;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.core.util.internal.instant.InstantFormatter;
import org.apache.logging.log4j.core.util.internal.instant.InstantNumberFormatter;
import org.apache.logging.log4j.core.util.internal.instant.InstantPatternFormatter;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Plugin(
   name = "DatePatternConverter",
   category = "Converter"
)
@ConverterKeys({"d", "date"})
@NullMarked
@PerformanceSensitive({"allocation"})
public final class DatePatternConverter extends LogEventPatternConverter implements ArrayPatternConverter {
   private static final String CLASS_NAME = DatePatternConverter.class.getSimpleName();
   private final InstantFormatter formatter;

   private DatePatternConverter(final String[] options) {
      super("Date", "date");
      this.formatter = createFormatter(options);
   }

   private static InstantFormatter createFormatter(final String[] options) {
      try {
         return createFormatterUnsafely(options);
      } catch (Exception var2) {
         logOptionReadFailure(options, var2, "failed for options: {}, falling back to the default instance");
         return InstantPatternFormatter.newBuilder().setPattern(NamedInstantPattern.DEFAULT.getPattern()).build();
      }
   }

   private static InstantFormatter createFormatterUnsafely(final String[] options) {
      String pattern = readPattern(options);
      TimeZone timeZone = readTimeZone(options);
      Locale locale = readLocale(options);
      if ("UNIX".equals(pattern)) {
         return InstantNumberFormatter.EPOCH_SECONDS_ROUNDED;
      } else {
         return (InstantFormatter)("UNIX_MILLIS".equals(pattern)
            ? InstantNumberFormatter.EPOCH_MILLIS_ROUNDED
            : InstantPatternFormatter.newBuilder().setPattern(pattern).setTimeZone(timeZone).setLocale(locale).build());
      }
   }

   private static String readPattern(final String[] options) {
      return options != null && options.length > 0 && options[0] != null ? decodeNamedPattern(options[0]) : NamedInstantPattern.DEFAULT.getPattern();
   }

   static String decodeNamedPattern(final String pattern) {
      try {
         NamedInstantPattern namedInstantPattern = NamedInstantPattern.valueOf(pattern);
         return InstantPatternFormatter.LEGACY_FORMATTERS_ENABLED ? namedInstantPattern.getLegacyPattern() : namedInstantPattern.getPattern();
      } catch (IllegalArgumentException var2) {
         return pattern;
      }
   }

   private static TimeZone readTimeZone(final String[] options) {
      try {
         if (options != null && options.length > 1 && options[1] != null) {
            return TimeZone.getTimeZone(options[1]);
         }
      } catch (Exception var2) {
         logOptionReadFailure(options, var2, "failed to read the time zone at index 1 of options: {}, falling back to the default time zone");
      }

      return TimeZone.getDefault();
   }

   private static Locale readLocale(final String[] options) {
      try {
         if (options != null && options.length > 2 && options[2] != null) {
            return Locale.forLanguageTag(options[2]);
         }
      } catch (Exception var2) {
         logOptionReadFailure(options, var2, "failed to read the locale at index 2 of options: {}, falling back to the default locale");
      }

      return Locale.getDefault();
   }

   private static void logOptionReadFailure(final String[] options, final Exception error, final String message) {
      if (LOGGER.isWarnEnabled()) {
         String quotedOptions = Arrays.stream(options).map(option -> '`' + option + '`').collect(Collectors.joining(", "));
         LOGGER.warn("[{}] " + message, CLASS_NAME, quotedOptions, error);
      }
   }

   public static DatePatternConverter newInstance(final String[] options) {
      return new DatePatternConverter(options);
   }

   @Deprecated
   public void format(final Date date, final StringBuilder buffer) {
      this.format(date.getTime(), buffer);
   }

   @Override
   public void format(final LogEvent event, final StringBuilder output) {
      this.format(event.getInstant(), output);
   }

   @Deprecated
   public void format(final long epochMillis, final StringBuilder buffer) {
      MutableInstant instant = new MutableInstant();
      instant.initFromEpochMilli(epochMillis, 0);
      this.format((Instant)instant, buffer);
   }

   @Deprecated
   public void format(final Instant instant, final StringBuilder buffer) {
      this.formatter.formatTo(buffer, instant);
   }

   @Override
   public void format(@Nullable final Object object, final StringBuilder buffer) {
      Objects.requireNonNull(buffer, "buffer");
      if (object != null) {
         if (object instanceof LogEvent) {
            this.format((LogEvent)object, buffer);
         } else if (object instanceof Date) {
            this.format((Date)object, buffer);
         } else if (object instanceof Instant) {
            this.format((Instant)object, buffer);
         } else if (object instanceof Long) {
            this.format(((Long)object).longValue(), buffer);
         }

         LOGGER.warn("[{}]: unsupported object type `{}`", CLASS_NAME, object.getClass().getCanonicalName());
      }
   }

   @Override
   public void format(final StringBuilder buffer, final Object... objects) {
      Objects.requireNonNull(buffer, "buffer");
      if (objects != null) {
         for (Object object : objects) {
            if (object instanceof Date) {
               this.format((Date)object, buffer);
               break;
            }
         }
      }
   }

   public String getPattern() {
      return this.formatter instanceof InstantPatternFormatter ? ((InstantPatternFormatter)this.formatter).getPattern() : null;
   }

   public TimeZone getTimeZone() {
      return this.formatter instanceof InstantPatternFormatter ? ((InstantPatternFormatter)this.formatter).getTimeZone() : null;
   }
}
