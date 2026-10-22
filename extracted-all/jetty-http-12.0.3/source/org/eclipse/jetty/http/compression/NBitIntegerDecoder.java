package org.eclipse.jetty.http.compression;

import java.nio.ByteBuffer;

public class NBitIntegerDecoder {
   private int _prefix;
   private long _total;
   private long _multiplier;
   private boolean _started;

   public void setPrefix(int prefix) {
      if (this._started) {
         throw new IllegalStateException();
      } else {
         this._prefix = prefix;
      }
   }

   public int decodeInt(ByteBuffer buffer) {
      return Math.toIntExact(this.decodeLong(buffer));
   }

   public long decodeLong(ByteBuffer buffer) {
      if (!this._started) {
         if (!buffer.hasRemaining()) {
            return -1L;
         }

         this._started = true;
         this._multiplier = 1L;
         int nbits = 255 >>> 8 - this._prefix;
         this._total = (long)(buffer.get() & nbits);
         if (this._total < (long)nbits) {
            long total = this._total;
            this.reset();
            return total;
         }
      }

      while (buffer.hasRemaining()) {
         int b = buffer.get() & 255;
         this._total = Math.addExact(this._total, (long)(b & 127) * this._multiplier);
         this._multiplier = Math.multiplyExact(this._multiplier, 128);
         if ((b & 128) == 0) {
            long total = this._total;
            this.reset();
            return total;
         }
      }

      return -1L;
   }

   public void reset() {
      this._prefix = 0;
      this._total = 0L;
      this._multiplier = 1L;
      this._started = false;
   }
}
