package com.ghgande.j2mod.modbus.io;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastByteArrayInputStream extends InputStream {
   private static final Logger logger = LoggerFactory.getLogger(FastByteArrayInputStream.class);
   protected int count;
   int pos;
   int mark;
   byte[] buf;

   FastByteArrayInputStream(byte[] buffer) {
      this.buf = buffer;
      this.count = buffer.length;
      this.pos = 0;
      this.mark = 0;
   }

   @Override
   public int read() throws IOException {
      logger.debug("read()");
      logger.debug("count={} pos={}", this.count, this.pos);
      return this.pos < this.count ? this.buf[this.pos++] & 0xFF : -1;
   }

   @Override
   public int read(byte[] toBuf) throws IOException {
      logger.debug("read(byte[])");
      return this.read(toBuf, 0, toBuf.length);
   }

   @Override
   public int read(byte[] toBuf, int offset, int length) throws IOException {
      logger.debug("read(byte[],int,int)");
      int avail = this.count - this.pos;
      if (avail <= 0) {
         return -1;
      } else {
         if (length > avail) {
            length = avail;
         }

         for (int i = 0; i < length; i++) {
            toBuf[offset++] = this.buf[this.pos++];
         }

         return length;
      }
   }

   @Override
   public long skip(long count) {
      int myCount = (int)count;
      if (myCount + this.pos > this.count) {
         myCount = this.count - this.pos;
      }

      this.pos += myCount;
      return (long)myCount;
   }

   @Override
   public int available() {
      return this.count - this.pos;
   }

   public int getCount() {
      return this.count;
   }

   @Override
   public synchronized void mark(int readlimit) {
      logger.debug("mark()");
      this.mark = this.pos;
      logger.debug("mark={} pos={}", this.mark, this.pos);
   }

   @Override
   public synchronized void reset() {
      logger.debug("reset()");
      this.pos = this.mark;
      logger.debug("mark={} pos={}", this.mark, this.pos);
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   public byte[] toByteArray() {
      byte[] toBuf = new byte[this.count];
      System.arraycopy(this.buf, 0, toBuf, 0, this.count);
      return toBuf;
   }

   public synchronized byte[] getBufferBytes() {
      byte[] dest = new byte[this.count];
      System.arraycopy(this.buf, 0, dest, 0, dest.length);
      return dest;
   }

   public int getBufferOffset() {
      return this.pos;
   }

   public int getBufferLength() {
      return this.count;
   }
}
