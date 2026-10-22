package nonapi.io.github.classgraph.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class ProxyingInputStream extends InputStream {
   private InputStream inputStream;
   private static Method readAllBytes;
   private static Method readNBytes1;
   private static Method readNBytes3;
   private static Method skipNBytes;
   private static Method transferTo;

   public ProxyingInputStream(InputStream inputStream) {
      this.inputStream = inputStream;
   }

   @Override
   public int read() throws IOException {
      return this.inputStream.read();
   }

   @Override
   public int read(byte[] b) throws IOException {
      return this.inputStream.read(b);
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      return this.inputStream.read(b, off, len);
   }

   @Override
   public byte[] readAllBytes() throws IOException {
      if (readAllBytes == null) {
         throw new UnsupportedOperationException();
      } else {
         try {
            return (byte[])readAllBytes.invoke(this.inputStream);
         } catch (Exception var2) {
            throw new IOException(var2);
         }
      }
   }

   @Override
   public byte[] readNBytes(int len) throws IOException {
      if (readNBytes1 == null) {
         throw new UnsupportedOperationException();
      } else {
         try {
            return (byte[])readNBytes1.invoke(this.inputStream, len);
         } catch (Exception var3) {
            throw new IOException(var3);
         }
      }
   }

   @Override
   public int readNBytes(byte[] b, int off, int len) throws IOException {
      if (readNBytes3 == null) {
         throw new UnsupportedOperationException();
      } else {
         try {
            return (Integer)readNBytes3.invoke(this.inputStream, b, off, len);
         } catch (Exception var5) {
            throw new IOException(var5);
         }
      }
   }

   @Override
   public int available() throws IOException {
      return this.inputStream.available();
   }

   @Override
   public boolean markSupported() {
      return this.inputStream.markSupported();
   }

   @Override
   public synchronized void mark(int readlimit) {
      this.inputStream.mark(readlimit);
   }

   @Override
   public synchronized void reset() throws IOException {
      this.inputStream.reset();
   }

   @Override
   public long skip(long n) throws IOException {
      return this.inputStream.skip(n);
   }

   @Override
   public void skipNBytes(long n) throws IOException {
      if (skipNBytes == null) {
         throw new UnsupportedOperationException();
      } else {
         try {
            skipNBytes.invoke(this.inputStream, n);
         } catch (Exception var4) {
            throw new IOException(var4);
         }
      }
   }

   @Override
   public long transferTo(OutputStream out) throws IOException {
      if (transferTo == null) {
         throw new UnsupportedOperationException();
      } else {
         try {
            return (Long)transferTo.invoke(this.inputStream, out);
         } catch (Exception var3) {
            throw new IOException(var3);
         }
      }
   }

   @Override
   public String toString() {
      return this.inputStream.toString();
   }

   @Override
   public void close() throws IOException {
      if (this.inputStream != null) {
         try {
            this.inputStream.close();
         } finally {
            this.inputStream = null;
         }
      }
   }

   static {
      try {
         readAllBytes = InputStream.class.getDeclaredMethod("readAllBytes");
      } catch (SecurityException | NoSuchMethodException var5) {
      }

      try {
         readNBytes1 = InputStream.class.getDeclaredMethod("readNBytes", int.class);
      } catch (SecurityException | NoSuchMethodException var4) {
      }

      try {
         readNBytes3 = InputStream.class.getDeclaredMethod("readNBytes", byte[].class, int.class, int.class);
      } catch (SecurityException | NoSuchMethodException var3) {
      }

      try {
         skipNBytes = InputStream.class.getDeclaredMethod("skipNBytes", long.class);
      } catch (SecurityException | NoSuchMethodException var2) {
      }

      try {
         transferTo = InputStream.class.getDeclaredMethod("transferTo", OutputStream.class);
      } catch (SecurityException | NoSuchMethodException var1) {
      }
   }
}
