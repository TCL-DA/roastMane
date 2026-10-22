package com.ghgande.j2mod.modbus.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastByteArrayOutputStream extends OutputStream {
   private static final Logger logger = LoggerFactory.getLogger(FastByteArrayOutputStream.class);
   private static final int DEFAULT_INIT_SIZE = 100;
   private static final int DEFAULT_BUMP_SIZE = 100;
   protected int count;
   private final int bumpLen;
   byte[] buf;

   FastByteArrayOutputStream() {
      this.buf = new byte[100];
      this.bumpLen = 100;
   }

   FastByteArrayOutputStream(int initialSize) {
      this.buf = new byte[initialSize];
      this.bumpLen = 100;
   }

   public FastByteArrayOutputStream(int initialSize, int bumpSize) {
      this.buf = new byte[initialSize];
      this.bumpLen = bumpSize;
   }

   public int size() {
      return this.count;
   }

   public void reset() {
      this.count = 0;
   }

   @Override
   public void write(int b) throws IOException {
      if (this.count + 1 > this.buf.length) {
         this.bump(1);
      }

      this.buf[this.count++] = (byte)b;
   }

   @Override
   public void write(byte[] fromBuf) throws IOException {
      int needed = this.count + fromBuf.length - this.buf.length;
      if (needed > 0) {
         this.bump(needed);
      }

      for (byte aFromBuf : fromBuf) {
         this.buf[this.count++] = aFromBuf;
      }
   }

   @Override
   public void write(byte[] fromBuf, int offset, int length) throws IOException {
      int needed = this.count + length - this.buf.length;
      if (needed > 0) {
         this.bump(needed);
      }

      int fromLen = offset + length;

      for (int i = offset; i < fromLen; i++) {
         this.buf[this.count++] = fromBuf[i];
      }
   }

   public synchronized void writeTo(OutputStream out) throws IOException {
      out.write(this.buf, 0, this.count);
   }

   @Override
   public String toString() {
      try {
         return new String(this.buf, 0, this.count, "US-ASCII");
      } catch (Exception var2) {
         logger.debug("Problem converting bytes to string - {}", var2.getMessage());
         return "";
      }
   }

   public String toString(String encoding) throws UnsupportedEncodingException {
      return new String(this.buf, 0, this.count, encoding);
   }

   public byte[] toByteArray() {
      byte[] toBuf = new byte[this.count];
      System.arraycopy(this.buf, 0, toBuf, 0, this.count);
      return toBuf;
   }

   public void toByteArray(byte[] toBuf, int offset) {
      int toLen = toBuf.length > this.count ? this.count : toBuf.length;
      System.arraycopy(this.buf, offset, toBuf, offset, toLen - offset);
   }

   public synchronized byte[] getBufferBytes() {
      byte[] dest = new byte[this.count];
      System.arraycopy(this.buf, 0, dest, 0, dest.length);
      return dest;
   }

   public int getBufferOffset() {
      return 0;
   }

   public int getBufferLength() {
      return this.count;
   }

   public void makeSpace(int sizeNeeded) {
      int needed = this.count + sizeNeeded - this.buf.length;
      if (needed > 0) {
         this.bump(needed);
      }
   }

   public void addSize(int sizeAdded) {
      this.count += sizeAdded;
   }

   private void bump(int needed) {
      byte[] toBuf = new byte[this.buf.length + needed + this.bumpLen];
      System.arraycopy(this.buf, 0, toBuf, 0, this.count);
      this.buf = toBuf;
   }
}
