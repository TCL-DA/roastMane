package org.ocpsoft.prettytime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.ocpsoft.prettytime.impl.DurationImpl;
import org.ocpsoft.prettytime.impl.ResourcesTimeFormat;
import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;
import org.ocpsoft.prettytime.units.Century;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Decade;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millennium;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Second;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

public class PrettyTime {
   private volatile Instant reference;
   private volatile Locale locale = Locale.getDefault();
   private final Map<TimeUnit, TimeFormat> units = new ConcurrentHashMap<>();
   private volatile List<TimeUnit> cachedUnits;
   private String overrideResourceBundle;

   public PrettyTime() {
      this((String)null);
   }

   public PrettyTime(String overrideResourceBundle) {
      this.overrideResourceBundle = overrideResourceBundle;
      this.initTimeUnits();
   }

   public PrettyTime(Date reference) {
      this();
      this.setReference(reference);
   }

   public PrettyTime(Date reference, String overrideResourceBundle) {
      this(overrideResourceBundle);
      this.setReference(reference);
   }

   public PrettyTime(Instant reference) {
      this();
      this.setReference(reference);
   }

   public PrettyTime(Instant reference, String overrideResourceBundle) {
      this(overrideResourceBundle);
      this.setReference(reference);
   }

   public PrettyTime(LocalDateTime reference) {
      this(reference, ZoneId.systemDefault());
   }

   public PrettyTime(LocalDateTime reference, String overrideResourceBundle) {
      this(reference, ZoneId.systemDefault(), overrideResourceBundle);
   }

   public PrettyTime(LocalDateTime reference, ZoneId zoneId) {
      this(reference, zoneId, null);
   }

   public PrettyTime(LocalDateTime reference, ZoneId zoneId, String overrideResourceBundle) {
      this(reference != null ? reference.atZone(zoneId).toInstant() : null, overrideResourceBundle);
   }

   public PrettyTime(LocalDate reference) {
      this(reference, ZoneId.systemDefault(), null);
   }

   public PrettyTime(LocalDate reference, String overrideResourceBundle) {
      this(reference, ZoneId.systemDefault(), overrideResourceBundle);
   }

   public PrettyTime(LocalDate reference, ZoneId zoneId) {
      this(reference, zoneId, null);
   }

   public PrettyTime(LocalDate reference, ZoneId zoneId, String overrideResourceBundle) {
      this(reference != null ? reference.atStartOfDay(zoneId).toInstant() : null, overrideResourceBundle);
   }

   public PrettyTime(Locale locale) {
      this();
      this.setLocale(locale);
   }

   public PrettyTime(Locale locale, String overrideResourceBundle) {
      this(overrideResourceBundle);
      this.setLocale(locale);
   }

   public PrettyTime(Date reference, Locale locale) {
      this(locale);
      this.setReference(reference);
   }

   public PrettyTime(Date reference, Locale locale, String overrideResourceBundle) {
      this(locale, overrideResourceBundle);
      this.setReference(reference);
   }

   public Duration approximateDuration(Date then) {
      if (then == null) {
         then = this.now();
      }

      Instant ref = this.reference != null ? this.reference : Instant.now();
      long difference = then.getTime() - ref.toEpochMilli();
      if (difference == 0L) {
         difference = 1L;
      }

      return this.calculateDuration(difference);
   }

   public Duration approximateDuration(Instant then) {
      return this.approximateDuration(then != null ? Date.from(then) : null);
   }

   public Duration approximateDuration(LocalDate then) {
      return this.approximateDuration(then != null ? then.atStartOfDay(ZoneId.systemDefault()).toInstant() : null);
   }

   public Duration approximateDuration(LocalDate then, ZoneId zoneId) {
      return this.approximateDuration(then != null ? then.atStartOfDay(ZoneId.systemDefault()).toInstant() : null);
   }

   public Duration approximateDuration(LocalDateTime then) {
      return this.approximateDuration(then != null ? then.atZone(ZoneId.systemDefault()).toInstant() : null);
   }

   public Duration approximateDuration(LocalDateTime then, ZoneId zoneId) {
      return this.approximateDuration(then != null ? then.atZone(zoneId).toInstant() : null);
   }

   public List<Duration> calculatePreciseDuration(Date then) {
      return this.calculatePreciseDuration(then != null ? then.toInstant() : null);
   }

