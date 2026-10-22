package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.concurrent.ConcurrentNavigableMap;

public interface SlidingWindowTrimmer<V> {
   void trim(ConcurrentNavigableMap<Long, V> var1, long var2);

   void setTimeReservoir(TimeReservoir<V> var1);
}
