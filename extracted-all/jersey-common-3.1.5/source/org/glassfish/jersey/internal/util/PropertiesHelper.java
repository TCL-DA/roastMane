package org.glassfish.jersey.internal.util;

import jakarta.ws.rs.RuntimeType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.LocalizationMessages;

public final class PropertiesHelper {
   private static final Logger LOGGER = Logger.getLogger(PropertiesHelper.class.getName());
   private static final boolean METAINF_SERVICES_LOOKUP_DISABLE_DEFAULT = false;
   private static final boolean JAXRS_SERVICE_LOADING_ENABLE_DEFAULT = true;
   private static final String RUNTIME_SERVER_LOWER = RuntimeType.SERVER.name().toLowerCase(Locale.ROOT);
   private static final String RUNTIME_CLIENT_LOWER = RuntimeType.CLIENT.name().toLowerCase(Locale.ROOT);

   public static PrivilegedAction<Properties> getSystemProperties() {
      return new PrivilegedAction<Properties>() {
         public Properties run() {
            return System.getProperties();
         }
      };
   }

   public static PrivilegedAction<String> getSystemProperty(final String name) {
      return new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(name);
         }
      };
   }

   public static PrivilegedAction<String> getSystemProperty(final String name, final String def) {
      return new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(name, def);
         }
      };
   }

   public static <T> T getValue(Map<String, ?> properties, String key, T defaultValue, Map<String, String> legacyMap) {
      return getValue(properties, null, key, defaultValue, legacyMap);
   }

   public static <T> T getValue(Map<String, ?> properties, RuntimeType runtimeType, String key, T defaultValue, Map<String, String> legacyMap) {
      return getValue(properties, runtimeType, key, defaultValue, (Class<T>)defaultValue.getClass(), legacyMap);
   }

   public static <T> T getValue(Map<String, ?> properties, String key, T defaultValue, Class<T> type, Map<String, String> legacyMap) {
      return getValue(properties, null, key, defaultValue, type, legacyMap);
   }

   public static <T> T getValue(Map<String, ?> properties, RuntimeType runtimeType, String key, T defaultValue, Class<T> type, Map<String, String> legacyMap) {
      T value = getValue(properties, runtimeType, key, type, legacyMap);
      if (value == null) {
         value = defaultValue;
      }

      return value;
   }

   public static <T> T getValue(Map<String, ?> properties, String key, Class<T> type, Map<String, String> legacyMap) {
      return getValue(properties, null, key, type, legacyMap);
   }

   public static <T> T getValue(Map<String, ?> properties, RuntimeType runtimeType, String key, Class<T> type, Map<String, String> legacyMap) {
      Object value = null;
      if (runtimeType != null) {
         String runtimeAwareKey = getPropertyNameForRuntime(key, runtimeType);
         if (key.equals(runtimeAwareKey)) {
            runtimeAwareKey = key + "." + toLowerCase(runtimeType);
         }

         value = properties.get(runtimeAwareKey);
      }

      if (value == null) {
         value = properties.get(key);
      }

      if (value == null) {
         value = getLegacyFallbackValue(properties, legacyMap, key);
      }

      return value == null ? null : convertValue(value, type);
   }

   public static String getPropertyNameForRuntime(String key, RuntimeType runtimeType) {
      if (runtimeType != null && key.startsWith("jersey.config")) {
         RuntimeType[] types = RuntimeType.values();

         for (RuntimeType type : types) {
            if (key.startsWith("jersey.config." + toLowerCase(type))) {
               return key;
            }
         }

         return key.replace("jersey.config", "jersey.config." + toLowerCase(runtimeType));
      } else {
         return key;
      }
   }

   private static Object getLegacyFallbackValue(Map<String, ?> properties, Map<String, String> legacyFallbackMap, String key) {
      if (legacyFallbackMap != null && legacyFallbackMap.containsKey(key)) {
         String fallbackKey = legacyFallbackMap.get(key);
         Object value = properties.get(fallbackKey);
         if (value != null && LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.config(LocalizationMessages.PROPERTIES_HELPER_DEPRECATED_PROPERTY_NAME(fallbackKey, key));
         }

         return value;
      } else {
         return null;
      }
   }

   public static <T> T convertValue(Object value, Class<T> type) {
      if ((type.equals(Integer.class) || type.equals(int.class)) && Number.class.isInstance(value)) {
         return (T)((Number)value).intValue();
      } else if ((type.equals(Long.class) || type.equals(long.class)) && Number.class.isInstance(value)) {
         return (T)((Number)value).longValue();
      } else if (type.isInstance(value)) {
         return type.cast(value);
      } else {
         Constructor constructor = AccessController.doPrivileged(ReflectionHelper.getStringConstructorPA(type));
         if (constructor != null) {
            try {
               return type.cast(constructor.newInstance(value));
            } catch (Exception var6) {
            }
         }

         Method valueOf = AccessController.doPrivileged(ReflectionHelper.getValueOfStringMethodPA(type));
         if (valueOf != null) {
            try {
               return type.cast(valueOf.invoke(null, value));
            } catch (Exception var5) {
            }
         }

         if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning(LocalizationMessages.PROPERTIES_HELPER_GET_VALUE_NO_TRANSFORM(String.valueOf(value), value.getClass().getName(), type.getName()));
         }

         return null;
      }
   }

   public static boolean isMetaInfServicesEnabled(Map<String, Object> properties, RuntimeType runtimeType) {
      boolean disableMetaInfServicesLookup = false;
      if (properties != null) {
         disableMetaInfServicesLookup = CommonProperties.getValue(properties, runtimeType, "jersey.config.disableMetainfServicesLookup", false, Boolean.class);
      }

      return !disableMetaInfServicesLookup;
   }

   public static boolean isJaxRsServiceLoadingEnabled(Map<String, Object> properties) {
      boolean enableServicesLoading = true;
      if (properties != null) {
         enableServicesLoading = CommonProperties.getValue(properties, "jakarta.ws.rs.loadServices", true);
      }

      return enableServicesLoading;
   }

   public static boolean isProperty(Map<String, Object> properties, String name) {
      return properties.containsKey(name) && isProperty(properties.get(name));
   }

   public static boolean isProperty(Object value) {
      return value instanceof Boolean ? Boolean.class.cast(value) : value != null && Boolean.parseBoolean(value.toString());
   }

   private static String toLowerCase(RuntimeType runtimeType) {
      switch (runtimeType) {
         case CLIENT:
            return RUNTIME_CLIENT_LOWER;
         default:
            return RUNTIME_SERVER_LOWER;
      }
   }

   private PropertiesHelper() {
   }
}
