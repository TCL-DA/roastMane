package com.fasterxml.jackson.dataformat.yaml;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.DumperOptions.Version;

public class YAMLFactory extends JsonFactory {
   private static final long serialVersionUID = 1L;
   public static final String FORMAT_NAME_YAML = "YAML";
   protected static final int DEFAULT_YAML_PARSER_FEATURE_FLAGS = YAMLParser.Feature.collectDefaults();
   protected static final int DEFAULT_YAML_GENERATOR_FEATURE_FLAGS = YAMLGenerator.Feature.collectDefaults();
   private static final byte UTF8_BOM_1 = -17;
   private static final byte UTF8_BOM_2 = -69;
   private static final byte UTF8_BOM_3 = -65;
   protected int _yamlParserFeatures = DEFAULT_YAML_PARSER_FEATURE_FLAGS;
   protected int _yamlGeneratorFeatures = DEFAULT_YAML_GENERATOR_FEATURE_FLAGS;
   protected final Version _version;
   protected final StringQuotingChecker _quotingChecker;
   protected final LoaderOptions _loaderOptions;
   protected final DumperOptions _dumperOptions;

   public YAMLFactory() {
      this((ObjectCodec)null);
   }

   public YAMLFactory(ObjectCodec oc) {
      super(oc);
      this._yamlParserFeatures = DEFAULT_YAML_PARSER_FEATURE_FLAGS;
      this._yamlGeneratorFeatures = DEFAULT_YAML_GENERATOR_FEATURE_FLAGS;
      this._version = null;
      this._quotingChecker = StringQuotingChecker.Default.instance();
      this._loaderOptions = null;
      this._dumperOptions = null;
   }

   public YAMLFactory(YAMLFactory src, ObjectCodec oc) {
      super(src, oc);
      this._yamlParserFeatures = src._yamlParserFeatures;
      this._yamlGeneratorFeatures = src._yamlGeneratorFeatures;
      this._version = src._version;
      this._quotingChecker = src._quotingChecker;
      this._loaderOptions = src._loaderOptions;
      this._dumperOptions = src._dumperOptions;
   }

   protected YAMLFactory(YAMLFactoryBuilder b) {
      super(b, false);
      this._yamlGeneratorFeatures = b.formatGeneratorFeaturesMask();
      this._version = b.yamlVersionToWrite();
      this._quotingChecker = b.stringQuotingChecker();
      this._loaderOptions = b.loaderOptions();
      this._dumperOptions = b.dumperOptions();
   }

   public YAMLFactoryBuilder rebuild() {
      return new YAMLFactoryBuilder(this);
   }

   public static YAMLFactoryBuilder builder() {
      return new YAMLFactoryBuilder();
   }

   public YAMLFactory copy() {
      this._checkInvalidCopy(YAMLFactory.class);
      return new YAMLFactory(this, null);
   }

   protected Object readResolve() {
      return new YAMLFactory(this, this._objectCodec);
   }

   public com.fasterxml.jackson.core.Version version() {
      return PackageVersion.VERSION;
   }

   public boolean canUseCharArrays() {
      return false;
   }

   public Class<YAMLParser.Feature> getFormatReadFeatureType() {
      return YAMLParser.Feature.class;
   }

   public Class<YAMLGenerator.Feature> getFormatWriteFeatureType() {
      return YAMLGenerator.Feature.class;
   }

   public String getFormatName() {
      return "YAML";
   }

   public MatchStrength hasFormat(InputAccessor acc) throws IOException {
      if (!acc.hasMoreBytes()) {
         return MatchStrength.INCONCLUSIVE;
      } else {
         byte b = acc.nextByte();
         if (b == -17) {
            if (!acc.hasMoreBytes()) {
               return MatchStrength.INCONCLUSIVE;
            }

            if (acc.nextByte() != -69) {
               return MatchStrength.NO_MATCH;
            }

            if (!acc.hasMoreBytes()) {
               return MatchStrength.INCONCLUSIVE;
            }

            if (acc.nextByte() != -65) {
               return MatchStrength.NO_MATCH;
            }

            if (!acc.hasMoreBytes()) {
               return MatchStrength.INCONCLUSIVE;
            }

            b = acc.nextByte();
         }

         return b == 45 && acc.hasMoreBytes() && acc.nextByte() == 45 && acc.hasMoreBytes() && acc.nextByte() == 45
            ? MatchStrength.FULL_MATCH
            : MatchStrength.INCONCLUSIVE;
      }
   }

   public final YAMLFactory configure(YAMLParser.Feature f, boolean state) {
      if (state) {
         this.enable(f);
      } else {
         this.disable(f);
      }

      return this;
   }

   public YAMLFactory enable(YAMLParser.Feature f) {
      this._yamlParserFeatures = this._yamlParserFeatures | f.getMask();
      return this;
   }

   public YAMLFactory disable(YAMLParser.Feature f) {
      this._yamlParserFeatures = this._yamlParserFeatures & ~f.getMask();
      return this;
   }

   public final boolean isEnabled(YAMLParser.Feature f) {
      return (this._yamlParserFeatures & f.getMask()) != 0;
   }

   public int getFormatParserFeatures() {
      return this._yamlParserFeatures;
   }

   public final YAMLFactory configure(YAMLGenerator.Feature f, boolean state) {
      if (state) {
         this.enable(f);
      } else {
         this.disable(f);
      }

      return this;
   }

   public YAMLFactory enable(YAMLGenerator.Feature f) {
      this._yamlGeneratorFeatures = this._yamlGeneratorFeatures | f.getMask();
      return this;
   }

