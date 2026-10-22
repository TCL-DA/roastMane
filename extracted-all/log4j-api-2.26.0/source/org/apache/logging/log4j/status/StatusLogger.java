package org.apache.logging.log4j.status;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedNoReferenceMessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.jspecify.annotations.Nullable;

public class StatusLogger extends AbstractLogger {
   private static final long serialVersionUID = 2L;
   private static final String DEBUG_PROPERTY_NAME = "log4j2.debug";
   public static final String MAX_STATUS_ENTRIES = "log4j2.status.entries";
   static final int DEFAULT_FALLBACK_LISTENER_BUFFER_CAPACITY = 0;
   public static final String DEFAULT_STATUS_LISTENER_LEVEL = "log4j2.StatusLogger.level";
   static final Level DEFAULT_FALLBACK_LISTENER_LEVEL = Level.ERROR;
   public static final String STATUS_DATE_FORMAT = "log4j2.StatusLogger.dateFormat";
   static final String STATUS_DATE_FORMAT_ZONE = "log4j2.StatusLogger.dateFormatZone";
   public static final String PROPERTIES_FILE_NAME = "log4j2.StatusLogger.properties";
   private final StatusLogger.Config config;
   private final StatusConsoleListener fallbackListener;
   private final List<StatusListener> listeners;
   private final transient ReadWriteLock listenerLock = new ReentrantReadWriteLock();
   private final transient Lock listenerReadLock = this.listenerLock.readLock();
   private final transient Lock listenerWriteLock = this.listenerLock.writeLock();
   private final Queue<StatusData> buffer = new ConcurrentLinkedQueue<>();

   StatusLogger() {
      this(
         StatusLogger.class.getSimpleName(),
         ParameterizedNoReferenceMessageFactory.INSTANCE,
         StatusLogger.Config.getInstance(),
         new StatusConsoleListener(Objects.requireNonNull(StatusLogger.Config.getInstance().fallbackListenerLevel), System.err)
      );
   }

   public StatusLogger(final String name, final MessageFactory messageFactory, final StatusLogger.Config config, final StatusConsoleListener fallbackListener) {
      super(Objects.requireNonNull(name, "name"), Objects.requireNonNull(messageFactory, "messageFactory"));
      this.config = Objects.requireNonNull(config, "config");
      this.fallbackListener = Objects.requireNonNull(fallbackListener, "fallbackListener");
      this.listeners = new ArrayList<>();
   }

   public static StatusLogger getLogger() {
      return StatusLogger.InstanceHolder.INSTANCE;
   }

   public static void setLogger(final StatusLogger logger) {
      StatusLogger.InstanceHolder.INSTANCE = Objects.requireNonNull(logger, "logger");
   }

   public StatusConsoleListener getFallbackListener() {
      return this.fallbackListener;
   }

   @Deprecated
   public void setLevel(final Level level) {
      Objects.requireNonNull(level, "level");
      this.fallbackListener.setLevel(level);
   }

   public void registerListener(final StatusListener listener) {
      Objects.requireNonNull(listener, "listener");
      this.listenerWriteLock.lock();

      try {
         this.listeners.add(listener);
      } finally {
         this.listenerWriteLock.unlock();
      }
   }

   public void removeListener(final StatusListener listener) {
      Objects.requireNonNull(listener, "listener");
      this.listenerWriteLock.lock();

      try {
         this.listeners.remove(listener);
         closeListenerSafely(listener);
      } finally {
         this.listenerWriteLock.unlock();
      }
   }

   @Deprecated
   public void updateListenerLevel(final Level level) {
      Objects.requireNonNull(level, "level");
      this.fallbackListener.setLevel(level);
   }

   public Iterable<StatusListener> getListeners() {
      this.listenerReadLock.lock();

      Collection var1;
      try {
         var1 = Collections.unmodifiableCollection(this.listeners);
      } finally {
         this.listenerReadLock.unlock();
      }

      return var1;
   }

   public void reset() {
      this.listenerWriteLock.lock();

      try {
         Iterator<StatusListener> listenerIterator = this.listeners.iterator();

         while (listenerIterator.hasNext()) {
            StatusListener listener = listenerIterator.next();
            closeListenerSafely(listener);
            listenerIterator.remove();
         }
      } finally {
         this.listenerWriteLock.unlock();
      }

      this.fallbackListener.close();
      this.buffer.clear();
   }

