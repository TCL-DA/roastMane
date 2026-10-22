package org.eclipse.parsson;

import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import org.eclipse.parsson.api.BufferPool;

class JsonWriterFactoryImpl implements JsonWriterFactory {
   private final Map<String, ?> config;
   private final boolean prettyPrinting;
   private final BufferPool bufferPool;

   JsonWriterFactoryImpl(Map<String, ?> config, boolean prettyPrinting, BufferPool bufferPool) {
      this.config = config;
      this.prettyPrinting = prettyPrinting;
      this.bufferPool = bufferPool;
   }

   public JsonWriter createWriter(Writer writer) {
      return new JsonWriterImpl(writer, this.prettyPrinting, this.bufferPool);
   }

   public JsonWriter createWriter(OutputStream out) {
      return new JsonWriterImpl(out, this.prettyPrinting, this.bufferPool);
   }

   public JsonWriter createWriter(OutputStream out, Charset charset) {
      return new JsonWriterImpl(out, charset, this.prettyPrinting, this.bufferPool);
   }

   public Map<String, ?> getConfigInUse() {
      return this.config;
   }
}
