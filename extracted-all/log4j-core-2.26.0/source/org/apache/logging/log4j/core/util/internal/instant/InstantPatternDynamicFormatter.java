package org.apache.logging.log4j.core.util.internal.instant;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.Nullable;

final class InstantPatternDynamicFormatter implements InstantPatternFormatter {
   static final ChronoUnit PRECISION_THRESHOLD = ChronoUnit.MINUTES;
   private final AtomicReference<InstantPatternDynamicFormatter.TimestampedFormatter> timestampedFormatterRef;

   InstantPatternDynamicFormatter(final String pattern, final Locale locale, final TimeZone timeZone) {
      InstantPatternDynamicFormatter.TimestampedFormatter timestampedFormatter = createTimestampedFormatter(pattern, locale, timeZone, null);
      this.timestampedFormatterRef = new AtomicReference<>(timestampedFormatter);
   }

   @Override
   public String getPattern() {
      return this.timestampedFormatterRef.get().formatter.getPattern();
   }

   @Override
   public Locale getLocale() {
      return this.timestampedFormatterRef.get().formatter.getLocale();
   }

   @Override
   public TimeZone getTimeZone() {
      return this.timestampedFormatterRef.get().formatter.getTimeZone();
   }

   @Override
   public ChronoUnit getPrecision() {
      return this.timestampedFormatterRef.get().formatter.getPrecision();
   }

   @Override
   public void formatTo(final StringBuilder buffer, final Instant instant) {
      Objects.requireNonNull(buffer, "buffer");
      Objects.requireNonNull(instant, "instant");
      this.getEffectiveFormatter(instant).formatTo(buffer, instant);
   }

   private InstantPatternFormatter getEffectiveFormatter(final Instant instant) {
      InstantPatternDynamicFormatter.TimestampedFormatter oldTimestampedFormatter = this.timestampedFormatterRef.get();
      long instantEpochMinutes = toEpochMinutes(instant);
      InstantPatternFormatter oldFormatter = oldTimestampedFormatter.formatter;
      if (oldTimestampedFormatter.instantEpochMinutes == instantEpochMinutes) {
         return oldFormatter;
      } else {
         InstantPatternDynamicFormatter.TimestampedFormatter newTimestampedFormatter = createTimestampedFormatter(
            oldFormatter.getPattern(), oldFormatter.getLocale(), oldFormatter.getTimeZone(), instant
         );
         this.timestampedFormatterRef.compareAndSet(oldTimestampedFormatter, newTimestampedFormatter);
         return newTimestampedFormatter.formatter;
      }
   }

   private static InstantPatternDynamicFormatter.TimestampedFormatter createTimestampedFormatter(
      final String pattern, final Locale locale, final TimeZone timeZone, @Nullable Instant creationInstant
   ) {
      if (creationInstant == null) {
         creationInstant = new MutableInstant();
         java.time.Instant currentInstant = java.time.Instant.now();
         ((MutableInstant)creationInstant).initFromEpochSecond(currentInstant.getEpochSecond(), creationInstant.getNanoOfSecond());
      }

      InstantPatternFormatter formatter = createFormatter(pattern, locale, timeZone, PRECISION_THRESHOLD, creationInstant);
      long creationInstantEpochMinutes = toEpochMinutes(creationInstant);
      return new InstantPatternDynamicFormatter.TimestampedFormatter(creationInstantEpochMinutes, formatter);
   }