   public List<Duration> calculatePreciseDuration(Instant then) {
      if (then == null) {
         then = Instant.now();
      }

      Instant reference = this.reference != null ? this.reference : Instant.now();
      List<Duration> result = new ArrayList<>();
      long difference = then.toEpochMilli() - reference.toEpochMilli();
      Duration duration = this.calculateDuration(difference);
      result.add(duration);

      while (0L != duration.getDelta()) {
         duration = this.calculateDuration(duration.getDelta());
         if (!result.isEmpty()) {
            Duration last = result.get(result.size() - 1);
            if (last.getUnit().equals(duration.getUnit())) {
               break;
            }
         }

         if (duration.getUnit().isPrecise()) {
            result.add(duration);
         }
      }

      return result;
   }

   public List<Duration> calculatePreciseDuration(LocalDateTime then) {
      return this.calculatePreciseDuration(then, ZoneId.systemDefault());
   }

   public List<Duration> calculatePreciseDuration(LocalDateTime then, ZoneId zoneId) {
      return this.calculatePreciseDuration(then != null ? then.atZone(zoneId).toInstant() : null);
   }

   public List<Duration> calculatePreciseDuration(LocalDate then) {
      return this.calculatePreciseDuration(then != null ? then.atStartOfDay() : null);
   }

   public List<Duration> calculatePreciseDuration(LocalDate then, ZoneId zoneId) {
      return this.calculatePreciseDuration(then != null ? then.atStartOfDay(zoneId).toInstant() : null);
   }

   public String format(Date then) {
      if (then == null) {
         then = this.now();
      }

      Duration d = this.approximateDuration(then);
      return this.format(d);
   }

   public String format(Calendar then) {
      return then == null ? this.format(this.now()) : this.format(then.getTime());
   }

   public String format(Duration duration) {
      if (duration == null) {
         return this.format(this.now());
      } else {
         TimeFormat format = this.getFormat(duration.getUnit());
         String time = format.format(duration);
         return format.decorate(duration, time);
      }
   }

   public String format(List<Duration> durations) {
      if (durations != null && !durations.isEmpty()) {
         StringBuilder result = new StringBuilder();
         Duration duration = null;
         TimeFormat format = null;

         for (int i = 0; i < durations.size(); i++) {
            duration = durations.get(i);
            format = this.getFormat(duration.getUnit());
            if (format == null) {
               throw new IllegalArgumentException("Unsupported time unit: " + duration.getUnit());
            }

            if (i < durations.size() - 1) {
               result.append(format.formatUnrounded(duration)).append(" ");
            } else {
               result.append(format.format(duration));
            }
         }

         return format.decorateUnrounded(duration, result.toString());
      } else {
         return this.format(this.now());
      }
   }

   public String format(Instant then) {
      return this.format(this.approximateDuration(then));
   }

   public String format(ZonedDateTime then) {
      return this.format(then != null ? then.toInstant() : null);
   }

   public String format(OffsetDateTime then) {
      return this.format(then != null ? then.toInstant() : null);
   }

   public String format(LocalDateTime then, ZoneId zoneId) {
      return this.format(then != null ? then.atZone(zoneId) : null);
   }

   public String format(LocalDateTime then) {
      return this.format(then, ZoneId.systemDefault());
   }

   public String format(LocalDate then, ZoneId zoneId) {
      return this.format(then != null ? then.atStartOfDay(zoneId) : null);
   }

   public String format(LocalDate then) {
      return this.format(then != null ? then.atStartOfDay() : null);
   }

   public String formatUnrounded(Date then) {
      if (then == null) {
         then = this.now();
      }

      Duration d = this.approximateDuration(then);
      return this.formatUnrounded(d);
   }

   public String formatUnrounded(Calendar then) {
      return then == null ? this.formatUnrounded(this.now()) : this.formatUnrounded(then.getTime());
   }

   public String formatUnrounded(Duration duration) {
      if (duration == null) {
         return this.formatUnrounded(this.now());
      } else {
         TimeFormat format = this.getFormat(duration.getUnit());
         String time = format.formatUnrounded(duration);
         return format.decorateUnrounded(duration, time);
      }
   }

