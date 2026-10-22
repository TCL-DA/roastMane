package org.apache.logging.log4j.util;

import aQute.bnd.annotation.spi.ServiceProvider;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

@ServiceProvider(
   value = PropertySource.class,
   resolution = "optional"
)
public class EnvironmentPropertySource implements PropertySource {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String PREFIX = "LOG4J_";
   private static final int DEFAULT_PRIORITY = 100;
   private static final PropertySource INSTANCE = new EnvironmentPropertySource();

   public static PropertySource provider() {
      return INSTANCE;
   }

   @Override
   public int getPriority() {
      return 100;
   }

   private static void logException(final SecurityException error) {
      LOGGER.error("The environment variables are not available to Log4j due to security restrictions.", (Throwable)error);
   }

   private static void logException(final SecurityException error, final String key) {
      LOGGER.error("The environment variable {} is not available to Log4j due to security restrictions.", key, error);
   }

   @Override
   public void forEach(final BiConsumer<String, String> action) {
      Map<String, String> getenv;
      try {
         getenv = System.getenv();
      } catch (SecurityException var6) {
         logException(var6);
         return;
      }

      for (Entry<String, String> entry : getenv.entrySet()) {
         String key = entry.getKey();
         if (key.startsWith("LOG4J_")) {
            action.accept(key.substring("LOG4J_".length()), entry.getValue());
         }
      }
   }

   @Override
   public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
      StringBuilder sb = new StringBuilder("LOG4J");
      boolean empty = true;

      for (CharSequence token : tokens) {
         empty = false;
         sb.append('_');

         for (int i = 0; i < token.length(); i++) {
            sb.append(Character.toUpperCase(token.charAt(i)));
         }
      }

      return empty ? null : sb.toString();
   }

   @Override
   public Collection<String> getPropertyNames() {
      try {
         return System.getenv().keySet();
      } catch (SecurityException var2) {
         logException(var2);
         return PropertySource.super.getPropertyNames();
      }
   }

   @Override
   public String getProperty(final String key) {
      try {
         return System.getenv(key);
      } catch (SecurityException var3) {
         logException(var3, key);
         return PropertySource.super.getProperty(key);
      }
   }

   @Override
   public boolean containsProperty(final String key) {
      try {
         return System.getenv().containsKey(key);
      } catch (SecurityException var3) {
         logException(var3, key);
         return PropertySource.super.containsProperty(key);
      }
   }
}
