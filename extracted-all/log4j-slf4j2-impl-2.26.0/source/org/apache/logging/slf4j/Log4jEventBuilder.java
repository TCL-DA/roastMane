package org.apache.logging.slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.BridgeAware;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.slf4j.spi.CallerBoundaryAware;
import org.slf4j.spi.LoggingEventBuilder;

public class Log4jEventBuilder implements LoggingEventBuilder, CallerBoundaryAware {
   private static final String FQCN = Log4jEventBuilder.class.getName();
   private final Log4jMarkerFactory markerFactory;
   private final Logger logger;
   private final List<Object> arguments = new ArrayList<>();
   private String message = null;
   private Marker marker = null;
   private Throwable throwable = null;
   private Map<String, String> keyValuePairs = null;
   private final Level level;
   private String fqcn = FQCN;

   public Log4jEventBuilder(final Log4jMarkerFactory markerFactory, final Logger logger, final Level level) {
      this.markerFactory = markerFactory;
      this.logger = logger;
      this.level = level;
   }

   public LoggingEventBuilder setCause(final Throwable cause) {
      this.throwable = cause;
      return this;
   }

   public LoggingEventBuilder addMarker(final org.slf4j.Marker marker) {
      this.marker = this.markerFactory.getLog4jMarker(marker);
      return this;
   }

   public LoggingEventBuilder addArgument(final Object p) {
      this.arguments.add(p);
      return this;
   }

   public LoggingEventBuilder addArgument(final Supplier<?> objectSupplier) {
      this.arguments.add(objectSupplier.get());
      return this;
   }

   public LoggingEventBuilder addKeyValue(final String key, final Object value) {
      if (this.keyValuePairs == null) {
         this.keyValuePairs = new HashMap<>();
      }

      this.keyValuePairs.put(key, String.valueOf(value));
      return this;
   }

   public LoggingEventBuilder addKeyValue(final String key, final Supplier<Object> valueSupplier) {
      if (this.keyValuePairs == null) {
         this.keyValuePairs = new HashMap<>();
      }

      this.keyValuePairs.put(key, String.valueOf(valueSupplier.get()));
      return this;
   }

   public LoggingEventBuilder setMessage(final String message) {
      this.message = message;
      return this;
   }

   public LoggingEventBuilder setMessage(final Supplier<String> messageSupplier) {
      this.message = messageSupplier.get();
      return this;
   }

   public void log() {
      LogBuilder logBuilder = this.logger.atLevel(this.level).withMarker(this.marker).withThrowable(this.throwable);
      if (logBuilder instanceof BridgeAware) {
         ((BridgeAware)logBuilder).setEntryPoint(this.fqcn);
      }

      if (this.keyValuePairs != null && !this.keyValuePairs.isEmpty()) {
         Instance c = CloseableThreadContext.putAll(this.keyValuePairs);

         try {
            logBuilder.log(this.message, this.arguments.toArray());
         } catch (Throwable var6) {
            if (c != null) {
               try {
                  c.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (c != null) {
            c.close();
         }
      } else {
         logBuilder.log(this.message, this.arguments.toArray());
      }
   }

   public void log(final String message) {
      this.setMessage(message);
      this.log();
   }

   public void log(final String message, final Object arg) {
      this.setMessage(message);
      this.addArgument(arg);
      this.log();
   }

   public void log(final String message, final Object arg0, final Object arg1) {
      this.setMessage(message);
      this.addArgument(arg0);
      this.addArgument(arg1);
      this.log();
   }

   public void log(final String message, final Object... args) {
      this.setMessage(message);

      for (Object arg : args) {
         this.addArgument(arg);
      }

      this.log();
   }

   public void log(final Supplier<String> messageSupplier) {
      this.setMessage(messageSupplier);
      this.log();
   }

   public void setCallerBoundary(String fqcn) {
      this.fqcn = fqcn;
   }
}
