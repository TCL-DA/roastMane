package jakarta.json;

import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public final class Json {
   private Json() {
   }

   public static JsonParser createParser(Reader reader) {
      return JsonProvider.provider().createParser(reader);
   }

   public static JsonParser createParser(InputStream in) {
      return JsonProvider.provider().createParser(in);
   }

   public static JsonGenerator createGenerator(Writer writer) {
      return JsonProvider.provider().createGenerator(writer);
   }

   public static JsonGenerator createGenerator(OutputStream out) {
      return JsonProvider.provider().createGenerator(out);
   }

   public static JsonParserFactory createParserFactory(Map<String, ?> config) {
      return JsonProvider.provider().createParserFactory(config);
   }

   public static JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
      return JsonProvider.provider().createGeneratorFactory(config);
   }

   public static JsonWriter createWriter(Writer writer) {
      return JsonProvider.provider().createWriter(writer);
   }

   public static JsonWriter createWriter(OutputStream out) {
      return JsonProvider.provider().createWriter(out);
   }

   public static JsonReader createReader(Reader reader) {
      return JsonProvider.provider().createReader(reader);
   }

   public static JsonReader createReader(InputStream in) {
      return JsonProvider.provider().createReader(in);
   }

   public static JsonReaderFactory createReaderFactory(Map<String, ?> config) {
      return JsonProvider.provider().createReaderFactory(config);
   }

   public static JsonWriterFactory createWriterFactory(Map<String, ?> config) {
      return JsonProvider.provider().createWriterFactory(config);
   }

   public static JsonArrayBuilder createArrayBuilder() {
      return JsonProvider.provider().createArrayBuilder();
   }

   public static JsonArrayBuilder createArrayBuilder(JsonArray array) {
      return JsonProvider.provider().createArrayBuilder(array);
   }

   public static JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
      return JsonProvider.provider().createArrayBuilder(collection);
   }

   public static JsonObjectBuilder createObjectBuilder() {
      return JsonProvider.provider().createObjectBuilder();
   }

   public static JsonObjectBuilder createObjectBuilder(JsonObject object) {
      return JsonProvider.provider().createObjectBuilder(object);
   }

   public static JsonObjectBuilder createObjectBuilder(Map<String, ?> map) {
      return JsonProvider.provider().createObjectBuilder(map);
   }

   public static JsonPointer createPointer(String jsonPointer) {
      return JsonProvider.provider().createPointer(jsonPointer);
   }

   public static JsonPatchBuilder createPatchBuilder() {
      return JsonProvider.provider().createPatchBuilder();
   }

   public static JsonPatchBuilder createPatchBuilder(JsonArray array) {
      return JsonProvider.provider().createPatchBuilder(array);
   }

   public static JsonPatch createPatch(JsonArray array) {
      return JsonProvider.provider().createPatch(array);
   }

   public static JsonPatch createDiff(JsonStructure source, JsonStructure target) {
      return JsonProvider.provider().createDiff(source, target);
   }

   public static JsonMergePatch createMergePatch(JsonValue patch) {
      return JsonProvider.provider().createMergePatch(patch);
   }

   public static JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
      return JsonProvider.provider().createMergeDiff(source, target);
   }

   public static JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
      return JsonProvider.provider().createBuilderFactory(config);
   }

   public static JsonString createValue(String value) {
      return JsonProvider.provider().createValue(value);
   }

   public static JsonNumber createValue(int value) {
      return JsonProvider.provider().createValue(value);
   }

   public static JsonNumber createValue(long value) {
      return JsonProvider.provider().createValue(value);
   }

   public static JsonNumber createValue(double value) {
      return JsonProvider.provider().createValue(value);
   }

   public static JsonNumber createValue(BigDecimal value) {
      return JsonProvider.provider().createValue(value);
   }

   public static JsonNumber createValue(BigInteger value) {
      return JsonProvider.provider().createValue(value);
   }

   public static String encodePointer(String pointer) {
      return pointer.replace("~", "~0").replace("/", "~1");
   }

   public static String decodePointer(String escaped) {
      return escaped.replace("~1", "/").replace("~0", "~");
   }

   public static JsonNumber createValue(Number value) {
      return JsonProvider.provider().createValue(value);
   }
}
