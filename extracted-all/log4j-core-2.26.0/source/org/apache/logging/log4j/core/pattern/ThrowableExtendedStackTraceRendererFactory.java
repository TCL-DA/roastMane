package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;

final class ThrowableExtendedStackTraceRendererFactory extends ThrowableStackTraceRendererFactory {
   static final ThrowableExtendedStackTraceRendererFactory INSTANCE = new ThrowableExtendedStackTraceRendererFactory();

   private ThrowableExtendedStackTraceRendererFactory() {
   }

   ThrowableExtendedStackTraceRenderer createStackTraceRenderer(ThrowableFormatOptions options) {
      return new ThrowableExtendedStackTraceRenderer(options.getIgnorePackages(), options.getLines());
   }
}
