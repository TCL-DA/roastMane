package org.apache.logging.log4j.core.pattern;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;

final class ThrowableExtendedStackTraceRenderer extends ThrowableStackTraceRenderer<ThrowableExtendedStackTraceRenderer.ExtendedContext> {
   private static final ThrowableExtendedStackTraceRenderer.ClassLoadingStrategy[] CLASS_LOADING_STRATEGIES = new ThrowableExtendedStackTraceRenderer.ClassLoadingStrategy[]{
      (loader, className) -> loader != null ? loader.loadClass(className) : null,
      (loader, className) -> LoaderUtil.loadClass(className),
      (loader, className) -> ThrowableExtendedStackTraceRenderer.class.getClassLoader().loadClass(className)
   };

   ThrowableExtendedStackTraceRenderer(final List<String> ignoredPackageNames, final int maxLineCount) {
      super(ignoredPackageNames, maxLineCount);
   }

   ThrowableExtendedStackTraceRenderer.ExtendedContext createContext(final Throwable throwable) {
      return ThrowableExtendedStackTraceRenderer.ExtendedContext.ofThrowable(throwable);
   }

   void renderStackTraceElement(
      final StringBuilder buffer,
      final StackTraceElement stackTraceElement,
      final ThrowableExtendedStackTraceRenderer.ExtendedContext context,
      final String prefix,
      final String lineSeparator
   ) {
      boolean stackTraceElementIgnored = this.isStackTraceElementIgnored(stackTraceElement);
      if (stackTraceElementIgnored) {
         context.ignoredStackTraceElementCount++;
      } else {
         if (context.ignoredStackTraceElementCount > 0) {
            this.renderSuppressedCount(buffer, context, prefix, lineSeparator);
            context.ignoredStackTraceElementCount = 0;
         }

         this.acquireLineCapacity(context);
         buffer.append(prefix);
         buffer.append("\tat ");
         buffer.append(stackTraceElement);
         ClassResourceInfo classResourceInfo = context.classResourceInfoByName.get(stackTraceElement.getClassName());
         if (classResourceInfo != null) {
            buffer.append(' ');
            classResourceInfo.render(buffer);
         }

         buffer.append(lineSeparator);
      }
   }

   private interface ClassLoadingStrategy {
      Class<?> run(final ClassLoader loader, final String className) throws Exception;
   }

   static final class ExtendedContext extends ThrowableStackTraceRenderer.Context {
      private final Map<String, ClassResourceInfo> classResourceInfoByName;

      private ExtendedContext(
         final int ignoredStackTraceElementCount,
         final Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> metadataByThrowable,
         final Map<String, ClassResourceInfo> classResourceInfoByName
      ) {
         super(ignoredStackTraceElementCount, metadataByThrowable);
         this.classResourceInfoByName = classResourceInfoByName;
      }

      private static ThrowableExtendedStackTraceRenderer.ExtendedContext ofThrowable(final Throwable throwable) {
         Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> metadataByThrowable = ThrowableStackTraceRenderer.Context.Metadata.ofThrowable(throwable);
         Map<String, ClassResourceInfo> classResourceInfoByName = createClassResourceInfoByName(throwable, metadataByThrowable);
         return new ThrowableExtendedStackTraceRenderer.ExtendedContext(0, metadataByThrowable, classResourceInfoByName);
      }

      private static Map<String, ClassResourceInfo> createClassResourceInfoByName(
         final Throwable rootThrowable, final Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> metadataByThrowable
      ) {
         Deque<Class<?>> executionStackTrace = StackLocatorUtil.getCurrentStackTrace();
         Map<String, ClassResourceInfo> classResourceInfoByName = new HashMap<>();
         Set<Throwable> visitedThrowables = new HashSet<>();
         Queue<Throwable> pendingThrowables = new ArrayDeque<>(Collections.singleton(rootThrowable));

         Throwable throwable;
         while ((throwable = pendingThrowables.poll()) != null && visitedThrowables.add(throwable)) {
            Throwable cause = throwable.getCause();
            if (cause != null) {
               pendingThrowables.offer(cause);
            }

            ThrowableStackTraceRenderer.Context.Metadata metadata = metadataByThrowable.get(throwable);
            if (metadata != null) {
               Class<?> executionStackTraceElementClass = executionStackTrace.isEmpty() ? null : executionStackTrace.peekLast();
               ClassLoader lastLoader = null;
               StackTraceElement[] stackTraceElements = metadata.stackTrace;

               for (int throwableStackIndex = metadata.stackLength - 1; throwableStackIndex >= 0; throwableStackIndex--) {
                  StackTraceElement throwableStackTraceElement = stackTraceElements[throwableStackIndex];
                  String throwableStackTraceElementClassName = throwableStackTraceElement.getClassName();
                  ClassResourceInfo classResourceInfo = classResourceInfoByName.get(throwableStackTraceElementClassName);
                  if (classResourceInfo != null) {
                     if (classResourceInfo.clazz != null) {
                        lastLoader = classResourceInfo.clazz.getClassLoader();
                     }
                  } else if (executionStackTraceElementClass != null && throwableStackTraceElementClassName.equals(executionStackTraceElementClass.getName())) {
                     classResourceInfo = new ClassResourceInfo(executionStackTraceElementClass, true);
                     classResourceInfoByName.put(throwableStackTraceElementClassName, classResourceInfo);
                     lastLoader = classResourceInfo.clazz.getClassLoader();
                     executionStackTrace.pollLast();
                     executionStackTraceElementClass = executionStackTrace.peekLast();
                  } else {
                     Class<?> stackTraceElementClass = loadClass(lastLoader, throwableStackTraceElementClassName);
                     classResourceInfo = stackTraceElementClass != null ? new ClassResourceInfo(stackTraceElementClass, false) : ClassResourceInfo.UNKNOWN;
                     classResourceInfoByName.put(throwableStackTraceElementClassName, classResourceInfo);
                  }
               }
            }
         }

         return classResourceInfoByName;
      }

      private static Class<?> loadClass(final ClassLoader loader, final String className) {
         for (ThrowableExtendedStackTraceRenderer.ClassLoadingStrategy strategy : ThrowableExtendedStackTraceRenderer.CLASS_LOADING_STRATEGIES) {
            try {
               Class<?> clazz = strategy.run(loader, className);
               if (clazz != null) {
                  return clazz;
               }
            } catch (Exception var7) {
            }
         }

         return null;
      }
   }
}
