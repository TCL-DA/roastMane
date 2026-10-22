package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Plugin(
   name = "ExtendedThrowablePatternConverter",
   category = "Converter"
)
@ConverterKeys({"xEx", "xThrowable", "xException"})
public final class ExtendedThrowablePatternConverter extends ThrowablePatternConverter {
   private ExtendedThrowablePatternConverter(@Nullable final Configuration config, final String[] options) {
      super("ExtendedThrowable", "throwable", options, config, ThrowablePropertyRendererFactory.INSTANCE, ThrowableExtendedStackTraceRendererFactory.INSTANCE);
   }

   private static ThrowableExtendedStackTraceRenderer createRenderer(final ThrowableFormatOptions options) {
      return new ThrowableExtendedStackTraceRenderer(options.getIgnorePackages(), options.getLines());
   }

   public static ExtendedThrowablePatternConverter newInstance(@Nullable final Configuration config, final String[] options) {
      return new ExtendedThrowablePatternConverter(config, options);
   }
}
