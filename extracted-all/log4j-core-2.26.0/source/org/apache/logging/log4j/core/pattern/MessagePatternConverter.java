package org.apache.logging.log4j.core.pattern;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MultiformatMessage;
import org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "MessagePatternConverter",
   category = "Converter"
)
@ConverterKeys({"m", "msg", "message"})
@PerformanceSensitive({"allocation"})
public class MessagePatternConverter extends LogEventPatternConverter {
   private static final String LOOKUPS = "lookups";
   private static final String NOLOOKUPS = "nolookups";

   private MessagePatternConverter() {
      super("Message", "message");
   }

   private static TextRenderer loadMessageRenderer(final String[] options) {
      if (options != null) {
         for (String option : options) {
            String var5 = Strings.toRootUpperCase(option);
            switch (var5) {
               case "ANSI":
                  return new JAnsiTextRenderer(options, JAnsiTextRenderer.DEFAULT_MESSAGE_STYLE_MAP);
               case "HTML":
                  return new HtmlTextRenderer(options);
            }
         }
      }

      return null;
   }

   public static MessagePatternConverter newInstance(final Configuration config, final String[] options) {
      String[] formats = withoutLookupOptions(options);
      TextRenderer textRenderer = loadMessageRenderer(formats);
      MessagePatternConverter result = (MessagePatternConverter)(formats != null && formats.length != 0
         ? new MessagePatternConverter.FormattedMessagePatternConverter(formats)
         : MessagePatternConverter.SimpleMessagePatternConverter.INSTANCE);
      if (textRenderer != null) {
         result = new MessagePatternConverter.RenderingPatternConverter(result, textRenderer);
      }

      return result;
   }

   private static String[] withoutLookupOptions(final String[] options) {
      if (options != null && options.length != 0) {
         List<String> results = new ArrayList<>(options.length);

         for (String option : options) {
            if (!"lookups".equalsIgnoreCase(option) && !"nolookups".equalsIgnoreCase(option)) {
               results.add(option);
            } else {
               LOGGER.info("The {} option will be ignored. Message Lookups are no longer supported.", option);
            }
         }

         return results.toArray(Strings.EMPTY_ARRAY);
      } else {
         return options;
      }
   }

   @Override
   public void format(final LogEvent event, final StringBuilder toAppendTo) {
      throw new UnsupportedOperationException();
   }

   private static final class FormattedMessagePatternConverter extends MessagePatternConverter {
      private final String[] formats;

      FormattedMessagePatternConverter(final String[] formats) {
         this.formats = formats;
      }

      @Override
      public void format(final LogEvent event, final StringBuilder toAppendTo) {
         Message msg = event.getMessage();
         if (msg instanceof StringBuilderFormattable) {
            if (msg instanceof MultiFormatStringBuilderFormattable) {
               ((MultiFormatStringBuilderFormattable)msg).formatTo(this.formats, toAppendTo);
            } else {
               ((StringBuilderFormattable)msg).formatTo(toAppendTo);
            }
         } else if (msg != null) {
            toAppendTo.append(msg instanceof MultiformatMessage ? ((MultiformatMessage)msg).getFormattedMessage(this.formats) : msg.getFormattedMessage());
         }
      }
   }

   private static final class RenderingPatternConverter extends MessagePatternConverter {
      private final MessagePatternConverter delegate;
      private final TextRenderer textRenderer;

      RenderingPatternConverter(final MessagePatternConverter delegate, final TextRenderer textRenderer) {
         this.delegate = delegate;
         this.textRenderer = textRenderer;
      }

      @Override
      public void format(final LogEvent event, final StringBuilder toAppendTo) {
         StringBuilder workingBuilder = new StringBuilder(80);
         this.delegate.format(event, workingBuilder);
         this.textRenderer.render(workingBuilder, toAppendTo);
      }
   }

   private static final class SimpleMessagePatternConverter extends MessagePatternConverter {
      private static final MessagePatternConverter INSTANCE = new MessagePatternConverter.SimpleMessagePatternConverter();

      @Override
      public void format(final LogEvent event, final StringBuilder toAppendTo) {
         Message msg = event.getMessage();
         if (msg instanceof StringBuilderFormattable) {
            ((StringBuilderFormattable)msg).formatTo(toAppendTo);
         } else if (msg != null) {
            toAppendTo.append(msg.getFormattedMessage());
         }
      }
   }
}
