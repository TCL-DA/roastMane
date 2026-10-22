package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamWriteCapability;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.Tag;

public class YAMLGenerator extends GeneratorBase {
   protected static final long MIN_INT_AS_LONG = -2147483648L;
   protected static final long MAX_INT_AS_LONG = 2147483647L;
   protected static final Pattern PLAIN_NUMBER_P = Pattern.compile("-?[0-9]*(\\.[0-9]*)?");
   protected static final String TAG_BINARY = Tag.BINARY.toString();
   protected final IOContext _ioContext;
   protected int _formatFeatures;
   protected Writer _writer;
   protected DumperOptions _outputOptions;
   protected final Version _docVersion;
   private static final ScalarStyle STYLE_UNQUOTED_NAME = ScalarStyle.PLAIN;
   private static final ScalarStyle STYLE_SCALAR = ScalarStyle.PLAIN;
   private static final ScalarStyle STYLE_QUOTED = ScalarStyle.DOUBLE_QUOTED;
   private static final ScalarStyle STYLE_LITERAL = ScalarStyle.LITERAL;
   private static final ScalarStyle STYLE_BASE64 = STYLE_LITERAL;
   private static final ScalarStyle STYLE_PLAIN = ScalarStyle.PLAIN;
   protected Emitter _emitter;
   protected String _objectId;
   protected String _typeId;
   protected int _rootValueCount;
   protected final StringQuotingChecker _quotingChecker;
   private static final ImplicitTuple NO_TAGS = new ImplicitTuple(true, true);
   private static final ImplicitTuple EXPLICIT_TAGS = new ImplicitTuple(false, false);

   public YAMLGenerator(IOContext ctxt, int jsonFeatures, int yamlFeatures, StringQuotingChecker quotingChecker, ObjectCodec codec, Writer out, Version version) throws IOException {
      super(jsonFeatures, codec);
      this._ioContext = ctxt;
      this._formatFeatures = yamlFeatures;
      this._quotingChecker = (StringQuotingChecker)(quotingChecker == null ? StringQuotingChecker.Default.instance() : quotingChecker);
      this._writer = out;
      this._docVersion = version;
      this._outputOptions = this.buildDumperOptions(jsonFeatures, yamlFeatures, version);
      this._emitter = new Emitter(this._writer, this._outputOptions);
      this._emit(new StreamStartEvent(null, null));
      this._emitStartDocument();
   }

   public YAMLGenerator(
      IOContext ctxt, int jsonFeatures, int yamlFeatures, StringQuotingChecker quotingChecker, ObjectCodec codec, Writer out, DumperOptions dumperOptions
   ) throws IOException {
      super(jsonFeatures, codec);
      this._ioContext = ctxt;
      this._formatFeatures = yamlFeatures;
      this._quotingChecker = (StringQuotingChecker)(quotingChecker == null ? StringQuotingChecker.Default.instance() : quotingChecker);
      this._writer = out;
      this._docVersion = dumperOptions.getVersion();
      this._outputOptions = dumperOptions;
      this._emitter = new Emitter(this._writer, this._outputOptions);
      this._emit(new StreamStartEvent(null, null));
      this._emitStartDocument();
   }

   @Deprecated
   public YAMLGenerator(IOContext ctxt, int jsonFeatures, int yamlFeatures, ObjectCodec codec, Writer out, Version version) throws IOException {
      this(ctxt, jsonFeatures, yamlFeatures, null, codec, out, version);
   }

   protected DumperOptions buildDumperOptions(int jsonFeatures, int yamlFeatures, Version version) {
      DumperOptions opt = new DumperOptions();
      if (YAMLGenerator.Feature.CANONICAL_OUTPUT.enabledIn(this._formatFeatures)) {
         opt.setCanonical(true);
      } else {
         opt.setCanonical(false);
         opt.setDefaultFlowStyle(FlowStyle.BLOCK);
      }

      opt.setSplitLines(YAMLGenerator.Feature.SPLIT_LINES.enabledIn(this._formatFeatures));
      if (YAMLGenerator.Feature.INDENT_ARRAYS.enabledIn(this._formatFeatures)) {
         opt.setIndicatorIndent(1);
         opt.setIndent(2);
      }

      if (YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR.enabledIn(this._formatFeatures)) {
         opt.setIndicatorIndent(2);
         opt.setIndentWithIndicator(true);
      }

      if (YAMLGenerator.Feature.USE_PLATFORM_LINE_BREAKS.enabledIn(this._formatFeatures)) {
         opt.setLineBreak(LineBreak.getPlatformLineBreak());
      }

      if (YAMLGenerator.Feature.ALLOW_LONG_KEYS.enabledIn(this._formatFeatures)) {
         opt.setMaxSimpleKeyLength(1024);
      }

      return opt;
   }

