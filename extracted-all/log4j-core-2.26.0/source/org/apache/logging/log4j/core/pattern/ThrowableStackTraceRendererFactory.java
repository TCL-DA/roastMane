package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;

class ThrowableStackTraceRendererFactory {
   static final ThrowableStackTraceRendererFactory INSTANCE = new ThrowableStackTraceRendererFactory();

   ThrowableStackTraceRenderer<?> createStackTraceRenderer(final ThrowableFormatOptions options) {
      return new ThrowableStackTraceRenderer(options.getIgnorePackages(), options.getLines());
   }
}
