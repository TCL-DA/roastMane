package org.apache.logging.log4j.util;

import aQute.bnd.annotation.spi.ServiceProvider;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

@ServiceProvider(
   value = PropertySource.class,
   resolution = "optional"
)
public class SystemPropertiesPropertySource implements PropertySource {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int DEFAULT_PRIORITY = 0;
   private static final String PREFIX = "log4j2.";
   private static final PropertySource INSTANCE = new SystemPropertiesPropertySource();

   public static PropertySource provider() {
      return INSTANCE;
   }

   public static String getSystemProperty(final String key, final String defaultValue) {
      String value = INSTANCE.getProperty(key);
      return value != null ? value : defaultValue;
   }

   private static void logException(final SecurityException error) {
      LOGGER.error("The Java system properties are not available to Log4j due to security restrictions.", (Throwable)error);
   }

   private static void logException(final SecurityException error, final String key) {
      LOGGER.error("The Java system property {} is not available to Log4j due to security restrictions.", key, error);
   }

   @Override
   public int getPriority() {
      return 0;
   }

   @Override
   public void forEach(final BiConsumer<String, String> action) {
      Properties properties;
      try {
         properties = System.getProperties();
      } catch (SecurityException var10) {
         logException(var10);
         return;
      }

      Object[] keySet;
      synchronized (properties) {
         keySet = properties.keySet().toArray();
      }

      for (Object key : keySet) {
         String keyStr = Objects.toString(key, null);
         action.accept(keyStr, properties.getProperty(keyStr));
      }
   }

   @Override
   public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
      return "log4j2." + PropertySource.Util.joinAsCamelCase(tokens);
   }

   @Override
   public Collection<String> getPropertyNames() {
      try {
         return System.getProperties().stringPropertyNames();
      } catch (SecurityException var2) {
         logException(var2);
         return PropertySource.super.getPropertyNames();
      }
   }

   @Override
   public String getProperty(final String key) {
      try {
         return System.getProperty(key);
      } catch (SecurityException var3) {
         logException(var3, key);
         return PropertySource.super.getProperty(key);
      }
   }

   @Override
   public boolean containsProperty(final String key) {
      return this.getProperty(key) != null;
   }
}
