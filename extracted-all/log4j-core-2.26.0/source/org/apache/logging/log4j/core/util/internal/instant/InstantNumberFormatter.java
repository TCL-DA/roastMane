package org.apache.logging.log4j.core.util.internal.instant;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.apache.logging.log4j.core.time.Instant;

public enum InstantNumberFormatter implements InstantFormatter {
   EPOCH_NANOS(ChronoUnit.NANOS, (instant, buffer) -> {
      long nanos = epochNanos(instant);
      buffer.append(nanos);
   }),
   EPOCH_MILLIS(ChronoUnit.NANOS, (instant, buffer) -> {
      long nanos = epochNanos(instant);
      buffer.append(nanos);
      buffer.insert(buffer.length() - 6, '.');
   }),
   EPOCH_MILLIS_ROUNDED(ChronoUnit.MILLIS, (instant, buffer) -> {
      long millis = instant.getEpochMillisecond();
      buffer.append(millis);
   }),
   EPOCH_MILLIS_NANOS(ChronoUnit.NANOS, (instant, buffer) -> {
      long nanos = epochNanos(instant);
      long fraction = nanos % 1000000L;
      buffer.append(fraction);
   }),
   EPOCH_SECONDS(ChronoUnit.NANOS, (instant, buffer) -> {
      long nanos = epochNanos(instant);
      buffer.append(nanos);
      buffer.insert(buffer.length() - 9, '.');
   }),
   EPOCH_SECONDS_ROUNDED(ChronoUnit.SECONDS, (instant, buffer) -> {
      long seconds = instant.getEpochSecond();
      buffer.append(seconds);
   }),
   EPOCH_SECONDS_NANOS(ChronoUnit.NANOS, (instant, buffer) -> {
      long secondsNanos = (long)instant.getNanoOfSecond();
      buffer.append(secondsNanos);
   });

   private final ChronoUnit precision;
   private final BiConsumer<Instant, StringBuilder> formatter;

   private static long epochNanos(final Instant instant) {
      long nanos = Math.multiplyExact(1000000000L, instant.getEpochSecond());
      return Math.addExact(nanos, (long)instant.getNanoOfSecond());
   }

   private InstantNumberFormatter(final ChronoUnit precision, final BiConsumer<Instant, StringBuilder> formatter) {
      this.precision = precision;
      this.formatter = formatter;
   }

   @Override
   public ChronoUnit getPrecision() {
      return this.precision;
   }

   @Override
   public void formatTo(final StringBuilder buffer, final Instant instant) {
      Objects.requireNonNull(buffer, "buffer");
      Objects.requireNonNull(instant, "instant");
      this.formatter.accept(instant, buffer);
   }
}
