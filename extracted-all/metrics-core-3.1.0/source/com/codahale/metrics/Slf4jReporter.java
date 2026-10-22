package com.codahale.metrics;

import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class Slf4jReporter extends ScheduledReporter {
   private final Slf4jReporter.LoggerProxy loggerProxy;
   private final Marker marker;

   public static Slf4jReporter.Builder forRegistry(MetricRegistry registry) {
      return new Slf4jReporter.Builder(registry);
   }

   private Slf4jReporter(
      MetricRegistry registry, Slf4jReporter.LoggerProxy loggerProxy, Marker marker, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter
   ) {
      super(registry, "logger-reporter", filter, rateUnit, durationUnit);
      this.loggerProxy = loggerProxy;
      this.marker = marker;
   }

   @Override
   public void report(
      SortedMap<String, Gauge> gauges,
      SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms,
      SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers
   ) {
      for (Entry<String, Gauge> entry : gauges.entrySet()) {
         this.logGauge(entry.getKey(), entry.getValue());
      }

      for (Entry<String, Counter> entry : counters.entrySet()) {
         this.logCounter(entry.getKey(), entry.getValue());
      }

      for (Entry<String, Histogram> entry : histograms.entrySet()) {
         this.logHistogram(entry.getKey(), entry.getValue());
      }

      for (Entry<String, Meter> entry : meters.entrySet()) {
         this.logMeter(entry.getKey(), entry.getValue());
      }

      for (Entry<String, Timer> entry : timers.entrySet()) {
         this.logTimer(entry.getKey(), entry.getValue());
      }
   }

   private void logTimer(String name, Timer timer) {
      Snapshot snapshot = timer.getSnapshot();
      this.loggerProxy
         .log(
            this.marker,
            "type=TIMER, name={}, count={}, min={}, max={}, mean={}, stddev={}, median={}, p75={}, p95={}, p98={}, p99={}, p999={}, mean_rate={}, m1={}, m5={}, m15={}, rate_unit={}, duration_unit={}",
            name,
            timer.getCount(),
            this.convertDuration((double)snapshot.getMin()),
            this.convertDuration((double)snapshot.getMax()),
            this.convertDuration(snapshot.getMean()),
            this.convertDuration(snapshot.getStdDev()),
            this.convertDuration(snapshot.getMedian()),
            this.convertDuration(snapshot.get75thPercentile()),
            this.convertDuration(snapshot.get95thPercentile()),
            this.convertDuration(snapshot.get98thPercentile()),
            this.convertDuration(snapshot.get99thPercentile()),
            this.convertDuration(snapshot.get999thPercentile()),
            this.convertRate(timer.getMeanRate()),
            this.convertRate(timer.getOneMinuteRate()),
            this.convertRate(timer.getFiveMinuteRate()),
            this.convertRate(timer.getFifteenMinuteRate()),
            this.getRateUnit(),
            this.getDurationUnit()
         );
   }

   private void logMeter(String name, Meter meter) {
      this.loggerProxy
         .log(
            this.marker,
            "type=METER, name={}, count={}, mean_rate={}, m1={}, m5={}, m15={}, rate_unit={}",
            name,
            meter.getCount(),
            this.convertRate(meter.getMeanRate()),
            this.convertRate(meter.getOneMinuteRate()),
            this.convertRate(meter.getFiveMinuteRate()),
            this.convertRate(meter.getFifteenMinuteRate()),
            this.getRateUnit()
         );
   }

   private void logHistogram(String name, Histogram histogram) {
      Snapshot snapshot = histogram.getSnapshot();
      this.loggerProxy
         .log(
            this.marker,
            "type=HISTOGRAM, name={}, count={}, min={}, max={}, mean={}, stddev={}, median={}, p75={}, p95={}, p98={}, p99={}, p999={}",
            name,
            histogram.getCount(),
            snapshot.getMin(),
            snapshot.getMax(),
            snapshot.getMean(),
            snapshot.getStdDev(),
            snapshot.getMedian(),
            snapshot.get75thPercentile(),
            snapshot.get95thPercentile(),
            snapshot.get98thPercentile(),
            snapshot.get99thPercentile(),
            snapshot.get999thPercentile()
         );
   }

   private void logCounter(String name, Counter counter) {
      this.loggerProxy.log(this.marker, "type=COUNTER, name={}, count={}", name, counter.getCount());
   }

   private void logGauge(String name, Gauge gauge) {
      this.loggerProxy.log(this.marker, "type=GAUGE, name={}, value={}", name, gauge.getValue());
   }

   @Override
   protected String getRateUnit() {
      return "events/" + super.getRateUnit();
   }

   public static class Builder {
      private final MetricRegistry registry;
      private Logger logger;
      private Slf4jReporter.LoggingLevel loggingLevel;
      private Marker marker;
      private TimeUnit rateUnit;
      private TimeUnit durationUnit;
      private MetricFilter filter;

      private Builder(MetricRegistry registry) {
         this.registry = registry;
         this.logger = LoggerFactory.getLogger("metrics");
         this.marker = null;
         this.rateUnit = TimeUnit.SECONDS;
         this.durationUnit = TimeUnit.MILLISECONDS;
         this.filter = MetricFilter.ALL;
         this.loggingLevel = Slf4jReporter.LoggingLevel.INFO;
      }

      public Slf4jReporter.Builder outputTo(Logger logger) {
         this.logger = logger;
         return this;
      }

      public Slf4jReporter.Builder markWith(Marker marker) {
         this.marker = marker;
         return this;
      }

      public Slf4jReporter.Builder convertRatesTo(TimeUnit rateUnit) {
         this.rateUnit = rateUnit;
         return this;
      }

      public Slf4jReporter.Builder convertDurationsTo(TimeUnit durationUnit) {
         this.durationUnit = durationUnit;
         return this;
      }

      public Slf4jReporter.Builder filter(MetricFilter filter) {
         this.filter = filter;
         return this;
      }

      public Slf4jReporter.Builder withLoggingLevel(Slf4jReporter.LoggingLevel loggingLevel) {
         this.loggingLevel = loggingLevel;
         return this;
      }

      public Slf4jReporter build() {
         Slf4jReporter.LoggerProxy loggerProxy;
         switch (this.loggingLevel) {
            case TRACE:
               loggerProxy = new Slf4jReporter.TraceLoggerProxy(this.logger);
               break;
            case INFO:
               loggerProxy = new Slf4jReporter.InfoLoggerProxy(this.logger);
               break;
            case WARN:
               loggerProxy = new Slf4jReporter.WarnLoggerProxy(this.logger);
               break;
            case ERROR:
               loggerProxy = new Slf4jReporter.ErrorLoggerProxy(this.logger);
               break;
            case DEBUG:
            default:
               loggerProxy = new Slf4jReporter.DebugLoggerProxy(this.logger);
         }

         return new Slf4jReporter(this.registry, loggerProxy, this.marker, this.rateUnit, this.durationUnit, this.filter);
      }
   }

   private static class DebugLoggerProxy extends Slf4jReporter.LoggerProxy {
      public DebugLoggerProxy(Logger logger) {
         super(logger);
      }

      @Override
      public void log(Marker marker, String format, Object... arguments) {
         this.logger.debug(marker, format, arguments);
      }
   }

   private static class ErrorLoggerProxy extends Slf4jReporter.LoggerProxy {
      public ErrorLoggerProxy(Logger logger) {
         super(logger);
      }

      @Override
      public void log(Marker marker, String format, Object... arguments) {
         this.logger.error(marker, format, arguments);
      }
   }

   private static class InfoLoggerProxy extends Slf4jReporter.LoggerProxy {
      public InfoLoggerProxy(Logger logger) {
         super(logger);
      }

      @Override
      public void log(Marker marker, String format, Object... arguments) {
         this.logger.info(marker, format, arguments);
      }
   }

   abstract static class LoggerProxy {
      protected final Logger logger;

      public LoggerProxy(Logger logger) {
         this.logger = logger;
      }

      abstract void log(Marker var1, String var2, Object... var3);
   }

   public static enum LoggingLevel {
      TRACE,
      DEBUG,
      INFO,
      WARN,
      ERROR;
   }

   private static class TraceLoggerProxy extends Slf4jReporter.LoggerProxy {
      public TraceLoggerProxy(Logger logger) {
         super(logger);
      }

      @Override
      public void log(Marker marker, String format, Object... arguments) {
         this.logger.trace(marker, format, arguments);
      }
   }

   private static class WarnLoggerProxy extends Slf4jReporter.LoggerProxy {
      public WarnLoggerProxy(Logger logger) {
         super(logger);
      }

      @Override
      public void log(Marker marker, String format, Object... arguments) {
         this.logger.warn(marker, format, arguments);
      }
   }
}
