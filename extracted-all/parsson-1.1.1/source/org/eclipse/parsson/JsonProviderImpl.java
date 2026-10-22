package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.parsson.api.BufferPool;

public class JsonProviderImpl extends JsonProvider {
   private final BufferPool bufferPool = new BufferPoolImpl();

   public JsonGenerator createGenerator(Writer writer) {
      return new JsonGeneratorImpl(writer, this.bufferPool);
   }

   public JsonGenerator createGenerator(OutputStream out) {
      return new JsonGeneratorImpl(out, this.bufferPool);
   }

   public JsonParser createParser(Reader reader) {
      return new JsonParserImpl(reader, this.bufferPool);
   }

   public JsonParser createParser(InputStream in) {
      return new JsonParserImpl(in, this.bufferPool);
   }

   public JsonParserFactory createParserFactory(Map<String, ?> config) {
      BufferPool pool = null;
      if (config != null && config.containsKey(BufferPool.class.getName())) {
         pool = (BufferPool)config.get(BufferPool.class.getName());
      }

      if (pool == null) {
         pool = this.bufferPool;
      }

      return new JsonParserFactoryImpl(pool);
   }

   public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
      Map<String, Object> providerConfig;
      boolean prettyPrinting;
      BufferPool pool;
      if (config == null) {
         providerConfig = Collections.emptyMap();
         prettyPrinting = false;
         pool = this.bufferPool;
      } else {
         providerConfig = new HashMap<>();
         if (prettyPrinting = isPrettyPrintingEnabled(config)) {
            providerConfig.put("jakarta.json.stream.JsonGenerator.prettyPrinting", true);
         }

         pool = (BufferPool)config.get(BufferPool.class.getName());
         if (pool != null) {
            providerConfig.put(BufferPool.class.getName(), pool);
         } else {
            pool = this.bufferPool;
         }

         providerConfig = Collections.unmodifiableMap(providerConfig);
      }