   public String formatUnrounded(List<Duration> durations) {
      if (durations != null && !durations.isEmpty()) {
         StringBuilder result = new StringBuilder();
         Duration duration = null;
         TimeFormat format = null;

         for (int i = 0; i < durations.size(); i++) {
            duration = durations.get(i);
            format = this.getFormat(duration.getUnit());
            result.append(format.formatUnrounded(duration));
            if (i < durations.size() - 1) {
               result.append(" ");
            }
         }

         return format.decorateUnrounded(duration, result.toString());
      } else {
         return this.format(this.now());
      }
   }

   public String formatUnrounded(Instant then) {
      return this.formatUnrounded(this.approximateDuration(then));
   }

   public String formatUnrounded(ZonedDateTime then) {
      return this.formatUnrounded(then != null ? then.toInstant() : null);
   }

   public String formatUnrounded(OffsetDateTime then) {
      return this.formatUnrounded(then != null ? then.toInstant() : null);
   }

   public String formatUnrounded(LocalDateTime then, ZoneId zoneId) {
      return this.formatUnrounded(then != null ? then.atZone(zoneId) : null);
   }

   public String formatUnrounded(LocalDateTime then) {
      return this.formatUnrounded(then, ZoneId.systemDefault());
   }

   public String formatUnrounded(LocalDate then, ZoneId zoneId) {
      return this.formatUnrounded(then != null ? then.atStartOfDay(zoneId) : null);
   }

   public String formatUnrounded(LocalDate then) {
      return this.formatUnrounded(then != null ? then.atStartOfDay() : null);
   }

   public String formatDuration(Date then) {
      Duration duration = this.approximateDuration(then);
      return this.formatDuration(duration);
   }

   public String formatDuration(Calendar then) {
      if (then == null) {
         return this.formatDuration(this.now());
      } else {
         Duration duration = this.approximateDuration(then.getTime());
         return this.formatDuration(duration);
      }
   }

   public String formatDuration(Duration duration) {
      if (duration == null) {
         return this.format(this.now());
      } else {
         TimeFormat timeFormat = this.getFormat(duration.getUnit());
         return timeFormat.format(duration);
      }
   }

   public String formatDuration(List<Duration> durations) {
      if (durations != null && !durations.isEmpty()) {
         StringBuilder result = new StringBuilder();
         Duration duration = null;
         TimeFormat format = null;

         for (int i = 0; i < durations.size(); i++) {
            duration = durations.get(i);
            format = this.getFormat(duration.getUnit());
            if (i < durations.size() - 1) {
               result.append(format.formatUnrounded(duration)).append(" ");
            } else {
               result.append(format.format(duration));
            }
         }

         return result.toString();
      } else {
         return this.format(this.now());
      }
   }

   public String formatDuration(Instant then) {
      return this.formatDuration(this.approximateDuration(then));
   }

   public String formatDuration(ZonedDateTime then) {
      return this.formatDuration(then != null ? then.toInstant() : null);
   }

   public String formatDuration(OffsetDateTime then) {
      return this.formatDuration(then != null ? then.toInstant() : null);
   }

   public String formatDuration(LocalDateTime then, ZoneId zoneId) {
      return this.formatDuration(then != null ? then.atZone(zoneId) : null);
   }

   public String formatDuration(LocalDateTime then) {
      return this.formatDuration(then, ZoneId.systemDefault());
   }

   public String formatDuration(LocalDate then, ZoneId zoneId) {
      return this.formatDuration(then != null ? then.atStartOfDay(zoneId) : null);
   }

   public String formatDuration(LocalDate then) {
      return this.formatDuration(then != null ? then.atStartOfDay() : null);
   }

   public String formatDurationUnrounded(Date then) {
      Duration duration = this.approximateDuration(then);
      return this.formatDurationUnrounded(duration);
   }

   public String formatDurationUnrounded(Calendar then) {
      if (then == null) {
         return this.formatDuration(this.now());
      } else {
         Duration duration = this.approximateDuration(then.getTime());
         return this.formatDurationUnrounded(duration);
      }
   }

   public String formatDurationUnrounded(Duration duration) {
      if (duration == null) {
         return this.format(this.now());
      } else {
         TimeFormat timeFormat = this.getFormat(duration.getUnit());
         return timeFormat.formatUnrounded(duration);
      }
   }