   private static InstantPatternFormatter createFormatter(
      final String pattern, final Locale locale, final TimeZone timeZone, final ChronoUnit precisionThreshold, final Instant creationInstant
   ) {
      List<InstantPatternDynamicFormatter.PatternSequence> sequences = sequencePattern(pattern, precisionThreshold);
      final InstantPatternFormatter[] formatters = sequences.stream().map(sequence -> {
         InstantPatternFormatter formatter = sequence.createFormatter(locale, timeZone);
         boolean constant = sequence.isConstantForDurationOf(precisionThreshold);
         if (!constant) {
            return formatter;
         } else {
            StringBuilder buffer = new StringBuilder();
            formatter.formatTo(buffer, creationInstant);
            final String formattedInstant = buffer.toString();
            return new InstantPatternDynamicFormatter.AbstractFormatter(formatter.getPattern(), locale, timeZone, formatter.getPrecision()) {
               @Override
               public void formatTo(final StringBuilder buffer, final Instant instant) {
                  buffer.append(formattedInstant);
               }
            };
         }
      }).toArray(InstantPatternFormatter[]::new);
      switch (formatters.length) {
         case 0:
            return new InstantPatternDynamicFormatter.AbstractFormatter(pattern, locale, timeZone, ChronoUnit.FOREVER) {
               @Override
               public void formatTo(final StringBuilder buffer, final Instant instant) {
               }
            };
         case 1:
            return formatters[0];
         case 2:
            final InstantPatternFormatter first = formatters[0];
            final InstantPatternFormatter second = formatters[1];
            return new InstantPatternDynamicFormatter.AbstractFormatter(pattern, locale, timeZone, min(first.getPrecision(), second.getPrecision())) {
               @Override
               public void formatTo(StringBuilder buffer, Instant instant) {
                  first.formatTo(buffer, instant);
                  second.formatTo(buffer, instant);
               }
            };
         default:
            ChronoUnit precision = Stream.of(formatters).map(InstantFormatter::getPrecision).min(Comparator.comparing(ChronoUnit::getDuration)).get();
            return new InstantPatternDynamicFormatter.AbstractFormatter(pattern, locale, timeZone, precision) {
               @Override
               public void formatTo(final StringBuilder buffer, final Instant instant) {
                  for (int formatterIndex = 0; formatterIndex < formatters.length; formatterIndex++) {
                     InstantPatternFormatter formatter = formatters[formatterIndex];
                     formatter.formatTo(buffer, instant);
                  }
               }
            };
      }
   }

   private static ChronoUnit min(ChronoUnit left, ChronoUnit right) {
      return left.getDuration().compareTo(right.getDuration()) < 0 ? left : right;
   }

   static List<InstantPatternDynamicFormatter.PatternSequence> sequencePattern(final String pattern, final ChronoUnit precisionThreshold) {
      List<InstantPatternDynamicFormatter.PatternSequence> sequences = sequencePattern(pattern);
      return mergeFactories(sequences, precisionThreshold);
   }

   private static List<InstantPatternDynamicFormatter.PatternSequence> sequencePattern(final String pattern) {
      if (pattern.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<InstantPatternDynamicFormatter.PatternSequence> sequences = new ArrayList<>();
         int startIndex = 0;

         while (startIndex < pattern.length()) {
            char c = pattern.charAt(startIndex);
            boolean dynamic = isDynamicPatternLetter(c);
            if (dynamic) {
               int endIndex = startIndex + 1;

               while (endIndex < pattern.length() && pattern.charAt(endIndex) == c) {
                  endIndex++;
               }

               String sequenceContent = pattern.substring(startIndex, endIndex);
               InstantPatternDynamicFormatter.PatternSequence sequence;
               switch (c) {
                  case 'S':
                     sequence = new InstantPatternDynamicFormatter.SecondPatternSequence(0, "", sequenceContent.length());
                     break;
                  case 's':
                     sequence = new InstantPatternDynamicFormatter.SecondPatternSequence(sequenceContent.length(), "", 0);
                     break;
                  default:
                     sequence = new InstantPatternDynamicFormatter.DynamicPatternSequence(sequenceContent);
               }

               sequences.add(sequence);
               startIndex = endIndex;
            } else if (c != '\'') {
               InstantPatternDynamicFormatter.PatternSequence sequence = new InstantPatternDynamicFormatter.StaticPatternSequence("" + c);
               sequences.add(sequence);
               startIndex++;
            } else {
               int endIndex = startIndex + 1;

               while (true) {
                  if (endIndex < pattern.length()) {
                     if (pattern.charAt(endIndex) != '\'') {
                        endIndex++;
                        continue;
                     }

                     if (endIndex + 1 < pattern.length() && pattern.charAt(endIndex + 1) == '\'') {
                        endIndex += 2;
                        continue;
                     }
                  }

                  if (endIndex >= pattern.length()) {
                     endIndex = -1;
                  }

                  InstantPatternDynamicFormatter.PatternSequence sequence = getStaticPatternSequence(pattern, startIndex, endIndex);
                  sequences.add(sequence);
                  startIndex = endIndex + 1;
                  break;
               }
            }
         }

         return sequences;
      }
   }

