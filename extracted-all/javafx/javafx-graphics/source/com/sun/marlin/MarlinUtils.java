package com.sun.marlin;

import com.sun.javafx.logging.PlatformLogger;
import java.lang.ref.Cleaner;

public final class MarlinUtils {
   private static final PlatformLogger LOG;
   private static final Cleaner cleaner;

   private MarlinUtils() {
   }

   public static void logInfo(String var0) {
      if (MarlinConst.USE_LOGGER) {
         LOG.info(var0);
      } else if (MarlinConst.ENABLE_LOGS) {
         System.out.print("INFO: ");
         System.out.println(var0);
      }
   }

   public static void logException(String var0, Throwable var1) {
      if (MarlinConst.USE_LOGGER) {
         LOG.warning(var0, var1);
      } else if (MarlinConst.ENABLE_LOGS) {
         System.out.print("WARNING: ");
         System.out.println(var0);
         var1.printStackTrace(System.err);
      }
   }

   public static ThreadGroup getRootThreadGroup() {
      ThreadGroup var0 = Thread.currentThread().getThreadGroup();

      for (ThreadGroup var1 = var0.getParent(); var1 != null; var1 = var1.getParent()) {
         var0 = var1;
      }

      return var0;
   }

   static Cleaner getCleaner() {
      return cleaner;
   }

   static {
      if (MarlinConst.USE_LOGGER) {
         LOG = PlatformLogger.getLogger("prism.marlin");
      } else {
         LOG = null;
      }

      cleaner = Cleaner.create();
   }
}
