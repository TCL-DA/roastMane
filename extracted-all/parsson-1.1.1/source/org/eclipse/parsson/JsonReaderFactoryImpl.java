package org.eclipse.parsson;

import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import org.eclipse.parsson.api.BufferPool;

class JsonReaderFactoryImpl implements JsonReaderFactory {
   private final Map<String, ?> config;
   private final BufferPool bufferPool;
   private final boolean rejectDuplicateKeys;

   JsonReaderFactoryImpl(Map<String, ?> config, BufferPool bufferPool, boolean rejectDuplicateKeys) {
      this.config = config;
      this.bufferPool = bufferPool;
      this.rejectDuplicateKeys = rejectDuplicateKeys;
   }

   public JsonReader createReader(Reader reader) {
      return new JsonReaderImpl(reader, this.bufferPool, this.rejectDuplicateKeys, this.config);
   }

   public JsonReader createReader(InputStream in) {
      return new JsonReaderImpl(in, this.bufferPool, this.rejectDuplicateKeys, this.config);
   }

   public JsonReader createReader(InputStream in, Charset charset) {
      return new JsonReaderImpl(in, charset, this.bufferPool, this.rejectDuplicateKeys, this.config);
   }

   public Map<String, ?> getConfigInUse() {
      return this.config;
   }
}
