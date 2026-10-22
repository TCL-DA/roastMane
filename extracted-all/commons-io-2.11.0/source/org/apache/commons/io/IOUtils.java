package org.apache.commons.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.io.function.IOConsumer;
import org.apache.commons.io.output.AppendableWriter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.ThresholdingOutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;

public class IOUtils {
   public static final int CR = 13;
   public static final int DEFAULT_BUFFER_SIZE = 8192;
   public static final char DIR_SEPARATOR = File.separatorChar;
   public static final char DIR_SEPARATOR_UNIX = '/';
   public static final char DIR_SEPARATOR_WINDOWS = '\\';
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   public static final int EOF = -1;
   public static final int LF = 10;
   @Deprecated
   public static final String LINE_SEPARATOR = System.lineSeparator();
   public static final String LINE_SEPARATOR_UNIX = StandardLineSeparator.LF.getString();
   public static final String LINE_SEPARATOR_WINDOWS = StandardLineSeparator.CRLF.getString();
   private static final ThreadLocal<byte[]> SKIP_BYTE_BUFFER = ThreadLocal.withInitial(IOUtils::byteArray);
   private static final ThreadLocal<char[]> SKIP_CHAR_BUFFER = ThreadLocal.withInitial(IOUtils::charArray);

   public static BufferedInputStream buffer(InputStream inputStream) {
      Objects.requireNonNull(inputStream, "inputStream");
      return inputStream instanceof BufferedInputStream ? (BufferedInputStream)inputStream : new BufferedInputStream(inputStream);
   }

   public static BufferedInputStream buffer(InputStream inputStream, int size) {
      Objects.requireNonNull(inputStream, "inputStream");
      return inputStream instanceof BufferedInputStream ? (BufferedInputStream)inputStream : new BufferedInputStream(inputStream, size);
   }

   public static BufferedOutputStream buffer(OutputStream outputStream) {
      Objects.requireNonNull(outputStream, "outputStream");
      return outputStream instanceof BufferedOutputStream ? (BufferedOutputStream)outputStream : new BufferedOutputStream(outputStream);
   }

   public static BufferedOutputStream buffer(OutputStream outputStream, int size) {
      Objects.requireNonNull(outputStream, "outputStream");
      return outputStream instanceof BufferedOutputStream ? (BufferedOutputStream)outputStream : new BufferedOutputStream(outputStream, size);
   }

   public static BufferedReader buffer(Reader reader) {
      return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
   }

