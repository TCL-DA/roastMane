package org.eclipse.jetty.http.compression;

import java.nio.ByteBuffer;

public class NBitIntegerEncoder {
   private NBitIntegerEncoder() {
   }

   public static int octetsNeeded(int prefix, long value) {
      if (prefix > 0 && prefix <= 8) {
         int nbits = 255 >>> 8 - prefix;
         value -= (long)nbits;
         if (value < 0L) {
            return 1;
         } else if (value == 0L) {
            return 2;
         } else {
            int lz = Long.numberOfLeadingZeros(value);
            int log = 64 - lz;
            return 1 + (log + 6) / 7;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static void encode(ByteBuffer buffer, int prefix, long value) {
      if (prefix > 0 && prefix <= 8) {
         if (prefix == 8) {
            buffer.put((byte)0);
         }

         int bits = 255 >>> 8 - prefix;
         int p = buffer.position() - 1;
         if (value < (long)bits) {
            buffer.put(p, (byte)((int)((long)(buffer.get(p) & ~bits) | value)));
         } else {
            buffer.put(p, (byte)(buffer.get(p) | bits));

            long length;
            for (length = value - (long)bits; (length & -128L) != 0L; length >>>= 7) {
               buffer.put((byte)((int)(length & 127L | 128L)));
            }

            buffer.put((byte)((int)length));
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
