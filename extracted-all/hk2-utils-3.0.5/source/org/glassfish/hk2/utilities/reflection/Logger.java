package org.glassfish.hk2.utilities.reflection;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;

public class Logger {
   private static final Logger INSTANCE = new Logger();
   private static final String HK2_LOGGER_NAME = "org.jvnet.hk2.logger";
   private static final boolean STDOUT_DEBUG = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.logger.debugToStdout", "false"));
      }
   });
   private final java.util.logging.Logger jdkLogger = java.util.logging.Logger.getLogger("org.jvnet.hk2.logger");

   private Logger() {
   }

   public static Logger getLogger() {
      return INSTANCE;
   }

   public void debug(String debuggingMessage) {
      this.jdkLogger.finer(debuggingMessage);
      if (STDOUT_DEBUG) {
         System.out.println("HK2DEBUG: " + debuggingMessage);
      }
   }

   public void debug(String debuggingMessage, Throwable th) {
      this.jdkLogger.log(Level.FINER, debuggingMessage, th);
      if (STDOUT_DEBUG) {
         System.out.println("HK2DEBUG: " + debuggingMessage);
         printThrowable(th);
      }
   }

   public void warning(String warningMessage) {
      this.jdkLogger.warning(warningMessage);
      if (STDOUT_DEBUG) {
         System.out.println("HK2DEBUG (Warning): " + warningMessage);
      }
   }

   public void warning(String warningMessage, Throwable th) {
      this.jdkLogger.log(Level.WARNING, warningMessage, th);
      if (STDOUT_DEBUG) {
         System.out.println("HK2DEBUG (Warning): " + warningMessage);
         printThrowable(th);
      }
   }

   public static void printThrowable(Throwable th) {
      int lcv = 0;

      for (Throwable cause = th; cause != null; cause = cause.getCause()) {
         System.out.println("HK2DEBUG: Throwable[" + lcv++ + "] message is " + cause.getMessage());
         cause.printStackTrace(System.out);
      }
   }

   public void debug(String className, String methodName, Throwable th) {
      this.jdkLogger.throwing(className, methodName, th);
      if (STDOUT_DEBUG) {
         System.out.println("HK2DEBUG: className=" + className + " methodName=" + methodName);
         printThrowable(th);
      }
   }
}