   public com.fasterxml.jackson.core.Version version() {
      return PackageVersion.VERSION;
   }

   public YAMLGenerator useDefaultPrettyPrinter() {
      return this;
   }

   public YAMLGenerator setPrettyPrinter(PrettyPrinter pp) {
      return this;
   }

   public Object getOutputTarget() {
      return this._writer;
   }

   public int getOutputBuffered() {
      return -1;
   }

   public int getFormatFeatures() {
      return this._formatFeatures;
   }

   public JsonGenerator overrideFormatFeatures(int values, int mask) {
      this._formatFeatures = this._formatFeatures & ~mask | values & mask;
      return this;
   }

   public boolean canUseSchema(FormatSchema schema) {
      return false;
   }

   public boolean canWriteFormattedNumbers() {
      return true;
   }

   public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
      return DEFAULT_TEXTUAL_WRITE_CAPABILITIES;
   }

   public YAMLGenerator enable(YAMLGenerator.Feature f) {
      this._formatFeatures = this._formatFeatures | f.getMask();
      return this;
   }

   public YAMLGenerator disable(YAMLGenerator.Feature f) {
      this._formatFeatures = this._formatFeatures & ~f.getMask();
      return this;
   }

   public final boolean isEnabled(YAMLGenerator.Feature f) {
      return (this._formatFeatures & f.getMask()) != 0;
   }

   public YAMLGenerator configure(YAMLGenerator.Feature f, boolean state) {
      if (state) {
         this.enable(f);
      } else {
         this.disable(f);
      }

      return this;
   }

   public final void writeFieldName(String name) throws IOException {
      if (this._writeContext.writeFieldName(name) == 4) {
         this._reportError("Can not write a field name, expecting a value");
      }

      this._writeFieldName(name);
   }

   public final void writeFieldName(SerializableString name) throws IOException {
      if (this._writeContext.writeFieldName(name.getValue()) == 4) {
         this._reportError("Can not write a field name, expecting a value");
      }

      this._writeFieldName(name.getValue());
   }

   public void writeFieldId(long id) throws IOException {
      String idStr = Long.valueOf(id).toString();
      if (this._writeContext.writeFieldName(idStr) == 4) {
         this._reportError("Can not write a field id, expecting a value");
      }

      this._writeScalar(idStr, "int", STYLE_SCALAR);
   }

   private final void _writeFieldName(String name) throws IOException {
      this._writeScalar(name, "string", this._quotingChecker.needToQuoteName(name) ? STYLE_QUOTED : STYLE_UNQUOTED_NAME);
   }

   public final void flush() throws IOException {
      if (this.isEnabled(com.fasterxml.jackson.core.JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
         this._writer.flush();
      }
   }

   public void close() throws IOException {
      if (!this.isClosed()) {
         this._emitEndDocument();
         this._emit(new StreamEndEvent(null, null));
         super.close();
         if (this._writer != null) {
            if (this._ioContext.isResourceManaged() || this.isEnabled(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
               this._writer.close();
            } else if (this.isEnabled(com.fasterxml.jackson.core.JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
               this._writer.flush();
            }
         }
      }
   }

   public final void writeStartArray() throws IOException {
      this._verifyValueWrite("start an array");
      this._writeContext = this._writeContext.createChildArrayContext();
      FlowStyle style = this._outputOptions.getDefaultFlowStyle();
      String yamlTag = this._typeId;
      boolean implicit = yamlTag == null;
      String anchor = this._objectId;
      if (anchor != null) {
         this._objectId = null;
      }

      this._emit(new SequenceStartEvent(anchor, yamlTag, implicit, null, null, style));
   }

   public final void writeEndArray() throws IOException {
      if (!this._writeContext.inArray()) {
         this._reportError("Current context not Array but " + this._writeContext.typeDesc());
      }

      this._typeId = null;
      this._writeContext = this._writeContext.getParent();
      this._emit(new SequenceEndEvent(null, null));
   }

   public final void writeStartObject() throws IOException {
      this._verifyValueWrite("start an object");
      this._writeContext = this._writeContext.createChildObjectContext();
      FlowStyle style = this._outputOptions.getDefaultFlowStyle();
      String yamlTag = this._typeId;
      boolean implicit = yamlTag == null;
      String anchor = this._objectId;
      if (anchor != null) {
         this._objectId = null;
      }

      this._emit(new MappingStartEvent(anchor, yamlTag, implicit, null, null, style));
   }

   public final void writeEndObject() throws IOException {
      if (!this._writeContext.inObject()) {
         this._reportError("Current context not Object but " + this._writeContext.typeDesc());
      }

      this._typeId = null;
      this._writeContext = this._writeContext.getParent();
      this._emit(new MappingEndEvent(null, null));
   }

   public void writeString(String text) throws IOException, JsonGenerationException {
      if (text == null) {
         this.writeNull();
      } else {
         this._verifyValueWrite("write String value");
         if (text.isEmpty()) {
            this._writeScalar(text, "string", STYLE_QUOTED);
         } else {
            ScalarStyle style;
            if (YAMLGenerator.Feature.MINIMIZE_QUOTES.enabledIn(this._formatFeatures)) {
               if (text.indexOf(10) >= 0) {
                  style = STYLE_LITERAL;
               } else if (!this._quotingChecker.needToQuoteValue(text)
                  && (!YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS.enabledIn(this._formatFeatures) || !PLAIN_NUMBER_P.matcher(text).matches())) {
                  style = STYLE_PLAIN;
               } else {
                  style = STYLE_QUOTED;
               }
            } else if (YAMLGenerator.Feature.LITERAL_BLOCK_STYLE.enabledIn(this._formatFeatures) && text.indexOf(10) >= 0) {
               style = STYLE_LITERAL;
            } else {
               style = STYLE_QUOTED;
            }

            this._writeScalar(text, "string", style);
         }
      }
   }

   public void writeString(char[] text, int offset, int len) throws IOException {
      this.writeString(new String(text, offset, len));
   }

   public final void writeString(SerializableString sstr) throws IOException {
      this.writeString(sstr.toString());
   }

   public void writeRawUTF8String(byte[] text, int offset, int len) throws IOException {
      this._reportUnsupportedOperation();
   }

   public final void writeUTF8String(byte[] text, int offset, int len) throws IOException {
      this.writeString(new String(text, offset, len, "UTF-8"));
   }

   public void writeRaw(String text) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeRaw(String text, int offset, int len) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeRaw(char[] text, int offset, int len) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeRaw(char c) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeRawValue(String text) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeRawValue(String text, int offset, int len) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeRawValue(char[] text, int offset, int len) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
      if (data == null) {
         this.writeNull();
      } else {
         this._verifyValueWrite("write Binary value");
         if (offset > 0 || offset + len != data.length) {
            data = Arrays.copyOfRange(data, offset, offset + len);
         }

         this._writeScalarBinary(b64variant, data);
      }
   }

   public void writeBoolean(boolean state) throws IOException {
      this._verifyValueWrite("write boolean value");
      this._writeScalar(state ? "true" : "false", "bool", STYLE_SCALAR);
   }

   public void writeNumber(int i) throws IOException {
      this._verifyValueWrite("write number");
      this._writeScalar(String.valueOf(i), "int", STYLE_SCALAR);
   }

   public void writeNumber(long l) throws IOException {
      if (l <= 2147483647L && l >= -2147483648L) {
         this.writeNumber((int)l);
      } else {
         this._verifyValueWrite("write number");
         this._writeScalar(String.valueOf(l), "long", STYLE_SCALAR);
      }
   }

   public void writeNumber(BigInteger v) throws IOException {
      if (v == null) {
         this.writeNull();
      } else {
         this._verifyValueWrite("write number");
         this._writeScalar(String.valueOf(v.toString()), "java.math.BigInteger", STYLE_SCALAR);
      }
   }

   public void writeNumber(double d) throws IOException {
      this._verifyValueWrite("write number");
      this._writeScalar(String.valueOf(d), "double", STYLE_SCALAR);
   }

   public void writeNumber(float f) throws IOException {
      this._verifyValueWrite("write number");
      this._writeScalar(String.valueOf(f), "float", STYLE_SCALAR);
   }

   public void writeNumber(BigDecimal dec) throws IOException {
      if (dec == null) {
         this.writeNull();
      } else {
         this._verifyValueWrite("write number");
         String str = this.isEnabled(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) ? dec.toPlainString() : dec.toString();
         this._writeScalar(str, "java.math.BigDecimal", STYLE_SCALAR);
      }
   }

   public void writeNumber(String encodedValue) throws IOException, JsonGenerationException, UnsupportedOperationException {
      if (encodedValue == null) {
         this.writeNull();
      } else {
         this._verifyValueWrite("write number");
         this._writeScalar(encodedValue, "number", STYLE_SCALAR);
      }
   }

   public void writeNull() throws IOException {
      this._verifyValueWrite("write null value");
      this._writeScalar("null", "object", STYLE_SCALAR);
   }

   public boolean canWriteObjectId() {
      return YAMLGenerator.Feature.USE_NATIVE_OBJECT_ID.enabledIn(this._formatFeatures);
   }

   public boolean canWriteTypeId() {
      return YAMLGenerator.Feature.USE_NATIVE_TYPE_ID.enabledIn(this._formatFeatures);
   }

   public void writeTypeId(Object id) throws IOException {
      this._typeId = String.valueOf(id);
   }

   public void writeObjectRef(Object id) throws IOException {
      this._verifyValueWrite("write Object reference");
      AliasEvent evt = new AliasEvent(String.valueOf(id), null, null);
      this._emit(evt);
   }

   public void writeObjectId(Object id) throws IOException {
      this._objectId = id == null ? null : String.valueOf(id);
   }

   protected final void _verifyValueWrite(String typeMsg) throws IOException {
      int status = this._writeContext.writeValue();
      if (status == 5) {
         this._reportError("Can not " + typeMsg + ", expecting field name");
      }

      if (this._writeContext.inRoot() && this._writeContext.getCurrentIndex() > 0) {
         this._emitEndDocument();
         this._emitStartDocument();
      }
   }

   protected void _releaseBuffers() {
   }

   protected void _writeScalar(String value, String type, ScalarStyle style) throws IOException {
      this._emit(this._scalarEvent(value, style));
   }

   private void _writeScalarBinary(Base64Variant b64variant, byte[] data) throws IOException {
      if (b64variant == Base64Variants.getDefaultVariant()) {
         b64variant = Base64Variants.MIME;
      }

      String lf = this._lf();
      String encoded = this._base64encode(b64variant, data, lf);
      this._emit(new ScalarEvent(null, TAG_BINARY, EXPLICIT_TAGS, encoded, null, null, STYLE_BASE64));
   }

   protected ScalarEvent _scalarEvent(String value, ScalarStyle style) {
      String yamlTag = this._typeId;
      if (yamlTag != null) {
         this._typeId = null;
      }

      String anchor = this._objectId;
      if (anchor != null) {
         this._objectId = null;
      }

      return new ScalarEvent(anchor, yamlTag, NO_TAGS, value, null, null, style);
   }

   private String _base64encode(Base64Variant b64v, byte[] input, String linefeed) {
      int inputEnd = input.length;
      StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
      int chunksBeforeLF = b64v.getMaxLineLength() >> 2;
      int inputPtr = 0;
      int safeInputEnd = inputEnd - 3;

      while (inputPtr <= safeInputEnd) {
         int b24 = input[inputPtr++] << 8;
         b24 |= input[inputPtr++] & 255;
         b24 = b24 << 8 | input[inputPtr++] & 255;
         b64v.encodeBase64Chunk(sb, b24);
         if (--chunksBeforeLF <= 0) {
            sb.append(linefeed);
            chunksBeforeLF = b64v.getMaxLineLength() >> 2;
         }
      }

      int inputLeft = inputEnd - inputPtr;
      if (inputLeft > 0) {
         int b24 = input[inputPtr++] << 16;
         if (inputLeft == 2) {
            b24 |= (input[inputPtr++] & 255) << 8;
         }

         b64v.encodeBase64Partial(sb, b24, inputLeft);
      }

      return sb.toString();
   }

   protected String _lf() {
      return this._outputOptions.getLineBreak().getString();
   }

   protected void _emitStartDocument() throws IOException {
      Map<String, String> noTags = Collections.emptyMap();
      boolean startMarker = YAMLGenerator.Feature.WRITE_DOC_START_MARKER.enabledIn(this._formatFeatures);
      this._emit(new DocumentStartEvent(null, null, startMarker, this._docVersion, noTags));
   }

   protected void _emitEndDocument() throws IOException {
      this._emit(new DocumentEndEvent(null, null, false));
   }

   protected final void _emit(Event e) throws IOException {
      this._emitter.emit(e);
   }

   public static enum Feature implements FormatFeature {
      WRITE_DOC_START_MARKER(true),
      USE_NATIVE_OBJECT_ID(true),
      USE_NATIVE_TYPE_ID(true),
      CANONICAL_OUTPUT(false),
      SPLIT_LINES(true),
      MINIMIZE_QUOTES(false),
      ALWAYS_QUOTE_NUMBERS_AS_STRINGS(false),
      LITERAL_BLOCK_STYLE(false),
      INDENT_ARRAYS(false),
      INDENT_ARRAYS_WITH_INDICATOR(false),
      USE_PLATFORM_LINE_BREAKS(false),
      ALLOW_LONG_KEYS(false);

      protected final boolean _defaultState;
      protected final int _mask;

      public static int collectDefaults() {
         int flags = 0;

         for (YAMLGenerator.Feature f : values()) {
            if (f.enabledByDefault()) {
               flags |= f.getMask();
            }
         }

         return flags;
      }

      private Feature(boolean defaultState) {
         this._defaultState = defaultState;
         this._mask = 1 << this.ordinal();
      }

      public boolean enabledByDefault() {
         return this._defaultState;
      }

      public boolean enabledIn(int flags) {
         return (flags & this._mask) != 0;
      }

      public int getMask() {
         return this._mask;
      }
   }
}
