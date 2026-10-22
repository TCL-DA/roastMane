package jakarta.activation;

import java.util.logging.Level;
import java.util.logging.Logger;

class LogSupport {
   private static boolean debug = false;
   private static Logger logger;
   private static final Level level = Level.FINE;

   private LogSupport() {
   }

   public static void log(String msg) {
      if (debug) {
         System.out.println(msg);
      }

      logger.log(level, msg);
   }

   public static void log(String msg, Throwable t) {
      if (debug) {
         System.out.println(msg + "; Exception: " + t);
      }

      logger.log(level, msg, t);
   }

   public static boolean isLoggable() {
      return debug || logger.isLoggable(level);
   }

   static {
      try {
         debug = Boolean.getBoolean("jakarta.activation.debug") || Boolean.getBoolean("javax.activation.debug");
      } catch (Throwable var1) {
      }

      logger = Logger.getLogger("jakarta.activation");
   }
}
