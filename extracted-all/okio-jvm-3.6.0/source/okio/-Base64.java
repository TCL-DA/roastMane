@file:JvmName(name = "-Base64")

package okio

import java.util.Arrays

internal final val BASE64: ByteArray = ByteString.Companion.encodeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/").getData$okio()
internal final val BASE64_URL_SAFE: ByteArray =
   ByteString.Companion.encodeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_").getData$okio()

/** @deprecated */
@JvmSynthetic
fun `getBASE64$annotations`() {
}

/** @deprecated */
@JvmSynthetic
fun `getBASE64_URL_SAFE$annotations`() {
}

internal fun String.decodeBase64ToArray(): ByteArray? {
   var limit: Int;
   for (limit = $this$decodeBase64ToArray.length(); limit > 0; limit--) {
      var out: Char = `$this$decodeBase64ToArray`.charAt(limit - 1);
      if (out != '=' && out != '\n' && out != '\r' && out != ' ' && out != '\t') {
         break;
      }
   }

   var var10: ByteArray = new byte[(int)((long)limit * 6L / 8L)];
   var outCount: Int = 0;
   var inCount: Int = 0;
   var word: Int = 0;
   var lastWordChars: Int = 0;

   for (int var7 = limit; pos < var7; pos++) {
      var c: Char = `$this$decodeBase64ToArray`.charAt(lastWordChars);
      var var17: Int;
      if ('A' <= c && c < '[') {
         var17 = c - 'A';
      } else if ('a' <= c && c < '{') {
         var17 = c - 'G';
      } else if ('0' <= c && c < ':') {
         var17 = c + 4;
      } else if (c != '+' && c != '-') {
         if (c != '/' && c != '_') {
            if (c != '\n' && c != '\r' && c != ' ' && c != '\t') {
               return null;
            }
            continue;
         }

         var17 = 63;
      } else {
         var17 = 62;
      }

      word = word shl 6 or var17;
      if (++inCount % 4 == 0) {
         var10[outCount++] = (byte)(word shr 16);
         var10[outCount++] = (byte)(word shr 8);
         var10[outCount++] = (byte)word;
      }
   }

   switch (inCount % 4) {
      case 1:
         return null;
      case 2:
         var10[outCount++] = (byte)((word shl 12) shr 16);
         break;
      case 3:
         var10[outCount++] = (byte)((word shl 6) shr 16);
         var10[outCount++] = (byte)((word shl 6) shr 8);
      default:
   }

   if (outCount == var10.length) {
      return var10;
   } else {
      var var10000: ByteArray = Arrays.copyOf(var10, outCount);
      return var10000;
   }
}

internal fun ByteArray.encodeBase64(map: ByteArray): String {
   var out: ByteArray = new byte[(`$this$encodeBase64`.length + 2) / 3 * 4];
   var index: Int = 0;
   var end: Int = `$this$encodeBase64`.length - `$this$encodeBase64`.length % 3;
   var i: Int = 0;

   while (i < end) {
      var b0: Int = `$this$encodeBase64`[i++];
      var b0x: Int = `$this$encodeBase64`[i++];
      var b1: Int = `$this$encodeBase64`[i++];
      out[index++] = map[(b0 and 255) shr 2];
      out[index++] = map[(b0 and 3) shl 4 or (b0x and 255) shr 4];
      out[index++] = map[(b0x and 15) shl 2 or (b1 and 255) shr 6];
      out[index++] = map[b1 and 63];
   }

   switch ($this$encodeBase64.length - end) {
      case 1:
         out[index++] = map[(`$this$encodeBase64`[i] and 255) shr 2];
         out[index++] = map[(`$this$encodeBase64`[i] and 3) shl 4];
         out[index++] = 61;
         out[index] = 61;
         break;
      case 2:
         var var22: Int = `$this$encodeBase64`[i++];
         var var24: Int = `$this$encodeBase64`[i];
         out[index++] = map[(var22 and 255) shr 2];
         out[index++] = map[(var22 and 3) shl 4 or (var24 and 255) shr 4];
         out[index++] = map[(var24 and 15) shl 2];
         out[index] = 61;
      default:
   }

   return _JvmPlatformKt.toUtf8String(out);
}

@JvmSynthetic
fun `encodeBase64$default`(var0: ByteArray, var1: ByteArray, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = BASE64;
   }

   return encodeBase64(var0, var1);
}

