package org.apache.logging.log4j.core.pattern;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
class ThrowableStackTraceRenderer<C extends ThrowableStackTraceRenderer.Context> implements ThrowableRenderer {
   private static final RuntimeException MAX_LINE_COUNT_EXCEEDED = new RuntimeException("max-line-count-exceeded");
   private static final String CAUSED_BY_CAPTION = "Caused by: ";
   static final String SUPPRESSED_CAPTION = "Suppressed: ";
   final List<String> ignoredPackageNames;
   final int maxLineCount;

   ThrowableStackTraceRenderer(final List<String> ignoredPackageNames, final int maxLineCount) {
      this.ignoredPackageNames = ignoredPackageNames;
      this.maxLineCount = maxLineCount;
   }

   @Override
   public final void renderThrowable(final StringBuilder buffer, final Throwable throwable, final String lineSeparator) {
      if (this.maxLineCount > 0) {
         try {
            C context = this.createContext(throwable);
            this.renderThrowable(buffer, throwable, context, new HashSet<>(), lineSeparator);
         } catch (Exception var5) {
            if (var5 != MAX_LINE_COUNT_EXCEEDED) {
               throw var5;
            }
         }
      }
   }

   C createContext(final Throwable throwable) {
      Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> metadataByThrowable = ThrowableStackTraceRenderer.Context.Metadata.ofThrowable(throwable);
      return (C)(new ThrowableStackTraceRenderer.Context(0, metadataByThrowable));
   }

   void renderThrowable(
      final StringBuilder buffer, final Throwable throwable, final C context, final Set<Throwable> visitedThrowables, final String lineSeparator
   ) {
      this.renderThrowable(buffer, throwable, context, visitedThrowables, "", lineSeparator, "");
   }

   private void renderThrowable(
      final StringBuilder buffer,
      final Throwable throwable,
      final C context,
      final Set<Throwable> visitedThrowables,
      final String prefix,
      final String lineSeparator,
      final String caption
   ) {
      this.acquireLineCapacity(context);
      boolean circular = !visitedThrowables.add(throwable);
      buffer.append(prefix);
      buffer.append(caption);
      if (circular) {
         buffer.append("[CIRCULAR REFERENCE: ");
         renderThrowableMessage(buffer, throwable);
         buffer.append(']');
         buffer.append(lineSeparator);
      } else {
         ThrowableStackTraceRenderer.Context.Metadata metadata = context.metadataByThrowable.get(throwable);
         renderThrowableMessage(buffer, throwable);
         buffer.append(lineSeparator);
         this.renderStackTraceElements(buffer, context, metadata, prefix, lineSeparator);
         this.renderSuppressed(buffer, metadata.suppressed, context, visitedThrowables, prefix + '\t', lineSeparator);
         this.renderCause(buffer, throwable.getCause(), context, visitedThrowables, prefix, lineSeparator);
      }
   }

   void acquireLineCapacity(final C context) {
      if (context.lineCount < this.maxLineCount) {
         context.lineCount++;
      } else {
         throw MAX_LINE_COUNT_EXCEEDED;
      }
   }

   void renderSuppressed(
      final StringBuilder buffer,
      final Throwable[] suppressedThrowables,
      final C context,
      final Set<Throwable> visitedThrowables,
      final String prefix,
      final String lineSeparator
   ) {
      for (Throwable suppressedThrowable : suppressedThrowables) {
         this.renderThrowable(buffer, suppressedThrowable, context, visitedThrowables, prefix, lineSeparator, "Suppressed: ");
      }
   }

   private void renderCause(
      final StringBuilder buffer,
      @Nullable final Throwable cause,
      final C context,
      final Set<Throwable> visitedThrowables,
      final String prefix,
      final String lineSeparator
   ) {
      if (cause != null) {
         this.renderThrowable(buffer, cause, context, visitedThrowables, prefix, lineSeparator, "Caused by: ");
      }
   }

   static void renderThrowableMessage(final StringBuilder buffer, final Throwable throwable) {
      buffer.append(throwable);
   }

   final void renderStackTraceElements(
      final StringBuilder buffer, final C context, final ThrowableStackTraceRenderer.Context.Metadata metadata, final String prefix, final String lineSeparator
   ) {
      context.ignoredStackTraceElementCount = 0;
      StackTraceElement[] stackTraceElements = metadata.stackTrace;

      for (int i = 0; i < metadata.stackLength; i++) {
         this.renderStackTraceElement(buffer, stackTraceElements[i], context, prefix, lineSeparator);
      }

      if (context.ignoredStackTraceElementCount > 0) {
         this.renderSuppressedCount(buffer, context, prefix, lineSeparator);
      }

      if (metadata.commonElementCount != 0) {
         this.acquireLineCapacity(context);
         buffer.append(prefix);
         buffer.append("\t... ");
         buffer.append(metadata.commonElementCount);
         buffer.append(" more");
         buffer.append(lineSeparator);
      }
   }

