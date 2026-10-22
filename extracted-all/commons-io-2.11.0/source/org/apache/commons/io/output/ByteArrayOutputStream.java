package org.apache.commons.io.output;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayOutputStream extends AbstractByteArrayOutputStream {
   public ByteArrayOutputStream() {
      this(1024);
   }

   public ByteArrayOutputStream(int size) {
      if (size < 0) {
         throw new IllegalArgumentException("Negative initial size: " + size);
      } else {
         synchronized (this) {
            this.needNewBuffer(size);
         }
      }
   }

   @Override
   public void write(byte[] b, int off, int len) {
      if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      } else if (len != 0) {
         synchronized (this) {
            this.writeImpl(b, off, len);
         }
      }
   }

   @Override
   public synchronized void write(int b) {
      this.writeImpl(b);
   }

   @Override
   public synchronized int write(InputStream in) throws IOException {
      return this.writeImpl(in);
   }

   @Override
   public synchronized int size() {
      return this.count;
   }

   @Override
   public synchronized void reset() {
      this.resetImpl();
   }

   @Override
   public synchronized void writeTo(OutputStream out) throws IOException {
      this.writeToImpl(out);
   }

   public static InputStream toBufferedInputStream(InputStream input) throws IOException {
      return toBufferedInputStream(input, 1024);
   }

   public static InputStream toBufferedInputStream(InputStream input, int size) throws IOException {
      InputStream var4;
      try (ByteArrayOutputStream output = new ByteArrayOutputStream(size)) {
         output.write(input);
         var4 = output.toInputStream();
      }

      return var4;
   }

   @Override
   public synchronized InputStream toInputStream() {
      return this.toInputStream(ByteArrayInputStream::new);
   }

   @Override
   public synchronized byte[] toByteArray() {
      return this.toByteArrayImpl();
   }
}
