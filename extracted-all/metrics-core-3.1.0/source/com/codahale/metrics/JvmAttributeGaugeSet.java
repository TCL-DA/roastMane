package com.codahale.metrics;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JvmAttributeGaugeSet implements MetricSet {
   private final RuntimeMXBean runtime;

   public JvmAttributeGaugeSet() {
      this(ManagementFactory.getRuntimeMXBean());
   }

   public JvmAttributeGaugeSet(RuntimeMXBean runtime) {
      this.runtime = runtime;
   }

   @Override
   public Map<String, Metric> getMetrics() {
      Map<String, Metric> gauges = new HashMap<>();
      gauges.put("name", new Gauge<String>() {
         public String getValue() {
            return JvmAttributeGaugeSet.this.runtime.getName();
         }
      });
      gauges.put(
         "vendor",
         new Gauge<String>() {
            public String getValue() {
               return String.format(
                  Locale.US,
                  "%s %s %s (%s)",
                  JvmAttributeGaugeSet.this.runtime.getVmVendor(),
                  JvmAttributeGaugeSet.this.runtime.getVmName(),
                  JvmAttributeGaugeSet.this.runtime.getVmVersion(),
                  JvmAttributeGaugeSet.this.runtime.getSpecVersion()
               );
            }
         }
      );
      gauges.put("uptime", new Gauge<Long>() {
         public Long getValue() {
            return JvmAttributeGaugeSet.this.runtime.getUptime();
         }
      });
      return Collections.unmodifiableMap(gauges);
   }
}
