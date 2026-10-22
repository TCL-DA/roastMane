package com.sun.webkit.perf;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class PerfLogger {
   private static Thread shutdownHook;
   private static Map<PlatformLogger, PerfLogger> loggers;
   private final HashMap<String, PerfLogger.ProbeStat> probes = new HashMap<>();
   private final PlatformLogger log;
   private final boolean isEnabled;
   private final Comparator timeComparator = (var1x, var2) -> {
      long var3 = this.probes.get((String)var1x).totalTime;
      long var5 = this.probes.get((String)var2).totalTime;
      if (var3 > var5) {
         return 1;
      } else {
         return var3 < var5 ? -1 : 0;
      }
   };
   private final Comparator countComparator = (var1x, var2) -> {
      long var3 = (long)this.probes.get((String)var1x).count;
      long var5 = (long)this.probes.get((String)var2).count;
      if (var3 > var5) {
         return 1;
      } else {
         return var3 < var5 ? -1 : 0;
      }
   };

   public static synchronized PerfLogger getLogger(PlatformLogger var0) {
      if (loggers == null) {
         loggers = new HashMap<>();
      }

      PerfLogger var1 = loggers.get(var0);
      if (var1 == null) {
         var1 = new PerfLogger(var0);
         loggers.put(var0, var1);
      }

      if (var1.isEnabled() && shutdownHook == null) {
         shutdownHook = new Thread() {
            @Override
            public void run() {
               for (PerfLogger var2 : PerfLogger.loggers.values()) {
                  if (var2.isEnabled()) {
                     var2.log(false);
                  }
               }
            }
         };
         Runtime.getRuntime().addShutdownHook(shutdownHook);
      }

      return var1;
   }

   public static synchronized PerfLogger getLogger(String var0) {
      return getLogger(PlatformLogger.getLogger("com.sun.webkit.perf." + var0));
   }

   private PerfLogger(PlatformLogger var1) {
      this.log = var1;
      this.isEnabled = var1.isLoggable(Level.FINE);
      this.startCount("TOTALTIME");
   }

   public boolean isEnabled() {
      return this.isEnabled;
   }

   private synchronized String fullName(String var1) {
      return this.log.getName() + "." + var1;
   }

   public synchronized void reset() {
      for (Entry var2 : this.probes.entrySet()) {
         ((PerfLogger.ProbeStat)var2.getValue()).reset();
      }

      this.startCount("TOTALTIME");
   }

   public static synchronized void resetAll() {
      for (PerfLogger var1 : loggers.values()) {
         var1.reset();
      }
   }

   private synchronized PerfLogger.ProbeStat registerProbe(String var1) {
      String var2 = var1.intern();
      if (this.probes.containsKey(var2)) {
         this.log.fine("Warning: \"" + this.fullName(var2) + "\" probe already exists");
      } else {
         this.log.fine("Registering \"" + this.fullName(var2) + "\" probe");
      }

      PerfLogger.ProbeStat var3 = new PerfLogger.ProbeStat(var2);
      this.probes.put(var2, var3);
      return var3;
   }

   public synchronized PerfLogger.ProbeStat getProbeStat(String var1) {
      String var2 = var1.intern();
      PerfLogger.ProbeStat var3 = this.probes.get(var2);
      if (var3 != null) {
         var3.snapshot();
      }

      return var3;
   }

   public synchronized void startCount(String var1) {
      if (this.isEnabled()) {
         String var2 = var1.intern();
         PerfLogger.ProbeStat var3 = this.probes.get(var2);
         if (var3 == null) {
            var3 = this.registerProbe(var2);
         }

         var3.reset();
         var3.resume();
      }
   }

   public synchronized void suspendCount(String var1) {
      if (this.isEnabled()) {
         String var2 = var1.intern();
         PerfLogger.ProbeStat var3 = this.probes.get(var2);
         if (var3 != null) {
            var3.suspend();
         } else {
            this.log.fine("Warning: \"" + this.fullName(var2) + "\" probe is not registered");
         }
      }
   }

   public synchronized void resumeCount(String var1) {
      if (this.isEnabled()) {
         String var2 = var1.intern();
         PerfLogger.ProbeStat var3 = this.probes.get(var2);
         if (var3 == null) {
            var3 = this.registerProbe(var2);
         }

         var3.resume();
      }
   }

   public synchronized void log(StringBuffer var1) {
      if (this.isEnabled()) {
         var1.append("=========== Performance Statistics =============\n");
         PerfLogger.ProbeStat var2 = this.getProbeStat("TOTALTIME");
         ArrayList var3 = new ArrayList();
         var3.addAll(this.probes.keySet());
         var1.append("\nTime:\n");
         Collections.sort(var3, this.timeComparator);

         for (String var5 : var3) {
            PerfLogger.ProbeStat var6 = this.getProbeStat(var5);
            var1.append(String.format("%s: %dms", this.fullName(var5), var6.totalTime));
            if (var2.totalTime > 0L) {
               var1.append(String.format(", %.2f%%%n", 100.0F * (float)var6.totalTime / (float)var2.totalTime));
            } else {
               var1.append("\n");
            }
         }

         var1.append("\nInvocations count:\n");
         Collections.sort(var3, this.countComparator);

         for (String var8 : var3) {
            var1.append(String.format("%s: %d%n", this.fullName(var8), this.getProbeStat(var8).count));
         }

         var1.append("================================================\n");
      }
   }

   public synchronized void log() {
      this.log(true);
   }

   private synchronized void log(boolean var1) {
      StringBuffer var2 = new StringBuffer();
      this.log(var2);
      if (var1) {
         this.log.fine(var2.toString());
      } else {
         System.out.println(var2.toString());
         System.out.flush();
      }
   }

   public static synchronized void logAll() {
      for (PerfLogger var1 : loggers.values()) {
         var1.log();
      }
   }

   public static final class ProbeStat {
      private final String probe;
      private int count;
      private long totalTime;
      private long startTime;
      private boolean isRunning = false;

      private ProbeStat(String var1) {
         this.probe = var1;
      }

      public String getProbe() {
         return this.probe;
      }

      public int getCount() {
         return this.count;
      }

      public long getTotalTime() {
         return this.totalTime;
      }

      private void reset() {
         this.count = 0;
         this.totalTime = this.startTime = 0L;
      }

      private void suspend() {
         if (this.isRunning) {
            this.totalTime = this.totalTime + (System.currentTimeMillis() - this.startTime);
            this.isRunning = false;
         }
      }

      private void resume() {
         this.isRunning = true;
         this.count++;
         this.startTime = System.currentTimeMillis();
      }

      private void snapshot() {
         if (this.isRunning) {
            this.totalTime = this.totalTime + (System.currentTimeMillis() - this.startTime);
            this.startTime = System.currentTimeMillis();
         }
      }

      @Override
      public String toString() {
         return super.toString() + "[count=" + this.count + ", time=" + this.totalTime + "]";
      }
   }
}
