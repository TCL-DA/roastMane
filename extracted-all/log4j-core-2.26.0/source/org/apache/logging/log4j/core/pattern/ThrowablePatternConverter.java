package org.apache.logging.log4j.core.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Plugin(
   name = "ThrowablePatternConverter",
   category = "Converter"
)
@ConverterKeys({"ex", "throwable", "exception"})
public class ThrowablePatternConverter extends LogEventPatternConverter {
   @Deprecated
   protected final List<PatternFormatter> formatters;
   private final Function<LogEvent, String> effectiveLineSeparatorProvider;
   protected final ThrowableFormatOptions options;
   private final ThrowableRenderer renderer;

   @Deprecated
   protected ThrowablePatternConverter(final String name, final String style, final String[] options) {
      this(name, style, options, null, null, null);
   }

   @Deprecated
   protected ThrowablePatternConverter(final String name, final String style, final String[] options, @Nullable final Configuration config) {
      this(name, style, options, config, null, null);
   }

   ThrowablePatternConverter(
      final String name,
      final String style,
      final String[] options,
      @Nullable final Configuration config,
      @Nullable final ThrowablePropertyRendererFactory propertyRendererFactory,
      @Nullable final ThrowableStackTraceRendererFactory stackTraceRendererFactory
   ) {
      super(name, style);
      this.options = ThrowableFormatOptions.newInstance(options);
      List<PatternFormatter> suffixFormatters = new ArrayList<>();
      this.effectiveLineSeparatorProvider = createEffectiveLineSeparator(this.options.getSeparator(), this.options.getSuffix(), config, suffixFormatters);
      this.formatters = Collections.unmodifiableList(suffixFormatters);
      this.renderer = createEffectiveRenderer(options, this.options, propertyRendererFactory, stackTraceRendererFactory);
   }

   public static ThrowablePatternConverter newInstance(@Nullable final Configuration config, final String[] options) {
      return new ThrowablePatternConverter("Throwable", "throwable", options, config, null, null);
   }

   @Override
   public void format(final LogEvent event, final StringBuilder buffer) {
      Objects.requireNonNull(event, "event");
      Objects.requireNonNull(buffer, "buffer");
      Throwable throwable = event.getThrown();
      if (throwable != null) {
         String lineSeparator = this.effectiveLineSeparatorProvider.apply(event);
         this.renderer.renderThrowable(buffer, throwable, lineSeparator);
      }
   }

   @Override
   public boolean handlesThrowable() {
      return true;
   }

   public ThrowableFormatOptions getOptions() {
      return this.options;
   }

   private static Function<LogEvent, String> createEffectiveLineSeparator(
      final String separator, @Nullable final String suffix, @Nullable final Configuration config, final List<PatternFormatter> suffixFormatters
   ) {
      Objects.requireNonNull(separator, "separator");
      Objects.requireNonNull(suffixFormatters, "suffixFormatters");
      if (suffix != null) {
         PatternParser parser = PatternLayout.createPatternParser(config);

         for (PatternFormatter suffixFormatter : parser.parse(suffix)) {
            if (!suffixFormatter.handlesThrowable()) {
               suffixFormatters.add(suffixFormatter);
            }
         }

         return logEvent -> {
            StringBuilder buffer = new StringBuilder();
            buffer.append(' ');

            for (PatternFormatter suffixFormatterx : suffixFormatters) {
               suffixFormatterx.format(logEvent, buffer);
            }

            boolean blankSuffix = buffer.length() == 1;
            if (blankSuffix) {
               return separator;
            } else {
               buffer.append(separator);
               return buffer.toString();
            }
         };
      } else {
         return logEvent -> separator;
      }
   }

   private static ThrowableRenderer createEffectiveRenderer(
      final String[] rawOptions,
      final ThrowableFormatOptions options,
      @Nullable final ThrowablePropertyRendererFactory propertyRendererFactory,
      @Nullable final ThrowableStackTraceRendererFactory stackTraceRendererFactory
   ) {
      ThrowablePropertyRendererFactory effectivePropertyRendererFactory = propertyRendererFactory != null
         ? propertyRendererFactory
         : ThrowablePropertyRendererFactory.INSTANCE;
      ThrowableRenderer propertyRenderer = effectivePropertyRendererFactory.createPropertyRenderer(rawOptions);
      if (propertyRenderer != null) {
         return propertyRenderer;
      } else {
         ThrowableStackTraceRendererFactory effectiveStackTraceRendererFactory = stackTraceRendererFactory != null
            ? stackTraceRendererFactory
            : ThrowableStackTraceRendererFactory.INSTANCE;
         return effectiveStackTraceRendererFactory.createStackTraceRenderer(options);
      }
   }

   @Deprecated
   protected String getSuffix(final LogEvent logEvent) {
      Objects.requireNonNull(logEvent, "logEvent");
      String effectiveLineSeparator = this.effectiveLineSeparatorProvider.apply(logEvent);
      return this.options.getSeparator().equals(effectiveLineSeparator)
         ? ""
         : effectiveLineSeparator.substring(1, effectiveLineSeparator.length() - this.options.getSeparator().length());
   }
}
