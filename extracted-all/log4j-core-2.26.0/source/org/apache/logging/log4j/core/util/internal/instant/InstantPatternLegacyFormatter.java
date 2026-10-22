package org.apache.logging.log4j.core.util.internal.instant;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Supplier;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.apache.logging.log4j.core.util.datetime.FixedDateFormat;
import org.apache.logging.log4j.util.BiConsumer;

final class InstantPatternLegacyFormatter implements InstantPatternFormatter {
   private final ChronoUnit precision;
   private final String pattern;
   private final Locale locale;
   private final TimeZone timeZone;
   private final BiConsumer<StringBuilder, Instant> formatter;

   InstantPatternLegacyFormatter(final String pattern, final Locale locale, final TimeZone timeZone) {
      this.precision = new InstantPatternDynamicFormatter(pattern, locale, timeZone).getPrecision();
      this.pattern = pattern;
      this.locale = locale;
      this.timeZone = timeZone;
      this.formatter = createFormatter(pattern, locale, timeZone);
   }

   private static BiConsumer<StringBuilder, Instant> createFormatter(final String pattern, final Locale locale, final TimeZone timeZone) {
      FixedDateFormat fixedFormatter = FixedDateFormat.createIfSupported(pattern, timeZone.getID());
      return fixedFormatter != null ? adaptFixedFormatter(fixedFormatter) : createFastFormatter(pattern, locale, timeZone);
   }

   private static BiConsumer<StringBuilder, Instant> adaptFixedFormatter(final FixedDateFormat formatter) {
      Supplier<char[]> charBufferSupplier = memoryEfficientInstanceSupplier(() -> new char[formatter.getLength() << 1]);
      return (buffer, instant) -> {
         char[] charBuffer = charBufferSupplier.get();
         int length = formatter.formatInstant(instant, charBuffer, 0);
         buffer.append(charBuffer, 0, length);
      };
   }

   private static BiConsumer<StringBuilder, Instant> createFastFormatter(final String pattern, final Locale locale, final TimeZone timeZone) {
      FastDateFormat formatter = FastDateFormat.getInstance(pattern, timeZone, locale);
      Supplier<Calendar> calendarSupplier = memoryEfficientInstanceSupplier(() -> Calendar.getInstance(timeZone, locale));
      return (buffer, instant) -> {
         Calendar calendar = calendarSupplier.get();
         calendar.setTimeInMillis(instant.getEpochMillisecond());
         formatter.format(calendar, buffer);
      };
   }

   private static <V> Supplier<V> memoryEfficientInstanceSupplier(final Supplier<V> supplier) {
      return Constants.ENABLE_THREADLOCALS ? ThreadLocal.withInitial(supplier)::get : supplier;
   }

   @Override
   public ChronoUnit getPrecision() {
      return this.precision;
   }

   @Override
   public void formatTo(final StringBuilder buffer, final Instant instant) {
      Objects.requireNonNull(buffer, "buffer");
      Objects.requireNonNull(instant, "instant");
      this.formatter.accept(buffer, instant);
   }

   @Override
   public String getPattern() {
      return this.pattern;
   }

   @Override
   public Locale getLocale() {
      return this.locale;
   }

   @Override
   public TimeZone getTimeZone() {
      return this.timeZone;
   }
}
