package com.digitalpetri.netty.fsm;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface Scheduler {
   Scheduler.Cancellable schedule(Runnable var1, long var2, TimeUnit var4);

   static Scheduler fromScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
      return (command, delay, unit) -> {
         ScheduledFuture<?> future = scheduledExecutor.schedule(command, delay, unit);
         return () -> future.cancel(false);
      };
   }

   public interface Cancellable {
      boolean cancel();
   }
}