   public static BufferedReader buffer(Reader reader, int size) {
      return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, size);
   }

   public static BufferedWriter buffer(Writer writer) {
      return writer instanceof BufferedWriter ? (BufferedWriter)writer : new BufferedWriter(writer);
   }

   public static BufferedWriter buffer(Writer writer, int size) {
      return writer instanceof BufferedWriter ? (BufferedWriter)writer : new BufferedWriter(writer, size);
   }

   public static byte[] byteArray() {
      return byteArray(8192);
   }

   public static byte[] byteArray(int size) {
      return new byte[size];
   }

   private static char[] charArray() {
      return charArray(8192);
   }

   private static char[] charArray(int size) {
      return new char[size];
   }

   public static void close(Closeable closeable) throws IOException {
      if (closeable != null) {
         closeable.close();
      }
   }

   public static void close(Closeable... closeables) throws IOException {
      if (closeables != null) {
         for (Closeable closeable : closeables) {
            close(closeable);
         }
      }
   }

   public static void close(Closeable closeable, IOConsumer<IOException> consumer) throws IOException {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var3) {
            if (consumer != null) {
               consumer.accept(var3);
            }
         }
      }
   }

   public static void close(URLConnection conn) {
      if (conn instanceof HttpURLConnection) {
         ((HttpURLConnection)conn).disconnect();
      }
   }

   public static void closeQuietly(Closeable closeable) {
      closeQuietly(closeable, (Consumer<IOException>)null);
   }

   public static void closeQuietly(Closeable... closeables) {
      if (closeables != null) {
         for (Closeable closeable : closeables) {
            closeQuietly(closeable);
         }
      }
   }

   public static void closeQuietly(Closeable closeable, Consumer<IOException> consumer) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var3) {
            if (consumer != null) {
               consumer.accept(var3);
            }
         }
      }
   }

   public static void closeQuietly(InputStream input) {
      closeQuietly((Closeable)input);
   }

   public static void closeQuietly(OutputStream output) {
      closeQuietly((Closeable)output);
   }

   public static void closeQuietly(Reader reader) {
      closeQuietly((Closeable)reader);
   }

   public static void closeQuietly(Selector selector) {
      closeQuietly((Closeable)selector);
   }

   public static void closeQuietly(ServerSocket serverSocket) {
      closeQuietly((Closeable)serverSocket);
   }

   public static void closeQuietly(Socket socket) {
      closeQuietly((Closeable)socket);
   }

   public static void closeQuietly(Writer writer) {
      closeQuietly((Closeable)writer);
   }

   public static long consume(InputStream input) throws IOException {
      return copyLarge(input, NullOutputStream.NULL_OUTPUT_STREAM, getByteArray());
   }

   public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
      if (input1 == input2) {
         return true;
      } else if (input1 != null && input2 != null) {
         byte[] array1 = getByteArray();
         byte[] array2 = byteArray();

         while (true) {
            int pos1 = 0;
            int pos2 = 0;

            for (int index = 0; index < 8192; index++) {
               if (pos1 == index) {
                  int count1;
                  do {
                     count1 = input1.read(array1, pos1, 8192 - pos1);
                  } while (count1 == 0);

                  if (count1 == -1) {
                     return pos2 == index && input2.read() == -1;
                  }

                  pos1 += count1;
               }

               if (pos2 == index) {
                  int count2;
                  do {
                     count2 = input2.read(array2, pos2, 8192 - pos2);
                  } while (count2 == 0);

                  if (count2 == -1) {
                     return pos1 == index && input1.read() == -1;
                  }

                  pos2 += count2;
               }

               if (array1[index] != array2[index]) {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
      if (input1 == input2) {
         return true;
      } else if (input1 != null && input2 != null) {
         char[] array1 = getCharArray();
         char[] array2 = charArray();

         while (true) {
            int pos1 = 0;
            int pos2 = 0;

            for (int index = 0; index < 8192; index++) {
               if (pos1 == index) {
                  int count1;
                  do {
                     count1 = input1.read(array1, pos1, 8192 - pos1);
                  } while (count1 == 0);

                  if (count1 == -1) {
                     return pos2 == index && input2.read() == -1;
                  }

                  pos1 += count1;
               }

               if (pos2 == index) {
                  int count2;
                  do {
                     count2 = input2.read(array2, pos2, 8192 - pos2);
                  } while (count2 == 0);

                  if (count2 == -1) {
                     return pos1 == index && input1.read() == -1;
                  }

                  pos2 += count2;
               }

               if (array1[index] != array2[index]) {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public static boolean contentEqualsIgnoreEOL(Reader reader1, Reader reader2) throws IOException {
      if (reader1 == reader2) {
         return true;
      } else if (reader1 == null ^ reader2 == null) {
         return false;
      } else {
         BufferedReader br1 = toBufferedReader(reader1);
         BufferedReader br2 = toBufferedReader(reader2);
         String line1 = br1.readLine();

         String line2;
         for (line2 = br2.readLine(); line1 != null && line1.equals(line2); line2 = br2.readLine()) {
            line1 = br1.readLine();
         }

         return Objects.equals(line1, line2);
      }
   }

   public static int copy(InputStream inputStream, OutputStream outputStream) throws IOException {
      long count = copyLarge(inputStream, outputStream);
      return count > 2147483647L ? -1 : (int)count;
   }

   public static long copy(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException {
      return copyLarge(inputStream, outputStream, byteArray(bufferSize));
   }

   @Deprecated
   public static void copy(InputStream input, Writer writer) throws IOException {
      copy(input, writer, Charset.defaultCharset());
   }

   public static void copy(InputStream input, Writer writer, Charset inputCharset) throws IOException {
      InputStreamReader reader = new InputStreamReader(input, Charsets.toCharset(inputCharset));
      copy(reader, writer);
   }

   public static void copy(InputStream input, Writer writer, String inputCharsetName) throws IOException {
      copy(input, writer, Charsets.toCharset(inputCharsetName));
   }

   public static long copy(Reader reader, Appendable output) throws IOException {
      return copy(reader, output, CharBuffer.allocate(8192));
   }

   public static long copy(Reader reader, Appendable output, CharBuffer buffer) throws IOException {
      long count = 0L;

      int n;
      while (-1 != (n = reader.read(buffer))) {
         ((Buffer)buffer).flip();
         output.append(buffer, 0, n);
         count += (long)n;
      }

      return count;
   }

   @Deprecated
   public static void copy(Reader reader, OutputStream output) throws IOException {
      copy(reader, output, Charset.defaultCharset());
   }

   public static void copy(Reader reader, OutputStream output, Charset outputCharset) throws IOException {
      OutputStreamWriter writer = new OutputStreamWriter(output, Charsets.toCharset(outputCharset));
      copy(reader, (Writer)writer);
      writer.flush();
   }

   public static void copy(Reader reader, OutputStream output, String outputCharsetName) throws IOException {
      copy(reader, output, Charsets.toCharset(outputCharsetName));
   }

   public static int copy(Reader reader, Writer writer) throws IOException {
      long count = copyLarge(reader, writer);
      return count > 2147483647L ? -1 : (int)count;
   }

   public static long copy(URL url, File file) throws IOException {
      long var4;
      try (OutputStream outputStream = Files.newOutputStream(Objects.requireNonNull(file, "file").toPath())) {
         var4 = copy(url, outputStream);
      }

      return var4;
   }

   public static long copy(URL url, OutputStream outputStream) throws IOException {
      long var4;
      try (InputStream inputStream = Objects.requireNonNull(url, "url").openStream()) {
         var4 = copyLarge(inputStream, outputStream);
      }

      return var4;
   }

   public static long copyLarge(InputStream inputStream, OutputStream outputStream) throws IOException {
      return copy(inputStream, outputStream, 8192);
   }

   public static long copyLarge(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
      Objects.requireNonNull(inputStream, "inputStream");
      Objects.requireNonNull(outputStream, "outputStream");
      long count = 0L;

      int n;
      while (-1 != (n = inputStream.read(buffer))) {
         outputStream.write(buffer, 0, n);
         count += (long)n;
      }

      return count;
   }

   public static long copyLarge(InputStream input, OutputStream output, long inputOffset, long length) throws IOException {
      return copyLarge(input, output, inputOffset, length, getByteArray());
   }

   public static long copyLarge(InputStream input, OutputStream output, long inputOffset, long length, byte[] buffer) throws IOException {
      if (inputOffset > 0L) {
         skipFully(input, inputOffset);
      }

      if (length == 0L) {
         return 0L;
      } else {
         int bufferLength = buffer.length;
         int bytesToRead = bufferLength;
         if (length > 0L && length < (long)bufferLength) {
            bytesToRead = (int)length;
         }

         long totalRead = 0L;

         int read;
         while (bytesToRead > 0 && -1 != (read = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, read);
            totalRead += (long)read;
            if (length > 0L) {
               bytesToRead = (int)Math.min(length - totalRead, (long)bufferLength);
            }
         }

         return totalRead;
      }
   }

   public static long copyLarge(Reader reader, Writer writer) throws IOException {
      return copyLarge(reader, writer, getCharArray());
   }

   public static long copyLarge(Reader reader, Writer writer, char[] buffer) throws IOException {
      long count = 0L;

      int n;
      while (-1 != (n = reader.read(buffer))) {
         writer.write(buffer, 0, n);
         count += (long)n;
      }

      return count;
   }

   public static long copyLarge(Reader reader, Writer writer, long inputOffset, long length) throws IOException {
      return copyLarge(reader, writer, inputOffset, length, getCharArray());
   }

   public static long copyLarge(Reader reader, Writer writer, long inputOffset, long length, char[] buffer) throws IOException {
      if (inputOffset > 0L) {
         skipFully(reader, inputOffset);
      }

      if (length == 0L) {
         return 0L;
      } else {
         int bytesToRead = buffer.length;
         if (length > 0L && length < (long)buffer.length) {
            bytesToRead = (int)length;
         }

         long totalRead = 0L;

         int read;
         while (bytesToRead > 0 && -1 != (read = reader.read(buffer, 0, bytesToRead))) {
            writer.write(buffer, 0, read);
            totalRead += (long)read;
            if (length > 0L) {
               bytesToRead = (int)Math.min(length - totalRead, (long)buffer.length);
            }
         }

         return totalRead;
      }
   }

   static byte[] getByteArray() {
      return SKIP_BYTE_BUFFER.get();
   }

   static char[] getCharArray() {
      return SKIP_CHAR_BUFFER.get();
   }

   public static int length(byte[] array) {
      return array == null ? 0 : array.length;
   }

   public static int length(char[] array) {
      return array == null ? 0 : array.length;
   }

   public static int length(CharSequence csq) {
      return csq == null ? 0 : csq.length();
   }

   public static int length(Object[] array) {
      return array == null ? 0 : array.length;
   }

   public static LineIterator lineIterator(InputStream input, Charset charset) {
      return new LineIterator(new InputStreamReader(input, Charsets.toCharset(charset)));
   }

   public static LineIterator lineIterator(InputStream input, String charsetName) {
      return lineIterator(input, Charsets.toCharset(charsetName));
   }

   public static LineIterator lineIterator(Reader reader) {
      return new LineIterator(reader);
   }

   public static int read(InputStream input, byte[] buffer) throws IOException {
      return read(input, buffer, 0, buffer.length);
   }

   public static int read(InputStream input, byte[] buffer, int offset, int length) throws IOException {
      if (length < 0) {
         throw new IllegalArgumentException("Length must not be negative: " + length);
      } else {
         int remaining = length;

         while (remaining > 0) {
            int location = length - remaining;
            int count = input.read(buffer, offset + location, remaining);
            if (-1 == count) {
               break;
            }

            remaining -= count;
         }

         return length - remaining;
      }
   }

   public static int read(ReadableByteChannel input, ByteBuffer buffer) throws IOException {
      int length = buffer.remaining();

      while (buffer.remaining() > 0) {
         int count = input.read(buffer);
         if (-1 == count) {
            break;
         }
      }

      return length - buffer.remaining();
   }

   public static int read(Reader reader, char[] buffer) throws IOException {
      return read(reader, buffer, 0, buffer.length);
   }

   public static int read(Reader reader, char[] buffer, int offset, int length) throws IOException {
      if (length < 0) {
         throw new IllegalArgumentException("Length must not be negative: " + length);
      } else {
         int remaining = length;

         while (remaining > 0) {
            int location = length - remaining;
            int count = reader.read(buffer, offset + location, remaining);
            if (-1 == count) {
               break;
            }

            remaining -= count;
         }

         return length - remaining;
      }
   }

   public static void readFully(InputStream input, byte[] buffer) throws IOException {
      readFully(input, buffer, 0, buffer.length);
   }

   public static void readFully(InputStream input, byte[] buffer, int offset, int length) throws IOException {
      int actual = read(input, buffer, offset, length);
      if (actual != length) {
         throw new EOFException("Length to read: " + length + " actual: " + actual);
      }
   }

   public static byte[] readFully(InputStream input, int length) throws IOException {
      byte[] buffer = byteArray(length);
      readFully(input, buffer, 0, buffer.length);
      return buffer;
   }

   public static void readFully(ReadableByteChannel input, ByteBuffer buffer) throws IOException {
      int expected = buffer.remaining();
      int actual = read(input, buffer);
      if (actual != expected) {
         throw new EOFException("Length to read: " + expected + " actual: " + actual);
      }
   }

   public static void readFully(Reader reader, char[] buffer) throws IOException {
      readFully(reader, buffer, 0, buffer.length);
   }

   public static void readFully(Reader reader, char[] buffer, int offset, int length) throws IOException {
      int actual = read(reader, buffer, offset, length);
      if (actual != length) {
         throw new EOFException("Length to read: " + length + " actual: " + actual);
      }
   }

   @Deprecated
   public static List<String> readLines(InputStream input) throws IOException {
      return readLines(input, Charset.defaultCharset());
   }

   public static List<String> readLines(InputStream input, Charset charset) throws IOException {
      InputStreamReader reader = new InputStreamReader(input, Charsets.toCharset(charset));
      return readLines(reader);
   }

   public static List<String> readLines(InputStream input, String charsetName) throws IOException {
      return readLines(input, Charsets.toCharset(charsetName));
   }

   public static List<String> readLines(Reader reader) throws IOException {
      BufferedReader bufReader = toBufferedReader(reader);
      List<String> list = new ArrayList<>();

      String line;
      while ((line = bufReader.readLine()) != null) {
         list.add(line);
      }

      return list;
   }

   public static byte[] resourceToByteArray(String name) throws IOException {
      return resourceToByteArray(name, null);
   }

   public static byte[] resourceToByteArray(String name, ClassLoader classLoader) throws IOException {
      return toByteArray(resourceToURL(name, classLoader));
   }

   public static String resourceToString(String name, Charset charset) throws IOException {
      return resourceToString(name, charset, null);
   }

   public static String resourceToString(String name, Charset charset, ClassLoader classLoader) throws IOException {
      return toString(resourceToURL(name, classLoader), charset);
   }

   public static URL resourceToURL(String name) throws IOException {
      return resourceToURL(name, null);
   }

   public static URL resourceToURL(String name, ClassLoader classLoader) throws IOException {
      URL resource = classLoader == null ? IOUtils.class.getResource(name) : classLoader.getResource(name);
      if (resource == null) {
         throw new IOException("Resource not found: " + name);
      } else {
         return resource;
      }
   }

   public static long skip(InputStream input, long toSkip) throws IOException {
      if (toSkip < 0L) {
         throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
      } else {
         long remain = toSkip;

         while (remain > 0L) {
            byte[] byteArray = getByteArray();
            long n = (long)input.read(byteArray, 0, (int)Math.min(remain, (long)byteArray.length));
            if (n < 0L) {
               break;
            }

            remain -= n;
         }

         return toSkip - remain;
      }
   }

   public static long skip(ReadableByteChannel input, long toSkip) throws IOException {
      if (toSkip < 0L) {
         throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
      } else {
         ByteBuffer skipByteBuffer = ByteBuffer.allocate((int)Math.min(toSkip, 8192L));
         long remain = toSkip;

         while (remain > 0L) {
            ((Buffer)skipByteBuffer).position(0);
            ((Buffer)skipByteBuffer).limit((int)Math.min(remain, 8192L));
            int n = input.read(skipByteBuffer);
            if (n == -1) {
               break;
            }

            remain -= (long)n;
         }

         return toSkip - remain;
      }
   }

   public static long skip(Reader reader, long toSkip) throws IOException {
      if (toSkip < 0L) {
         throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
      } else {
         long remain = toSkip;

         while (remain > 0L) {
            char[] charArray = getCharArray();
            long n = (long)reader.read(charArray, 0, (int)Math.min(remain, (long)charArray.length));
            if (n < 0L) {
               break;
            }

            remain -= n;
         }

         return toSkip - remain;
      }
   }

   public static void skipFully(InputStream input, long toSkip) throws IOException {
      if (toSkip < 0L) {
         throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
      } else {
         long skipped = skip(input, toSkip);
         if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
         }
      }
   }

   public static void skipFully(ReadableByteChannel input, long toSkip) throws IOException {
      if (toSkip < 0L) {
         throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
      } else {
         long skipped = skip(input, toSkip);
         if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
         }
      }
   }

   public static void skipFully(Reader reader, long toSkip) throws IOException {
      long skipped = skip(reader, toSkip);
      if (skipped != toSkip) {
         throw new EOFException("Chars to skip: " + toSkip + " actual: " + skipped);
      }
   }

   public static InputStream toBufferedInputStream(InputStream input) throws IOException {
      return ByteArrayOutputStream.toBufferedInputStream(input);
   }

   public static InputStream toBufferedInputStream(InputStream input, int size) throws IOException {
      return ByteArrayOutputStream.toBufferedInputStream(input, size);
   }

   public static BufferedReader toBufferedReader(Reader reader) {
      return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
   }

   public static BufferedReader toBufferedReader(Reader reader, int size) {
      return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, size);
   }

   public static byte[] toByteArray(InputStream inputStream) throws IOException {
      byte[] var5;
      try (
         UnsynchronizedByteArrayOutputStream ubaOutput = new UnsynchronizedByteArrayOutputStream();
         ThresholdingOutputStream thresholdOuput = new ThresholdingOutputStream(Integer.MAX_VALUE, os -> {
            throw new IllegalArgumentException(String.format("Cannot read more than %,d into a byte array", Integer.MAX_VALUE));
         }, os -> ubaOutput);
      ) {
         copy(inputStream, thresholdOuput);
         var5 = ubaOutput.toByteArray();
      }

      return var5;
   }

   public static byte[] toByteArray(InputStream input, int size) throws IOException {
      if (size < 0) {
         throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
      } else if (size == 0) {
         return EMPTY_BYTE_ARRAY;
      } else {
         byte[] data = byteArray(size);
         int offset = 0;

         int read;
         while (offset < size && (read = input.read(data, offset, size - offset)) != -1) {
            offset += read;
         }

         if (offset != size) {
            throw new IOException("Unexpected read size, current: " + offset + ", expected: " + size);
         } else {
            return data;
         }
      }
   }

   public static byte[] toByteArray(InputStream input, long size) throws IOException {
      if (size > 2147483647L) {
         throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + size);
      } else {
         return toByteArray(input, (int)size);
      }
   }

   @Deprecated
   public static byte[] toByteArray(Reader reader) throws IOException {
      return toByteArray(reader, Charset.defaultCharset());
   }

   public static byte[] toByteArray(Reader reader, Charset charset) throws IOException {
      byte[] var4;
      try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
         copy(reader, output, charset);
         var4 = output.toByteArray();
      }

      return var4;
   }

   public static byte[] toByteArray(Reader reader, String charsetName) throws IOException {
      return toByteArray(reader, Charsets.toCharset(charsetName));
   }

   @Deprecated
   public static byte[] toByteArray(String input) {
      return input.getBytes(Charset.defaultCharset());
   }

   public static byte[] toByteArray(URI uri) throws IOException {
      return toByteArray(uri.toURL());
   }

   public static byte[] toByteArray(URL url) throws IOException {
      URLConnection conn = url.openConnection();

      byte[] var2;
      try {
         var2 = toByteArray(conn);
      } finally {
         close(conn);
      }

      return var2;
   }

   public static byte[] toByteArray(URLConnection urlConn) throws IOException {
      byte[] var3;
      try (InputStream inputStream = urlConn.getInputStream()) {
         var3 = toByteArray(inputStream);
      }

      return var3;
   }

   @Deprecated
   public static char[] toCharArray(InputStream inputStream) throws IOException {
      return toCharArray(inputStream, Charset.defaultCharset());
   }

   public static char[] toCharArray(InputStream inputStream, Charset charset) throws IOException {
      CharArrayWriter writer = new CharArrayWriter();
      copy(inputStream, writer, charset);
      return writer.toCharArray();
   }

   public static char[] toCharArray(InputStream inputStream, String charsetName) throws IOException {
      return toCharArray(inputStream, Charsets.toCharset(charsetName));
   }

   public static char[] toCharArray(Reader reader) throws IOException {
      CharArrayWriter sw = new CharArrayWriter();
      copy(reader, (Writer)sw);
      return sw.toCharArray();
   }

   @Deprecated
   public static InputStream toInputStream(CharSequence input) {
      return toInputStream(input, Charset.defaultCharset());
   }

   public static InputStream toInputStream(CharSequence input, Charset charset) {
      return toInputStream(input.toString(), charset);
   }

   public static InputStream toInputStream(CharSequence input, String charsetName) {
      return toInputStream(input, Charsets.toCharset(charsetName));
   }

   @Deprecated
   public static InputStream toInputStream(String input) {
      return toInputStream(input, Charset.defaultCharset());
   }

   public static InputStream toInputStream(String input, Charset charset) {
      return new ByteArrayInputStream(input.getBytes(Charsets.toCharset(charset)));
   }

   public static InputStream toInputStream(String input, String charsetName) {
      byte[] bytes = input.getBytes(Charsets.toCharset(charsetName));
      return new ByteArrayInputStream(bytes);
   }

   @Deprecated
   public static String toString(byte[] input) {
      return new String(input, Charset.defaultCharset());
   }

   public static String toString(byte[] input, String charsetName) {
      return new String(input, Charsets.toCharset(charsetName));
   }

   @Deprecated
   public static String toString(InputStream input) throws IOException {
      return toString(input, Charset.defaultCharset());
   }

   public static String toString(InputStream input, Charset charset) throws IOException {
      String var4;
      try (StringBuilderWriter sw = new StringBuilderWriter()) {
         copy(input, sw, charset);
         var4 = sw.toString();
      }

      return var4;
   }

   public static String toString(InputStream input, String charsetName) throws IOException {
      return toString(input, Charsets.toCharset(charsetName));
   }

   public static String toString(Reader reader) throws IOException {
      String var3;
      try (StringBuilderWriter sw = new StringBuilderWriter()) {
         copy(reader, (Writer)sw);
         var3 = sw.toString();
      }

      return var3;
   }

   @Deprecated
   public static String toString(URI uri) throws IOException {
      return toString(uri, Charset.defaultCharset());
   }

   public static String toString(URI uri, Charset encoding) throws IOException {
      return toString(uri.toURL(), Charsets.toCharset(encoding));
   }

   public static String toString(URI uri, String charsetName) throws IOException {
      return toString(uri, Charsets.toCharset(charsetName));
   }

   @Deprecated
   public static String toString(URL url) throws IOException {
      return toString(url, Charset.defaultCharset());
   }

   public static String toString(URL url, Charset encoding) throws IOException {
      String var4;
      try (InputStream inputStream = url.openStream()) {
         var4 = toString(inputStream, encoding);
      }

      return var4;
   }

   public static String toString(URL url, String charsetName) throws IOException {
      return toString(url, Charsets.toCharset(charsetName));
   }

   public static void write(byte[] data, OutputStream output) throws IOException {
      if (data != null) {
         output.write(data);
      }
   }

   @Deprecated
   public static void write(byte[] data, Writer writer) throws IOException {
      write(data, writer, Charset.defaultCharset());
   }

   public static void write(byte[] data, Writer writer, Charset charset) throws IOException {
      if (data != null) {
         writer.write(new String(data, Charsets.toCharset(charset)));
      }
   }

   public static void write(byte[] data, Writer writer, String charsetName) throws IOException {
      write(data, writer, Charsets.toCharset(charsetName));
   }

   @Deprecated
   public static void write(char[] data, OutputStream output) throws IOException {
      write(data, output, Charset.defaultCharset());
   }

   public static void write(char[] data, OutputStream output, Charset charset) throws IOException {
      if (data != null) {
         output.write(new String(data).getBytes(Charsets.toCharset(charset)));
      }
   }

   public static void write(char[] data, OutputStream output, String charsetName) throws IOException {
      write(data, output, Charsets.toCharset(charsetName));
   }

   public static void write(char[] data, Writer writer) throws IOException {
      if (data != null) {
         writer.write(data);
      }
   }

   @Deprecated
   public static void write(CharSequence data, OutputStream output) throws IOException {
      write(data, output, Charset.defaultCharset());
   }

   public static void write(CharSequence data, OutputStream output, Charset charset) throws IOException {
      if (data != null) {
         write(data.toString(), output, charset);
      }
   }

   public static void write(CharSequence data, OutputStream output, String charsetName) throws IOException {
      write(data, output, Charsets.toCharset(charsetName));
   }

   public static void write(CharSequence data, Writer writer) throws IOException {
      if (data != null) {
         write(data.toString(), writer);
      }
   }

   @Deprecated
   public static void write(String data, OutputStream output) throws IOException {
      write(data, output, Charset.defaultCharset());
   }

   public static void write(String data, OutputStream output, Charset charset) throws IOException {
      if (data != null) {
         output.write(data.getBytes(Charsets.toCharset(charset)));
      }
   }

   public static void write(String data, OutputStream output, String charsetName) throws IOException {
      write(data, output, Charsets.toCharset(charsetName));
   }

   public static void write(String data, Writer writer) throws IOException {
      if (data != null) {
         writer.write(data);
      }
   }

   @Deprecated
   public static void write(StringBuffer data, OutputStream output) throws IOException {
      write(data, output, (String)null);
   }

   @Deprecated
   public static void write(StringBuffer data, OutputStream output, String charsetName) throws IOException {
      if (data != null) {
         output.write(data.toString().getBytes(Charsets.toCharset(charsetName)));
      }
   }

   @Deprecated
   public static void write(StringBuffer data, Writer writer) throws IOException {
      if (data != null) {
         writer.write(data.toString());
      }
   }

   public static void writeChunked(byte[] data, OutputStream output) throws IOException {
      if (data != null) {
         int bytes = data.length;
         int offset = 0;

         while (bytes > 0) {
            int chunk = Math.min(bytes, 8192);
            output.write(data, offset, chunk);
            bytes -= chunk;
            offset += chunk;
         }
      }
   }

   public static void writeChunked(char[] data, Writer writer) throws IOException {
      if (data != null) {
         int bytes = data.length;
         int offset = 0;

         while (bytes > 0) {
            int chunk = Math.min(bytes, 8192);
            writer.write(data, offset, chunk);
            bytes -= chunk;
            offset += chunk;
         }
      }
   }

   @Deprecated
   public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output) throws IOException {
      writeLines(lines, lineEnding, output, Charset.defaultCharset());
   }

   public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output, Charset charset) throws IOException {
      if (lines != null) {
         if (lineEnding == null) {
            lineEnding = System.lineSeparator();
         }

         Charset cs = Charsets.toCharset(charset);

         for (Object line : lines) {
            if (line != null) {
               output.write(line.toString().getBytes(cs));
            }

            output.write(lineEnding.getBytes(cs));
         }
      }
   }

   public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output, String charsetName) throws IOException {
      writeLines(lines, lineEnding, output, Charsets.toCharset(charsetName));
   }

   public static void writeLines(Collection<?> lines, String lineEnding, Writer writer) throws IOException {
      if (lines != null) {
         if (lineEnding == null) {
            lineEnding = System.lineSeparator();
         }

         for (Object line : lines) {
            if (line != null) {
               writer.write(line.toString());
            }

            writer.write(lineEnding);
         }
      }
   }

   public static Writer writer(Appendable appendable) {
      Objects.requireNonNull(appendable, "appendable");
      if (appendable instanceof Writer) {
         return (Writer)appendable;
      } else {
         return (Writer)(appendable instanceof StringBuilder ? new StringBuilderWriter((StringBuilder)appendable) : new AppendableWriter<>(appendable));
      }
   }
}
