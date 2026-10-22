package org.glassfish.jersey.client;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.MessageBodyWorkers;

public class ChunkedInput<T> extends GenericType<T> implements Closeable {
   private static final Logger LOGGER = Logger.getLogger(ChunkedInput.class.getName());
   private final AtomicBoolean closed = new AtomicBoolean(false);
   private ChunkParser parser = createParser("\r\n");
   private MediaType mediaType;
   private final InputStream inputStream;
   private final Annotation[] annotations;
   private final MultivaluedMap<String, String> headers;
   private final MessageBodyWorkers messageBodyWorkers;
   private final PropertiesDelegate propertiesDelegate;

   public static ChunkParser createParser(String boundary) {
      return new ChunkedInput.FixedBoundaryParser(boundary.getBytes());
   }

   public static ChunkParser createParser(byte[] boundary) {
      return new ChunkedInput.FixedBoundaryParser(boundary);
   }

   public static ChunkParser createMultiParser(String... boundaries) {
      return new ChunkedInput.FixedMultiBoundaryParser(boundaries);
   }

   protected ChunkedInput(
      Type chunkType,
      InputStream inputStream,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> headers,
      MessageBodyWorkers messageBodyWorkers,
      PropertiesDelegate propertiesDelegate
   ) {
      super(chunkType);
      this.inputStream = inputStream;
      this.annotations = annotations;
      this.mediaType = mediaType;
      this.headers = headers;
      this.messageBodyWorkers = messageBodyWorkers;
      this.propertiesDelegate = propertiesDelegate;
   }

   public ChunkParser getParser() {
      return this.parser;
   }

   public void setParser(ChunkParser parser) {
      this.parser = parser;
   }

   public MediaType getChunkType() {
      return this.mediaType;
   }

   public void setChunkType(MediaType mediaType) throws IllegalArgumentException {
      if (mediaType == null) {
         throw new IllegalArgumentException(LocalizationMessages.CHUNKED_INPUT_MEDIA_TYPE_NULL());
      } else {
         this.mediaType = mediaType;
      }
   }

   public void setChunkType(String mediaType) throws IllegalArgumentException {
      this.mediaType = MediaType.valueOf(mediaType);
   }

   @Override
   public void close() {
      if (this.closed.compareAndSet(false, true) && this.inputStream != null) {
         try {
            this.inputStream.close();
         } catch (IOException var2) {
            LOGGER.log(Level.FINE, LocalizationMessages.CHUNKED_INPUT_STREAM_CLOSING_ERROR(), (Throwable)var2);
         }
      }
   }

   public boolean isClosed() {
      return this.closed.get();
   }

