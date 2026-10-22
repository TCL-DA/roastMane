package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.TlsSyslogFrame;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.layout.internal.ExcludeChecker;
import org.apache.logging.log4j.core.layout.internal.IncludeChecker;
import org.apache.logging.log4j.core.layout.internal.ListChecker;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Priority;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageCollectionMessage;
import org.apache.logging.log4j.message.StructuredDataCollectionMessage;
import org.apache.logging.log4j.message.StructuredDataId;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.util.ProcessIdUtil;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "Rfc5424Layout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class Rfc5424Layout extends AbstractStringLayout {
   public static final int DEFAULT_ENTERPRISE_NUMBER = 32473;
   public static final String DEFAULT_ID = "Audit";
   public static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r?\\n");
   @Deprecated
   public static final Pattern PARAM_VALUE_ESCAPE_PATTERN = Pattern.compile("[\\\"\\]\\\\]");
   public static final Pattern ENTERPRISE_ID_PATTERN = Pattern.compile("\\d+(\\.\\d+)*");
   public static final String DEFAULT_MDCID = "mdc";
   private static final int SD_PARAM_NAME_MAX_LENGTH = 32;
   private static final String LF = "\n";
   private static final int TWO_DIGITS = 10;
   private static final int THREE_DIGITS = 100;
   private static final int MILLIS_PER_MINUTE = 60000;
   private static final int MINUTES_PER_HOUR = 60;
   private static final String COMPONENT_KEY = "RFC5424-Converter";
   private final Facility facility;
   private final String defaultId;
   private final String enterpriseNumber;
   private final boolean includeMdc;
   private final String mdcId;
   private final StructuredDataId mdcSdId;
   private final String localHostName;
   private final String appName;
   private final String messageId;
   private final String configName;
   private final String mdcPrefix;
   private final String eventPrefix;
   private final List<String> mdcExcludes;
   private final List<String> mdcIncludes;
   private final List<String> mdcRequired;
   private final ListChecker listChecker;
   private final boolean includeNewLine;
   private final String escapeNewLine;
   private final boolean useTlsMessageFormat;
   private long lastTimestamp = -1L;
   private String timestamppStr;
   private final List<PatternFormatter> exceptionFormatters;
   private final Map<String, Rfc5424Layout.FieldFormatter> fieldFormatters;
   private final String procId;

   private Rfc5424Layout(
      final Configuration config,
      final Facility facility,
      final String id,
      final String ein,
      final boolean includeMDC,
      final boolean includeNL,
      final String escapeNL,
      final String mdcId,
      final String mdcPrefix,
      final String eventPrefix,
      final String appName,
      final String messageId,
      final String excludes,
      final String includes,
      final String required,
      final Charset charset,
      final String exceptionPattern,
      final boolean useTLSMessageFormat,
      final LoggerFields[] loggerFields
   ) {
      super(charset);
      PatternParser exceptionParser = createPatternParser(config, ThrowablePatternConverter.class);
      this.exceptionFormatters = exceptionPattern == null ? null : exceptionParser.parse(exceptionPattern);
      this.facility = facility;
      this.defaultId = id == null ? "Audit" : id;
      this.enterpriseNumber = ein;
      this.includeMdc = includeMDC;
      this.includeNewLine = includeNL;
      this.escapeNewLine = escapeNL == null ? null : Matcher.quoteReplacement(escapeNL);
      this.mdcId = mdcId != null ? mdcId : (id == null ? "mdc" : id);
      this.mdcSdId = new StructuredDataId(this.mdcId, this.enterpriseNumber, null, null);
      this.mdcPrefix = mdcPrefix;
      this.eventPrefix = eventPrefix;
      this.appName = appName;
      this.messageId = messageId;
      this.useTlsMessageFormat = useTLSMessageFormat;
      this.localHostName = NetUtils.getCanonicalLocalHostname();
      ListChecker checker = null;
      if (excludes != null) {
         String[] array = excludes.split(Patterns.COMMA_SEPARATOR);
         if (array.length > 0) {
            this.mdcExcludes = new ArrayList<>(array.length);

            for (String str : array) {
               this.mdcExcludes.add(str.trim());
            }

            checker = new ExcludeChecker(this.mdcExcludes);
         } else {
            this.mdcExcludes = null;
         }
      } else {
         this.mdcExcludes = null;
      }

      if (includes != null) {
         String[] array = includes.split(Patterns.COMMA_SEPARATOR);
         if (array.length > 0) {
            this.mdcIncludes = new ArrayList<>(array.length);

            for (String str : array) {
               this.mdcIncludes.add(str.trim());
            }

            checker = new IncludeChecker(this.mdcIncludes);
         } else {
            this.mdcIncludes = null;
         }
      } else {
         this.mdcIncludes = null;
      }

      if (required != null) {
         String[] array = required.split(Patterns.COMMA_SEPARATOR);
         if (array.length > 0) {
            this.mdcRequired = new ArrayList<>(array.length);

            for (String str : array) {
               this.mdcRequired.add(str.trim());
            }
         } else {
            this.mdcRequired = null;
         }
      } else {
         this.mdcRequired = null;
      }

      this.listChecker = (ListChecker)(checker != null ? checker : ListChecker.NOOP_CHECKER);
      String name = config == null ? null : config.getName();
      this.configName = Strings.isNotEmpty(name) ? name : null;
      this.fieldFormatters = this.createFieldFormatters(loggerFields, config);
      this.procId = ProcessIdUtil.getProcessId();
   }

   private Map<String, Rfc5424Layout.FieldFormatter> createFieldFormatters(final LoggerFields[] loggerFields, final Configuration config) {
      Map<String, Rfc5424Layout.FieldFormatter> sdIdMap = new HashMap<>(loggerFields == null ? 0 : loggerFields.length);
      if (loggerFields != null) {
         for (LoggerFields loggerField : loggerFields) {
            StructuredDataId key = loggerField.getSdId() == null ? this.mdcSdId : loggerField.getSdId();
            Map<String, List<PatternFormatter>> sdParams = new HashMap<>();
            Map<String, String> fields = loggerField.getMap();
            if (!fields.isEmpty()) {
               PatternParser fieldParser = createPatternParser(config, null);

               for (Entry<String, String> entry : fields.entrySet()) {
                  List<PatternFormatter> formatters = fieldParser.parse(entry.getValue());
                  sdParams.put(entry.getKey(), formatters);
               }

               Rfc5424Layout.FieldFormatter fieldFormatter = new Rfc5424Layout.FieldFormatter(sdParams, loggerField.getDiscardIfAllFieldsAreEmpty());
               sdIdMap.put(key.toString(), fieldFormatter);
            }
         }
      }

      return sdIdMap.size() > 0 ? sdIdMap : null;
   }

   private static PatternParser createPatternParser(final Configuration config, final Class<? extends PatternConverter> filterClass) {
      if (config == null) {
         return new PatternParser(config, "Converter", LogEventPatternConverter.class, filterClass);
      } else {
         PatternParser parser = config.getComponent("RFC5424-Converter");
         if (parser == null) {
            parser = new PatternParser(config, "Converter", ThrowablePatternConverter.class);
            config.addComponent("RFC5424-Converter", parser);
            parser = config.getComponent("RFC5424-Converter");
         }

         return parser;
      }
   }

   @Override
   public Map<String, String> getContentFormat() {
      Map<String, String> result = new HashMap<>();
      result.put("structured", "true");
      result.put("formatType", "RFC5424");
      return result;
   }

   public String toSerializable(final LogEvent event) {
      StringBuilder buf = getStringBuilder();
      this.appendPriority(buf, event.getLevel());
      this.appendTimestamp(buf, event.getTimeMillis());
      this.appendSpace(buf);
      this.appendHostName(buf);
      this.appendSpace(buf);
      this.appendAppName(buf);
      this.appendSpace(buf);
      this.appendProcessId(buf);
      this.appendSpace(buf);
      this.appendMessageId(buf, event.getMessage());
      this.appendSpace(buf);
      this.appendStructuredElements(buf, event);
      this.appendMessage(buf, event);
      return this.useTlsMessageFormat ? new TlsSyslogFrame(buf.toString()).toString() : buf.toString();
   }

   private void appendPriority(final StringBuilder buffer, final Level logLevel) {
      buffer.append('<');
      buffer.append(Priority.getPriority(this.facility, logLevel));
      buffer.append(">1 ");
   }

   private void appendTimestamp(final StringBuilder buffer, final long milliseconds) {
      buffer.append(this.computeTimeStampString(milliseconds));
   }

   private void appendSpace(final StringBuilder buffer) {
      buffer.append(' ');
   }

   private void appendHostName(final StringBuilder buffer) {
      buffer.append(this.localHostName);
   }

   private void appendAppName(final StringBuilder buffer) {
      if (this.appName != null) {
         buffer.append(this.appName);
      } else if (this.configName != null) {
         buffer.append(this.configName);
      } else {
         buffer.append('-');
      }
   }

   private void appendProcessId(final StringBuilder buffer) {
      buffer.append(this.getProcId());
   }

   private void appendMessageId(final StringBuilder buffer, final Message message) {
      boolean isStructured = message instanceof StructuredDataMessage;
      String type = isStructured ? ((StructuredDataMessage)message).getType() : null;
      if (type != null) {
         buffer.append(type);
      } else if (this.messageId != null) {
         buffer.append(this.messageId);
      } else {
         buffer.append('-');
      }
   }

   private void appendMessage(final StringBuilder buffer, final LogEvent event) {
      Message message = event.getMessage();
      String text = !(message instanceof StructuredDataMessage) && !(message instanceof MessageCollectionMessage)
         ? message.getFormattedMessage()
         : message.getFormat();
      if (text != null && text.length() > 0) {
         buffer.append(' ').append(this.escapeNewlines(text, this.escapeNewLine));
      }

      if (this.exceptionFormatters != null && event.getThrown() != null) {
         StringBuilder exception = new StringBuilder("\n");

         for (PatternFormatter formatter : this.exceptionFormatters) {
            formatter.format(event, exception);
         }

         buffer.append(this.escapeNewlines(exception.toString(), this.escapeNewLine));
      }

      if (this.includeNewLine) {
         buffer.append("\n");
      }
   }

   private void appendStructuredElements(final StringBuilder buffer, final LogEvent event) {
      Message message = event.getMessage();
      boolean isStructured = message instanceof StructuredDataMessage || message instanceof StructuredDataCollectionMessage;
      if (!isStructured && this.fieldFormatters != null && this.fieldFormatters.isEmpty() && !this.includeMdc) {
         buffer.append('-');
      } else {
         Map<String, Rfc5424Layout.StructuredDataElement> sdElements = new HashMap<>();
         Map<String, String> contextMap = event.getContextData().toMap();
         if (this.mdcRequired != null) {
            this.checkRequired(contextMap);
         }

         if (this.fieldFormatters != null) {
            for (Entry<String, Rfc5424Layout.FieldFormatter> sdElement : this.fieldFormatters.entrySet()) {
               String sdId = sdElement.getKey();
               Rfc5424Layout.StructuredDataElement elem = sdElement.getValue().format(event);
               sdElements.put(sdId, elem);
            }
         }

         if (this.includeMdc && contextMap.size() > 0) {
            String mdcSdIdStr = this.mdcSdId.toString();
            Rfc5424Layout.StructuredDataElement union = sdElements.get(mdcSdIdStr);
            if (union != null) {
               union.union(contextMap);
               sdElements.put(mdcSdIdStr, union);
            } else {
               Rfc5424Layout.StructuredDataElement formattedContextMap = new Rfc5424Layout.StructuredDataElement(contextMap, this.mdcPrefix, false);
               sdElements.put(mdcSdIdStr, formattedContextMap);
            }
         }

         if (isStructured) {
            if (message instanceof MessageCollectionMessage) {
               for (StructuredDataMessage data : (StructuredDataCollectionMessage)message) {
                  this.addStructuredData(sdElements, data);
               }
            } else {
               this.addStructuredData(sdElements, (StructuredDataMessage)message);
            }
         }

         if (sdElements.isEmpty()) {
            buffer.append('-');
         } else {
            for (Entry<String, Rfc5424Layout.StructuredDataElement> entry : sdElements.entrySet()) {
               this.formatStructuredElement(entry.getKey(), entry.getValue(), buffer, this.listChecker);
            }
         }
      }
   }

   private void addStructuredData(final Map<String, Rfc5424Layout.StructuredDataElement> sdElements, final StructuredDataMessage data) {
      Map<String, String> map = data.getData();
      StructuredDataId id = data.getId();
      String sdId = this.getId(id);
      if (sdElements.containsKey(sdId)) {
         Rfc5424Layout.StructuredDataElement union = sdElements.get(id.toString());
         union.union(map);
         sdElements.put(sdId, union);
      } else {
         Rfc5424Layout.StructuredDataElement formattedData = new Rfc5424Layout.StructuredDataElement(map, this.eventPrefix, false);
         sdElements.put(sdId, formattedData);
      }
   }

   private String escapeNewlines(final String text, final String replacement) {
      return null == replacement ? text : NEWLINE_PATTERN.matcher(text).replaceAll(replacement);
   }

   protected String getProcId() {
      return this.procId;
   }

   protected List<String> getMdcExcludes() {
      return this.mdcExcludes;
   }

   protected List<String> getMdcIncludes() {
      return this.mdcIncludes;
   }

   List<String> getMdcRequired() {
      return this.mdcRequired;
   }

   boolean isIncludeNewLine() {
      return this.includeNewLine;
   }

   String getEscapeNewLine() {
      return this.escapeNewLine;
   }

   boolean isUseTlsMessageFormat() {
      return this.useTlsMessageFormat;
   }

   private String computeTimeStampString(final long now) {
      long last;
      synchronized (this) {
         last = this.lastTimestamp;
         if (now == this.lastTimestamp) {
            return this.timestamppStr;
         }
      }

      StringBuilder buffer = new StringBuilder();
      Calendar cal = new GregorianCalendar();
      cal.setTimeInMillis(now);
      buffer.append(Integer.toString(cal.get(1)));
      buffer.append('-');
      this.pad(cal.get(2) + 1, 10, buffer);
      buffer.append('-');
      this.pad(cal.get(5), 10, buffer);
      buffer.append('T');
      this.pad(cal.get(11), 10, buffer);
      buffer.append(':');
      this.pad(cal.get(12), 10, buffer);
      buffer.append(':');
      this.pad(cal.get(13), 10, buffer);
      buffer.append('.');
      this.pad(cal.get(14), 100, buffer);
      int tzmin = (cal.get(15) + cal.get(16)) / 60000;
      if (tzmin == 0) {
         buffer.append('Z');
      } else {
         if (tzmin < 0) {
            tzmin = -tzmin;
            buffer.append('-');
         } else {
            buffer.append('+');
         }

         int tzhour = tzmin / 60;
         tzmin -= tzhour * 60;
         this.pad(tzhour, 10, buffer);
         buffer.append(':');
         this.pad(tzmin, 10, buffer);
      }

      synchronized (this) {
         if (last == this.lastTimestamp) {
            this.lastTimestamp = now;
            this.timestamppStr = buffer.toString();
         }
      }

      return buffer.toString();
   }

   private void pad(final int val, int max, final StringBuilder buf) {
      while (max > 1) {
         if (val < max) {
            buf.append('0');
         }

         max /= 10;
      }

      buf.append(Integer.toString(val));
   }

   private void formatStructuredElement(final String id, final Rfc5424Layout.StructuredDataElement data, final StringBuilder sb, final ListChecker checker) {
      if ((id != null || this.defaultId != null) && !data.discard()) {
         sb.append('[');
         sb.append(id);
         if (!this.mdcSdId.toString().equals(id)) {
            this.appendMap(data.getPrefix(), data.getFields(), sb, ListChecker.NOOP_CHECKER);
         } else {
            this.appendMap(data.getPrefix(), data.getFields(), sb, checker);
         }

         sb.append(']');
      }
   }

   private String getId(final StructuredDataId id) {
      StringBuilder sb = new StringBuilder();
      if (id != null && id.getName() != null) {
         sb.append(id.getName());
      } else {
         sb.append(this.defaultId);
      }

      String ein = id != null ? id.getEnterpriseNumber() : this.enterpriseNumber;
      if ("-1".equals(ein)) {
         ein = this.enterpriseNumber;
      }

      if (!"-1".equals(ein)) {
         sb.append('@').append(ein);
      }

      return sb.toString();
   }

   private void checkRequired(final Map<String, String> map) {
      for (String key : this.mdcRequired) {
         String value = map.get(key);
         if (value == null) {
            throw new LoggingException("Required key " + key + " is missing from the " + this.mdcId);
         }
      }
   }

   private void appendMap(final String prefix, final Map<String, String> map, final StringBuilder sb, final ListChecker checker) {
      SortedMap<String, String> sorted = new TreeMap<>(map);

      for (Entry<String, String> entry : sorted.entrySet()) {
         if (checker.check(entry.getKey()) && entry.getValue() != null) {
            sb.append(' ');
            if (prefix != null) {
               sb.append(prefix);
            }

            String safeKey = this.sanitizeParamName(entry.getKey());
            String safeValue = this.escapeNewlines(this.escapeParamValue(entry.getValue()), this.escapeNewLine);
            StringBuilders.appendKeyDqValue(sb, safeKey, safeValue);
         }
      }
   }

   private String sanitizeParamName(final String key) {
      int length = key.length();
      if (length == 0) {
         return "?";
      } else if (length > 32) {
         return this.sanitizeParamNameSlowPath(key);
      } else {
         for (int i = 0; i < length; i++) {
            if (!isParamNameCharacterValid(key.charAt(i))) {
               return this.sanitizeParamNameSlowPath(key);
            }
         }

         return key;
      }
   }

   private String sanitizeParamNameSlowPath(final String key) {
      StringBuilder sb = new StringBuilder(32);
      int maxLength = Math.min(key.length(), 32);

      for (int i = 0; i < maxLength; i++) {
         char c = key.charAt(i);
         sb.append(isParamNameCharacterValid(c) ? c : '?');
      }

      return sb.toString();
   }

   private static boolean isParamNameCharacterValid(final char c) {
      return c > ' ' && c <= '~' && c != '=' && c != ']' && c != '"';
   }

   private String escapeParamValue(final String value) {
      StringBuilder output = null;

      for (int i = 0; i < value.length(); i++) {
         char cur = value.charAt(i);
         if (cur == '"' || cur == ']' || cur == '\\') {
            if (output == null) {
               output = new StringBuilder(value.substring(0, i));
            }

            output.append("\\");
         }

         if (output != null) {
            output.append(cur);
         }
      }

      return output != null ? output.toString() : value;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("facility=").append(this.facility.name());
      sb.append(" appName=").append(this.appName);
      sb.append(" defaultId=").append(this.defaultId);
      sb.append(" enterpriseNumber=").append(this.enterpriseNumber);
      sb.append(" newLine=").append(this.includeNewLine);
      sb.append(" includeMDC=").append(this.includeMdc);
      sb.append(" messageId=").append(this.messageId);
      return sb.toString();
   }

   @Deprecated
   public static Rfc5424Layout createLayout(
      final Facility facility,
      final String id,
      final int enterpriseNumber,
      final boolean includeMDC,
      final String mdcId,
      final String mdcPrefix,
      final String eventPrefix,
      final boolean newLine,
      final String escapeNL,
      final String appName,
      final String msgId,
      final String excludes,
      String includes,
      final String required,
      final String exceptionPattern,
      final boolean useTlsMessageFormat,
      final LoggerFields[] loggerFields,
      final Configuration config
   ) {
      if (includes != null && excludes != null) {
         LOGGER.error("mdcIncludes and mdcExcludes are mutually exclusive. Includes wil be ignored");
         includes = null;
      }

      return newBuilder()
         .setConfiguration(config)
         .setFacility(facility)
         .setId(id)
         .setEin(String.valueOf(enterpriseNumber))
         .setIncludeMDC(includeMDC)
         .setNewLine(newLine)
         .setNewLineEscape(escapeNL)
         .setMdcId(mdcId)
         .setMdcPrefix(mdcPrefix)
         .setEventPrefix(eventPrefix)
         .setAppName(appName)
         .setMessageId(msgId)
         .setMdcExcludes(excludes)
         .setMdcIncludes(includes)
         .setMdcRequired(required)
         .setCharset(StandardCharsets.UTF_8)
         .setExceptionPattern(exceptionPattern)
         .setUseTlsMessageFormat(useTlsMessageFormat)
         .setLoggerFields(loggerFields)
         .build();
   }

   @PluginBuilderFactory
   public static Rfc5424Layout.Rfc5424LayoutBuilder newBuilder() {
      return new Rfc5424Layout.Rfc5424LayoutBuilder();
   }

   public Facility getFacility() {
      return this.facility;
   }

   public String getDefaultId() {
      return this.defaultId;
   }

   public String getEnterpriseNumber() {
      return this.enterpriseNumber;
   }

   public boolean isIncludeMdc() {
      return this.includeMdc;
   }

   public String getMdcId() {
      return this.mdcId;
   }

   String getLocalHostName() {
      return this.localHostName;
   }

   private class FieldFormatter {
      private final Map<String, List<PatternFormatter>> delegateMap;
      private final boolean discardIfEmpty;

      public FieldFormatter(final Map<String, List<PatternFormatter>> fieldMap, final boolean discardIfEmpty) {
         this.discardIfEmpty = discardIfEmpty;
         this.delegateMap = fieldMap;
      }

      public Rfc5424Layout.StructuredDataElement format(final LogEvent event) {
         Map<String, String> map = new HashMap<>(this.delegateMap.size());

         for (Entry<String, List<PatternFormatter>> entry : this.delegateMap.entrySet()) {
            StringBuilder buffer = new StringBuilder();

            for (PatternFormatter formatter : entry.getValue()) {
               formatter.format(event, buffer);
            }

            map.put(entry.getKey(), buffer.toString());
         }

         return Rfc5424Layout.this.new StructuredDataElement(map, Rfc5424Layout.this.eventPrefix, this.discardIfEmpty);
      }
   }

   public static class Rfc5424LayoutBuilder
      extends AbstractStringLayout.Builder<Rfc5424Layout.Rfc5424LayoutBuilder>
      implements org.apache.logging.log4j.core.util.Builder<Rfc5424Layout> {
      @PluginBuilderAttribute
      private Facility facility = Facility.LOCAL0;
      @PluginBuilderAttribute
      private String id;
      @PluginBuilderAttribute
      private String ein = String.valueOf(32473);
      @PluginBuilderAttribute
      private Integer enterpriseNumber;
      @PluginBuilderAttribute
      private boolean includeMDC = true;
      @PluginBuilderAttribute
      private boolean newLine;
      @PluginBuilderAttribute
      private boolean includeNL;
      @PluginBuilderAttribute
      private String newLineEscape;
      @PluginBuilderAttribute
      private String escapeNL;
      @PluginBuilderAttribute
      private String mdcId = "mdc";
      @PluginBuilderAttribute
      private String mdcPrefix;
      @PluginBuilderAttribute
      private String eventPrefix;
      @PluginBuilderAttribute
      private String appName;
      @PluginBuilderAttribute
      private String messageId;
      @PluginBuilderAttribute
      private String mdcExcludes;
      @PluginBuilderAttribute
      private String excludes;
      @PluginBuilderAttribute
      private String mdcIncludes;
      @PluginBuilderAttribute
      private String includes;
      @PluginBuilderAttribute
      private String mdcRequired;
      @PluginBuilderAttribute
      private String required;
      @PluginBuilderAttribute
      private String exceptionPattern;
      @PluginBuilderAttribute
      private boolean useTlsMessageFormat;
      @PluginBuilderAttribute
      private boolean useTLSMessageFormat;
      @PluginElement("loggerFields")
      private LoggerFields[] loggerFields;

      @Deprecated
      public Rfc5424Layout.Rfc5424LayoutBuilder setConfig(final Configuration config) {
         this.setConfiguration(config);
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setFacility(final Facility facility) {
         this.facility = facility;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setId(final String id) {
         this.id = id;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setEin(final String ein) {
         this.ein = ein;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setIncludeMDC(final boolean includeMDC) {
         this.includeMDC = includeMDC;
         return this;
      }

      @Deprecated
      public Rfc5424Layout.Rfc5424LayoutBuilder setIncludeNL(final boolean includeNL) {
         this.includeNL = includeNL;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setNewLine(boolean newLine) {
         this.newLine = newLine;
         return this;
      }

      @Deprecated
      public Rfc5424Layout.Rfc5424LayoutBuilder setEscapeNL(final String escapeNL) {
         this.escapeNL = escapeNL;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setNewLineEscape(String newLineEscape) {
         this.newLineEscape = newLineEscape;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setMdcId(final String mdcId) {
         this.mdcId = mdcId;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setMdcPrefix(final String mdcPrefix) {
         this.mdcPrefix = mdcPrefix;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setEventPrefix(final String eventPrefix) {
         this.eventPrefix = eventPrefix;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setAppName(final String appName) {
         this.appName = appName;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setMessageId(final String messageId) {
         this.messageId = messageId;
         return this;
      }

      @Deprecated
      public Rfc5424Layout.Rfc5424LayoutBuilder setExcludes(final String excludes) {
         this.excludes = excludes;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setMdcExcludes(String mdcExcludes) {
         this.mdcExcludes = mdcExcludes;
         return this;
      }

      @Deprecated
      public Rfc5424Layout.Rfc5424LayoutBuilder setIncludes(String includes) {
         this.includes = includes;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setMdcIncludes(String mdcIncludes) {
         this.mdcIncludes = mdcIncludes;
         return this;
      }

      @Deprecated
      public Rfc5424Layout.Rfc5424LayoutBuilder setRequired(final String required) {
         this.required = required;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setMdcRequired(String mdcRequired) {
         this.mdcRequired = mdcRequired;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setCharset(final Charset charset) {
         return (Rfc5424Layout.Rfc5424LayoutBuilder)super.setCharset(charset);
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setExceptionPattern(final String exceptionPattern) {
         this.exceptionPattern = exceptionPattern;
         return this;
      }

      @Deprecated
      public Rfc5424Layout.Rfc5424LayoutBuilder setUseTLSMessageFormat(final boolean useTLSMessageFormat) {
         this.useTLSMessageFormat = useTLSMessageFormat;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setUseTlsMessageFormat(boolean useTlsMessageFormat) {
         this.useTlsMessageFormat = useTlsMessageFormat;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setLoggerFields(final LoggerFields[] loggerFields) {
         this.loggerFields = loggerFields;
         return this;
      }

      public Rfc5424Layout.Rfc5424LayoutBuilder setEnterpriseNumber(Integer enterpriseNumber) {
         this.enterpriseNumber = enterpriseNumber;
         return this;
      }

      public Rfc5424Layout build() {
         String effectiveIncludes = Objects.toString(this.mdcIncludes, this.includes);
         String effectiveExcludes = Objects.toString(this.mdcExcludes, this.excludes);
         if (effectiveIncludes != null && effectiveExcludes != null) {
            AbstractLayout.LOGGER.error("mdcIncludes and mdcExcludes are mutually exclusive. Includes will be ignored");
            effectiveIncludes = null;
         }

         if (this.enterpriseNumber != null) {
            this.ein = String.valueOf(this.enterpriseNumber);
         }

         if (this.ein != null && !Rfc5424Layout.ENTERPRISE_ID_PATTERN.matcher(this.ein).matches()) {
            AbstractLayout.LOGGER.warn(String.format("provided EID %s is not in valid format!", this.ein));
            return null;
         } else {
            Charset charset = this.getCharset();
            return new Rfc5424Layout(
               this.getConfiguration(),
               this.facility,
               this.id,
               this.ein,
               this.includeMDC,
               this.newLine || this.includeNL,
               Objects.toString(this.newLineEscape, this.escapeNL),
               this.mdcId,
               this.mdcPrefix,
               this.eventPrefix,
               this.appName,
               this.messageId,
               effectiveExcludes,
               effectiveIncludes,
               Objects.toString(this.mdcRequired, this.required),
               charset != null ? charset : StandardCharsets.UTF_8,
               this.exceptionPattern,
               this.useTlsMessageFormat || this.useTLSMessageFormat,
               this.loggerFields
            );
         }
      }
   }

   private class StructuredDataElement {
      private final Map<String, String> fields;
      private final boolean discardIfEmpty;
      private final String prefix;

      public StructuredDataElement(final Map<String, String> fields, final String prefix, final boolean discardIfEmpty) {
         this.discardIfEmpty = discardIfEmpty;
         this.fields = fields;
         this.prefix = prefix;
      }

      boolean discard() {
         if (!this.discardIfEmpty) {
            return false;
         } else {
            boolean foundNotEmptyValue = false;

            for (Entry<String, String> entry : this.fields.entrySet()) {
               if (Strings.isNotEmpty(entry.getValue())) {
                  foundNotEmptyValue = true;
                  break;
               }
            }

            return !foundNotEmptyValue;
         }
      }

      void union(final Map<String, String> addFields) {
         this.fields.putAll(addFields);
      }

      Map<String, String> getFields() {
         return this.fields;
      }

      String getPrefix() {
         return this.prefix;
      }
   }
}
