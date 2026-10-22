package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.glassfish.jersey.JerseyPriorities;
import org.glassfish.jersey.internal.PropertiesDelegate;

public abstract class TracingLogger {
   public static final String PROPERTY_NAME = TracingLogger.class.getName();
   private static final String HEADER_TRACING_PREFIX = "X-Jersey-Tracing-";
   public static final String HEADER_THRESHOLD = "X-Jersey-Tracing-Threshold";
   public static final String HEADER_ACCEPT = "X-Jersey-Tracing-Accept";
   public static final String HEADER_LOGGER = "X-Jersey-Tracing-Logger";
   private static final String HEADER_RESPONSE_FORMAT = "X-Jersey-Tracing-%03d";
   public static final TracingLogger.Level DEFAULT_LEVEL = TracingLogger.Level.TRACE;
   private static final String TRACING_LOGGER_NAME_PREFIX = "org.glassfish.jersey.tracing";
   private static final String DEFAULT_LOGGER_NAME_SUFFIX = "general";
   private static final TracingLogger EMPTY = new TracingLogger() {
      @Override
      public boolean isLogEnabled(TracingLogger.Event event) {
         return false;
      }

      @Override
      public void log(TracingLogger.Event event, Object... args) {
      }

      @Override
      public void logDuration(TracingLogger.Event event, long fromTimestamp, Object... args) {
      }

      @Override
      public long timestamp(TracingLogger.Event event) {
         return -1L;
      }

      @Override
      public void flush(MultivaluedMap<String, Object> headers) {
      }
   };

   public static TracingLogger getInstance(PropertiesDelegate propertiesDelegate) {
      if (propertiesDelegate == null) {
         return EMPTY;
      } else {
         Object tracingLogger = propertiesDelegate.getProperty(PROPERTY_NAME);
         return TracingLogger.class.isInstance(tracingLogger) ? (TracingLogger)tracingLogger : EMPTY;
      }
   }

   public static TracingLogger create(TracingLogger.Level threshold, String loggerNameSuffix) {
      return new TracingLogger.TracingLoggerImpl(threshold, loggerNameSuffix);
   }

   public static TracingLogger empty() {
      return EMPTY;
   }

   public abstract boolean isLogEnabled(TracingLogger.Event var1);

   public abstract void log(TracingLogger.Event var1, Object... var2);

   public abstract void logDuration(TracingLogger.Event var1, long var2, Object... var4);

   public abstract long timestamp(TracingLogger.Event var1);

   public abstract void flush(MultivaluedMap<String, Object> var1);

   public interface Event {
      String name();

      String category();

      TracingLogger.Level level();

      String messageFormat();
   }

   public static enum Level {
      SUMMARY,
      TRACE,
      VERBOSE;
   }

   private static final class TracingLoggerImpl extends TracingLogger {
      private final Logger logger;
      private final TracingLogger.Level threshold;
      private final TracingInfo tracingInfo;

      public TracingLoggerImpl(TracingLogger.Level threshold, String loggerNameSuffix) {
         this.threshold = threshold;
         this.tracingInfo = new TracingInfo();
         loggerNameSuffix = loggerNameSuffix != null ? loggerNameSuffix : "general";
         this.logger = Logger.getLogger("org.glassfish.jersey.tracing." + loggerNameSuffix);
      }

      @Override
      public boolean isLogEnabled(TracingLogger.Event event) {
         return this.isEnabled(event.level());
      }

      @Override
      public void log(TracingLogger.Event event, Object... args) {
         this.logDuration(event, -1L, args);
      }

      @Override
      public void logDuration(TracingLogger.Event event, long fromTimestamp, Object... args) {
         if (this.isEnabled(event.level())) {
            long toTimestamp;
            if (fromTimestamp == -1L) {
               toTimestamp = -1L;
            } else {
               toTimestamp = System.nanoTime();
            }

            long duration = 0L;
            if (fromTimestamp != -1L && toTimestamp != -1L) {
               duration = toTimestamp - fromTimestamp;
            }

            this.logImpl(event, duration, args);
         }
      }

      @Override
      public long timestamp(TracingLogger.Event event) {
         return this.isEnabled(event.level()) ? System.nanoTime() : -1L;
      }

      @Override
      public void flush(MultivaluedMap<String, Object> headers) {
         String[] messages = this.tracingInfo.getMessages();

         for (int i = 0; i < messages.length; i++) {
            headers.putSingle(String.format("X-Jersey-Tracing-%03d", i), messages[i]);
         }
      }

      private void logImpl(TracingLogger.Event event, long duration, Object... messageArgs) {
         if (this.isEnabled(event.level())) {
            String[] messageArgsStr = new String[messageArgs.length];

            for (int i = 0; i < messageArgs.length; i++) {
               messageArgsStr[i] = formatInstance(messageArgs[i]);
            }

            TracingInfo.Message message = new TracingInfo.Message(event, duration, messageArgsStr);
            this.tracingInfo.addMessage(message);
            java.util.logging.Level loggingLevel;
            switch (event.level()) {
               case SUMMARY:
                  loggingLevel = java.util.logging.Level.FINE;
                  break;
               case TRACE:
                  loggingLevel = java.util.logging.Level.FINER;
                  break;
               case VERBOSE:
                  loggingLevel = java.util.logging.Level.FINEST;
                  break;
               default:
                  loggingLevel = java.util.logging.Level.OFF;
            }

            if (this.logger.isLoggable(loggingLevel)) {
               this.logger.log(loggingLevel, event.name() + ' ' + message.toString() + " [" + TracingInfo.formatDuration(duration) + " ms]");
            }
         }
      }

      private boolean isEnabled(TracingLogger.Level level) {
         return this.threshold.ordinal() >= level.ordinal();
      }

      private static String formatInstance(Object instance) {
         StringBuilder textSB = new StringBuilder();
         if (instance == null) {
            textSB.append("null");
         } else if (instance instanceof Number || instance instanceof String || instance instanceof Method) {
            textSB.append(instance.toString());
         } else if (instance instanceof StatusType) {
            textSB.append(formatStatusInfo((StatusType)instance));
         } else {
            textSB.append('[');
            formatInstance(instance, textSB);
            int priority = JerseyPriorities.getPriorityValue(instance.getClass(), -1);
            if (priority != -1) {
               textSB.append(" #").append(priority);
            }

            if (instance instanceof WebApplicationException) {
               formatResponse(((WebApplicationException)instance).getResponse(), textSB);
            } else if (instance instanceof Response) {
               formatResponse((Response)instance, textSB);
            }

            textSB.append(']');
         }

         return textSB.toString();
      }

      private static void formatInstance(Object instance, StringBuilder textSB) {
         textSB.append(instance.getClass().getName()).append(" @").append(Integer.toHexString(System.identityHashCode(instance)));
      }

      private static void formatResponse(Response response, StringBuilder textSB) {
         textSB.append(" <").append(formatStatusInfo(response.getStatusInfo())).append('|');
         if (response.hasEntity()) {
            formatInstance(response.getEntity(), textSB);
         } else {
            textSB.append("-no-entity-");
         }

         textSB.append('>');
      }

      private static String formatStatusInfo(StatusType statusInfo) {
         return String.valueOf(statusInfo.getStatusCode()) + '/' + statusInfo.getFamily() + '|' + statusInfo.getReasonPhrase();
      }
   }
}