   private static void closeListenerSafely(final StatusListener listener) {
      try {
         listener.close();
      } catch (IOException var4) {
         String message = String.format("failed closing listener: %s", listener);
         RuntimeException extendedError = new RuntimeException(message, var4);
         extendedError.printStackTrace(System.err);
      }
   }

   @Deprecated
   public List<StatusData> getStatusData() {
      return Collections.unmodifiableList(new ArrayList<>(this.buffer));
   }

   @Deprecated
   public void clear() {
      this.buffer.clear();
   }

   @Override
   public Level getLevel() {
      Level leastSpecificLevel = this.fallbackListener.getStatusLevel();

      for (int listenerIndex = 0; listenerIndex < this.listeners.size(); listenerIndex++) {
         StatusListener listener = this.listeners.get(listenerIndex);
         Level listenerLevel = listener.getStatusLevel();
         if (listenerLevel.isLessSpecificThan(leastSpecificLevel)) {
            leastSpecificLevel = listenerLevel;
         }
      }

      return leastSpecificLevel;
   }

   @SuppressFBWarnings({"INFORMATION_EXPOSURE_THROUGH_AN_ERROR_MESSAGE"})
   @Override
   public void logMessage(final String fqcn, final Level level, final Marker marker, final Message message, final Throwable throwable) {
      try {
         StatusData statusData = this.createStatusData(fqcn, level, message, throwable);
         this.buffer(statusData);
         this.notifyListeners(statusData);
      } catch (Exception var7) {
         var7.printStackTrace(System.err);
      }
   }

   private void buffer(final StatusData statusData) {
      if (this.config.bufferCapacity != 0) {
         this.buffer.add(statusData);

         while (this.buffer.size() >= this.config.bufferCapacity) {
            this.buffer.remove();
         }
      }
   }

   private void notifyListeners(final StatusData statusData) {
      this.listenerReadLock.lock();

      boolean foundListeners;
      try {
         foundListeners = !this.listeners.isEmpty();
         this.listeners.forEach(listener -> this.notifyListener(listener, statusData));
      } finally {
         this.listenerReadLock.unlock();
      }

      if (!foundListeners) {
         this.notifyListener(this.fallbackListener, statusData);
      }
   }

   private void notifyListener(final StatusListener listener, final StatusData statusData) {
      boolean levelEnabled = this.isLevelEnabled(listener.getStatusLevel(), statusData.getLevel());
      if (levelEnabled) {
         listener.log(statusData);
      }
   }

   private StatusData createStatusData(@Nullable final String fqcn, final Level level, final Message message, @Nullable final Throwable throwable) {
      StackTraceElement caller = getStackTraceElement(fqcn);
      Instant instant = Instant.now();
      return new StatusData(caller, level, message, throwable, null, this.config.instantFormatter, instant);
   }

