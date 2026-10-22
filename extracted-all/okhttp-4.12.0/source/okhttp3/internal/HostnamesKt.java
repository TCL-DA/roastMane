package okhttp3.internal

import java.net.IDN
import java.net.InetAddress
import java.util.Arrays
import java.util.Locale
import kotlin.jvm.internal.Intrinsics
import okio.Buffer

public fun String.toCanonicalHost(): String? {
   var host: java.lang.String = `$this$toCanonicalHost`;
   if (!StringsKt.contains$default(`$this$toCanonicalHost`, ":", false, 2, null)) {
      try {
         var var7: java.lang.String = IDN.toASCII(host);
         var var8: Locale = Locale.US;
         var7 = var7.toLowerCase(var8);
         if (var7.length() == 0) {
            return null;
         } else {
            return if (containsInvalidHostnameAsciiCodes(var7)) null else var7;
         }
      } catch (IllegalArgumentException var4) {
         return null;
      }
   } else {
      var var10000: InetAddress = if (StringsKt.startsWith$default(`$this$toCanonicalHost`, "[", false, 2, null)
            && StringsKt.endsWith$default(`$this$toCanonicalHost`, "]", false, 2, null))
         decodeIpv6(`$this$toCanonicalHost`, 1, `$this$toCanonicalHost`.length() - 1)
         else
         decodeIpv6(`$this$toCanonicalHost`, 0, `$this$toCanonicalHost`.length());
      if (var10000 == null) {
         return null;
      } else {
         var address: ByteArray = var10000.getAddress();
         if (address.length == 16) {
            return inet6AddressToAscii(address);
         } else if (address.length == 4) {
            return var10000.getHostAddress();
         } else {
            throw new AssertionError("Invalid IPv6 address: '" + `$this$toCanonicalHost` + '\'');
         }
      }
   }
}

private fun String.containsInvalidHostnameAsciiCodes(): Boolean {
   var i: Int = 0;

   for (int var2 = $this$containsInvalidHostnameAsciiCodes.length(); i < var2; i++) {
      var c: Char = `$this$containsInvalidHostnameAsciiCodes`.charAt(i);
      if (Intrinsics.compare(c, 31) <= 0 || Intrinsics.compare(c, 127) >= 0) {
         return true;
      }

      if (StringsKt.indexOf$default(" #%/:?@[\\]", c, 0, false, 6, null) != -1) {
         return true;
      }
   }

   return false;
}

private fun decodeIpv6(input: String, pos: Int, limit: Int): InetAddress? {
   var address: ByteArray = new byte[16];
   var b: Int = 0;
   var compress: Int = -1;
   var groupOffset: Int = -1;
   var i: Int = pos;

   while (i < limit) {
      if (b == address.length) {
         return null;
      }

      if (i + 2 <= limit && StringsKt.startsWith$default(input, "::", i, false, 4, null)) {
         if (compress != -1) {
            return null;
         }

         i += 2;
         b += 2;
         compress = b;
         if (i == limit) {
            break;
         }
      } else if (b != 0) {
         if (!StringsKt.startsWith$default(input, ":", i, false, 4, null)) {
            if (!StringsKt.startsWith$default(input, ".", i, false, 4, null)) {
               return null;
            }

            if (!decodeIpv4Suffix(input, groupOffset, limit, address, b - 2)) {
               return null;
            }

            b += 2;
            break;
         }

         i++;
      }

      var value: Int = 0;

      for (groupOffset = i; i < limit; i++) {
         var groupLength: Int = Util.parseHexDigit(input.charAt(i));
         if (groupLength == -1) {
            break;
         }

         value = (value shl 4) + groupLength;
      }

      if (i - groupOffset == 0 || i - groupOffset > 4) {
         return null;
      }

      address[b++] = (byte)(value ushr 8 and 255);
      address[b++] = (byte)(value and 255);
   }

   if (b != address.length) {
      if (compress == -1) {
         return null;
      }

      System.arraycopy(address, compress, address, address.length - (b - compress), b - compress);
      Arrays.fill(address, compress, compress + (address.length - b), (byte)0);
   }

   return InetAddress.getByAddress(address);
}

private fun decodeIpv4Suffix(input: String, pos: Int, limit: Int, address: ByteArray, addressOffset: Int): Boolean {
   var b: Int = addressOffset;
   var i: Int = pos;

   while (i < limit) {
      if (b == address.length) {
         return false;
      }

      if (b != addressOffset) {
         if (input.charAt(i) != '.') {
            return false;
         }

         i++;
      }

      var value: Int = 0;

      var groupOffset: Int;
      for (groupOffset = i; i < limit; i++) {
         var groupLength: Char = input.charAt(i);
         if (Intrinsics.compare(groupLength, 48) < 0 || Intrinsics.compare(groupLength, 57) > 0) {
            break;
         }

         if (value == 0 && groupOffset != i) {
            return false;
         }

         value = value * 10 + groupLength - 48;
         if (value > 255) {
            return false;
         }
      }

      if (i - groupOffset == 0) {
         return false;
      }

      address[b++] = (byte)value;
   }

   return b == addressOffset + 4;
}

private fun inet6AddressToAscii(address: ByteArray): String {
   var var7: Int = -1;
   var longestRunLength: Int = 0;

   for (int i = 0; i < address.length; i += 2) {
      var group: Int = i;

      while (i < 16 && address[i] == 0 && address[i + 1] == 0) {
         i += 2;
      }

      var currentRunLength: Int = i - group;
      if (i - group > longestRunLength && i - group >= 4) {
         var7 = group;
         longestRunLength = currentRunLength;
      }
   }

   var var8: Buffer = new Buffer();
   var var9: Int = 0;

   while (i < address.length) {
      if (var9 == var7) {
         var8.writeByte(58);
         var9 += longestRunLength;
         if (var9 == 16) {
            var8.writeByte(58);
         }
      } else {
         if (var9 > 0) {
            var8.writeByte(58);
         }

         var8.writeHexadecimalUnsignedLong((long)(Util.and(address[var9], 255) shl 8 or Util.and(address[var9 + 1], 255)));
         var9 += 2;
      }
   }

   return var8.readUtf8();
}

