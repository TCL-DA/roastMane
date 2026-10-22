package org.glassfish.jersey.client;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface ClientExecutor {
   <T> Future<T> submit(Callable<T> var1);

   Future<?> submit(Runnable var1);

   <T> Future<T> submit(Runnable var1, T var2);

   <T> ScheduledFuture<T> schedule(Callable<T> var1, long var2, TimeUnit var4);

   ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);
}
