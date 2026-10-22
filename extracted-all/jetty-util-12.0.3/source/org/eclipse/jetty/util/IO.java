package org.eclipse.jetty.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IO {
   private static final Logger LOG = LoggerFactory.getLogger(IO.class);
   public static final String CRLF = "\r\n";
   public static final byte[] CRLF_BYTES = new byte[]{13, 10};
   public static final int bufferSize = 65536;

   public static void copy(InputStream in, OutputStream out) throws IOException {
      copy(in, out, -1L);
   }

   public static void copy(Reader in, Writer out) throws IOException {
      copy(in, out, -1L);
   }

   public static void copy(InputStream in, OutputStream out, long byteCount) throws IOException {
      byte[] buffer = new byte[65536];
      if (byteCount >= 0L) {
         while (byteCount > 0L) {
            int max = byteCount < 65536L ? (int)byteCount : 65536;
            int len = in.read(buffer, 0, max);
            if (len == -1) {
               break;
            }

            byteCount -= (long)len;
            out.write(buffer, 0, len);
         }
      } else {
         while (true) {
            int len = in.read(buffer, 0, 65536);
            if (len < 0) {
               break;
            }

            out.write(buffer, 0, len);
         }
      }
   }

   public static void copy(Reader in, Writer out, long byteCount) throws IOException {
      char[] buffer = new char[65536];
      if (byteCount >= 0L) {
         while (byteCount > 0L) {
            int len;
            if (byteCount < 65536L) {
               len = in.read(buffer, 0, (int)byteCount);
            } else {
               len = in.read(buffer, 0, 65536);
            }

            if (len == -1) {
               break;
            }

            byteCount -= (long)len;
            out.write(buffer, 0, len);
         }
      } else if (out instanceof PrintWriter pout) {
         while (!pout.checkError()) {
            int lenx = in.read(buffer, 0, 65536);
            if (lenx == -1) {
               break;
            }

            out.write(buffer, 0, lenx);
         }
      } else {
         while (true) {
            int lenx = in.read(buffer, 0, 65536);
            if (lenx == -1) {
               break;
            }

            out.write(buffer, 0, lenx);
         }
      }
   }

   public static void copy(File from, File to) throws IOException {
      if (from.isDirectory()) {
         copyDir(from, to);
      } else {
         copyFile(from, to);
      }
   }

   public static void copyDir(File from, File to) throws IOException {
      if (to.exists()) {
         if (!to.isDirectory()) {
            throw new IllegalArgumentException(to.toString());
         }
      } else {
         to.mkdirs();
      }

      File[] files = from.listFiles();
      if (files != null) {
         for (File file : files) {
            String name = file.getName();
            if (!".".equals(name) && !"..".equals(name)) {
               copy(file, new File(to, name));
            }
         }
      }
   }

   public static void copyDir(Path srcDir, Path destDir, CopyOption... copyOptions) throws IOException {
      if (!Files.isDirectory(Objects.requireNonNull(srcDir))) {
         throw new IllegalArgumentException("Source is not a directory: " + srcDir);
      } else if (!Files.isDirectory(Objects.requireNonNull(destDir))) {
         throw new IllegalArgumentException("Dest is not a directory: " + destDir);
      } else {
         try (Stream<Path> sourceStream = Files.walk(srcDir, 20)) {
            Iterator<Path> iterFiles = sourceStream.filter(x$0 -> Files.isRegularFile(x$0)).iterator();

            while (iterFiles.hasNext()) {
               Path sourceFile = iterFiles.next();
               URI relativeSrc = srcDir.toUri().relativize(sourceFile.toUri());
               Path destFile = destDir.resolve(relativeSrc.toASCIIString());
               if (!Files.exists(destFile.getParent())) {
                  Files.createDirectories(destFile.getParent());
               }

               Files.copy(sourceFile, destFile, copyOptions);
            }
         }
      }
   }

   public static void copyFile(File from, File to) throws IOException {
      try (
         InputStream in = new FileInputStream(from);
         OutputStream out = new FileOutputStream(to);
      ) {
         copy(in, out);
      }
   }

   public static IOException rethrow(Throwable cause) {
      if (cause instanceof ExecutionException xx) {
         cause = xx.getCause();
      }

      if (cause instanceof CompletionException xx) {
         cause = xx.getCause();
      }

      if (cause instanceof IOException) {
         return (IOException)cause;
      } else if (cause instanceof Error) {
         throw (Error)cause;
      } else if (cause instanceof RuntimeException) {
         throw (RuntimeException)cause;
      } else {
         return (IOException)(cause instanceof InterruptedException
            ? (InterruptedIOException)new InterruptedIOException().initCause(cause)
            : new IOException(cause));
      }
   }

   public static String toString(Path path, Charset charset) throws IOException {
      byte[] buf = Files.readAllBytes(path);
      return new String(buf, charset);
   }

   public static String toString(InputStream in) throws IOException {
      return toString(in, (Charset)null);
   }

   public static String toString(InputStream in, String encoding) throws IOException {
      return toString(in, encoding == null ? null : Charset.forName(encoding));
   }

   public static String toString(InputStream in, Charset encoding) throws IOException {
      StringWriter writer = new StringWriter();
      InputStreamReader reader = encoding == null ? new InputStreamReader(in) : new InputStreamReader(in, encoding);
      copy(reader, writer);
      return writer.toString();
   }

   public static String toString(Reader in) throws IOException {
      StringWriter writer = new StringWriter();
      copy(in, writer);
      return writer.toString();
   }

   public static boolean delete(File file) {
      if (file == null) {
         return false;
      } else if (!file.exists()) {
         return false;
      } else {
         if (file.isDirectory()) {
            File[] files = file.listFiles();

            for (int i = 0; files != null && i < files.length; i++) {
               delete(files[i]);
            }
         }

         return file.delete();
      }
   }

   public static boolean delete(Path path) {
      if (path == null) {
         return false;
      } else {
         try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
               public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                  Files.delete(dir);
                  return FileVisitResult.CONTINUE;
               }

               public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                  Files.delete(file);
                  return FileVisitResult.CONTINUE;
               }
            });
            return true;
         } catch (IOException var2) {
            return false;
         }
      }
   }

   public static boolean isEmptyDir(File dir) {
      if (dir == null) {
         return true;
      } else if (!dir.exists()) {
         return true;
      } else if (!dir.isDirectory()) {
         return false;
      } else {
         String[] list = dir.list();
         return list == null ? true : list.length <= 0;
      }
   }

   public static void close(AutoCloseable closeable) {
      try {
         if (closeable != null) {
            closeable.close();
         }
      } catch (Exception var2) {
         LOG.trace("IGNORED", var2);
      }
   }

   public static void close(Closeable closeable) {
      close((AutoCloseable)closeable);
   }

   public static void close(InputStream is) {
      close((AutoCloseable)is);
   }

   public static void close(OutputStream os) {
      close((AutoCloseable)os);
   }

   public static void close(Reader reader) {
      close((AutoCloseable)reader);
   }

   public static void close(Writer writer) {
      close((AutoCloseable)writer);
   }

   public static byte[] readBytes(InputStream in) throws IOException {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      copy(in, bout);
      return bout.toByteArray();
   }

   public static long write(GatheringByteChannel out, ByteBuffer[] buffers, int offset, int length) throws IOException {
      long total = 0L;

      label26:
      while (length > 0) {
         long wrote = out.write(buffers, offset, length);
         if (wrote == 0L) {
            break;
         }

         total += wrote;

         for (int i = offset; i < buffers.length; i++) {
            if (buffers[i].hasRemaining()) {
               length -= i - offset;
               offset = i;
               continue label26;
            }
         }

         length = 0;
      }

      return total;
   }

   public static File asFile(Object fileObject) {
      if (fileObject == null) {
         return null;
      } else if (fileObject instanceof File) {
         return (File)fileObject;
      } else if (fileObject instanceof String) {
         return new File((String)fileObject);
      } else {
         return fileObject instanceof Path ? ((Path)fileObject).toFile() : null;
      }
   }

   private IO() {
   }
}