   void renderStackTraceElement(
      final StringBuilder buffer, final StackTraceElement stackTraceElement, final C context, final String prefix, final String lineSeparator
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
         buffer.append(lineSeparator);
      }
   }

   boolean isStackTraceElementIgnored(final StackTraceElement element) {
      if (this.ignoredPackageNames != null) {
         String className = element.getClassName();

         for (String ignoredPackageName : this.ignoredPackageNames) {
            if (className.startsWith(ignoredPackageName)) {
               return true;
            }
         }
      }

      return false;
   }

   void renderSuppressedCount(final StringBuilder buffer, final C context, final String prefix, final String lineSeparator) {
      this.acquireLineCapacity(context);
      buffer.append(prefix);
      if (context.ignoredStackTraceElementCount == 1) {
         buffer.append("\t...");
      } else {
         buffer.append("\t... suppressed ");
         buffer.append(context.ignoredStackTraceElementCount);
         buffer.append(" lines");
      }

      buffer.append(lineSeparator);
   }

   static class Context {
      int lineCount = 0;
      int ignoredStackTraceElementCount;
      final Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> metadataByThrowable;

      Context(final int ignoredStackTraceElementCount, final Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> metadataByThrowable) {
         this.ignoredStackTraceElementCount = ignoredStackTraceElementCount;
         this.metadataByThrowable = metadataByThrowable;
      }

      static final class Metadata {
         final int commonElementCount;
         final int stackLength;
         final StackTraceElement[] stackTrace;
         final Throwable[] suppressed;

         private Metadata(final int commonElementCount, final int stackLength, final StackTraceElement[] stackTrace, final Throwable[] suppressed) {
            this.commonElementCount = commonElementCount;
            this.stackLength = stackLength;
            this.stackTrace = stackTrace;
            this.suppressed = suppressed;
         }

         static Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> ofThrowable(final Throwable throwable) {
            Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> metadataByThrowable = new HashMap<>();
            populateMetadata(metadataByThrowable, new HashSet<>(), null, throwable);
            return metadataByThrowable;
         }

         private static void populateMetadata(
            final Map<Throwable, ThrowableStackTraceRenderer.Context.Metadata> metadataByThrowable,
            final Set<Throwable> visitedThrowables,
            final Throwable parentThrowable,
            final Throwable throwable
         ) {
            StackTraceElement[] rootTrace = parentThrowable == null ? null : parentThrowable.getStackTrace();
            ThrowableStackTraceRenderer.Context.Metadata metadata = populateMetadata(rootTrace, throwable.getStackTrace(), throwable.getSuppressed());
            metadataByThrowable.put(throwable, metadata);

            for (Throwable suppressed : metadata.suppressed) {
               if (!visitedThrowables.contains(suppressed)) {
                  visitedThrowables.add(suppressed);
                  populateMetadata(metadataByThrowable, visitedThrowables, throwable, suppressed);
               }
            }

            Throwable cause = throwable.getCause();
            if (cause != null && !visitedThrowables.contains(cause)) {
               visitedThrowables.add(cause);
               populateMetadata(metadataByThrowable, visitedThrowables, throwable, cause);
            }
         }

         private static ThrowableStackTraceRenderer.Context.Metadata populateMetadata(
            final StackTraceElement[] parentTrace, final StackTraceElement[] currentTrace, final Throwable[] suppressed
         ) {
            int commonElementCount;
            int stackLength;
            if (parentTrace != null) {
               int parentIndex = parentTrace.length - 1;

               int currentIndex;
               for (currentIndex = currentTrace.length - 1;
                  parentIndex >= 0 && currentIndex >= 0 && parentTrace[parentIndex].equals(currentTrace[currentIndex]);
                  currentIndex--
               ) {
                  parentIndex--;
               }

               commonElementCount = currentTrace.length - 1 - currentIndex;
               stackLength = currentIndex + 1;
            } else {
               commonElementCount = 0;
               stackLength = currentTrace.length;
            }

            return new ThrowableStackTraceRenderer.Context.Metadata(commonElementCount, stackLength, currentTrace, suppressed);
         }
      }
   }
}
