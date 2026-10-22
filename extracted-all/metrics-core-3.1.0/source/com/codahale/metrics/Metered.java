package com.codahale.metrics;

public interface Metered extends Metric, Counting {
   @Override
   long getCount();

   double getFifteenMinuteRate();

   double getFiveMinuteRate();

   double getMeanRate();

   double getOneMinuteRate();
}
