package org.eclipse.parsson;

import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import org.eclipse.parsson.api.BufferPool;

class JsonGeneratorFactoryImpl implements JsonGeneratorFactory {
   private final boolean prettyPrinting;
   private final Map<String, ?> config;
   private final BufferPool bufferPool;

   JsonGeneratorFactoryImpl(Map<String, ?> config, boolean prettyPrinting, BufferPool bufferPool) {
      this.config = config;
      this.prettyPrinting = prettyPrinting;
      this.bufferPool = bufferPool;
   }

   public JsonGenerator createGenerator(Writer writer) {
      return (JsonGenerator)(this.prettyPrinting ? new JsonPrettyGeneratorImpl(writer, this.bufferPool) : new JsonGeneratorImpl(writer, this.bufferPool));
   }

   public JsonGenerator createGenerator(OutputStream out) {
      return (JsonGenerator)(this.prettyPrinting ? new JsonPrettyGeneratorImpl(out, this.bufferPool) : new JsonGeneratorImpl(out, this.bufferPool));
   }

   public JsonGenerator createGenerator(OutputStream out, Charset charset) {
      return (JsonGenerator)(this.prettyPrinting
         ? new JsonPrettyGeneratorImpl(out, charset, this.bufferPool)
         : new JsonGeneratorImpl(out, charset, this.bufferPool));
   }

   public Map<String, ?> getConfigInUse() {
      return this.config;
   }
}
