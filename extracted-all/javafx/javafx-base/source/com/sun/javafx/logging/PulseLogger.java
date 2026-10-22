package com.sun.javafx.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

public class PulseLogger {
   public static final boolean PULSE_LOGGING_ENABLED;
   private static final String[] DEFAULT_LOGGERS = new String[]{"com.sun.javafx.logging.PrintLogger", "com.sun.javafx.logging.jfr.JFRPulseLogger"};
   private static final Logger[] loggers;

   public static void pulseStart() {
      for (Logger var3 : loggers) {
         var3.pulseStart();
      }
   }

   public static void pulseEnd() {
      for (Logger var3 : loggers) {
         var3.pulseEnd();
      }
   }

   public static void renderStart() {
      for (Logger var3 : loggers) {
         var3.renderStart();
      }
   }

   public static void renderEnd() {
      for (Logger var3 : loggers) {
         var3.renderEnd();
      }
   }

   public static void addMessage(String var0) {
      for (Logger var4 : loggers) {
         var4.addMessage(var0);
      }
   }

   public static void incrementCounter(String var0) {
      for (Logger var4 : loggers) {
         var4.incrementCounter(var0);
      }
   }

   public static void newPhase(String var0) {
      for (Logger var4 : loggers) {
         var4.newPhase(var0);
      }
   }

   public static void newInput(String var0) {
      for (Logger var4 : loggers) {
         var4.newInput(var0);
      }
   }

   public static boolean isPulseLoggingRequested() {
      return AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("javafx.pulseLogger")));
   }

   private static Logger loadLogger(String var0) {
      try {
         Class var1 = Class.forName(var0);
         if (var1 != null) {
            Method var2 = var1.getDeclaredMethod("createInstance");
            return (Logger)var2.invoke(null);
         }
      } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoClassDefFoundError var3) {
      }

      return null;
   }

   static {
      ArrayList var0 = new ArrayList();

      for (String var4 : DEFAULT_LOGGERS) {
         Logger var5 = loadLogger(var4);
         if (var5 != null) {
            var0.add(var5);
         }
      }

      loggers = var0.toArray(new Logger[var0.size()]);
      PULSE_LOGGING_ENABLED = loggers.length > 0;
   }
}