   public String formatDurationUnrounded(List<Duration> durations) {
      if (durations != null && !durations.isEmpty()) {
         StringBuilder result = new StringBuilder();
         Duration duration = null;
         TimeFormat format = null;

         for (int i = 0; i < durations.size(); i++) {
            duration = durations.get(i);
            format = this.getFormat(duration.getUnit());
            result.append(format.formatUnrounded(duration));
            if (i < durations.size() - 1) {
               result.append(" ");
            }
         }

         return result.toString();
      } else {
         return this.format(this.now());
      }
   }

   public String formatDurationUnrounded(Instant then) {
      return this.formatDurationUnrounded(this.approximateDuration(then));
   }

   public String formatDurationUnrounded(ZonedDateTime then) {
      return this.formatDurationUnrounded(then != null ? then.toInstant() : null);
   }

   public String formatDurationUnrounded(OffsetDateTime then) {
      return this.formatDurationUnrounded(then != null ? then.toInstant() : null);
   }

   public String formatDurationUnrounded(LocalDateTime then, ZoneId zoneId) {
      return this.formatDurationUnrounded(then != null ? then.atZone(zoneId) : null);
   }

   public String formatDurationUnrounded(LocalDateTime then) {
      return this.formatDurationUnrounded(then, ZoneId.systemDefault());
   }

   public String formatDurationUnrounded(LocalDate then, ZoneId zoneId) {
      return this.formatDurationUnrounded(then != null ? then.atStartOfDay(zoneId) : null);
   }

   public String formatDurationUnrounded(LocalDate then) {
      return this.formatDurationUnrounded(then != null ? then.atStartOfDay() : null);
   }

   public TimeFormat getFormat(TimeUnit unit) {
      if (unit == null) {
         return null;
      } else if (this.units.get(unit) != null) {
         return this.units.get(unit);
      } else {
         Map<String, TimeFormat> map = new ConcurrentHashMap<>();
         this.units.keySet().forEach(key -> map.put(key.toString(), this.units.get(key)));
         return map.get(unit.toString());
      }
   }

   public Instant getReference() {
      return this.reference;
   }

   public Date getReferenceAsLegacyDate() {
      return this.reference != null ? Date.from(this.reference) : null;
   }

   public PrettyTime setReference(Date timestamp) {
      return this.setReference(timestamp != null ? timestamp.toInstant() : null);
   }

   public PrettyTime setReference(Instant timestamp) {
      this.reference = timestamp;
      return this;
   }

   public PrettyTime setReference(LocalDateTime localDateTime) {
      return this.setReference(localDateTime, ZoneId.systemDefault());
   }

   public PrettyTime setReference(LocalDateTime localDateTime, ZoneId zoneId) {
      return this.setReference(localDateTime != null ? localDateTime.atZone(zoneId).toInstant() : null);
   }

   public PrettyTime setReference(LocalDate localDate) {
      return this.setReference(localDate != null ? localDate.atStartOfDay() : null);
   }

   public PrettyTime setReference(LocalDate localDate, ZoneId zoneId) {
      return this.setReference(localDate != null ? localDate.atStartOfDay(zoneId).toInstant() : null);
   }

   public List<TimeUnit> getUnits() {
      if (this.cachedUnits == null) {
         List<TimeUnit> result = new ArrayList<>(this.units.keySet());
         Collections.sort(result, Comparator.comparing(TimeUnit::getMillisPerUnit));
         this.cachedUnits = Collections.unmodifiableList(result);
      }

      return this.cachedUnits;
   }

   public <UNIT extends TimeUnit> UNIT getUnit(Class<UNIT> unitType) {
      if (unitType == null) {
         return null;
      } else {
         for (TimeUnit unit : this.units.keySet()) {
            if (unitType.isAssignableFrom(unit.getClass())) {
               return (UNIT)unit;
            }
         }

         return null;
      }
   }

   public PrettyTime registerUnit(TimeUnit unit, TimeFormat format) {
      this.cachedUnits = null;
      this.units
         .put(
            Objects.requireNonNull(unit, "TimeUnit to register must not be null."), Objects.requireNonNull(format, "TimeFormat to register must not be null.")
         );
      if (unit instanceof LocaleAware) {
         ((LocaleAware)unit).setLocale(this.locale);
      }

      if (format instanceof LocaleAware) {
         ((LocaleAware)format).setLocale(this.locale);
      }

      return this;
   }

