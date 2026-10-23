package org.cropster.roastingintelligence.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "CropsterThrowablePatternConverter",
   category = "Converter"
)
@ConverterKeys({"cropEx"})
public final class CropsterThrowablePatternConverter extends LogEventPatternConverter {
   private static final String LINE_SEPARATOR = System.lineSeparator();
   private static final CropsterThrowablePatternConverter INSTANCE = new CropsterThrowablePatternConverter();

   private CropsterThrowablePatternConverter() {
      super("CropsterThrowable", "cropEx");
   }

   public static CropsterThrowablePatternConverter newInstance(String[] options) {
      return INSTANCE;
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      Throwable throwable = event.getThrown();
      if (throwable != null) {
         appendThrowable(event.getLevel(), throwable, toAppendTo);
         toAppendTo.append(LINE_SEPARATOR);
      }
   }

   private static void appendThrowable(Level level, Throwable throwable, StringBuilder stringBuilder) {
      boolean omitCauseStackTrace = Level.WARN.equals(level);
      boolean filterStackTrace = Level.INFO.equals(level) || Level.WARN.equals(level);
      StackTraceElement[] previousTrace = null;

      for (Throwable current = throwable; current != null; current = current.getCause()) {
         StackTraceElement[] trace = current.getStackTrace();
         int tracePos = trace.length - 1;
         int framesInCommon = 0;
         if (previousTrace != null) {
            stringBuilder.append("Caused by: ");

            for (int previousPos = previousTrace.length - 1;
               tracePos >= 0 && previousPos >= 0 && trace[tracePos].equals(previousTrace[previousPos]);
               previousPos--
            ) {
               tracePos--;
            }

            framesInCommon = trace.length - 1 - tracePos;
         }

         stringBuilder.append(current);
         stringBuilder.append(LINE_SEPARATOR);
         if (previousTrace != null && omitCauseStackTrace) {
            stringBuilder.append("\t... ");
            stringBuilder.append(trace.length);
            stringBuilder.append(" omitted ... (log level is ").append(level).append(")");
            stringBuilder.append(LINE_SEPARATOR);
         } else {
            int framesOmitted = 0;

            for (int i = 0; i <= tracePos; i++) {
               if (filterStackTrace && !trace[i].getClassName().startsWith("org.cropster") && !trace[i].getClassName().startsWith("com.cropster")) {
                  framesOmitted++;
               } else {
                  if (framesOmitted > 0) {
                     stringBuilder.append("\t... ");
                     stringBuilder.append(framesOmitted);
                     stringBuilder.append(" omitted ...");
                     stringBuilder.append(LINE_SEPARATOR);
                     framesOmitted = 0;
                  }

                  stringBuilder.append("\tat ");
                  stringBuilder.append(trace[i]);
                  stringBuilder.append(LINE_SEPARATOR);
               }
            }

            if (framesOmitted + framesInCommon != 0) {
               stringBuilder.append("\t... ");
               stringBuilder.append(framesOmitted + framesInCommon);
               stringBuilder.append(" more");
               stringBuilder.append(LINE_SEPARATOR);
            }
         }

         previousTrace = trace;
      }
   }
}
