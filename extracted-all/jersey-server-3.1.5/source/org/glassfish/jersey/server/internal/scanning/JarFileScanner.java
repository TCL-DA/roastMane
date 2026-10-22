package org.glassfish.jersey.server.internal.scanning;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;
import org.glassfish.jersey.server.internal.LocalizationMessages;

public final class JarFileScanner extends AbstractResourceFinderAdapter {
   private static final Logger LOGGER = Logger.getLogger(JarFileScanner.class.getName());
   private static final char JAR_FILE_SEPARATOR = '/';
   private final JarInputStream jarInputStream;
   private final String parent;
   private final boolean recursive;
   private JarEntry next = null;

   public JarFileScanner(InputStream inputStream, String parent, boolean recursive) throws IOException {
      this.jarInputStream = new JarInputStream(inputStream);
      this.parent = !parent.isEmpty() && !parent.endsWith(String.valueOf('/')) ? parent + '/' : parent;
      this.recursive = recursive;
   }

   @Override
   public boolean hasNext() {
      if (this.next == null) {
         try {
            do {
               this.next = this.jarInputStream.getNextJarEntry();
            } while (
               this.next != null
                  && (
                     this.next.isDirectory()
                        || !this.next.getName().startsWith(this.parent)
                        || !this.recursive && this.next.getName().substring(this.parent.length()).indexOf(47) != -1
                  )
            );
         } catch (SecurityException | IOException var2) {
            LOGGER.log(Level.CONFIG, LocalizationMessages.JAR_SCANNER_UNABLE_TO_READ_ENTRY(), (Throwable)var2);
            return false;
         }
      }

      if (this.next == null) {
         this.close();
         return false;
      } else {
         return true;
      }
   }

   public String next() {
      if (this.next == null && !this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         String name = this.next.getName();
         this.next = null;
         return name;
      }
   }

   @Override
   public void reset() {
      throw new UnsupportedOperationException();
   }

   @Override
   public InputStream open() {
      return new InputStream() {
         @Override
         public int read() throws IOException {
            return JarFileScanner.this.jarInputStream.read();
         }

         @Override
         public int read(byte[] bytes) throws IOException {
            return JarFileScanner.this.jarInputStream.read(bytes);
         }

         @Override
         public int read(byte[] bytes, int i, int i2) throws IOException {
            return JarFileScanner.this.jarInputStream.read(bytes, i, i2);
         }

         @Override
         public long skip(long l) throws IOException {
            return JarFileScanner.this.jarInputStream.skip(l);
         }

         @Override
         public int available() throws IOException {
            return JarFileScanner.this.jarInputStream.available();
         }

         @Override
         public void close() throws IOException {
            JarFileScanner.this.jarInputStream.closeEntry();
         }

         @Override
         public synchronized void mark(int i) {
            JarFileScanner.this.jarInputStream.mark(i);
         }

         @Override
         public synchronized void reset() throws IOException {
            JarFileScanner.this.jarInputStream.reset();
         }

         @Override
         public boolean markSupported() {
            return JarFileScanner.this.jarInputStream.markSupported();
         }
      };
   }

   @Override
   public void close() {
      try {
         this.jarInputStream.close();
      } catch (IOException var2) {
         LOGGER.log(Level.FINE, LocalizationMessages.JAR_SCANNER_UNABLE_TO_CLOSE_FILE(), (Throwable)var2);
      }
   }
}