   public PrettyTime setUnits(ResourcesTimeUnit... units) {
      if (units != null && units.length != 0) {
         this.clearUnits();

         for (ResourcesTimeUnit unit : units) {
            TimeFormat format = new ResourcesTimeFormat(unit);
            this.registerUnit(unit, format);
         }

         return this;
      } else {
         throw new IllegalArgumentException("TimeUnit instance(s) to register must be provided.");
      }
   }

   public PrettyTime setUnits(TimeFormat format, TimeUnit... units) {
      if (units != null && units.length != 0) {
         Objects.requireNonNull(format, "TimeFormat to register must not be null.");
         this.clearUnits();

         for (TimeUnit unit : units) {
            this.registerUnit(unit, format);
         }

         return this;
      } else {
         throw new IllegalArgumentException("TimeUnit instance(s) to register must be provided.");
      }
   }

   public <UNIT extends TimeUnit> TimeFormat removeUnit(Class<UNIT> unitType) {
      if (unitType == null) {
         return null;
      } else {
         for (TimeUnit unit : this.units.keySet()) {
            if (unitType.isAssignableFrom(unit.getClass())) {
               this.cachedUnits = null;
               return this.units.remove(unit);
            }
         }

         return null;
      }
   }

   public TimeFormat removeUnit(TimeUnit unit) {
      if (unit == null) {
         return null;
      } else {
         this.cachedUnits = null;
         return this.units.remove(unit);
      }
   }

   public Locale getLocale() {
      return this.locale;
   }

   public PrettyTime setLocale(Locale locale) {
      if (locale == null) {
         locale = Locale.getDefault();
      }

      this.locale = locale;

      for (TimeUnit unit : this.units.keySet()) {
         if (unit instanceof LocaleAware) {
            ((LocaleAware)unit).setLocale(locale);
         }
      }

      for (TimeFormat format : this.units.values()) {
         if (format instanceof LocaleAware) {
            ((LocaleAware)format).setLocale(locale);
         }
      }

      this.cachedUnits = null;
      return this;
   }

   @Override
   public String toString() {
      return "PrettyTime [reference=" + this.reference + ", locale=" + this.locale + "]";
   }

   public List<TimeUnit> clearUnits() {
      List<TimeUnit> result = this.getUnits();
      this.cachedUnits = null;
      this.units.clear();
      return result;
   }

   private Date now() {
      return new Date();
   }

   private void initTimeUnits() {
      this.addUnit(new JustNow());
      this.addUnit(new Millisecond());
      this.addUnit(new Second());
      this.addUnit(new Minute());
      this.addUnit(new Hour());
      this.addUnit(new Day());
      this.addUnit(new Week());
      this.addUnit(new Month());
      this.addUnit(new Year());
      this.addUnit(new Decade());
      this.addUnit(new Century());
      this.addUnit(new Millennium());
   }

   private void addUnit(ResourcesTimeUnit unit) {
      this.registerUnit(unit, new ResourcesTimeFormat(unit, this.overrideResourceBundle));
   }

   private Duration calculateDuration(long difference) {
      long absoluteDifference = Math.abs(difference);
      List<TimeUnit> localUnits = this.getUnits();
      DurationImpl result = new DurationImpl();

      for (int i = 0; i < localUnits.size(); i++) {
         TimeUnit unit = localUnits.get(i);
         long millisPerUnit = Math.abs(unit.getMillisPerUnit());
         long quantity = Math.abs(unit.getMaxQuantity());
         boolean isLastUnit = i == localUnits.size() - 1;
         if (0L == quantity && !isLastUnit) {
            quantity = localUnits.get(i + 1).getMillisPerUnit() / unit.getMillisPerUnit();
         }

         if (millisPerUnit * quantity > absoluteDifference || isLastUnit) {
            result.setUnit(unit);
            if (millisPerUnit > absoluteDifference) {
               result.setQuantity(this.getSign(difference));
               result.setDelta(0L);
            } else {
               result.setQuantity(difference / millisPerUnit);
               result.setDelta(difference - result.getQuantity() * millisPerUnit);
            }
            break;
         }
      }

      return result;
   }

   private long getSign(long difference) {
      return 0L > difference ? -1L : 1L;
   }
}
