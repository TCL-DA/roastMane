package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.util.Throwables;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class ThrowableInvertedPropertyRendererFactory extends ThrowablePropertyRendererFactory {
   static final ThrowableInvertedPropertyRendererFactory INSTANCE = new ThrowableInvertedPropertyRendererFactory();

   private ThrowableInvertedPropertyRendererFactory() {
      super(ThrowableInvertedPropertyRendererFactory::extractThrowingMethod);
   }

   @Nullable
   private static StackTraceElement extractThrowingMethod(final Throwable throwable) {
      Throwable rootThrowable = Throwables.getRootCause(throwable);
      return rootThrowable.getStackTrace()[0];
   }
}
