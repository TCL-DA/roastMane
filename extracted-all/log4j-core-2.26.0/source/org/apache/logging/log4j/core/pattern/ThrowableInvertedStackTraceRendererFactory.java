package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;

final class ThrowableInvertedStackTraceRendererFactory extends ThrowableStackTraceRendererFactory {
   static final ThrowableInvertedStackTraceRendererFactory INSTANCE = new ThrowableInvertedStackTraceRendererFactory();

   private ThrowableInvertedStackTraceRendererFactory() {
   }

   ThrowableInvertedStackTraceRenderer createStackTraceRenderer(ThrowableFormatOptions options) {
      return new ThrowableInvertedStackTraceRenderer(options.getIgnorePackages(), options.getLines());
   }
}
