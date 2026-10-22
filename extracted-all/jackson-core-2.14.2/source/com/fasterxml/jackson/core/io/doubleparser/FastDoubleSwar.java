package com.fasterxml.jackson.core.io.doubleparser;

class FastDoubleSwar {
   public static int tryToParseEightDigitsUtf16(char[] a, int offset) {
      long first = (long)a[offset] | (long)a[offset + 1] << 16 | (long)a[offset + 2] << 32 | (long)a[offset + 3] << 48;
      long second = (long)a[offset + 4] | (long)a[offset + 5] << 16 | (long)a[offset + 6] << 32 | (long)a[offset + 7] << 48;
      return tryToParseEightDigitsUtf16(first, second);
   }

   public static int tryToParseEightDigitsUtf16(long first, long second) {
      long fval = first - 13511005043687472L;
      long sval = second - 13511005043687472L;
      long fpre = first + 19703549022044230L | fval;
      long spre = second + 19703549022044230L | sval;
      return ((fpre | spre) & -35747867511423104L) != 0L ? -1 : (int)(sval * 281475406208040961L >>> 48) + (int)(fval * 281475406208040961L >>> 48) * 10000;
   }

   public static int tryToParseEightDigitsUtf8(byte[] a, int offset) {
      return tryToParseEightDigitsUtf8(readLongFromByteArrayLittleEndian(a, offset));
   }

   public static int tryToParseEightDigitsUtf8(long chunk) {
      long val = chunk - 3472328296227680304L;
      long predicate = (chunk + 5063812098665367110L | val) & -9187201950435737472L;
      if (predicate != 0L) {
         return -1;
      } else {
         val = val * 2561L >>> 8;
         val = (val & 1095216660735L) * 4294967296000100L + (val >>> 16 & 1095216660735L) * 42949672960001L >>> 32;
         return (int)val;
      }
   }

   public static long tryToParseEightHexDigitsUtf16(char[] a, int offset) {
      long first = (long)a[offset] << 48 | (long)a[offset + 1] << 32 | (long)a[offset + 2] << 16 | (long)a[offset + 3];
      long second = (long)a[offset + 4] << 48 | (long)a[offset + 5] << 32 | (long)a[offset + 6] << 16 | (long)a[offset + 7];
      return tryToParseEightHexDigitsUtf16(first, second);
   }

   public static long tryToParseEightHexDigitsUtf16(long first, long second) {
      long lfirst = tryToParseFourHexDigitsUtf16(first);
      long lsecond = tryToParseFourHexDigitsUtf16(second);
      return lfirst << 16 | lsecond;
   }

   public static long tryToParseEightHexDigitsUtf8(byte[] a, int offset) {
      return tryToParseEightHexDigitsUtf8(readLongFromByteArrayBigEndian(a, offset));
   }

   public static long tryToParseEightHexDigitsUtf8(long chunk) {
      long vec = chunk - 3472328296227680304L;
      long gt_09 = vec + 8536140394893047414L;
      gt_09 &= -9187201950435737472L;
      long ge_30 = vec + 5714873654208057167L;
      ge_30 &= -9187201950435737472L;
      long le_37 = 3978709506094217015L + (vec ^ 9187201950435737471L);
      if (gt_09 != (ge_30 & le_37)) {
         return -1L;
      } else {
         long gt_09mask = (gt_09 >>> 7) * 255L;
         long v = vec & ~gt_09mask | vec - (2821266740684990247L & gt_09mask);
         long v2 = v | v >>> 4;
         long v3 = v2 & 71777214294589695L;
         long v4 = v3 | v3 >>> 8;
         return v4 >>> 16 & 4294901760L | v4 & 65535L;
      }
   }

   public static long tryToParseFourHexDigitsUtf16(long chunk) {
      long vec = chunk - 13511005043687472L;
      long gt_09 = vec + 9220697983773212662L;
      gt_09 &= -9223231297218904064L;
      long ge_30 = vec + 9209720292175216591L;
      ge_30 &= -9223231297218904064L;
      long le_37 = 15481359945891895L + (vec ^ 9223231297218904063L);
      if (gt_09 != (ge_30 & le_37)) {
         return -1L;
      } else {
         long gt_09mask = (gt_09 >>> 15) * 65535L;
         long v = vec & ~gt_09mask | vec - (10977691597996071L & gt_09mask);
         long v2 = v | v >>> 12;
         return (v2 | v2 >>> 24) & 65535L;
      }
   }

   public static long readLongFromByteArrayLittleEndian(byte[] a, int offset) {
      return ((long)a[offset + 7] & 255L) << 56
         | ((long)a[offset + 6] & 255L) << 48
         | ((long)a[offset + 5] & 255L) << 40
         | ((long)a[offset + 4] & 255L) << 32
         | ((long)a[offset + 3] & 255L) << 24
         | ((long)a[offset + 2] & 255L) << 16
         | ((long)a[offset + 1] & 255L) << 8
         | (long)a[offset] & 255L;
   }

   public static long readLongFromByteArrayBigEndian(byte[] a, int offset) {
      return ((long)a[offset] & 255L) << 56
         | ((long)a[offset + 1] & 255L) << 48
         | ((long)a[offset + 2] & 255L) << 40
         | ((long)a[offset + 3] & 255L) << 32
         | ((long)a[offset + 4] & 255L) << 24
         | ((long)a[offset + 5] & 255L) << 16
         | ((long)a[offset + 6] & 255L) << 8
         | (long)a[offset + 7] & 255L;
   }
}
