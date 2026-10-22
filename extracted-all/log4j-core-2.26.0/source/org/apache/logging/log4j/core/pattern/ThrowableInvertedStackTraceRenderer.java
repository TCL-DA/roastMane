package org.apache.logging.log4j.core.pattern;

import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

final class ThrowableInvertedStackTraceRenderer extends ThrowableStackTraceRenderer<ThrowableStackTraceRenderer.Context> {
   private static final String WRAPPED_BY_CAPTION = "Wrapped by: ";

   ThrowableInvertedStackTraceRenderer(final List<String> ignoredPackageNames, final int maxLineCount) {
      super(ignoredPackageNames, maxLineCount);
   }

   @Override
   void renderThrowable(
      final StringBuilder buffer,
      final Throwable throwable,
      final ThrowableStackTraceRenderer.Context context,
      final Set<Throwable> visitedThrowables,
      final String lineSeparator
   ) {
      this.renderThrowable(buffer, throwable, context, visitedThrowables, "", lineSeparator, false);
   }

   private void renderThrowable(
      final StringBuilder buffer,
      final Throwable throwable,
      final ThrowableStackTraceRenderer.Context context,
      final Set<Throwable> visitedThrowables,
      final String prefix,
      final String lineSeparator,
      boolean lineCapacityAcquired
   ) {
      boolean circular = !visitedThrowables.add(throwable);
      if (circular) {
         if (!lineCapacityAcquired) {
            this.acquireLineCapacity(context);
         }

         buffer.append("[CIRCULAR REFERENCE: ");
         renderThrowableMessage(buffer, throwable);
         buffer.append(']');
         buffer.append(lineSeparator);
      } else {
         ThrowableStackTraceRenderer.Context.Metadata metadata = context.metadataByThrowable.get(throwable);
         lineCapacityAcquired = this.renderCause(buffer, throwable.getCause(), context, visitedThrowables, prefix, lineSeparator, lineCapacityAcquired);
         if (!lineCapacityAcquired) {
            this.acquireLineCapacity(context);
         }

         renderThrowableMessage(buffer, throwable);
         buffer.append(lineSeparator);
         this.renderStackTraceElements(buffer, context, metadata, prefix, lineSeparator);
         this.renderSuppressed(buffer, metadata.suppressed, context, visitedThrowables, prefix + '\t', lineSeparator);
      }
   }

   private boolean renderCause(
      final StringBuilder buffer,
      @Nullable final Throwable cause,
      final ThrowableStackTraceRenderer.Context context,
      final Set<Throwable> visitedThrowables,
      final String prefix,
      final String lineSeparator,
      final boolean lineCapacityAcquired
   ) {
      if (cause != null) {
         this.renderThrowable(buffer, cause, context, visitedThrowables, prefix, lineSeparator, lineCapacityAcquired);
         this.acquireLineCapacity(context);
         buffer.append(prefix);
         buffer.append("Wrapped by: ");
         return true;
      } else {
         return lineCapacityAcquired;
      }
   }

   @Override
   void renderSuppressed(
      final StringBuilder buffer,
      final Throwable[] suppressedThrowables,
      final ThrowableStackTraceRenderer.Context context,
      final Set<Throwable> visitedThrowables,
      final String prefix,
      final String lineSeparator
   ) {
      if (suppressedThrowables.length > 0) {
         this.acquireLineCapacity(context);
         buffer.append(prefix);
         buffer.append("Suppressed: ");

         for (int suppressedThrowableIndex = 0; suppressedThrowableIndex < suppressedThrowables.length; suppressedThrowableIndex++) {
            Throwable suppressedThrowable = suppressedThrowables[suppressedThrowableIndex];
            boolean lineCapacityAcquired = suppressedThrowableIndex == 0;
            this.renderThrowable(buffer, suppressedThrowable, context, visitedThrowables, prefix, lineSeparator, lineCapacityAcquired);
         }
      }
   }
}
