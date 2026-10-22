package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.concurrent.TimeUnit;

public interface TimeReservoir<V> {
   int size(long var1, TimeUnit var3);

   void update(V var1, long var2, TimeUnit var4);

   UniformTimeSnapshot getSnapshot(long var1, TimeUnit var3);

   long interval(TimeUnit var1);
}