   public YAMLFactory disable(YAMLGenerator.Feature f) {
      this._yamlGeneratorFeatures = this._yamlGeneratorFeatures & ~f.getMask();
      return this;
   }

   public final boolean isEnabled(YAMLGenerator.Feature f) {
      return (this._yamlGeneratorFeatures & f.getMask()) != 0;
   }

   public int getFormatGeneratorFeatures() {
      return this._yamlGeneratorFeatures;
   }

   public YAMLParser createParser(String content) throws IOException {
      return this.createParser(new StringReader(content));
   }

   public YAMLParser createParser(File f) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(f), true);
      return this._createParser(this._decorate(new FileInputStream(f), ctxt), ctxt);
   }

   public YAMLParser createParser(URL url) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(url), true);
      return this._createParser(this._decorate(this._optimizedStreamFromURL(url), ctxt), ctxt);
   }

   public YAMLParser createParser(InputStream in) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(in), false);
      return this._createParser(this._decorate(in, ctxt), ctxt);
   }

   public YAMLParser createParser(Reader r) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(r), false);
      return this._createParser(this._decorate(r, ctxt), ctxt);
   }

   public YAMLParser createParser(char[] data) throws IOException {
      return this.createParser(data, 0, data.length);
   }

   public YAMLParser createParser(char[] data, int offset, int len) throws IOException {
      return this.createParser(new CharArrayReader(data, offset, len));
   }

   public YAMLParser createParser(byte[] data) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(data), true);
      if (this._inputDecorator != null) {
         InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
         if (in != null) {
            return this._createParser(in, ctxt);
         }
      }

      return this._createParser(data, 0, data.length, ctxt);
   }

   public YAMLParser createParser(byte[] data, int offset, int len) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(data, offset, len), true);
      if (this._inputDecorator != null) {
         InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
         if (in != null) {
            return this._createParser(in, ctxt);
         }
      }

      return this._createParser(data, offset, len, ctxt);
   }

   public YAMLGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(out), false);
      ctxt.setEncoding(enc);
      return this._createGenerator(this._createWriter(this._decorate(out, ctxt), enc, ctxt), ctxt);
   }

   public YAMLGenerator createGenerator(OutputStream out) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(out), false);
      return this._createGenerator(this._createWriter(this._decorate(out, ctxt), JsonEncoding.UTF8, ctxt), ctxt);
   }

   public YAMLGenerator createGenerator(Writer out) throws IOException {
      IOContext ctxt = this._createContext(this._createContentReference(out), false);
      return this._createGenerator(this._decorate(out, ctxt), ctxt);
   }

   public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
      OutputStream out = new FileOutputStream(f);
      IOContext ctxt = this._createContext(this._createContentReference(f), true);
      ctxt.setEncoding(enc);
      return this._createGenerator(this._createWriter(this._decorate(out, ctxt), enc, ctxt), ctxt);
   }

   protected YAMLParser _createParser(InputStream in, IOContext ctxt) throws IOException {
      return new YAMLParser(ctxt, this._parserFeatures, this._yamlParserFeatures, this._loaderOptions, this._objectCodec, this._createReader(in, null, ctxt));
   }

   protected YAMLParser _createParser(Reader r, IOContext ctxt) throws IOException {
      return new YAMLParser(ctxt, this._parserFeatures, this._yamlParserFeatures, this._loaderOptions, this._objectCodec, r);
   }

   protected YAMLParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
      return new YAMLParser(
         ctxt, this._parserFeatures, this._yamlParserFeatures, this._loaderOptions, this._objectCodec, new CharArrayReader(data, offset, len)
      );
   }

   protected YAMLParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
      return new YAMLParser(
         ctxt, this._parserFeatures, this._yamlParserFeatures, this._loaderOptions, this._objectCodec, this._createReader(data, offset, len, null, ctxt)
      );
   }

   protected YAMLGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
      int feats = this._yamlGeneratorFeatures;
      return this._dumperOptions == null
         ? new YAMLGenerator(ctxt, this._generatorFeatures, feats, this._quotingChecker, this._objectCodec, out, this._version)
         : new YAMLGenerator(ctxt, this._generatorFeatures, feats, this._quotingChecker, this._objectCodec, out, this._dumperOptions);
   }

   protected YAMLGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
      throw new IllegalStateException();
   }

   protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
      return (Writer)(enc == JsonEncoding.UTF8 ? new UTF8Writer(out) : new OutputStreamWriter(out, enc.getJavaName()));
   }

   protected Reader _createReader(InputStream in, JsonEncoding enc, IOContext ctxt) throws IOException {
      if (enc == null) {
         enc = JsonEncoding.UTF8;
      }

      if (enc != JsonEncoding.UTF8) {
         return new InputStreamReader(in, enc.getJavaName());
      } else {
         boolean autoClose = ctxt.isResourceManaged() || this.isEnabled(Feature.AUTO_CLOSE_SOURCE);
         return new UTF8Reader(in, autoClose);
      }
   }

   protected Reader _createReader(byte[] data, int offset, int len, JsonEncoding enc, IOContext ctxt) throws IOException {
      if (enc == null) {
         enc = JsonEncoding.UTF8;
      }

      if (enc != null && enc != JsonEncoding.UTF8) {
         ByteArrayInputStream in = new ByteArrayInputStream(data, offset, len);
         return new InputStreamReader(in, enc.getJavaName());
      } else {
         return new UTF8Reader(data, offset, len, true);
      }
   }
}