   public T read() throws IllegalStateException {
      if (this.closed.get()) {
         throw new IllegalStateException(LocalizationMessages.CHUNKED_INPUT_CLOSED());
      } else {
         try {
            byte[] chunk = this.parser.readChunk(this.inputStream);
            if (chunk != null) {
               ByteArrayInputStream chunkStream = new ByteArrayInputStream(chunk);
               return (T)this.messageBodyWorkers
                  .readFrom(
                     this.getRawType(),
                     this.getType(),
                     this.annotations,
                     this.mediaType,
                     this.headers,
                     this.propertiesDelegate,
                     chunkStream,
                     Collections.emptyList(),
                     false
                  );
            }

            this.close();
         } catch (IOException var3) {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, var3.getMessage(), (Throwable)var3);
            this.close();
         }

         return null;
      }
   }

   private abstract static class AbstractBoundaryParser implements ChunkParser {
      private AbstractBoundaryParser() {
      }

      @Override
      public byte[] readChunk(InputStream in) throws IOException {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] delimiterBuffer = new byte[this.getDelimiterBufferSize()];

         int data;
         int dPos;
         do {
            dPos = 0;

            while ((data = in.read()) != -1) {
               byte b = (byte)data;
               byte[] delimiter = this.getDelimiter(b, dPos, delimiterBuffer);
               if (delimiter != null && b == delimiter[dPos]) {
                  delimiterBuffer[dPos++] = b;
                  if (dPos == delimiter.length) {
                     break;
                  }
               } else if (dPos > 0) {
                  delimiter = this.getDelimiter(dPos - 1, delimiterBuffer);
                  delimiterBuffer[dPos] = b;
                  int matched = matchTail(delimiterBuffer, 1, dPos, delimiter);
                  if (matched == 0) {
                     buffer.write(delimiterBuffer, 0, dPos);
                     buffer.write(b);
                     dPos = 0;
                  } else {
                     if (matched == delimiter.length) {
                        break;
                     }

                     buffer.write(delimiterBuffer, 0, dPos + 1 - matched);
                     dPos = matched;
                  }
               } else {
                  buffer.write(b);
               }
            }
         } while (data != -1 && buffer.size() == 0);

         if (dPos > 0 && dPos != this.getDelimiter(dPos - 1, delimiterBuffer).length) {
            buffer.write(delimiterBuffer, 0, dPos);
         }

         return buffer.size() > 0 ? buffer.toByteArray() : null;
      }

      abstract byte[] getDelimiter(byte var1, int var2, byte[] var3);

      abstract byte[] getDelimiter(int var1, byte[] var2);

      abstract int getDelimiterBufferSize();

      private static int matchTail(byte[] buffer, int offset, int length, byte[] pattern) {
         if (pattern == null) {
            return 0;
         } else {
            label28:
            for (int i = 0; i < length; i++) {
               int tailLength = length - i;

               for (int j = 0; j < tailLength; j++) {
                  if (buffer[offset + i + j] != pattern[j]) {
                     continue label28;
                  }
               }

               return tailLength;
            }

            return 0;
         }
      }
   }

   private static class FixedBoundaryParser extends ChunkedInput.AbstractBoundaryParser {
      private final byte[] delimiter;

      public FixedBoundaryParser(byte[] boundary) {
         this.delimiter = Arrays.copyOf(boundary, boundary.length);
      }

      @Override
      byte[] getDelimiter(byte b, int pos, byte[] delimiterBuffer) {
         return this.delimiter;
      }

      @Override
      byte[] getDelimiter(int pos, byte[] delimiterBuffer) {
         return this.delimiter;
      }

      @Override
      int getDelimiterBufferSize() {
         return this.delimiter.length;
      }
   }

   private static class FixedMultiBoundaryParser extends ChunkedInput.AbstractBoundaryParser {
      private final List<byte[]> delimiters = new ArrayList<>();
      private final int longestDelimiterLength;

      public FixedMultiBoundaryParser(String... boundaries) {
         for (String boundary : boundaries) {
            byte[] boundaryBytes = boundary.getBytes();
            this.delimiters.add(Arrays.copyOf(boundaryBytes, boundaryBytes.length));
         }

         Collections.sort(this.delimiters, new Comparator<byte[]>() {
            public int compare(byte[] o1, byte[] o2) {
               return Integer.compare(o1.length, o2.length);
            }
         });
         byte[] longestDelimiter = this.delimiters.get(this.delimiters.size() - 1);
         this.longestDelimiterLength = longestDelimiter.length;
      }

      @Override
      byte[] getDelimiter(byte b, int pos, byte[] delimiterBuffer) {
         byte[] buffer = Arrays.copyOf(delimiterBuffer, delimiterBuffer.length);
         buffer[pos] = b;
         return this.getDelimiter(pos, buffer);
      }

      @Override
      byte[] getDelimiter(int pos, byte[] delimiterBuffer) {
         for (byte[] delimiter : this.delimiters) {
            if (pos <= delimiter.length) {
               for (int i = 0; i <= pos && i < delimiter.length && delimiter[i] == delimiterBuffer[i]; i++) {
                  if (pos == i) {
                     return delimiter;
                  }
               }
            }
         }

         return null;
      }

      @Override
      int getDelimiterBufferSize() {
         return this.longestDelimiterLength;
      }
   }
}