      return new JsonGeneratorFactoryImpl(providerConfig, prettyPrinting, pool);
   }

   public JsonReader createReader(Reader reader) {
      return new JsonReaderImpl(reader, this.bufferPool);
   }

   public JsonReader createReader(InputStream in) {
      return new JsonReaderImpl(in, this.bufferPool);
   }

   public JsonWriter createWriter(Writer writer) {
      return new JsonWriterImpl(writer, this.bufferPool);
   }

   public JsonWriter createWriter(OutputStream out) {
      return new JsonWriterImpl(out, this.bufferPool);
   }

   public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
      Map<String, Object> providerConfig;
      boolean prettyPrinting;
      BufferPool pool;
      if (config == null) {
         providerConfig = Collections.emptyMap();
         prettyPrinting = false;
         pool = this.bufferPool;
      } else {
         providerConfig = new HashMap<>();
         if (prettyPrinting = isPrettyPrintingEnabled(config)) {
            providerConfig.put("jakarta.json.stream.JsonGenerator.prettyPrinting", true);
         }

         pool = (BufferPool)config.get(BufferPool.class.getName());
         if (pool != null) {
            providerConfig.put(BufferPool.class.getName(), pool);
         } else {
            pool = this.bufferPool;
         }

         providerConfig = Collections.unmodifiableMap(providerConfig);
      }

      return new JsonWriterFactoryImpl(providerConfig, prettyPrinting, pool);
   }

   public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
      Map<String, Object> providerConfig;
      boolean rejectDuplicateKeys;
      BufferPool pool;
      if (config == null) {
         providerConfig = Collections.emptyMap();
         rejectDuplicateKeys = false;
         pool = this.bufferPool;
      } else {
         providerConfig = new HashMap<>();
         this.addKnowProperty(providerConfig, config, "jakarta.json.JsonConfig.keyStrategy");
         if (rejectDuplicateKeys = isRejectDuplicateKeysEnabled(config)) {
            providerConfig.put("org.eclipse.parsson.rejectDuplicateKeys", true);
         }

         pool = (BufferPool)config.get(BufferPool.class.getName());
         if (pool != null) {
            providerConfig.put(BufferPool.class.getName(), pool);
         } else {
            pool = this.bufferPool;
         }

         providerConfig = Collections.unmodifiableMap(providerConfig);
      }

      return new JsonReaderFactoryImpl(providerConfig, pool, rejectDuplicateKeys);
   }

   public JsonObjectBuilder createObjectBuilder() {
      return new JsonObjectBuilderImpl(this.bufferPool);
   }

   public JsonObjectBuilder createObjectBuilder(JsonObject object) {
      return new JsonObjectBuilderImpl(object, this.bufferPool);
   }

   public JsonObjectBuilder createObjectBuilder(Map<String, ?> map) {
      return new JsonObjectBuilderImpl(map, this.bufferPool);
   }

   public JsonArrayBuilder createArrayBuilder() {
      return new JsonArrayBuilderImpl(this.bufferPool);
   }

   public JsonArrayBuilder createArrayBuilder(JsonArray array) {
      return new JsonArrayBuilderImpl(array, this.bufferPool);
   }

   public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
      return new JsonArrayBuilderImpl(collection, this.bufferPool);
   }

   public JsonPointer createPointer(String jsonPointer) {
      return new JsonPointerImpl(jsonPointer);
   }

   public JsonPatchBuilder createPatchBuilder() {
      return new JsonPatchBuilderImpl();
   }

   public JsonPatchBuilder createPatchBuilder(JsonArray array) {
      return new JsonPatchBuilderImpl(array);
   }

   public JsonPatch createPatch(JsonArray array) {
      return new JsonPatchImpl(array);
   }

   public JsonPatch createDiff(JsonStructure source, JsonStructure target) {
      return new JsonPatchImpl(JsonPatchImpl.diff(source, target));
   }

   public JsonMergePatch createMergePatch(JsonValue patch) {
      return new JsonMergePatchImpl(patch);
   }

   public JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
      return new JsonMergePatchImpl(JsonMergePatchImpl.diff(source, target));
   }

   public JsonString createValue(String value) {
      return new JsonStringImpl(value);
   }

   public JsonNumber createValue(int value) {
      return JsonNumberImpl.getJsonNumber(value);
   }

   public JsonNumber createValue(long value) {
      return JsonNumberImpl.getJsonNumber(value);
   }

   public JsonNumber createValue(double value) {
      return JsonNumberImpl.getJsonNumber(value);
   }

   public JsonNumber createValue(BigInteger value) {
      return JsonNumberImpl.getJsonNumber(value);
   }

   public JsonNumber createValue(BigDecimal value) {
      return JsonNumberImpl.getJsonNumber(value);
   }

   public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
      BufferPool pool = this.bufferPool;
      boolean rejectDuplicateKeys = false;
      if (config != null) {
         if (config.containsKey(BufferPool.class.getName())) {
            pool = (BufferPool)config.get(BufferPool.class.getName());
         }

         rejectDuplicateKeys = isRejectDuplicateKeysEnabled(config);
      }

      return new JsonBuilderFactoryImpl(pool, rejectDuplicateKeys);
   }

   public JsonNumber createValue(Number value) {
      return JsonNumberImpl.getJsonNumber(value);
   }

   private void addKnowProperty(Map<String, Object> providerConfig, Map<String, ?> config, String property) {
      if (config.containsKey(property)) {
         providerConfig.put(property, config.get(property));
      }
   }

   static boolean isPrettyPrintingEnabled(Map<String, ?> config) {
      return config.containsKey("jakarta.json.stream.JsonGenerator.prettyPrinting");
   }

   static boolean isRejectDuplicateKeysEnabled(Map<String, ?> config) {
      return config.containsKey("org.eclipse.parsson.rejectDuplicateKeys");
   }
}
