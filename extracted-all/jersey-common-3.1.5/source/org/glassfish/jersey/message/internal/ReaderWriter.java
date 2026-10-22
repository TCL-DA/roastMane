package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MediaType;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.PropertiesHelper;

public final class ReaderWriter {
   private static final Logger LOGGER = Logger.getLogger(ReaderWriter.class.getName());
   @Deprecated
   public static final Charset UTF8 = StandardCharsets.UTF_8;
   public static final int BUFFER_SIZE = getBufferSize();
   private static final int MAX_BUFFER_SIZE = 2147483639;

   private static int getBufferSize() {
      String value = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("jersey.config.io.bufferSize"));
      if (value != null) {
         try {
            int i = Integer.parseInt(value);
            if (i <= 0) {
               throw new NumberFormatException("Value not positive.");
            }

            return i;
         } catch (NumberFormatException var2) {
            LOGGER.log(
               Level.CONFIG,
               "Value of jersey.config.io.bufferSize property is not a valid positive integer [" + value + "]. Reverting to default [" + 8192 + "].",
               (Throwable)var2
            );
         }
      }

      return 8192;
   }

   public static void writeTo(InputStream in, OutputStream out) throws IOException {
      byte[] data = new byte[BUFFER_SIZE];

      int read;
      while ((read = in.read(data)) != -1) {
         out.write(data, 0, read);
      }
   }

   public static void writeTo(Reader in, Writer out) throws IOException {
      char[] data = new char[BUFFER_SIZE];

      int read;
      while ((read = in.read(data)) != -1) {
         out.write(data, 0, read);
      }
   }

   public static Charset getCharset(MediaType m) {
      String name = m == null ? null : (String)m.getParameters().get("charset");
      return name == null ? StandardCharsets.UTF_8 : Charset.forName(name);
   }

   public static String readFromAsString(InputStream in, MediaType type) throws IOException {
      return new String(readAllBytes(in), getCharset(type));
   }

   public static String readFromAsString(Reader reader) throws IOException {
      StringBuilder sb = new StringBuilder();
      char[] c = new char[BUFFER_SIZE];

      int l;
      while ((l = reader.read(c)) != -1) {
         sb.append(c, 0, l);
      }

      return sb.toString();
   }

   private static byte[] readAllBytes(InputStream inputStream) throws IOException {
      List<byte[]> bufs = null;
      byte[] result = null;
      int total = 0;
      int remaining = Integer.MAX_VALUE;

      int n;
      do {
         byte[] buf = new byte[Math.min(remaining, BUFFER_SIZE)];
         int nread = 0;

         while ((n = inputStream.read(buf, nread, Math.min(buf.length - nread, remaining))) > 0) {
            nread += n;
            remaining -= n;
            if (nread == BUFFER_SIZE) {
               break;
            }
         }

         if (nread > 0) {
            if (2147483639 - total < nread) {
               throw new OutOfMemoryError("Required array size too large");
            }

            if (nread < buf.length) {
               buf = Arrays.copyOfRange(buf, 0, nread);
            }

            total += nread;
            if (result == null) {
               result = buf;
            } else {
               if (bufs == null) {
                  bufs = new ArrayList<>();
                  bufs.add(result);
               }

               bufs.add(buf);
            }
         }
      } while (n >= 0 && remaining > 0);

      if (bufs == null) {
         if (result == null) {
            return new byte[0];
         } else {
            return result.length == total ? result : Arrays.copyOf(result, total);
         }
      } else {
         result = new byte[total];
         int offset = 0;
         remaining = total;

         for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
         }

         return result;
      }
   }

   public static void writeToAsString(String s, OutputStream out, MediaType type) throws IOException {
      Writer osw = new OutputStreamWriter(out, getCharset(type));
      osw.write(s);
      osw.flush();
   }

   public static void safelyClose(Closeable closeable) {
      try {
         closeable.close();
      } catch (IOException var2) {
         LOGGER.log(Level.FINE, LocalizationMessages.MESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED(), (Throwable)var2);
      } catch (ProcessingException var3) {
         LOGGER.log(Level.FINE, LocalizationMessages.MESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED(), (Throwable)var3);
      }
   }

   private ReaderWriter() {
   }
}
