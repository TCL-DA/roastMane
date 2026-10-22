package org.jvnet.mimepull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CleanUpExecutorFactory {
   private static final Logger LOGGER = Logger.getLogger(CleanUpExecutorFactory.class.getName());

   protected CleanUpExecutorFactory() {
   }

   public static CleanUpExecutorFactory newInstance() {
      try {
         return FactoryFinder.find(CleanUpExecutorFactory.class);
      } catch (ReflectiveOperationException var1) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Not using " + CleanUpExecutorFactory.class.getName(), (Throwable)var1);
         }

         return null;
      }
   }

   public abstract ScheduledExecutorService getScheduledExecutorService();
}
