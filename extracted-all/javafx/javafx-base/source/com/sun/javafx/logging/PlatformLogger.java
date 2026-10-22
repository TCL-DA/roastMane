package com.sun.javafx.logging;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PlatformLogger implements java.lang.System.Logger {
   private static final Map<String, WeakReference<PlatformLogger>> loggers = new HashMap<>();
   private final java.lang.System.Logger loggerProxy;
   private boolean loggingEnabled = true;

   private java.lang.System.Logger.Level getSystemLoggerLevel(PlatformLogger.Level var1) {
      switch (var1) {
         case ALL:
            return java.lang.System.Logger.Level.ALL;
         case FINEST:
            return java.lang.System.Logger.Level.TRACE;
         case FINER:
            return java.lang.System.Logger.Level.TRACE;
         case FINE:
            return java.lang.System.Logger.Level.DEBUG;
         case INFO:
            return java.lang.System.Logger.Level.INFO;
         case WARNING:
            return java.lang.System.Logger.Level.WARNING;
         case SEVERE:
            return java.lang.System.Logger.Level.ERROR;
         case OFF:
            return java.lang.System.Logger.Level.OFF;
         default:
            return java.lang.System.Logger.Level.ALL;
      }
   }

   public static synchronized PlatformLogger getLogger(String var0) {
      PlatformLogger var1 = null;
      WeakReference var2 = loggers.get(var0);
      if (var2 != null) {
         var1 = (PlatformLogger)var2.get();
      }

      if (var1 == null) {
         var1 = new PlatformLogger(System.getLogger(var0));
         loggers.put(var0, new WeakReference<>(var1));
      }

      return var1;
   }

   protected PlatformLogger(java.lang.System.Logger var1) {
      this.loggerProxy = var1;
   }

   @Override
   public String getName() {
      return this.loggerProxy.getName();
   }

   @Override
   public boolean isLoggable(java.lang.System.Logger.Level var1) {
      return this.loggerProxy.isLoggable(var1);
   }

   @Override
   public void log(java.lang.System.Logger.Level var1, ResourceBundle var2, String var3, Object... var4) {
      this.loggerProxy.log(var1, var2, var3, var4);
   }

   @Override
   public void log(java.lang.System.Logger.Level var1, ResourceBundle var2, String var3, Throwable var4) {
      this.loggerProxy.log(var1, var2, var3, var4);
   }

   public boolean isLoggable(PlatformLogger.Level var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.loggerProxy.isLoggable(this.getSystemLoggerLevel(var1));
      }
   }

   public void severe(String var1) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.ERROR, var1, (Object[])null);
      }
   }

   public void severe(String var1, Throwable var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.ERROR, var1, var2);
      }
   }

   public void severe(String var1, Object... var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.ERROR, var1, var2);
      }
   }

   public void warning(String var1) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.WARNING, var1, (Object[])null);
      }
   }

   public void warning(String var1, Throwable var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.WARNING, var1, var2);
      }
   }

   public void warning(String var1, Object... var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.WARNING, var1, var2);
      }
   }

   public void info(String var1) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.INFO, var1, (Object[])null);
      }
   }

   public void info(String var1, Throwable var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.INFO, var1, var2);
      }
   }

   public void info(String var1, Object... var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.INFO, var1, var2);
      }
   }

   public void fine(String var1) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.DEBUG, var1, (Object[])null);
      }
   }

   public void fine(String var1, Throwable var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.DEBUG, var1, var2);
      }
   }

   public void fine(String var1, Object... var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.DEBUG, var1, var2);
      }
   }

   public void finer(String var1) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.TRACE, var1, (Object[])null);
      }
   }

   public void finer(String var1, Throwable var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.TRACE, var1, var2);
      }
   }

   public void finer(String var1, Object... var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.TRACE, var1, var2);
      }
   }

   public void finest(String var1) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.TRACE, var1, (Object[])null);
      }
   }

   public void finest(String var1, Throwable var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.TRACE, var1, var2);
      }
   }

   public void finest(String var1, Object... var2) {
      if (this.loggingEnabled) {
         this.loggerProxy.log(java.lang.System.Logger.Level.TRACE, var1, var2);
      }
   }

   public void enableLogging() {
      this.loggingEnabled = true;
   }

   public void disableLogging() {
      this.loggingEnabled = false;
   }

   public static enum Level {
      ALL(java.lang.System.Logger.Level.ALL),
      FINEST(java.lang.System.Logger.Level.TRACE),
      FINER(java.lang.System.Logger.Level.TRACE),
      FINE(java.lang.System.Logger.Level.DEBUG),
      INFO(java.lang.System.Logger.Level.INFO),
      WARNING(java.lang.System.Logger.Level.WARNING),
      SEVERE(java.lang.System.Logger.Level.ERROR),
      OFF(java.lang.System.Logger.Level.OFF);

      final java.lang.System.Logger.Level systemLevel;

      private Level(java.lang.System.Logger.Level var3) {
         this.systemLevel = var3;
      }
   }
}
