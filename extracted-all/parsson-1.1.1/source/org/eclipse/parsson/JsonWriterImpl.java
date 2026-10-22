package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import org.eclipse.parsson.api.BufferPool;

class JsonWriterImpl implements JsonWriter {
   private final JsonGeneratorImpl generator;
   private boolean writeDone;
   private final JsonWriterImpl.NoFlushOutputStream os;

   JsonWriterImpl(Writer writer, BufferPool bufferPool) {
      this(writer, false, bufferPool);
   }

   JsonWriterImpl(Writer writer, boolean prettyPrinting, BufferPool bufferPool) {
      this.generator = (JsonGeneratorImpl)(prettyPrinting ? new JsonPrettyGeneratorImpl(writer, bufferPool) : new JsonGeneratorImpl(writer, bufferPool));
      this.os = null;
   }

   JsonWriterImpl(OutputStream out, BufferPool bufferPool) {
      this(out, StandardCharsets.UTF_8, false, bufferPool);
   }

   JsonWriterImpl(OutputStream out, boolean prettyPrinting, BufferPool bufferPool) {
      this(out, StandardCharsets.UTF_8, prettyPrinting, bufferPool);
   }

   JsonWriterImpl(OutputStream out, Charset charset, boolean prettyPrinting, BufferPool bufferPool) {
      this.os = new JsonWriterImpl.NoFlushOutputStream(out);
      this.generator = (JsonGeneratorImpl)(prettyPrinting
         ? new JsonPrettyGeneratorImpl(this.os, charset, bufferPool)
         : new JsonGeneratorImpl(this.os, charset, bufferPool));
   }

   public void writeArray(JsonArray array) {
      if (this.writeDone) {
         throw new IllegalStateException(JsonMessages.WRITER_WRITE_ALREADY_CALLED());
      } else {
         this.writeDone = true;
         this.generator.writeStartArray();

         for (JsonValue value : array) {
            this.generator.write(value);
         }

         this.generator.writeEnd();
         this.generator.flushBuffer();
         if (this.os != null) {
            this.generator.flush();
         }
      }
   }

   public void writeObject(JsonObject object) {
      if (this.writeDone) {
         throw new IllegalStateException(JsonMessages.WRITER_WRITE_ALREADY_CALLED());
      } else {
         this.writeDone = true;
         this.generator.writeStartObject();

         for (Entry<String, JsonValue> e : object.entrySet()) {
            this.generator.write(e.getKey(), e.getValue());
         }

         this.generator.writeEnd();
         this.generator.flushBuffer();
         if (this.os != null) {
            this.generator.flush();
         }
      }
   }

   public void write(JsonStructure value) {
      if (value instanceof JsonArray) {
         this.writeArray((JsonArray)value);
      } else {
         this.writeObject((JsonObject)value);
      }
   }

   public void write(JsonValue value) {
      switch (value.getValueType()) {
         case OBJECT:
            this.writeObject((JsonObject)value);
            return;
         case ARRAY:
            this.writeArray((JsonArray)value);
            return;
         default:
            if (this.writeDone) {
               throw new IllegalStateException(JsonMessages.WRITER_WRITE_ALREADY_CALLED());
            } else {
               this.writeDone = true;
               this.generator.write(value);
               this.generator.flushBuffer();
               if (this.os != null) {
                  this.generator.flush();
               }
            }
      }
   }

   public void close() {
      this.writeDone = true;
      this.generator.close();
   }

   private static final class NoFlushOutputStream extends FilterOutputStream {
      public NoFlushOutputStream(OutputStream out) {
         super(out);
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
         this.out.write(b, off, len);
      }

      @Override
      public void flush() {
      }
   }
}