   private static InstantPatternDynamicFormatter.PatternSequence getStaticPatternSequence(String pattern, int startIndex, int endIndex) {
      if (endIndex < 0) {
         String message = String.format("pattern ends with an incomplete string literal that started at index %d: `%s`", startIndex, pattern);
         throw new IllegalArgumentException(message);
      } else {
         String sequenceLiteral = startIndex + 1 == endIndex ? "'" : pattern.substring(startIndex + 1, endIndex).replace("''", "'");
         return new InstantPatternDynamicFormatter.StaticPatternSequence(sequenceLiteral);
      }
   }

   private static boolean isDynamicPatternLetter(final char c) {
      return "GuyDMLdgQqYwWEecFaBhKkHmsSAnNVvzOXxZ".indexOf(c) >= 0;
   }

   private static List<InstantPatternDynamicFormatter.PatternSequence> mergeFactories(
      final List<InstantPatternDynamicFormatter.PatternSequence> sequences, final ChronoUnit precisionThreshold
   ) {
      if (sequences.size() < 2) {
         return sequences;
      } else {
         List<InstantPatternDynamicFormatter.PatternSequence> mergedSequences = new ArrayList<>();
         InstantPatternDynamicFormatter.PatternSequence currentFactory = sequences.get(0);

         for (int i = 1; i < sequences.size(); i++) {
            InstantPatternDynamicFormatter.PatternSequence nextFactory = sequences.get(i);
            InstantPatternDynamicFormatter.PatternSequence mergedFactory = currentFactory.tryMerge(nextFactory, precisionThreshold);
            if (mergedFactory == null) {
               mergedSequences.add(currentFactory);
               currentFactory = nextFactory;
            } else {
               currentFactory = mergedFactory;
            }
         }

         mergedSequences.add(currentFactory);
         return mergedSequences;
      }
   }

   private static long toEpochMinutes(final Instant instant) {
      return instant.getEpochSecond() / 60L;
   }

   private static TemporalAccessor toTemporalAccessor(final Instant instant) {
      return (TemporalAccessor)(instant instanceof TemporalAccessor
         ? (TemporalAccessor)instant
         : java.time.Instant.ofEpochSecond(instant.getEpochSecond(), (long)instant.getNanoOfSecond()));
   }

   private abstract static class AbstractFormatter implements InstantPatternFormatter {
      private final String pattern;
      private final Locale locale;
      private final TimeZone timeZone;
      private final ChronoUnit precision;

      AbstractFormatter(final String pattern, final Locale locale, final TimeZone timeZone, final ChronoUnit precision) {
         this.pattern = pattern;
         this.locale = locale;
         this.timeZone = timeZone;
         this.precision = precision;
      }

