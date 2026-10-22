package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import org.eclipse.parsson.api.BufferPool;

class JsonParserFactoryImpl implements JsonParserFactory {
   private final Map<String, ?> config = Collections.emptyMap();
   private final BufferPool bufferPool;

   JsonParserFactoryImpl(BufferPool bufferPool) {
      this.bufferPool = bufferPool;
   }

   public JsonParser createParser(Reader reader) {
      return new JsonParserImpl(reader, this.bufferPool);
   }

   public JsonParser createParser(InputStream in) {
      return new JsonParserImpl(in, this.bufferPool);
   }

   public JsonParser createParser(InputStream in, Charset charset) {
      return new JsonParserImpl(in, charset, this.bufferPool);
   }

   public JsonParser createParser(JsonArray array) {
      return new JsonStructureParser(array);
   }

   public Map<String, ?> getConfigInUse() {
      return this.config;
   }

   public JsonParser createParser(JsonObject object) {
      return new JsonStructureParser(object);
   }
}
