package org.eclipse.jetty.util;

public class ProcessorUtils {
   public static final String AVAILABLE_PROCESSORS = "JETTY_AVAILABLE_PROCESSORS";
   private static int __availableProcessors = init();

   static int init() {
      String processors = System.getProperty("JETTY_AVAILABLE_PROCESSORS", System.getenv("JETTY_AVAILABLE_PROCESSORS"));
      if (processors != null) {
         try {
            return Integer.parseInt(processors);
         } catch (NumberFormatException var2) {
         }
      }

      return Runtime.getRuntime().availableProcessors();
   }

   public static int availableProcessors() {
      return __availableProcessors;
   }

   public static void setAvailableProcessors(int processors) {
      if (processors < 1) {
         throw new IllegalArgumentException("Invalid number of processors: " + processors);
      } else {
         __availableProcessors = processors;
      }
   }
}
