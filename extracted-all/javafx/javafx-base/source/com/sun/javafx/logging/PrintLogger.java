package com.sun.javafx.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class PrintLogger extends Logger {
   private static long THRESHOLD = (long)AccessController.doPrivileged(
         (PrivilegedAction<Integer>)(() -> Integer.getInteger("javafx.pulseLogger.threshold", 17))
      )
      .intValue();
   private static final int EXIT_ON_PULSE = AccessController.doPrivileged(
      (PrivilegedAction<Integer>)(() -> Integer.getInteger("javafx.pulseLogger.exitOnPulse", 0))
   );
   private int pulseCount = 1;
   private static final int INTER_PULSE_DATA = -1;
   private volatile int wrapCount = 0;
   private volatile PrintLogger.PulseData fxData;
   private volatile PrintLogger.PulseData renderData;
   private long lastPulseStartTime;
   private Thread fxThread;
   private final ThreadLocal<PrintLogger.ThreadLocalData> phaseData = new ThreadLocal<PrintLogger.ThreadLocalData>() {
      public PrintLogger.ThreadLocalData initialValue() {
         return PrintLogger.this.new ThreadLocalData();
      }
   };
   private PrintLogger.PulseData head = new PrintLogger.PulseData();
   private PrintLogger.PulseData tail = new PrintLogger.PulseData();
   private AtomicInteger active;
   private static final int AVAILABLE = 0;
   private static final int INCOMPLETE = 1;
   private static final int COMPLETE = 2;

   private PrintLogger() {
      this.head.next = this.tail;
      this.active = new AtomicInteger(0);
   }

   public static Logger createInstance() {
      boolean var0 = PulseLogger.isPulseLoggingRequested();
      return var0 ? new PrintLogger() : null;
   }

   private PrintLogger.PulseData allocate(int var1) {
      PrintLogger.PulseData var2;
      if (this.head != this.tail && this.head.state == 0) {
         var2 = this.head;
         this.head = this.head.next;
         var2.next = null;
      } else {
         var2 = new PrintLogger.PulseData();
      }

      this.tail.next = var2;
      this.tail = var2;
      var2.init(var1);
      return var2;
   }

   @Override
   public void pulseStart() {
      if (this.fxThread == null) {
         this.fxThread = Thread.currentThread();
      }

      if (this.fxData != null) {
         this.fxData.state = 2;
         if (this.active.incrementAndGet() == 1) {
            this.fxData.printAndReset();
            this.active.decrementAndGet();
         }
      }

      this.fxData = this.allocate(this.pulseCount++);
      if (this.lastPulseStartTime > 0L) {
         this.fxData.interval = (this.fxData.startTime - this.lastPulseStartTime) / 1000000L;
      }

      this.lastPulseStartTime = this.fxData.startTime;
   }

   @Override
   public void renderStart() {
      this.newPhase(null);
      this.fxData.pushedRender = true;
      this.renderData = this.fxData;
      this.active.incrementAndGet();
   }

   @Override
   public void pulseEnd() {
      if (this.fxData != null && !this.fxData.pushedRender) {
         this.fxData.state = 2;
         if (this.active.incrementAndGet() == 1) {
            this.fxData.printAndReset();
            this.active.decrementAndGet();
         }
      }

      this.fxData = null;
   }

   @Override
   public void renderEnd() {
      this.newPhase(null);
      this.renderData.state = 2;

      while (true) {
         this.renderData.printAndReset();
         if (this.active.decrementAndGet() == 0) {
            this.renderData = null;
            return;
         }

         this.renderData = this.renderData.next;
      }
   }

   @Override
   public void addMessage(String var1) {
      PrintLogger.PulseData var2;
      if (this.fxThread != null && Thread.currentThread() != this.fxThread) {
         var2 = this.renderData;
      } else {
         if (this.fxData == null) {
            this.fxData = this.allocate(-1);
         }

         var2 = this.fxData;
      }

      if (var2 != null) {
         var2.message.append("T").append(Thread.currentThread().getId()).append(" : ").append(var1).append("\n");
      }
   }

   @Override
   public void incrementCounter(String var1) {
      PrintLogger.PulseData var2;
      if (this.fxThread != null && Thread.currentThread() != this.fxThread) {
         var2 = this.renderData;
      } else {
         if (this.fxData == null) {
            this.fxData = this.allocate(-1);
         }

         var2 = this.fxData;
      }

      if (var2 != null) {
         Map var3 = var2.counters;
         PrintLogger.Counter var4 = (PrintLogger.Counter)var3.get(var1);
         if (var4 == null) {
            var4 = new PrintLogger.Counter();
            var3.put(var1, var4);
         }

         var4.value++;
      }
   }

   @Override
   public void newPhase(String var1) {
      long var2 = System.nanoTime();
      PrintLogger.ThreadLocalData var4 = this.phaseData.get();
      if (var4.phaseName != null) {
         PrintLogger.PulseData var5 = Thread.currentThread() == this.fxThread ? this.fxData : this.renderData;
         if (var5 != null) {
            var5.message
               .append("T")
               .append(Thread.currentThread().getId())
               .append(" (")
               .append((var4.phaseStart - var5.startTime) / 1000000L)
               .append(" +")
               .append((var2 - var4.phaseStart) / 1000000L)
               .append("ms): ")
               .append(var4.phaseName)
               .append("\n");
         }
      }

      var4.phaseName = var1;
      var4.phaseStart = var2;
   }

   private static class Counter {
      int value;
   }

   private final class PulseData {
      PrintLogger.PulseData next;
      volatile int state = 0;
      long startTime;
      long interval;
      int pulseCount;
      boolean pushedRender;
      StringBuffer message = new StringBuffer();
      Map<String, PrintLogger.Counter> counters = new ConcurrentHashMap<>();

      void init(int var1) {
         this.state = 1;
         this.pulseCount = var1;
         this.startTime = System.nanoTime();
         this.interval = 0L;
         this.pushedRender = false;
      }

      void printAndReset() {
         long var1 = System.nanoTime();
         long var3 = (var1 - this.startTime) / 1000000L;
         if (this.state != 2) {
            System.err.println("\nWARNING: logging incomplete state");
         }

         if (var3 <= PrintLogger.THRESHOLD) {
            if (this.pulseCount != -1) {
               System.err.print((PrintLogger.this.wrapCount++ % 10 == 0 ? "\n[" : "[") + this.pulseCount + " " + this.interval + "ms:" + var3 + "ms]");
            }
         } else {
            if (this.pulseCount == -1) {
               System.err.println("\n\nINTER PULSE LOG DATA");
            } else {
               System.err.print("\n\nPULSE: " + this.pulseCount + " [" + this.interval + "ms:" + var3 + "ms]");
               if (!this.pushedRender) {
                  System.err.print(" Required No Rendering");
               }

               System.err.println();
            }

            System.err.print(this.message);
            if (!this.counters.isEmpty()) {
               System.err.println("Counters:");
               ArrayList var5 = new ArrayList<>(this.counters.entrySet());
               Collections.sort(var5, (var0, var1x) -> ((String)var0.getKey()).compareTo((String)var1x.getKey()));

               for (Entry var7 : var5) {
                  System.err.println("\t" + (String)var7.getKey() + ": " + ((PrintLogger.Counter)var7.getValue()).value);
               }
            }

            PrintLogger.this.wrapCount = 0;
         }

         this.message.setLength(0);
         this.counters.clear();
         this.state = 0;
         if (PrintLogger.EXIT_ON_PULSE > 0 && this.pulseCount >= PrintLogger.EXIT_ON_PULSE) {
            System.err.println("Exiting after pulse #" + this.pulseCount);
            System.exit(0);
         }
      }
   }

   class ThreadLocalData {
      String phaseName;
      long phaseStart;
   }
}
