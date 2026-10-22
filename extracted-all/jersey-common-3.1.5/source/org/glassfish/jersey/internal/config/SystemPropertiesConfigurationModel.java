package org.glassfish.jersey.internal.config;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.spi.ExternalConfigurationModel;

public class SystemPropertiesConfigurationModel implements ExternalConfigurationModel<Void> {
   private static final Logger LOGGER = Logger.getLogger(SystemPropertiesConfigurationModel.class.getName());
   private static final Map<Class, Function> converters = new HashMap<>();
   private final Map<String, Object> properties = new HashMap<>();
   private final AtomicBoolean gotProperties = new AtomicBoolean(false);
   private final List<String> propertyClassNames;

   public SystemPropertiesConfigurationModel(List<String> propertyClassNames) {
      this.propertyClassNames = propertyClassNames;
   }

   protected List<String> getPropertyClassNames() {
      return this.propertyClassNames;
   }

   @Override
   public <T> T as(String name, Class<T> clazz) {
      if (converters.get(clazz) == null) {
         throw new IllegalArgumentException("Unsupported class type");
      } else {
         return name != null && clazz != null && this.hasProperty(name) ? clazz.cast(converters.get(clazz).apply(getSystemProperty(name))) : null;
      }
   }

   @Override
   public <T> Optional<T> getOptionalProperty(String name, Class<T> clazz) {
      return Optional.ofNullable(this.as(name, clazz));
   }

   @Override
   public ExternalConfigurationModel mergeProperties(Map<String, Object> inputProperties) {
      inputProperties.forEach((k, v) -> this.properties.put(k, v));
      return this;
   }

   public Void getConfig() {
      return null;
   }

   @Override
   public boolean isProperty(String name) {
      String property = getSystemProperty(name);
      return property != null && ("0".equals(property) || "1".equals(property) || "true".equalsIgnoreCase(property) || "false".equalsIgnoreCase(property));
   }

   public RuntimeType getRuntimeType() {
      return null;
   }

   public Map<String, Object> getProperties() {
      Boolean allowSystemPropertiesProvider = this.as("jersey.config.allowSystemPropertiesProvider", Boolean.class);
      if (!Boolean.TRUE.equals(allowSystemPropertiesProvider)) {
         LOGGER.finer(LocalizationMessages.WARNING_PROPERTIES());
         return this.properties;
      } else {
         if (this.gotProperties.compareAndSet(false, true)) {
            try {
               AccessController.doPrivileged(PropertiesHelper.getSystemProperties()).forEach((k, v) -> this.properties.put(String.valueOf(k), v));
            } catch (SecurityException var3) {
               LOGGER.warning(LocalizationMessages.SYSTEM_PROPERTIES_WARNING());
               return this.getExpectedSystemProperties();
            }
         }

         return this.properties;
      }
   }

   private Map<String, Object> getExpectedSystemProperties() {
      Map<String, Object> result = new HashMap<>();

      for (String propertyClass : this.getPropertyClassNames()) {
         mapFieldsToProperties(result, AccessController.doPrivileged(ReflectionHelper.classForNamePA(propertyClass)));
      }

      return result;
   }

   private static <T> void mapFieldsToProperties(Map<String, Object> properties, Class<T> clazz) {
      if (clazz != null) {
         Field[] fields = AccessController.doPrivileged(ReflectionHelper.getDeclaredFieldsPA(clazz));

         for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(String.class)) {
               String propertyValue = getPropertyNameByField(field);
               if (propertyValue != null) {
                  String value = getSystemProperty(propertyValue);
                  if (value != null) {
                     properties.put(propertyValue, value);
                  }
               }
            }
         }
      }
   }

   private static String getPropertyNameByField(Field field) {
      return AccessController.doPrivileged((PrivilegedAction<String>)(() -> {
         try {
            return (String)field.get(null);
         } catch (IllegalAccessException var2) {
            LOGGER.warning(var2.getLocalizedMessage());
            return null;
         }
      }));
   }

   private static String getSystemProperty(String name) {
      return AccessController.doPrivileged(PropertiesHelper.getSystemProperty(name));
   }

   public Object getProperty(String name) {
      return getSystemProperty(name);
   }

   public Collection<String> getPropertyNames() {
      return AccessController.doPrivileged(PropertiesHelper.getSystemProperties()).stringPropertyNames();
   }

   public boolean isEnabled(Feature feature) {
      return false;
   }

   public boolean isEnabled(Class<? extends Feature> featureClass) {
      return false;
   }

   public boolean isRegistered(Object component) {
      return false;
   }

   public boolean isRegistered(Class<?> componentClass) {
      return false;
   }

   public Map<Class<?>, Integer> getContracts(Class<?> componentClass) {
      return null;
   }

   public Set<Class<?>> getClasses() {
      return null;
   }

   public Set<Object> getInstances() {
      return null;
   }

   static {
      converters.put(String.class, s -> s);
      converters.put(Integer.class, s -> Integer.valueOf(s));
      converters.put(Long.class, s -> Long.parseLong(s));
      converters.put(Boolean.class, s -> s.equalsIgnoreCase("1") ? true : Boolean.parseBoolean(s));
   }
}