      @Override
      public ChronoUnit getPrecision() {
         return this.precision;
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

   static final class DynamicPatternSequence extends InstantPatternDynamicFormatter.PatternSequence {
      DynamicPatternSequence(final String singlePattern) {
         this(singlePattern, patternPrecision(singlePattern));
      }

      DynamicPatternSequence(final String pattern, final ChronoUnit precision) {
         super(pattern, precision);
      }

      @Override
      InstantPatternFormatter createFormatter(final Locale locale, final TimeZone timeZone) {
         final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.pattern, locale).withZone(timeZone.toZoneId());
         return new InstantPatternDynamicFormatter.AbstractFormatter(this.pattern, locale, timeZone, this.precision) {
            @Override
            public void formatTo(final StringBuilder buffer, final Instant instant) {
               TemporalAccessor instantAccessor = InstantPatternDynamicFormatter.toTemporalAccessor(instant);
               dateTimeFormatter.formatTo(instantAccessor, buffer);
            }
         };
      }

      @Nullable
      @Override
      InstantPatternDynamicFormatter.PatternSequence tryMerge(InstantPatternDynamicFormatter.PatternSequence other, ChronoUnit thresholdPrecision) {
         if (other instanceof InstantPatternDynamicFormatter.DynamicPatternSequence) {
            InstantPatternDynamicFormatter.DynamicPatternSequence otherDtf = (InstantPatternDynamicFormatter.DynamicPatternSequence)other;
            if (this.isConstantForDurationOf(thresholdPrecision) == otherDtf.isConstantForDurationOf(thresholdPrecision)) {
               ChronoUnit precision = this.precision.getDuration().compareTo(otherDtf.precision.getDuration()) < 0 ? this.precision : otherDtf.precision;
               return new InstantPatternDynamicFormatter.DynamicPatternSequence(mergePatterns(this.pattern, otherDtf.pattern), precision);
            }
         }

         if (other instanceof InstantPatternDynamicFormatter.StaticPatternSequence) {
            InstantPatternDynamicFormatter.StaticPatternSequence otherStatic = (InstantPatternDynamicFormatter.StaticPatternSequence)other;
            return new InstantPatternDynamicFormatter.DynamicPatternSequence(mergePatterns(this.pattern, otherStatic.pattern), this.precision);
         } else {
            return null;
         }
      }

      private static ChronoUnit patternPrecision(final String singlePattern) {
         validateContent(singlePattern);
         String paddingRemovedContent = removePadding(singlePattern);
         if (paddingRemovedContent.matches("G+")) {
            return ChronoUnit.ERAS;
         } else if (paddingRemovedContent.matches("[uyY]+")) {
            return ChronoUnit.YEARS;
         } else if (paddingRemovedContent.matches("[MLQq]+")) {
            return ChronoUnit.MONTHS;
         } else if (paddingRemovedContent.matches("w+")) {
            return ChronoUnit.WEEKS;
         } else if (paddingRemovedContent.matches("[DdgEecFW]+")) {
            return ChronoUnit.DAYS;
         } else if (paddingRemovedContent.matches("[aBhKkH]+")) {
            return ChronoUnit.HOURS;
         } else if (paddingRemovedContent.contains("m") || paddingRemovedContent.matches("[ZxXOzVv]+")) {
            return ChronoUnit.MINUTES;
         } else if (paddingRemovedContent.contains("s")) {
            return ChronoUnit.SECONDS;
         } else if (paddingRemovedContent.matches("S{1,3}") || paddingRemovedContent.contains("A")) {
            return ChronoUnit.MILLIS;
         } else if (paddingRemovedContent.matches("S{4,6}")) {
            return ChronoUnit.MICROS;
         } else if (!paddingRemovedContent.matches("S{7,9}") && !paddingRemovedContent.matches("[nN]+")) {
            String message = String.format("unrecognized pattern: `%s`", singlePattern);
            throw new IllegalArgumentException(message);
         } else {
            return ChronoUnit.NANOS;
         }
      }

      private static void validateContent(final String content) {
         String paddingRemovedContent = removePadding(content);
         if (paddingRemovedContent.isEmpty()) {
            String message = String.format("empty content: `%s`", content);
            throw new IllegalArgumentException(message);
         } else {
            char letter = paddingRemovedContent.charAt(0);
            boolean dynamic = InstantPatternDynamicFormatter.isDynamicPatternLetter(letter);
            if (!dynamic) {
               String message = String.format("pattern sequence doesn't start with a dynamic pattern letter: `%s`", content);
               throw new IllegalArgumentException(message);
            } else {
               boolean repeated = paddingRemovedContent.matches("^(\\Q" + letter + "\\E)+$");
               if (!repeated) {
                  String message = String.format("was expecting letter `%c` to be repeated through the entire pattern sequence: `%s`", letter, content);
                  throw new IllegalArgumentException(message);
               }
            }
         }
      }

      private static String removePadding(final String content) {
         return content.replaceAll("^p+", "");
      }
   }

   abstract static class PatternSequence {
      final String pattern;
      final ChronoUnit precision;

      PatternSequence(final String pattern, final ChronoUnit precision) {
         assert !"''".equals(pattern);

         DateTimeFormatter.ofPattern(pattern);
         this.pattern = pattern;
         this.precision = precision;
      }

      abstract InstantPatternFormatter createFormatter(final Locale locale, final TimeZone timeZone);

      @Nullable
      abstract InstantPatternDynamicFormatter.PatternSequence tryMerge(InstantPatternDynamicFormatter.PatternSequence other, ChronoUnit thresholdPrecision);

      boolean isConstantForDurationOf(final ChronoUnit thresholdPrecision) {
         return this.precision.compareTo(thresholdPrecision) >= 0;
      }

      static String escapeLiteral(String literal) {
         return literal.isEmpty() ? "" : "'" + literal.replace("'", "''") + "'";
      }

      static String mergePatterns(String left, String right) {
         if (left.isEmpty()) {
            return right;
         } else if (right.isEmpty()) {
            return left;
         } else {
            return left.charAt(left.length() - 1) == '\'' && right.charAt(0) == '\'' ? left.substring(0, left.length() - 1) + right.substring(1) : left + right;
         }
      }

