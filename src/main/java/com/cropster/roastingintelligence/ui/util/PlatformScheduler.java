package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.util.RenamingThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

public final class PlatformScheduler {
   private static final String CLASSNAME = PlatformScheduler.class.getSimpleName();
   private static ScheduledExecutorService platformExecutor = Executors.newSingleThreadScheduledExecutor(new RenamingThreadFactory(CLASSNAME));

   public static ScheduledFuture<?> schedule(Runnable runnable, int delay, TimeUnit timeUnit) {
      return platformExecutor.schedule(() -> Platform.runLater(runnable), (long)delay, timeUnit);
   }

   public static ScheduledFuture<?> scheduleBackgroundTask(Runnable runnable, int delay, TimeUnit timeUnit) {
      return platformExecutor.schedule(runnable, (long)delay, timeUnit);
   }

   public static ScheduledFuture<?> schedulePeriodicBackgroundTask(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit) {
      return platformExecutor.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
   }

   public static void executeBackgroundTask(Runnable runnable) {
      platformExecutor.execute(runnable::run);
   }

   public static void shutdown() {
      platformExecutor.shutdown();
   }

   private PlatformScheduler() {
   }
}
