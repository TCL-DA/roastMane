package org.glassfish.jersey.server;

import jakarta.ws.rs.core.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.message.internal.TracingLogger.Level;
import org.glassfish.jersey.server.internal.ServerTraceEvent;

public final class TracingUtils {
   private static final List<String> SUMMARY_HEADERS = new ArrayList<>();
   private static final TracingConfig DEFAULT_CONFIGURATION_TYPE = TracingConfig.OFF;

   private TracingUtils() {
   }

   public static void initTracingSupport(TracingConfig type, Level appThreshold, ContainerRequest containerRequest) {
      TracingLogger tracingLogger;
      if (isTracingSupportEnabled(type, containerRequest)) {
         tracingLogger = TracingLogger.create(getTracingThreshold(appThreshold, containerRequest), getTracingLoggerNameSuffix(containerRequest));
      } else {
         tracingLogger = TracingLogger.empty();
      }

      containerRequest.setProperty(TracingLogger.PROPERTY_NAME, tracingLogger);
   }

   public static void logStart(ContainerRequest request) {
      TracingLogger tracingLogger = TracingLogger.getInstance(request);
      if (tracingLogger.isLogEnabled(ServerTraceEvent.START)) {
         StringBuilder textSB = new StringBuilder();
         textSB.append(
            String.format(
               "baseUri=[%s] requestUri=[%s] method=[%s] authScheme=[%s]",
               request.getBaseUri(),
               request.getRequestUri(),
               request.getMethod(),
               toStringOrNA(request.getSecurityContext().getAuthenticationScheme())
            )
         );

         for (String header : SUMMARY_HEADERS) {
            textSB.append(String.format(" %s=%s", header, toStringOrNA(request.getRequestHeaders().get(header))));
         }

         tracingLogger.log(ServerTraceEvent.START, new Object[]{textSB.toString()});
      }

      if (tracingLogger.isLogEnabled(ServerTraceEvent.START_HEADERS)) {
         StringBuilder textSB = new StringBuilder();

         for (String header : request.getRequestHeaders().keySet()) {
            if (!SUMMARY_HEADERS.contains(header)) {
               textSB.append(String.format(" %s=%s", header, toStringOrNA(request.getRequestHeaders().get(header))));
            }
         }

         if (textSB.length() > 0) {
            textSB.insert(0, "Other request headers:");
         }

         tracingLogger.log(ServerTraceEvent.START_HEADERS, new Object[]{textSB.toString()});
      }
   }

   private static boolean isTracingSupportEnabled(TracingConfig type, ContainerRequest containerRequest) {
      return type == TracingConfig.ALL || type == TracingConfig.ON_DEMAND && containerRequest.getHeaderString("X-Jersey-Tracing-Accept") != null;
   }

   static TracingConfig getTracingConfig(Configuration configuration) {
      String tracingText = ServerProperties.getValue(configuration.getProperties(), "jersey.config.server.tracing.type", String.class);
      TracingConfig result;
      if (tracingText != null) {
         result = TracingConfig.valueOf(tracingText);
      } else {
         result = DEFAULT_CONFIGURATION_TYPE;
      }

      return result;
   }

   private static String getTracingLoggerNameSuffix(ContainerRequest request) {
      return request.getHeaderString("X-Jersey-Tracing-Logger");
   }

   static Level getTracingThreshold(Configuration configuration) {
      String thresholdText = ServerProperties.getValue(configuration.getProperties(), "jersey.config.server.tracing.threshold", String.class);
      return thresholdText == null ? TracingLogger.DEFAULT_LEVEL : Level.valueOf(thresholdText);
   }

   private static Level getTracingThreshold(Level appThreshold, ContainerRequest containerRequest) {
      String thresholdText = containerRequest.getHeaderString("X-Jersey-Tracing-Threshold");
      return thresholdText == null ? appThreshold : Level.valueOf(thresholdText);
   }

   private static String toStringOrNA(Object object) {
      return object == null ? "n/a" : String.valueOf(object);
   }

   static {
      SUMMARY_HEADERS.add("Accept".toLowerCase(Locale.ROOT));
      SUMMARY_HEADERS.add("Accept-Encoding".toLowerCase(Locale.ROOT));
      SUMMARY_HEADERS.add("Accept-Charset".toLowerCase(Locale.ROOT));
      SUMMARY_HEADERS.add("Accept-Language".toLowerCase(Locale.ROOT));
      SUMMARY_HEADERS.add("Content-Type".toLowerCase(Locale.ROOT));
      SUMMARY_HEADERS.add("Content-Length".toLowerCase(Locale.ROOT));
   }
}