      @Override
      public boolean equals(final Object object) {
         if (this == object) {
            return true;
         } else if (object != null && this.getClass() == object.getClass()) {
            InstantPatternDynamicFormatter.PatternSequence sequence = (InstantPatternDynamicFormatter.PatternSequence)object;
            return Objects.equals(this.pattern, sequence.pattern) && this.precision == sequence.precision;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.pattern, this.precision);
      }

      @Override
      public String toString() {
         return this.getClass().getSimpleName() + "[pattern='" + this.pattern + '\'' + ", precision=" + this.precision + ']';
      }
   }

   static class SecondPatternSequence extends InstantPatternDynamicFormatter.PatternSequence {
      private static final int[] POWERS_OF_TEN = new int[]{100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};
      private final int secondDigits;
      private final String separator;
      private final int fractionalDigits;

      SecondPatternSequence(int secondDigits, String separator, int fractionalDigits) {
         super(createPattern(secondDigits, separator, fractionalDigits), determinePrecision(secondDigits, fractionalDigits));
         int maxSecondDigits = 2;
         if (secondDigits > 2) {
            String message = String.format("More than %d `s` pattern letters are not supported, found: %d", 2, secondDigits);
            throw new IllegalArgumentException(message);
         } else {
            int maxFractionalDigits = 9;
            if (fractionalDigits > 9) {
               String message = String.format("More than %d `S` pattern letters are not supported, found: %d", 9, fractionalDigits);
               throw new IllegalArgumentException(message);
            } else {
               this.secondDigits = secondDigits;
               this.separator = separator;
               this.fractionalDigits = fractionalDigits;
            }
         }
      }

      private static String createPattern(int secondDigits, String separator, int fractionalDigits) {
         return Strings.repeat("s", secondDigits)
            + InstantPatternDynamicFormatter.StaticPatternSequence.escapeLiteral(separator)
            + Strings.repeat("S", fractionalDigits);
      }

      private static ChronoUnit determinePrecision(int secondDigits, int digits) {
         if (digits > 6) {
            return ChronoUnit.NANOS;
         } else if (digits > 3) {
            return ChronoUnit.MICROS;
         } else if (digits > 0) {
            return ChronoUnit.MILLIS;
         } else {
            return secondDigits > 0 ? ChronoUnit.SECONDS : ChronoUnit.FOREVER;
         }
      }

      private static void formatUnpaddedSeconds(StringBuilder buffer, Instant instant) {
         buffer.append(instant.getEpochSecond() % 60L);
      }

      private static void formatPaddedSeconds(StringBuilder buffer, Instant instant) {
         long secondsInMinute = instant.getEpochSecond() % 60L;
         buffer.append((char)((int)(secondsInMinute / 10L + 48L)));
         buffer.append((char)((int)(secondsInMinute % 10L + 48L)));
      }

      private void formatFractionalDigits(StringBuilder buffer, Instant instant) {
         int nanos = instant.getNanoOfSecond();
         int moreDigits = 0;

         for (int idx = 0; idx < this.fractionalDigits; idx++) {
            int digits = moreDigits;
            moreDigits = nanos / POWERS_OF_TEN[idx];
            buffer.append((char)(48 + moreDigits - 10 * digits));
         }
      }

      private static void formatMillis(StringBuilder buffer, Instant instant) {
         int ms = instant.getNanoOfSecond() / 1000000;
         int cs = ms / 10;
         int ds = cs / 10;
         buffer.append((char)(48 + ds));
         buffer.append((char)(48 + cs - 10 * ds));
         buffer.append((char)(48 + ms - 10 * cs));
      }

