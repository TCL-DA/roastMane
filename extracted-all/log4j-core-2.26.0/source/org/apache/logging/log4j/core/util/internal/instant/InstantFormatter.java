package org.apache.logging.log4j.core.util.internal.instant;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.apache.logging.log4j.core.time.Instant;

public interface InstantFormatter {
   ChronoUnit getPrecision();

   default String format(final Instant instant) {
      Objects.requireNonNull(instant, "instant");
      StringBuilder buffer = new StringBuilder();
      this.formatTo(buffer, instant);
      return buffer.toString();
   }

   void formatTo(StringBuilder buffer, Instant instant);
}
