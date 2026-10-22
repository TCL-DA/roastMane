package com.sun.javafx.tk.quantum;

import com.sun.javafx.tk.Toolkit;
import com.sun.prism.impl.PrismSettings;
import java.security.AccessController;
import java.security.PrivilegedAction;

abstract class PerformanceTrackerHelper {
   private static final PerformanceTrackerHelper instance = createInstance();

   public static PerformanceTrackerHelper getInstance() {
      return instance;
   }

   private PerformanceTrackerHelper() {
   }

   private static PerformanceTrackerHelper createInstance() {
      Object var0 = AccessController.doPrivileged(new PrivilegedAction<PerformanceTrackerHelper>() {
         public PerformanceTrackerHelper run() {
            try {
               if (PrismSettings.perfLog != null) {
                  final PerformanceTrackerHelper.PerformanceTrackerDefaultImpl var1 = new PerformanceTrackerHelper.PerformanceTrackerDefaultImpl();
                  if (PrismSettings.perfLogExitFlush) {
                     Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                           var1.outputLog();
                        }
                     });
                  }

                  return var1;
               }
            } catch (Throwable var2) {
            }

            return null;
         }
      });
      if (var0 == null) {
         var0 = new PerformanceTrackerHelper.PerformanceTrackerDummyImpl();
      }

      return (PerformanceTrackerHelper)var0;
   }

   public abstract void logEvent(String var1);

   public abstract void outputLog();

   public abstract boolean isPerfLoggingEnabled();

   public final long nanoTime() {
      return Toolkit.getToolkit().getPrimaryTimer().nanos();
   }

   private static final class PerformanceTrackerDefaultImpl extends PerformanceTrackerHelper {
      private long firstTime;
      private long lastTime;

      @Override
      public void logEvent(String var1) {
         long var2 = System.currentTimeMillis();
         if (this.firstTime == 0L) {
            this.firstTime = var2;
         }

         PerformanceLogger.setTime("JavaFX> " + var1 + " (" + (var2 - this.firstTime) + "ms total, " + (var2 - this.lastTime) + "ms)");
         this.lastTime = var2;
      }

      @Override
      public void outputLog() {
         this.logLaunchTime();
         PerformanceLogger.outputLog();
      }

      @Override
      public boolean isPerfLoggingEnabled() {
         return true;
      }

      private void logLaunchTime() {
         try {
            if (PerformanceLogger.getStartTime() <= 0L) {
               String var1 = AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty("launchTime")));
               if (var1 != null && !var1.equals("")) {
                  long var2 = Long.parseLong(var1);
                  PerformanceLogger.setStartTime("LaunchTime", var2);
               }
            }
         } catch (Throwable var4) {
            var4.printStackTrace();
         }
      }
   }

   private static final class PerformanceTrackerDummyImpl extends PerformanceTrackerHelper {
      @Override
      public void logEvent(String var1) {
      }

      @Override
      public void outputLog() {
      }

      @Override
      public boolean isPerfLoggingEnabled() {
         return false;
      }
   }
}
