package org.apache.logging.log4j.core.pattern;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public final class JAnsiTextRenderer implements TextRenderer {
   private static final Logger LOGGER = StatusLogger.getLogger();
   public static final Map<String, String> DefaultExceptionStyleMap;
   static final Map<String, String> DEFAULT_MESSAGE_STYLE_MAP = Collections.emptyMap();
   private static final Map<String, Map<String, String>> PREFEDINED_STYLE_MAPS;
   private static final String BEGIN_TOKEN = "@|";
   private static final String END_TOKEN = "|@";
   private static final int CSI_LENGTH = 2;
   private final String beginToken;
   private final int beginTokenLen;
   private final String endToken;
   private final int endTokenLen;
   private final Map<String, String> styleMap;

   private static Entry<String, String> entry(final String name, final AnsiEscape... codes) {
      StringBuilder sb = new StringBuilder(AnsiEscape.CSI.getCode());

      for (AnsiEscape code : codes) {
         sb.append(code.getCode());
      }

      return new SimpleImmutableEntry<>(name, sb.toString());
   }

   @SafeVarargs
   private static <V> Map<String, V> ofEntries(final Entry<String, V>... entries) {
      Map<String, V> map = new HashMap<>(entries.length);

      for (Entry<String, V> entry : entries) {
         map.put(entry.getKey(), entry.getValue());
      }

      return Collections.unmodifiableMap(map);
   }

   public JAnsiTextRenderer(final String[] formats, final Map<String, String> defaultStyleMap) {
      if (formats.length > 1) {
         String stylesStr = formats[1];
         Map<String, String> map = AnsiEscape.createMap(stylesStr.split("\\s", -1), new String[]{"BeginToken", "EndToken", "Style"}, ",");
         this.beginToken = Objects.toString(map.remove("BeginToken"), "@|");
         this.endToken = Objects.toString(map.remove("EndToken"), "|@");
         String predefinedStyle = map.remove("Style");
         Map<String, String> styleMap = new HashMap<>(map.size() + defaultStyleMap.size());
         defaultStyleMap.forEach((k, v) -> styleMap.put(Strings.toRootUpperCase(k), v));
         if (predefinedStyle != null) {
            Map<String, String> predefinedMap = PREFEDINED_STYLE_MAPS.get(predefinedStyle);
            if (predefinedMap != null) {
               map.putAll(predefinedMap);
            } else {
               LOGGER.warn("Unknown predefined map name {}, pick one of {}", predefinedStyle, PREFEDINED_STYLE_MAPS.keySet());
            }
         }

         styleMap.putAll(map);
         this.styleMap = Collections.unmodifiableMap(styleMap);
      } else {
         this.beginToken = "@|";
         this.endToken = "|@";
         this.styleMap = Collections.unmodifiableMap(defaultStyleMap);
      }

      this.beginTokenLen = this.beginToken.length();
      this.endTokenLen = this.endToken.length();
   }

   private void render(final String input, final StringBuilder output, final String... styleNames) {
      boolean first = true;

      for (String styleName : styleNames) {
         String escape = this.styleMap.get(Strings.toRootUpperCase(styleName));
         if (escape != null) {
            merge(escape, output, first);
         } else {
            merge(AnsiEscape.createSequence(styleName), output, first);
         }

         first = false;
      }

      output.append(input).append(AnsiEscape.getDefaultStyle());
   }

   private static void merge(final String escapeSequence, final StringBuilder output, final boolean first) {
      if (first) {
         output.append(escapeSequence);
      } else {
         output.setLength(output.length() - 1);
         output.append(AnsiEscape.SEPARATOR.getCode());
         output.append(escapeSequence.substring(2));
      }
   }

   @Override
   public void render(final String input, final StringBuilder output, final String styleName) throws IllegalArgumentException {
      this.render(input, output, styleName.split(",", -1));
   }

   @Override
   public void render(final StringBuilder input, final StringBuilder output) throws IllegalArgumentException {
      int pos = 0;

      while (true) {
         int beginTokenPos = input.indexOf(this.beginToken, pos);
         if (beginTokenPos == -1) {
            output.append((CharSequence)(pos == 0 ? input : input.substring(pos, input.length())));
            return;
         }

         output.append(input.substring(pos, beginTokenPos));
         int endTokenPos = input.indexOf(this.endToken, beginTokenPos);
         if (endTokenPos == -1) {
            LOGGER.warn("Missing matching end token {} for token at position {}: '{}'", this.endToken, beginTokenPos, input);
            output.append((CharSequence)(beginTokenPos == 0 ? input : input.substring(beginTokenPos, input.length())));
            return;
         }

         beginTokenPos += this.beginTokenLen;
         String spec = input.substring(beginTokenPos, endTokenPos);
         String[] items = spec.split("\\s", 2);
         if (items.length == 1) {
            LOGGER.warn("Missing argument in ANSI escape specification '{}'", spec);
            output.append(this.beginToken).append(spec).append(this.endToken);
         } else {
            this.render(items[1], output, items[0].split(",", -1));
         }

         pos = endTokenPos + this.endTokenLen;
      }
   }

   public Map<String, String> getStyleMap() {
      return this.styleMap;
   }

   @Override
   public String toString() {
      return "AnsiMessageRenderer [beginToken="
         + this.beginToken
         + ", beginTokenLen="
         + this.beginTokenLen
         + ", endToken="
         + this.endToken
         + ", endTokenLen="
         + this.endTokenLen
         + ", styleMap="
         + this.styleMap
         + "]";
   }

   static {
      Map<String, String> spock = ofEntries(
         entry("Prefix", AnsiEscape.WHITE),
         entry("Name", AnsiEscape.BG_RED, AnsiEscape.WHITE),
         entry("NameMessageSeparator", AnsiEscape.BG_RED, AnsiEscape.WHITE),
         entry("Message", AnsiEscape.BG_RED, AnsiEscape.WHITE, AnsiEscape.BOLD),
         entry("At", AnsiEscape.WHITE),
         entry("CauseLabel", AnsiEscape.WHITE),
         entry("Text", AnsiEscape.WHITE),
         entry("More", AnsiEscape.WHITE),
         entry("Suppressed", AnsiEscape.WHITE),
         entry("StackTraceElement.ClassLoaderName", AnsiEscape.WHITE),
         entry("StackTraceElement.ClassLoaderSeparator", AnsiEscape.WHITE),
         entry("StackTraceElement.ModuleName", AnsiEscape.WHITE),
         entry("StackTraceElement.ModuleVersionSeparator", AnsiEscape.WHITE),
         entry("StackTraceElement.ModuleVersion", AnsiEscape.WHITE),
         entry("StackTraceElement.ModuleNameSeparator", AnsiEscape.WHITE),
         entry("StackTraceElement.ClassName", AnsiEscape.YELLOW),
         entry("StackTraceElement.ClassMethodSeparator", AnsiEscape.YELLOW),
         entry("StackTraceElement.MethodName", AnsiEscape.YELLOW),
         entry("StackTraceElement.NativeMethod", AnsiEscape.YELLOW),
         entry("StackTraceElement.FileName", AnsiEscape.RED),
         entry("StackTraceElement.LineNumber", AnsiEscape.RED),
         entry("StackTraceElement.Container", AnsiEscape.RED),
         entry("StackTraceElement.ContainerSeparator", AnsiEscape.WHITE),
         entry("StackTraceElement.UnknownSource", AnsiEscape.RED),
         entry("ExtraClassInfo.Inexact", AnsiEscape.YELLOW),
         entry("ExtraClassInfo.Container", AnsiEscape.YELLOW),
         entry("ExtraClassInfo.ContainerSeparator", AnsiEscape.YELLOW),
         entry("ExtraClassInfo.Location", AnsiEscape.YELLOW),
         entry("ExtraClassInfo.Version", AnsiEscape.YELLOW)
      );
      Map<String, String> kirk = ofEntries(
         entry("Prefix", AnsiEscape.WHITE),
         entry("Name", AnsiEscape.BG_RED, AnsiEscape.YELLOW, AnsiEscape.BOLD),
         entry("NameMessageSeparator", AnsiEscape.BG_RED, AnsiEscape.YELLOW),
         entry("Message", AnsiEscape.BG_RED, AnsiEscape.WHITE, AnsiEscape.BOLD),
         entry("At", AnsiEscape.WHITE),
         entry("CauseLabel", AnsiEscape.WHITE),
         entry("Text", AnsiEscape.WHITE),
         entry("More", AnsiEscape.WHITE),
         entry("Suppressed", AnsiEscape.WHITE),
         entry("StackTraceElement.ClassLoaderName", AnsiEscape.WHITE),
         entry("StackTraceElement.ClassLoaderSeparator", AnsiEscape.WHITE),
         entry("StackTraceElement.ModuleName", AnsiEscape.WHITE),
         entry("StackTraceElement.ModuleVersionSeparator", AnsiEscape.WHITE),
         entry("StackTraceElement.ModuleVersion", AnsiEscape.WHITE),
         entry("StackTraceElement.ModuleNameSeparator", AnsiEscape.WHITE),
         entry("StackTraceElement.ClassName", AnsiEscape.BG_RED, AnsiEscape.WHITE),
         entry("StackTraceElement.ClassMethodSeparator", AnsiEscape.BG_RED, AnsiEscape.YELLOW),
         entry("StackTraceElement.MethodName", AnsiEscape.BG_RED, AnsiEscape.YELLOW),
         entry("StackTraceElement.NativeMethod", AnsiEscape.BG_RED, AnsiEscape.YELLOW),
         entry("StackTraceElement.FileName", AnsiEscape.RED),
         entry("StackTraceElement.LineNumber", AnsiEscape.RED),
         entry("StackTraceElement.Container", AnsiEscape.RED),
         entry("StackTraceElement.ContainerSeparator", AnsiEscape.WHITE),
         entry("StackTraceElement.UnknownSource", AnsiEscape.RED),
         entry("ExtraClassInfo.Inexact", AnsiEscape.YELLOW),
         entry("ExtraClassInfo.Container", AnsiEscape.WHITE),
         entry("ExtraClassInfo.ContainerSeparator", AnsiEscape.WHITE),
         entry("ExtraClassInfo.Location", AnsiEscape.YELLOW),
         entry("ExtraClassInfo.Version", AnsiEscape.YELLOW)
      );
      DefaultExceptionStyleMap = spock;
      Map<String, Map<String, String>> predefinedStyleMaps = new HashMap<>();
      predefinedStyleMaps.put("Spock", spock);
      predefinedStyleMaps.put("Kirk", kirk);
      PREFEDINED_STYLE_MAPS = Collections.unmodifiableMap(predefinedStyleMaps);
   }
}
