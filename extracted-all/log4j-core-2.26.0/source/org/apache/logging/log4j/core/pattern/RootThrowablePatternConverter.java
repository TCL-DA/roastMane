package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Plugin(
   name = "RootThrowablePatternConverter",
   category = "Converter"
)
@ConverterKeys({"rEx", "rThrowable", "rException"})
public final class RootThrowablePatternConverter extends ThrowablePatternConverter {
   private RootThrowablePatternConverter(@Nullable final Configuration config, final String[] options) {
      super(
         "RootThrowable", "throwable", options, config, ThrowableInvertedPropertyRendererFactory.INSTANCE, ThrowableInvertedStackTraceRendererFactory.INSTANCE
      );
   }

   public static RootThrowablePatternConverter newInstance(@Nullable final Configuration config, final String[] options) {
      return new RootThrowablePatternConverter(config, options);
   }
}
