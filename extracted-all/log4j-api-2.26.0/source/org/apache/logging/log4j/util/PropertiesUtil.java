package org.apache.logging.log4j.util;

import aQute.bnd.annotation.spi.ServiceConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

@ServiceConsumer(
   value = PropertySource.class,
   resolution = "optional",
   cardinality = "multiple"
)
public final class PropertiesUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String LOG4J_PROPERTIES_FILE_NAME = "log4j2.component.properties";
   private static final String LOG4J_SYSTEM_PROPERTIES_FILE_NAME = "log4j2.system.properties";
   private static final Lazy<PropertiesUtil> COMPONENT_PROPERTIES = Lazy.lazy(() -> new PropertiesUtil("log4j2.component.properties", false));
   private static final Pattern DURATION_PATTERN = Pattern.compile("([+-]?\\d+)\\s*(\\w+)?", 2);
   private final PropertiesUtil.Environment environment;

   public PropertiesUtil(final Properties props) {
      this(new PropertiesPropertySource(props));
   }

   public PropertiesUtil(final String propertiesFileName) {
      this(propertiesFileName, true);
   }

   private PropertiesUtil(final String propertiesFileName, final boolean useTccl) {
      this(new PropertyFilePropertySource(propertiesFileName, useTccl));
   }

   PropertiesUtil(final PropertySource source) {
      this.environment = new PropertiesUtil.Environment(source);
   }

   static Properties loadClose(final InputStream in, final Object source) {
      Properties props = new Properties();
      if (null != in) {
         try {
            props.load(in);
         } catch (IOException var12) {
            LOGGER.error("Unable to read source `{}`", source, var12);
         } finally {
            try {
               in.close();
            } catch (IOException var11) {
               LOGGER.error("Unable to close source `{}`", source, var11);
            }
         }
      }

      return props;
   }

   public static PropertiesUtil getProperties() {
      return COMPONENT_PROPERTIES.get();
   }

   public void addPropertySource(final PropertySource propertySource) {
      this.environment.addPropertySource(Objects.requireNonNull(propertySource));
   }

   public void removePropertySource(final PropertySource propertySource) {
      this.environment.removePropertySource(Objects.requireNonNull(propertySource));
   }

   public boolean hasProperty(final String name) {
      return this.environment.containsKey(name);
   }

   public boolean getBooleanProperty(final String name) {
      return this.getBooleanProperty(name, false);
   }

   public boolean getBooleanProperty(final String name, final boolean defaultValue) {
      String prop = this.getStringProperty(name);
      return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
   }

   public boolean getBooleanProperty(final String name, final boolean defaultValueIfAbsent, final boolean defaultValueIfPresent) {
      String prop = this.getStringProperty(name);
      return prop == null ? defaultValueIfAbsent : (prop.isEmpty() ? defaultValueIfPresent : "true".equalsIgnoreCase(prop));
   }

   public Boolean getBooleanProperty(final String[] prefixes, final String key, final Supplier<Boolean> supplier) {
      for (String prefix : prefixes) {
         if (this.hasProperty(prefix + key)) {
            return this.getBooleanProperty(prefix + key);
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public Charset getCharsetProperty(final String name) {
      return this.getCharsetProperty(name, Charset.defaultCharset());
   }

   public Charset getCharsetProperty(final String name, final Charset defaultValue) {
      String charsetName = this.getStringProperty(name);
      if (charsetName == null) {
         return defaultValue;
      } else if (Charset.isSupported(charsetName)) {
         return Charset.forName(charsetName);
      } else {
         ResourceBundle bundle = getCharsetsResourceBundle();
         if (bundle.containsKey(name)) {
            String mapped = bundle.getString(name);
            if (Charset.isSupported(mapped)) {
               return Charset.forName(mapped);
            }
         }

         LOGGER.warn("Unable to read charset `{}` from property `{}`. Falling back to the default: `{}`", charsetName, name, defaultValue);
         return defaultValue;
      }
   }

   public double getDoubleProperty(final String name, final double defaultValue) {
      String prop = this.getStringProperty(name);
      if (prop != null) {
         try {
            return Double.parseDouble(prop);
         } catch (NumberFormatException var6) {
            LOGGER.warn("Unable to read double `{}` from property `{}`. Falling back to the default: `{}`", prop, name, defaultValue, var6);
         }
      }

      return defaultValue;
   }

   public int getIntegerProperty(final String name, final int defaultValue) {
      String prop = this.getStringProperty(name);
      if (prop != null) {
         try {
            return Integer.parseInt(prop.trim());
         } catch (NumberFormatException var5) {
            LOGGER.warn("Unable to read int `{}` from property `{}`. Falling back to the default: `{}`", prop, name, defaultValue, var5);
         }
      }

      return defaultValue;
   }

   public Integer getIntegerProperty(final String[] prefixes, final String key, final Supplier<Integer> supplier) {
      for (String prefix : prefixes) {
         if (this.hasProperty(prefix + key)) {
            return this.getIntegerProperty(prefix + key, 0);
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public long getLongProperty(final String name, final long defaultValue) {
      String prop = this.getStringProperty(name);
      if (prop != null) {
         try {
            return Long.parseLong(prop);
         } catch (NumberFormatException var6) {
            LOGGER.warn("Unable to read long `{}` from property `{}`. Falling back to the default: `{}`", prop, name, defaultValue, var6);
         }
      }

      return defaultValue;
   }

   public Long getLongProperty(final String[] prefixes, final String key, final Supplier<Long> supplier) {
      for (String prefix : prefixes) {
         if (this.hasProperty(prefix + key)) {
            return this.getLongProperty(prefix + key, 0L);
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public Duration getDurationProperty(final String name, final Duration defaultValue) {
      String prop = this.getStringProperty(name);

      try {
         return parseDuration(prop);
      } catch (IllegalArgumentException var5) {
         LOGGER.warn(
            "Unable to read duration `{}` from property `{}`.\nExpected format 'n unit', where 'n' is an integer and 'unit' is one of: {}.",
            prop,
            name,
            PropertiesUtil.TimeUnit.getValidUnits().collect(Collectors.joining(", ")),
            var5
         );
         return defaultValue;
      }
   }

   public Duration getDurationProperty(final String[] prefixes, final String key, final Supplier<Duration> supplier) {
      for (String prefix : prefixes) {
         if (this.hasProperty(prefix + key)) {
            return this.getDurationProperty(prefix + key, null);
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public String getStringProperty(final String[] prefixes, final String key, final Supplier<String> supplier) {
      for (String prefix : prefixes) {
         String result = this.getStringProperty(prefix + key);
         if (result != null) {
            return result;
         }
      }

      return supplier != null ? supplier.get() : null;
   }

   public String getStringProperty(final String name) {
      return this.environment.get(name);
   }

   public String getStringProperty(final String name, final String defaultValue) {
      String prop = this.getStringProperty(name);
      return prop == null ? defaultValue : prop;
   }

   public static Properties getSystemProperties() {
      try {
         return new Properties(System.getProperties());
      } catch (SecurityException var1) {
         LOGGER.error("Unable to access system properties.", (Throwable)var1);
         return new Properties();
      }
   }

   @Deprecated
   public void reload() {
   }

   public static Properties extractSubset(final Properties properties, final String prefix) {
      Properties subset = new Properties();
      if (prefix != null && !prefix.isEmpty()) {
         String prefixToMatch = prefix.charAt(prefix.length() - 1) != '.' ? prefix + '.' : prefix;
         Collection<String> keys = new ArrayList<>();

         for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefixToMatch)) {
               subset.setProperty(key.substring(prefixToMatch.length()), properties.getProperty(key));
               keys.add(key);
            }
         }

         for (String keyx : keys) {
            properties.remove(keyx);
         }

         return subset;
      } else {
         return subset;
      }
   }

   static ResourceBundle getCharsetsResourceBundle() {
      return ResourceBundle.getBundle("Log4j-charsets");
   }

   public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties) {
      return partitionOnCommonPrefixes(properties, false);
   }

   public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties, final boolean includeBaseKey) {
      Map<String, Properties> parts = new ConcurrentHashMap<>();

      for (String key : properties.stringPropertyNames()) {
         int idx = key.indexOf(46);
         if (idx < 0) {
            if (includeBaseKey) {
               if (!parts.containsKey(key)) {
                  parts.put(key, new Properties());
               }

               parts.get(key).setProperty("", properties.getProperty(key));
            }
         } else {
            String prefix = key.substring(0, idx);
            if (!parts.containsKey(prefix)) {
               parts.put(prefix, new Properties());
            }

            parts.get(prefix).setProperty(key.substring(idx + 1), properties.getProperty(key));
         }
      }

      return parts;
   }

   public boolean isOsWindows() {
      return SystemPropertiesPropertySource.getSystemProperty("os.name", "").startsWith("Windows");
   }

   static Duration parseDuration(final CharSequence value) {
      Matcher matcher = DURATION_PATTERN.matcher(value);
      if (matcher.matches()) {
         return Duration.of(parseDurationAmount(matcher.group(1)), PropertiesUtil.TimeUnit.parseUnit(matcher.group(2)));
      } else {
         throw new IllegalArgumentException("Invalid duration value '" + value + "'.");
      }
   }

   private static long parseDurationAmount(final String amount) {
      try {
         return Long.parseLong(amount);
      } catch (NumberFormatException var2) {
         throw new IllegalArgumentException("Invalid duration amount '" + amount + "'", var2);
      }
   }

   private static final class Environment {
      private final Set<PropertySource> sources = ConcurrentHashMap.newKeySet();
      private final ThreadLocal<PropertySource> CURRENT_PROPERTY_SOURCE = new ThreadLocal<>();

      private Environment(final PropertySource propertySource) {
         PropertySource sysProps = new PropertyFilePropertySource("log4j2.system.properties", false);

         try {
            sysProps.forEach((key, value) -> {
               if (System.getProperty(key) == null) {
                  System.setProperty(key, value);
               }
            });
         } catch (SecurityException var4) {
            PropertiesUtil.LOGGER.warn("Unable to set Java system properties from {} file, due to security restrictions.", "log4j2.system.properties", var4);
         }

         this.sources.add(propertySource);
         ServiceLoaderUtil.safeStream(
               PropertySource.class, ServiceLoader.load(PropertySource.class, PropertiesUtil.class.getClassLoader()), PropertiesUtil.LOGGER
            )
            .forEach(this.sources::add);
      }

      private void addPropertySource(final PropertySource propertySource) {
         this.sources.add(propertySource);
      }

      private void removePropertySource(final PropertySource propertySource) {
         this.sources.remove(propertySource);
      }

      private String get(final String key) {
         List<CharSequence> tokens = PropertySource.Util.tokenize(key);
         return this.sources.stream().sorted(PropertySource.Comparator.INSTANCE).map(source -> {
            if (!tokens.isEmpty()) {
               String normalKey = Objects.toString(source.getNormalForm(tokens), null);
               if (normalKey != null && this.sourceContainsProperty(source, normalKey)) {
                  return this.sourceGetProperty(source, normalKey);
               }
            }

            return this.sourceGetProperty(source, key);
         }).filter(Objects::nonNull).findFirst().orElse(null);
      }

      private boolean sourceContainsProperty(final PropertySource source, final String key) {
         PropertySource recursiveSource;
         recursiveSource = this.CURRENT_PROPERTY_SOURCE.get();
         label34:
         if (recursiveSource == null) {
            this.CURRENT_PROPERTY_SOURCE.set(source);

            boolean e;
            try {
               e = source.containsProperty(key);
            } catch (Exception var8) {
               PropertiesUtil.LOGGER.warn("Failed to retrieve Log4j property {} from property source {}.", key, source, var8);
               break label34;
            } finally {
               this.CURRENT_PROPERTY_SOURCE.remove();
            }

            return e;
         }

         PropertiesUtil.LOGGER.warn("Recursive call to `containsProperty()` from property source {}.", recursiveSource);
         return false;
      }

      private String sourceGetProperty(final PropertySource source, final String key) {
         PropertySource recursiveSource;
         recursiveSource = this.CURRENT_PROPERTY_SOURCE.get();
         label34:
         if (recursiveSource == null) {
            this.CURRENT_PROPERTY_SOURCE.set(source);

            String e;
            try {
               e = source.getProperty(key);
            } catch (Exception var8) {
               PropertiesUtil.LOGGER.warn("Failed to retrieve Log4j property {} from property source {}.", key, source, var8);
               break label34;
            } finally {
               this.CURRENT_PROPERTY_SOURCE.remove();
            }

            return e;
         }

         PropertiesUtil.LOGGER.warn("Recursive call to `getProperty()` from property source {}.", recursiveSource);
         return null;
      }

      private boolean containsKey(final String key) {
         List<CharSequence> tokens = PropertySource.Util.tokenize(key);
         return this.sources.stream().anyMatch(s -> {
            CharSequence normalizedKey = tokens.isEmpty() ? null : s.getNormalForm(tokens);
            return this.sourceContainsProperty(s, key) || normalizedKey != null && this.sourceContainsProperty(s, normalizedKey.toString());
         });
      }
   }

   private static enum TimeUnit {
      NANOS(new String[]{"ns", "nano", "nanos", "nanosecond", "nanoseconds"}, ChronoUnit.NANOS),
      MICROS(new String[]{"us", "micro", "micros", "microsecond", "microseconds"}, ChronoUnit.MICROS),
      MILLIS(new String[]{"ms", "milli", "millis", "millisecond", "milliseconds"}, ChronoUnit.MILLIS),
      SECONDS(new String[]{"s", "second", "seconds"}, ChronoUnit.SECONDS),
      MINUTES(new String[]{"m", "minute", "minutes"}, ChronoUnit.MINUTES),
      HOURS(new String[]{"h", "hour", "hours"}, ChronoUnit.HOURS),
      DAYS(new String[]{"d", "day", "days"}, ChronoUnit.DAYS);

      private final String[] descriptions;
      private final TemporalUnit timeUnit;

      private TimeUnit(final String[] descriptions, final TemporalUnit timeUnit) {
         this.descriptions = descriptions;
         this.timeUnit = timeUnit;
      }

      private static Stream<String> getValidUnits() {
         return Arrays.stream(values()).flatMap(unit -> Arrays.stream(unit.descriptions));
      }

      private static TemporalUnit parseUnit(final String unit) {
         if (unit == null) {
            return ChronoUnit.MILLIS;
         } else {
            for (PropertiesUtil.TimeUnit value : values()) {
               for (String description : value.descriptions) {
                  if (unit.equals(description)) {
                     return value.timeUnit;
                  }
               }
            }

            throw new IllegalArgumentException("Invalid duration unit '" + unit + "'");
         }
      }
   }
}