      @Override
      InstantPatternFormatter createFormatter(Locale locale, TimeZone timeZone) {
         final BiConsumer<StringBuilder, Instant> secondDigitsFormatter = this.secondDigits == 2
            ? InstantPatternDynamicFormatter.SecondPatternSequence::formatPaddedSeconds
            : InstantPatternDynamicFormatter.SecondPatternSequence::formatUnpaddedSeconds;
         final BiConsumer<StringBuilder, Instant> fractionDigitsFormatter = this.fractionalDigits == 3
            ? InstantPatternDynamicFormatter.SecondPatternSequence::formatMillis
            : this::formatFractionalDigits;
         if (this.secondDigits == 0) {
            return new InstantPatternDynamicFormatter.AbstractFormatter(this.pattern, locale, timeZone, this.precision) {
               @Override
               public void formatTo(StringBuilder buffer, Instant instant) {
                  buffer.append(SecondPatternSequence.this.separator);
                  fractionDigitsFormatter.accept(buffer, instant);
               }
            };
         } else {
            return this.fractionalDigits == 0 ? new InstantPatternDynamicFormatter.AbstractFormatter(this.pattern, locale, timeZone, this.precision) {
               @Override
               public void formatTo(StringBuilder buffer, Instant instant) {
                  secondDigitsFormatter.accept(buffer, instant);
                  buffer.append(SecondPatternSequence.this.separator);
               }
            } : new InstantPatternDynamicFormatter.AbstractFormatter(this.pattern, locale, timeZone, this.precision) {
               @Override
               public void formatTo(StringBuilder buffer, Instant instant) {
                  secondDigitsFormatter.accept(buffer, instant);
                  buffer.append(SecondPatternSequence.this.separator);
                  fractionDigitsFormatter.accept(buffer, instant);
               }
            };
         }
      }

      @Nullable
      @Override
      InstantPatternDynamicFormatter.PatternSequence tryMerge(InstantPatternDynamicFormatter.PatternSequence other, ChronoUnit thresholdPrecision) {
         if (other instanceof InstantPatternDynamicFormatter.StaticPatternSequence) {
            InstantPatternDynamicFormatter.StaticPatternSequence staticOther = (InstantPatternDynamicFormatter.StaticPatternSequence)other;
            if (this.fractionalDigits == 0) {
               return new InstantPatternDynamicFormatter.SecondPatternSequence(this.secondDigits, this.separator + staticOther.literal, this.fractionalDigits);
            }
         }

         if (other instanceof InstantPatternDynamicFormatter.SecondPatternSequence) {
            InstantPatternDynamicFormatter.SecondPatternSequence secondOther = (InstantPatternDynamicFormatter.SecondPatternSequence)other;
            if (secondOther.secondDigits == 0 && secondOther.separator.isEmpty()) {
               return new InstantPatternDynamicFormatter.SecondPatternSequence(
                  this.secondDigits, this.separator, this.fractionalDigits + secondOther.fractionalDigits
               );
            }
         }

         return null;
      }
   }

   static final class StaticPatternSequence extends InstantPatternDynamicFormatter.PatternSequence {
      private final String literal;

      StaticPatternSequence(final String literal) {
         super(escapeLiteral(literal), ChronoUnit.FOREVER);
         this.literal = literal;
      }

      @Override
      InstantPatternFormatter createFormatter(final Locale locale, final TimeZone timeZone) {
         return new InstantPatternDynamicFormatter.AbstractFormatter(this.pattern, locale, timeZone, this.precision) {
            @Override
            public void formatTo(final StringBuilder buffer, final Instant instant) {
               buffer.append(StaticPatternSequence.this.literal);
            }
         };
      }

      @Nullable
      @Override
      InstantPatternDynamicFormatter.PatternSequence tryMerge(InstantPatternDynamicFormatter.PatternSequence other, ChronoUnit thresholdPrecision) {
         if (other instanceof InstantPatternDynamicFormatter.StaticPatternSequence) {
            InstantPatternDynamicFormatter.StaticPatternSequence otherStatic = (InstantPatternDynamicFormatter.StaticPatternSequence)other;
            return new InstantPatternDynamicFormatter.StaticPatternSequence(mergePatterns(this.literal, otherStatic.literal));
         } else if (other instanceof InstantPatternDynamicFormatter.DynamicPatternSequence) {
            InstantPatternDynamicFormatter.DynamicPatternSequence otherDtf = (InstantPatternDynamicFormatter.DynamicPatternSequence)other;
            return new InstantPatternDynamicFormatter.DynamicPatternSequence(mergePatterns(this.pattern, otherDtf.pattern), otherDtf.precision);
         } else {
            return null;
         }
      }
   }

   private static final class TimestampedFormatter {
      private final long instantEpochMinutes;
      private final InstantPatternFormatter formatter;

      private TimestampedFormatter(final long instantEpochMinutes, final InstantPatternFormatter formatter) {
         this.instantEpochMinutes = instantEpochMinutes;
         this.formatter = formatter;
      }
   }
}
