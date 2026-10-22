package com.codahale.metrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvReporter extends ScheduledReporter {
   private static final Logger LOGGER = LoggerFactory.getLogger(CsvReporter.class);
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private final File directory;
   private final Locale locale;
   private final Clock clock;

   public static CsvReporter.Builder forRegistry(MetricRegistry registry) {
      return new CsvReporter.Builder(registry);
   }

   private CsvReporter(MetricRegistry registry, File directory, Locale locale, TimeUnit rateUnit, TimeUnit durationUnit, Clock clock, MetricFilter filter) {
      super(registry, "csv-reporter", filter, rateUnit, durationUnit);
      this.directory = directory;
      this.locale = locale;
      this.clock = clock;
   }

   @Override
   public void report(
      SortedMap<String, Gauge> gauges,
      SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms,
      SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers
   ) {
      long timestamp = TimeUnit.MILLISECONDS.toSeconds(this.clock.getTime());

      for (Entry<String, Gauge> entry : gauges.entrySet()) {
         this.reportGauge(timestamp, entry.getKey(), entry.getValue());
      }

      for (Entry<String, Counter> entry : counters.entrySet()) {
         this.reportCounter(timestamp, entry.getKey(), entry.getValue());
      }

      for (Entry<String, Histogram> entry : histograms.entrySet()) {
         this.reportHistogram(timestamp, entry.getKey(), entry.getValue());
      }

      for (Entry<String, Meter> entry : meters.entrySet()) {
         this.reportMeter(timestamp, entry.getKey(), entry.getValue());
      }

      for (Entry<String, Timer> entry : timers.entrySet()) {
         this.reportTimer(timestamp, entry.getKey(), entry.getValue());
      }
   }

   private void reportTimer(long timestamp, String name, Timer timer) {
      Snapshot snapshot = timer.getSnapshot();
      this.report(
         timestamp,
         name,
         "count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit,duration_unit",
         "%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,calls/%s,%s",
         timer.getCount(),
         this.convertDuration((double)snapshot.getMax()),
         this.convertDuration(snapshot.getMean()),
         this.convertDuration((double)snapshot.getMin()),
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

   private void reportMeter(long timestamp, String name, Meter meter) {
      this.report(
         timestamp,
         name,
         "count,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit",
         "%d,%f,%f,%f,%f,events/%s",
         meter.getCount(),
         this.convertRate(meter.getMeanRate()),
         this.convertRate(meter.getOneMinuteRate()),
         this.convertRate(meter.getFiveMinuteRate()),
         this.convertRate(meter.getFifteenMinuteRate()),
         this.getRateUnit()
      );
   }

   private void reportHistogram(long timestamp, String name, Histogram histogram) {
      Snapshot snapshot = histogram.getSnapshot();
      this.report(
         timestamp,
         name,
         "count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999",
         "%d,%d,%f,%d,%f,%f,%f,%f,%f,%f,%f",
         histogram.getCount(),
         snapshot.getMax(),
         snapshot.getMean(),
         snapshot.getMin(),
         snapshot.getStdDev(),
         snapshot.getMedian(),
         snapshot.get75thPercentile(),
         snapshot.get95thPercentile(),
         snapshot.get98thPercentile(),
         snapshot.get99thPercentile(),
         snapshot.get999thPercentile()
      );
   }

   private void reportCounter(long timestamp, String name, Counter counter) {
      this.report(timestamp, name, "count", "%d", counter.getCount());
   }

   private void reportGauge(long timestamp, String name, Gauge gauge) {
      this.report(timestamp, name, "value", "%s", gauge.getValue());
   }

   private void report(long timestamp, String name, String header, String line, Object... values) {
      try {
         File file = new File(this.directory, this.sanitize(name) + ".csv");
         boolean fileAlreadyExists = file.exists();
         if (fileAlreadyExists || file.createNewFile()) {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), UTF_8));

            try {
               if (!fileAlreadyExists) {
                  out.println("t," + header);
               }

               out.printf(this.locale, String.format(this.locale, "%d,%s%n", timestamp, line), values);
            } finally {
               out.close();
            }
         }
      } catch (IOException var14) {
         LOGGER.warn("Error writing to {}", name, var14);
      }
   }

   protected String sanitize(String name) {
      return name;
   }

   public static class Builder {
      private final MetricRegistry registry;
      private Locale locale;
      private TimeUnit rateUnit;
      private TimeUnit durationUnit;
      private Clock clock;
      private MetricFilter filter;

      private Builder(MetricRegistry registry) {
         this.registry = registry;
         this.locale = Locale.getDefault();
         this.rateUnit = TimeUnit.SECONDS;
         this.durationUnit = TimeUnit.MILLISECONDS;
         this.clock = Clock.defaultClock();
         this.filter = MetricFilter.ALL;
      }

      public CsvReporter.Builder formatFor(Locale locale) {
         this.locale = locale;
         return this;
      }

      public CsvReporter.Builder convertRatesTo(TimeUnit rateUnit) {
         this.rateUnit = rateUnit;
         return this;
      }

      public CsvReporter.Builder convertDurationsTo(TimeUnit durationUnit) {
         this.durationUnit = durationUnit;
         return this;
      }

      public CsvReporter.Builder withClock(Clock clock) {
         this.clock = clock;
         return this;
      }

      public CsvReporter.Builder filter(MetricFilter filter) {
         this.filter = filter;
         return this;
      }

      public CsvReporter build(File directory) {
         return new CsvReporter(this.registry, directory, this.locale, this.rateUnit, this.durationUnit, this.clock, this.filter);
      }
   }
}
