package org.apache.logging.log4j.core.pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
class ThrowablePropertyRendererFactory {
   private static final ThrowableRenderer MESSAGE_RENDERER = (buffer, throwable, lineSeparator) -> {
      String message = throwable.getMessage();
      buffer.append(message);
   };
   private static final ThrowableRenderer LOCALIZED_MESSAGE_RENDERER = (buffer, throwable, lineSeparator) -> {
      String localizedMessage = throwable.getLocalizedMessage();
      buffer.append(localizedMessage);
   };
   private static final Function<Throwable, StackTraceElement> THROWING_METHOD_EXTRACTOR = throwable -> {
      StackTraceElement[] stackTraceElements = throwable.getStackTrace();
      return stackTraceElements != null && stackTraceElements.length > 0 ? stackTraceElements[0] : null;
   };
   static final ThrowablePropertyRendererFactory INSTANCE = new ThrowablePropertyRendererFactory(THROWING_METHOD_EXTRACTOR);
   private final Map<String, ThrowableRenderer> rendererByPropertyName;

   ThrowablePropertyRendererFactory(final Function<Throwable, StackTraceElement> throwingMethodExtractor) {
      this.rendererByPropertyName = createRendererByPropertyName(throwingMethodExtractor);
   }

   private static Map<String, ThrowableRenderer> createRendererByPropertyName(final Function<Throwable, StackTraceElement> throwingMethodExtractor) {
      Map<String, ThrowableRenderer> map = new HashMap<>();
      map.put("short.message", MESSAGE_RENDERER);
      map.put("short.localizedMessage", LOCALIZED_MESSAGE_RENDERER);
      map.put("short.className", createClassNameRenderer(throwingMethodExtractor));
      map.put("short.methodName", createMethodNameRenderer(throwingMethodExtractor));
      map.put("short.lineNumber", createLineNumberRenderer(throwingMethodExtractor));
      map.put("short.fileName", createFileNameRenderer(throwingMethodExtractor));
      return map;
   }

   private static ThrowableRenderer createClassNameRenderer(final Function<Throwable, StackTraceElement> throwingMethodExtractor) {
      return (buffer, throwable, lineSeparator) -> {
         StackTraceElement throwingMethod = throwingMethodExtractor.apply(throwable);
         if (throwingMethod != null) {
            String className = throwingMethod.getClassName();
            buffer.append(className);
         }
      };
   }

   private static ThrowableRenderer createMethodNameRenderer(final Function<Throwable, StackTraceElement> throwingMethodExtractor) {
      return (buffer, throwable, lineSeparator) -> {
         StackTraceElement throwingMethod = throwingMethodExtractor.apply(throwable);
         if (throwingMethod != null) {
            String methodName = throwingMethod.getMethodName();
            buffer.append(methodName);
         }
      };
   }

   private static ThrowableRenderer createLineNumberRenderer(final Function<Throwable, StackTraceElement> throwingMethodExtractor) {
      return (buffer, throwable, lineSeparator) -> {
         StackTraceElement throwingMethod = throwingMethodExtractor.apply(throwable);
         if (throwingMethod != null) {
            int lineNumber = throwingMethod.getLineNumber();
            buffer.append(lineNumber);
         }
      };
   }

   private static ThrowableRenderer createFileNameRenderer(final Function<Throwable, StackTraceElement> throwingMethodExtractor) {
      return (buffer, throwable, lineSeparator) -> {
         StackTraceElement throwingMethod = throwingMethodExtractor.apply(throwable);
         if (throwingMethod != null) {
            String fileName = throwingMethod.getFileName();
            buffer.append(fileName);
         }
      };
   }

   @Nullable
   final ThrowableRenderer createPropertyRenderer(final String[] options) {
      if (options != null && options.length > 0) {
         String propertyName = options[0];
         return this.rendererByPropertyName.get(propertyName);
      } else {
         return null;
      }
   }
}
