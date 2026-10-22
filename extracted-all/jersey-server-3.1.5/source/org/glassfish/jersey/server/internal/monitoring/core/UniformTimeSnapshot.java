package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.concurrent.TimeUnit;

public interface UniformTimeSnapshot {
   long size();

   long getMax();

   long getMin();

   double getMean();

   long getTimeInterval(TimeUnit var1);

   double getRate(TimeUnit var1);
}