   @Nullable
   private static StackTraceElement getStackTraceElement(@Nullable final String fqcn) {
      if (fqcn == null) {
         return null;
      } else {
         boolean next = false;
         StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

         for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (next && !fqcn.equals(className)) {
               return element;
            }

            if (fqcn.equals(className)) {
               next = true;
            } else if ("?".equals(className)) {
               break;
            }
         }

         return null;
      }
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final String message, final Throwable throwable) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final String message) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final String message, final Object... params) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(
      final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4
   ) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(
      final Level level,
      final Marker marker,
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4,
      final Object p5
   ) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(
      final Level level,
      final Marker marker,
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4,
      final Object p5,
      final Object p6
   ) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(
      final Level level,
      final Marker marker,
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4,
      final Object p5,
      final Object p6,
      final Object p7
   ) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(
      final Level level,
      final Marker marker,
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4,
      final Object p5,
      final Object p6,
      final Object p7,
      final Object p8
   ) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(
      final Level level,
      final Marker marker,
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4,
      final Object p5,
      final Object p6,
      final Object p7,
      final Object p8,
      final Object p9
   ) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final CharSequence message, final Throwable throwable) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final Object message, final Throwable throwable) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level level, final Marker marker, final Message message, final Throwable throwable) {
      return this.isEnabled(level, marker);
   }

   @Override
   public boolean isEnabled(final Level messageLevel, final Marker marker) {
      Objects.requireNonNull(messageLevel, "messageLevel");
      Level loggerLevel = this.getLevel();
      return this.isLevelEnabled(loggerLevel, messageLevel);
   }

   private boolean isLevelEnabled(final Level filteringLevel, final Level messageLevel) {
      return this.config.debugEnabled || filteringLevel.isLessSpecificThan(messageLevel);
   }

   public static final class Config {
      private static final StatusLogger.Config INSTANCE = new StatusLogger.Config();
      final boolean debugEnabled;
      final int bufferCapacity;
      @Nullable
      final Level fallbackListenerLevel;
      @Nullable
      final DateTimeFormatter instantFormatter;

      public Config(boolean debugEnabled, int bufferCapacity, @Nullable DateTimeFormatter instantFormatter) {
         this.debugEnabled = debugEnabled;
         if (bufferCapacity < 0) {
            throw new IllegalArgumentException("was expecting a positive `bufferCapacity`, found: " + bufferCapacity);
         } else {
            this.bufferCapacity = bufferCapacity;
            this.fallbackListenerLevel = null;
            this.instantFormatter = instantFormatter;
         }
      }

      private Config() {
         this(StatusLogger.PropertiesUtilsDouble.readAllAvailableProperties());
      }

      Config(final Properties... propertiesList) {
         this(StatusLogger.PropertiesUtilsDouble.normalizeProperties(propertiesList));
      }

      private Config(final Map<String, Object> normalizedProperties) {
         this.debugEnabled = readDebugEnabled(normalizedProperties);
         this.bufferCapacity = readBufferCapacity(normalizedProperties);
         this.fallbackListenerLevel = readFallbackListenerLevel(normalizedProperties);
         this.instantFormatter = readInstantFormatter(normalizedProperties);
      }

      public static StatusLogger.Config getInstance() {
         return INSTANCE;
      }

      private static boolean readDebugEnabled(final Map<String, Object> normalizedProperties) {
         String debug = StatusLogger.PropertiesUtilsDouble.readProperty(normalizedProperties, "log4j2.debug");
         return debug != null && !"false".equalsIgnoreCase(debug);
      }

      private static int readBufferCapacity(final Map<String, Object> normalizedProperties) {
         String propertyName = "log4j2.status.entries";
         String capacityString = StatusLogger.PropertiesUtilsDouble.readProperty(normalizedProperties, "log4j2.status.entries");
         int defaultCapacity = 0;
         int effectiveCapacity = 0;
         if (capacityString != null) {
            try {
               int capacity = Integer.parseInt(capacityString);
               if (capacity < 0) {
                  String message = String.format("was expecting a positive buffer capacity, found: %d", capacity);
                  throw new IllegalArgumentException(message);
               }

               effectiveCapacity = capacity;
            } catch (Exception var8) {
               String message = String.format(
                  "Failed reading the buffer capacity from the `%s` property: `%s`. Falling back to the default: %d.",
                  "log4j2.status.entries",
                  capacityString,
                  0
               );
               IllegalArgumentException extendedError = new IllegalArgumentException(message, var8);
               extendedError.printStackTrace(System.err);
            }
         }

         return effectiveCapacity;
      }

      private static Level readFallbackListenerLevel(final Map<String, Object> normalizedProperties) {
         String propertyName = "log4j2.StatusLogger.level";
         String level = StatusLogger.PropertiesUtilsDouble.readProperty(normalizedProperties, "log4j2.StatusLogger.level");
         Level defaultLevel = StatusLogger.DEFAULT_FALLBACK_LISTENER_LEVEL;

         try {
            return level != null ? Level.valueOf(level) : defaultLevel;
         } catch (Exception var7) {
            String message = String.format(
               "Failed reading the level from the `%s` property: `%s`. Falling back to the default: `%s`.", "log4j2.StatusLogger.level", level, defaultLevel
            );
            IllegalArgumentException extendedError = new IllegalArgumentException(message, var7);
            extendedError.printStackTrace(System.err);
            return defaultLevel;
         }
      }

      @Nullable
      private static DateTimeFormatter readInstantFormatter(final Map<String, Object> normalizedProperties) {
         String formatPropertyName = "log4j2.StatusLogger.dateFormat";
         String format = StatusLogger.PropertiesUtilsDouble.readProperty(normalizedProperties, "log4j2.StatusLogger.dateFormat");
         if (format == null) {
            return null;
         } else {
            DateTimeFormatter formatter;
            try {
               formatter = DateTimeFormatter.ofPattern(format);
            } catch (Exception var12) {
               String message = String.format("failed reading the instant format from the `%s` property: `%s`", "log4j2.StatusLogger.dateFormat", format);
               IllegalArgumentException extendedError = new IllegalArgumentException(message, var12);
               extendedError.printStackTrace(System.err);
               return null;
            }

            String zonePropertyName = "log4j2.StatusLogger.dateFormatZone";
            String zoneIdString = StatusLogger.PropertiesUtilsDouble.readProperty(normalizedProperties, "log4j2.StatusLogger.dateFormatZone");
            ZoneId defaultZoneId = ZoneId.systemDefault();
            ZoneId zoneId = defaultZoneId;
            if (zoneIdString != null) {
               try {
                  zoneId = ZoneId.of(zoneIdString);
               } catch (Exception var11) {
                  String message = String.format(
                     "Failed reading the instant formatting zone ID from the `%s` property: `%s`. Falling back to the default: `%s`.",
                     "log4j2.StatusLogger.dateFormatZone",
                     zoneIdString,
                     defaultZoneId
                  );
                  IllegalArgumentException extendedError = new IllegalArgumentException(message, var11);
                  extendedError.printStackTrace(System.err);
               }
            }

            return formatter.withZone(zoneId);
         }
      }
   }

   private static final class InstanceHolder {
      private static volatile StatusLogger INSTANCE = new StatusLogger();
   }

   static final class PropertiesUtilsDouble {
      @Nullable
      static String readProperty(final Map<String, Object> normalizedProperties, final String propertyName) {
         String normalizedPropertyName = normalizePropertyName(propertyName);
         Object value = normalizedProperties.get(normalizedPropertyName);
         return value instanceof String ? (String)value : null;
      }

      static Map<String, Object> readAllAvailableProperties() {
         Properties systemProperties = System.getProperties();
         Properties environmentProperties = readEnvironmentProperties();
         Properties fileProvidedProperties = readPropertiesFile("log4j2.StatusLogger.properties");
         return normalizeProperties(systemProperties, environmentProperties, fileProvidedProperties);
      }

      private static Properties readEnvironmentProperties() {
         Properties properties = new Properties();
         properties.putAll(System.getenv());
         return properties;
      }

      static Properties readPropertiesFile(final String propertiesFileName) {
         Properties properties = new Properties();
         String resourceName = '/' + propertiesFileName;
         URL url = StatusLogger.class.getResource(resourceName);
         if (url == null) {
            return properties;
         } else {
            try {
               InputStream stream = url.openStream();

               try {
                  properties.load(stream);
               } catch (Throwable var8) {
                  if (stream != null) {
                     try {
                        stream.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (stream != null) {
                  stream.close();
               }
            } catch (IOException var9) {
               String message = String.format("failed reading properties from `%s`", propertiesFileName);
               RuntimeException extendedError = new RuntimeException(message, var9);
               extendedError.printStackTrace(System.err);
            }

            return properties;
         }
      }

      private static Map<String, Object> normalizeProperties(Properties... propertiesList) {
         Map<String, Object> map = new HashMap<>();

         for (Properties properties : propertiesList) {
            properties.forEach((name, value) -> {
               boolean relevant = isRelevantPropertyName(name);
               if (relevant) {
                  String normalizedName = normalizePropertyName((String)name);
                  map.put(normalizedName, value);
               }
            });
         }

         return map;
      }

      private static boolean isRelevantPropertyName(@Nullable final Object propertyName) {
         return propertyName instanceof String && ((String)propertyName).matches("^(?i)log4j.*");
      }

      private static String normalizePropertyName(final String propertyName) {
         return propertyName.replaceAll("[._-]", "").replaceAll("\\P{InBasic_Latin}", ".").toLowerCase(Locale.US).replaceAll("^log4j2", "log4j");
      }
   }
}
