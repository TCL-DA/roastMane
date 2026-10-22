package org.glassfish.jersey.internal.guava;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public final class InetAddresses {
   private static final int IPV4_PART_COUNT = 4;
   private static final int IPV6_PART_COUNT = 8;

   private InetAddresses() {
   }

   private static byte[] ipStringToBytes(String ipString) {
      boolean hasColon = false;
      boolean hasDot = false;

      for (int i = 0; i < ipString.length(); i++) {
         char c = ipString.charAt(i);
         if (c == '.') {
            hasDot = true;
         } else if (c == ':') {
            if (hasDot) {
               return null;
            }

            hasColon = true;
         } else if (Character.digit(c, 16) == -1) {
            return null;
         }
      }

      if (hasColon) {
         if (hasDot) {
            ipString = convertDottedQuadToHex(ipString);
            if (ipString == null) {
               return null;
            }
         }

         return textToNumericFormatV6(ipString);
      } else {
         return hasDot ? textToNumericFormatV4(ipString) : null;
      }
   }

   private static byte[] textToNumericFormatV4(String ipString) {
      String[] address = ipString.split("\\.", 5);
      if (address.length != 4) {
         return null;
      } else {
         byte[] bytes = new byte[4];

         try {
            for (int i = 0; i < bytes.length; i++) {
               bytes[i] = parseOctet(address[i]);
            }

            return bytes;
         } catch (NumberFormatException var4) {
            return null;
         }
      }
   }

   private static byte[] textToNumericFormatV6(String ipString) {
      String[] parts = ipString.split(":", 10);
      if (parts.length >= 3 && parts.length <= 9) {
         int skipIndex = -1;

         for (int i = 1; i < parts.length - 1; i++) {
            if (parts[i].length() == 0) {
               if (skipIndex >= 0) {
                  return null;
               }

               skipIndex = i;
            }
         }

         int partsLo;
         int partsHi;
         if (skipIndex >= 0) {
            partsHi = skipIndex;
            partsLo = parts.length - skipIndex - 1;
            if (parts[0].length() == 0) {
               partsHi = skipIndex - 1;
               if (partsHi != 0) {
                  return null;
               }
            }

            if (parts[parts.length - 1].length() == 0) {
               if (--partsLo != 0) {
                  return null;
               }
            }
         } else {
            partsHi = parts.length;
            partsLo = 0;
         }

         int partsSkipped = 8 - (partsHi + partsLo);
         if (skipIndex >= 0 ? partsSkipped >= 1 : partsSkipped == 0) {
            ByteBuffer rawBytes = ByteBuffer.allocate(16);

            try {
               for (int ix = 0; ix < partsHi; ix++) {
                  rawBytes.putShort(parseHextet(parts[ix]));
               }

               for (int ix = 0; ix < partsSkipped; ix++) {
                  rawBytes.putShort((short)0);
               }

               for (int ix = partsLo; ix > 0; ix--) {
                  rawBytes.putShort(parseHextet(parts[parts.length - ix]));
               }
            } catch (NumberFormatException var8) {
               return null;
            }

            return rawBytes.array();
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String convertDottedQuadToHex(String ipString) {
      int lastColon = ipString.lastIndexOf(58);
      String initialPart = ipString.substring(0, lastColon + 1);
      String dottedQuad = ipString.substring(lastColon + 1);
      byte[] quad = textToNumericFormatV4(dottedQuad);
      if (quad == null) {
         return null;
      } else {
         String penultimate = Integer.toHexString((quad[0] & 255) << 8 | quad[1] & 255);
         String ultimate = Integer.toHexString((quad[2] & 255) << 8 | quad[3] & 255);
         return initialPart + penultimate + ":" + ultimate;
      }
   }

   private static byte parseOctet(String ipPart) {
      int octet = Integer.parseInt(ipPart);
      if (octet <= 255 && (!ipPart.startsWith("0") || ipPart.length() <= 1)) {
         return (byte)octet;
      } else {
         throw new NumberFormatException();
      }
   }

   private static short parseHextet(String ipPart) {
      int hextet = Integer.parseInt(ipPart, 16);
      if (hextet > 65535) {
         throw new NumberFormatException();
      } else {
         return (short)hextet;
      }
   }

   private static InetAddress bytesToInetAddress(byte[] addr) {
      try {
         return InetAddress.getByAddress(addr);
      } catch (UnknownHostException var2) {
         throw new AssertionError(var2);
      }
   }

   private static InetAddress forUriStringNoThrow(String hostAddr) {
      Preconditions.checkNotNull(hostAddr);
      String ipString;
      int expectBytes;
      if (hostAddr.startsWith("[") && hostAddr.endsWith("]")) {
         ipString = hostAddr.substring(1, hostAddr.length() - 1);
         expectBytes = 16;
      } else {
         ipString = hostAddr;
         expectBytes = 4;
      }

      byte[] addr = ipStringToBytes(ipString);
      return addr != null && addr.length == expectBytes ? bytesToInetAddress(addr) : null;
   }

   public static boolean isUriInetAddress(String ipString) {
      return forUriStringNoThrow(ipString) != null;
   }

   public static boolean isMappedIPv4Address(String ipString) {
      byte[] bytes = ipStringToBytes(ipString);
      if (bytes != null && bytes.length == 16) {
         for (int i = 0; i < 10; i++) {
            if (bytes[i] != 0) {
               return false;
            }
         }

         for (int ix = 10; ix < 12; ix++) {
            if (bytes[ix] != -1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
