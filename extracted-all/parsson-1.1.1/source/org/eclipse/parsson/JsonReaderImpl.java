package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParsingException;
import jakarta.json.stream.JsonParser.Event;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import org.eclipse.parsson.api.BufferPool;

class JsonReaderImpl implements JsonReader {
   private final JsonParserImpl parser;
   private boolean readDone;
   private final BufferPool bufferPool;

   JsonReaderImpl(Reader reader, BufferPool bufferPool) {
      this(reader, bufferPool, false, Collections.emptyMap());
   }

   JsonReaderImpl(Reader reader, BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.parser = new JsonParserImpl(reader, bufferPool, rejectDuplicateKeys, config);
      this.bufferPool = bufferPool;
   }

   JsonReaderImpl(InputStream in, BufferPool bufferPool) {
      this(in, bufferPool, false, Collections.emptyMap());
   }

   JsonReaderImpl(InputStream in, BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.parser = new JsonParserImpl(in, bufferPool, rejectDuplicateKeys, config);
      this.bufferPool = bufferPool;
   }

   JsonReaderImpl(InputStream in, Charset charset, BufferPool bufferPool) {
      this(in, charset, bufferPool, false, Collections.emptyMap());
   }

   JsonReaderImpl(InputStream in, Charset charset, BufferPool bufferPool, boolean rejectDuplicateKeys, Map<String, ?> config) {
      this.parser = new JsonParserImpl(in, charset, bufferPool, rejectDuplicateKeys, config);
      this.bufferPool = bufferPool;
   }

   public JsonStructure read() {
      if (this.readDone) {
         throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
      } else {
         this.readDone = true;
         if (this.parser.hasNext()) {
            try {
               Event e = this.parser.next();
               if (e == Event.START_ARRAY) {
                  return this.parser.getArray();
               }

               if (e == Event.START_OBJECT) {
                  return this.parser.getObject();
               }
            } catch (IllegalStateException var2) {
               throw new JsonParsingException(var2.getMessage(), var2, this.parser.getLastCharLocation());
            }
         }

         throw new JsonException(JsonMessages.INTERNAL_ERROR());
      }
   }

   public JsonObject readObject() {
      if (this.readDone) {
         throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
      } else {
         this.readDone = true;
         if (this.parser.hasNext()) {
            try {
               this.parser.next();
               return this.parser.getObject();
            } catch (IllegalStateException var2) {
               throw new JsonParsingException(var2.getMessage(), var2, this.parser.getLastCharLocation());
            }
         } else {
            throw new JsonException(JsonMessages.INTERNAL_ERROR());
         }
      }
   }

   public JsonArray readArray() {
      if (this.readDone) {
         throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
      } else {
         this.readDone = true;
         if (this.parser.hasNext()) {
            try {
               this.parser.next();
               return this.parser.getArray();
            } catch (IllegalStateException var2) {
               throw new JsonParsingException(var2.getMessage(), var2, this.parser.getLastCharLocation());
            }
         } else {
            throw new JsonException(JsonMessages.INTERNAL_ERROR());
         }
      }
   }

   public JsonValue readValue() {
      if (this.readDone) {
         throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
      } else {
         this.readDone = true;
         if (this.parser.hasNext()) {
            try {
               this.parser.next();
               return this.parser.getValue();
            } catch (IllegalStateException var2) {
               throw new JsonParsingException(var2.getMessage(), var2, this.parser.getLastCharLocation());
            }
         } else {
            throw new JsonException(JsonMessages.INTERNAL_ERROR());
         }
      }
   }

   public void close() {
      this.readDone = true;
      this.parser.close();
   }
}
