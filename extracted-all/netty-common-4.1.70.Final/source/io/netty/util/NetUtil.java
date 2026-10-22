package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class NetUtil {
   public static final Inet4Address LOCALHOST4 = NetUtilInitializations.createLocalhost4();
   public static final Inet6Address LOCALHOST6 = NetUtilInitializations.createLocalhost6();
   public static final InetAddress LOCALHOST;
   public static final NetworkInterface LOOPBACK_IF;
   public static final int SOMAXCONN = AccessController.doPrivileged(new PrivilegedAction<Integer>() {
      public Integer run() {
         int somaxconn = PlatformDependent.isWindows() ? 200 : 128;
         File file = new File("/proc/sys/net/core/somaxconn");
         BufferedReader in = null;

         try {
            if (file.exists()) {
               in = new BufferedReader(new FileReader(file));
               somaxconn = Integer.parseInt(in.readLine());
               if (NetUtil.logger.isDebugEnabled()) {
                  NetUtil.logger.debug("{}: {}", file, somaxconn);
               }
            } else {
               Integer tmp = null;
               if (SystemPropertyUtil.getBoolean("io.netty.net.somaxconn.trySysctl", false)) {
                  tmp = NetUtil.sysctlGetInt("kern.ipc.somaxconn");
                  if (tmp == null) {
                     tmp = NetUtil.sysctlGetInt("kern.ipc.soacceptqueue");
                     if (tmp != null) {
                        somaxconn = tmp;
                     }
                  } else {
                     somaxconn = tmp;
                  }
               }

               if (tmp == null) {
                  NetUtil.logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", file, somaxconn);
               }
            }
         } catch (Exception var13) {
            if (NetUtil.logger.isDebugEnabled()) {
               NetUtil.logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", file, somaxconn, var13);
            }
         } finally {
            if (in != null) {
               try {
                  in.close();
               } catch (Exception var12) {
               }
            }
         }

         return somaxconn;
      }
   });
   private static final int IPV6_WORD_COUNT = 8;
   private static final int IPV6_MAX_CHAR_COUNT = 39;
   private static final int IPV6_BYTE_COUNT = 16;
   private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
   private static final int IPV6_MIN_SEPARATORS = 2;
   private static final int IPV6_MAX_SEPARATORS = 8;
   private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
   private static final int IPV4_SEPARATORS = 3;
   private static final boolean IPV4_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv4Stack", false);
   private static final boolean IPV6_ADDRESSES_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv6Addresses", false);
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtil.class);

   private static Integer sysctlGetInt(String sysctlKey) throws IOException {
      Process process = new ProcessBuilder("sysctl", sysctlKey).start();

      try {
         InputStream is = process.getInputStream();
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);

         try {
            String line = br.readLine();
            if (line != null && line.startsWith(sysctlKey)) {
               for (int i = line.length() - 1; i > sysctlKey.length(); i--) {
                  if (!Character.isDigit(line.charAt(i))) {
                     return Integer.valueOf(line.substring(i + 1));
                  }
               }
            }

            return null;
         } finally {
            br.close();
         }
      } finally {
         if (process != null) {
            process.destroy();
         }
      }
   }

   public static boolean isIpV4StackPreferred() {
      return IPV4_PREFERRED;
   }

   public static boolean isIpV6AddressesPreferred() {
      return IPV6_ADDRESSES_PREFERRED;
   }

   public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
      if (isValidIpV4Address(ipAddressString)) {
         return validIpV4ToBytes(ipAddressString);
      } else if (isValidIpV6Address(ipAddressString)) {
         if (ipAddressString.charAt(0) == '[') {
            ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
         }

         int percentPos = ipAddressString.indexOf(37);
         if (percentPos >= 0) {
            ipAddressString = ipAddressString.substring(0, percentPos);
         }

         return getIPv6ByName(ipAddressString, true);
      } else {
         return null;
      }
   }

   private static int decimalDigit(String str, int pos) {
      return str.charAt(pos) - 48;
   }

   private static byte ipv4WordToByte(String ip, int from, int toExclusive) {
      int ret = decimalDigit(ip, from);
      if (++from == toExclusive) {
         return (byte)ret;
      } else {
         ret = ret * 10 + decimalDigit(ip, from);
         from++;
         return from == toExclusive ? (byte)ret : (byte)(ret * 10 + decimalDigit(ip, from));
      }
   }

   static byte[] validIpV4ToBytes(String ip) {
      int i;
      int var2;
      int var3;
      return new byte[]{
         ipv4WordToByte(ip, 0, i = ip.indexOf(46, 1)),
         ipv4WordToByte(ip, i + 1, var2 = ip.indexOf(46, i + 2)),
         ipv4WordToByte(ip, var2 + 1, var3 = ip.indexOf(46, var2 + 2)),
         ipv4WordToByte(ip, var3 + 1, ip.length())
      };
   }

   public static int ipv4AddressToInt(Inet4Address ipAddress) {
      byte[] octets = ipAddress.getAddress();
      return (octets[0] & 0xFF) << 24 | (octets[1] & 0xFF) << 16 | (octets[2] & 0xFF) << 8 | octets[3] & 0xFF;
   }

   public static String intToIpAddress(int i) {
      StringBuilder buf = new StringBuilder(15);
      buf.append(i >> 24 & 0xFF);
      buf.append('.');
      buf.append(i >> 16 & 0xFF);
      buf.append('.');
      buf.append(i >> 8 & 0xFF);
      buf.append('.');
      buf.append(i & 0xFF);
      return buf.toString();
   }

   public static String bytesToIpAddress(byte[] bytes) {
      return bytesToIpAddress(bytes, 0, bytes.length);
   }

   public static String bytesToIpAddress(byte[] bytes, int offset, int length) {
      switch (length) {
         case 4:
            return new StringBuilder(15)
               .append(bytes[offset] & 255)
               .append('.')
               .append(bytes[offset + 1] & 255)
               .append('.')
               .append(bytes[offset + 2] & 255)
               .append('.')
               .append(bytes[offset + 3] & 255)
               .toString();
         case 16:
            return toAddressString(bytes, offset, false);
         default:
            throw new IllegalArgumentException("length: " + length + " (expected: 4 or 16)");
      }
   }

   public static boolean isValidIpV6Address(String ip) {
      return isValidIpV6Address((CharSequence)ip);
   }

   public static boolean isValidIpV6Address(CharSequence ip) {
      int end = ip.length();
      if (end < 2) {
         return false;
      } else {
         char c = ip.charAt(0);
         int start;
         if (c == '[') {
            if (ip.charAt(--end) != ']') {
               return false;
            }

            start = 1;
            c = ip.charAt(1);
         } else {
            start = 0;
         }

         int colons;
         int compressBegin;
         if (c == ':') {
            if (ip.charAt(start + 1) != ':') {
               return false;
            }

            colons = 2;
            compressBegin = start;
            start += 2;
         } else {
            colons = 0;
            compressBegin = -1;
         }

         int wordLen = 0;
         int i = start;

         while (true) {
            label160: {
               if (i < end) {
                  c = ip.charAt(i);
                  if (isValidHexChar(c)) {
                     if (wordLen >= 4) {
                        return false;
                     }

                     wordLen++;
                     break label160;
                  }

                  switch (c) {
                     case '%':
                        end = i;
                        break;
                     case '.':
                        if ((compressBegin >= 0 || colons == 6) && (colons != 7 || compressBegin < start) && colons <= 7) {
                           int ipv4Start = i - wordLen;
                           int j = ipv4Start - 2;
                           if (isValidIPv4MappedChar(ip.charAt(j))) {
                              if (!isValidIPv4MappedChar(ip.charAt(j - 1))
                                 || !isValidIPv4MappedChar(ip.charAt(j - 2))
                                 || !isValidIPv4MappedChar(ip.charAt(j - 3))) {
                                 return false;
                              }

                              j -= 5;
                           }

                           while (j >= start) {
                              char tmpChar = ip.charAt(j);
                              if (tmpChar != '0' && tmpChar != ':') {
                                 return false;
                              }

                              j--;
                           }

                           int ipv4End = AsciiString.indexOf(ip, '%', ipv4Start + 7);
                           if (ipv4End < 0) {
                              ipv4End = end;
                           }

                           return isValidIpV4Address(ip, ipv4Start, ipv4End);
                        }

                        return false;
                     case ':':
                        if (colons > 7) {
                           return false;
                        }

                        if (ip.charAt(i - 1) == ':') {
                           if (compressBegin >= 0) {
                              return false;
                           }

                           compressBegin = i - 1;
                        } else {
                           wordLen = 0;
                        }

                        colons++;
                        break label160;
                     default:
                        return false;
                  }
               }

               if (compressBegin < 0) {
                  return colons == 7 && wordLen > 0;
               }

               return compressBegin + 2 == end || wordLen > 0 && (colons < 8 || compressBegin <= start);
            }

            i++;
         }
      }
   }

   private static boolean isValidIpV4Word(CharSequence word, int from, int toExclusive) {
      int len = toExclusive - from;
      char c0;
      if (len < 1 || len > 3 || (c0 = word.charAt(from)) < '0') {
         return false;
      } else {
         char c1;
         char c2;
         return len == 3
            ? (c1 = word.charAt(from + 1)) >= '0'
               && (c2 = word.charAt(from + 2)) >= '0'
               && (c0 <= '1' && c1 <= '9' && c2 <= '9' || c0 == '2' && c1 <= '5' && (c2 <= '5' || c1 < '5' && c2 <= '9'))
            : c0 <= '9' && (len == 1 || isValidNumericChar(word.charAt(from + 1)));
      }
   }

   private static boolean isValidHexChar(char c) {
      return c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
   }

   private static boolean isValidNumericChar(char c) {
      return c >= '0' && c <= '9';
   }

   private static boolean isValidIPv4MappedChar(char c) {
      return c == 'f' || c == 'F';
   }

   private static boolean isValidIPv4MappedSeparators(byte b0, byte b1, boolean mustBeZero) {
      return b0 == b1 && (b0 == 0 || !mustBeZero && b1 == -1);
   }

   private static boolean isValidIPv4Mapped(byte[] bytes, int currentIndex, int compressBegin, int compressLength) {
      boolean mustBeZero = compressBegin + compressLength >= 14;
      return currentIndex <= 12
         && currentIndex >= 2
         && (!mustBeZero || compressBegin < 12)
         && isValidIPv4MappedSeparators(bytes[currentIndex - 1], bytes[currentIndex - 2], mustBeZero)
         && PlatformDependent.isZero(bytes, 0, currentIndex - 3);
   }

   public static boolean isValidIpV4Address(CharSequence ip) {
      return isValidIpV4Address(ip, 0, ip.length());
   }

   public static boolean isValidIpV4Address(String ip) {
      return isValidIpV4Address(ip, 0, ip.length());
   }

   private static boolean isValidIpV4Address(CharSequence ip, int from, int toExcluded) {
      return ip instanceof String
         ? isValidIpV4Address((String)ip, from, toExcluded)
         : (ip instanceof AsciiString ? isValidIpV4Address((AsciiString)ip, from, toExcluded) : isValidIpV4Address0(ip, from, toExcluded));
   }

   private static boolean isValidIpV4Address(String ip, int from, int toExcluded) {
      int len = toExcluded - from;
      int i;
      int var5;
      int var6;
      int var7;
      int var8;
      return len <= 15
         && len >= 7
         && (i = ip.indexOf(46, from + 1)) > 0
         && isValidIpV4Word(ip, from, i)
         && (var7 = ip.indexOf(46, var5 = i + 2)) > 0
         && isValidIpV4Word(ip, var5 - 1, var7)
         && (var8 = ip.indexOf(46, var6 = var7 + 2)) > 0
         && isValidIpV4Word(ip, var6 - 1, var8)
         && isValidIpV4Word(ip, var8 + 1, toExcluded);
   }

   private static boolean isValidIpV4Address(AsciiString ip, int from, int toExcluded) {
      int len = toExcluded - from;
      int i;
      int var5;
      int var6;
      int var7;
      int var8;
      return len <= 15
         && len >= 7
         && (i = ip.indexOf('.', from + 1)) > 0
         && isValidIpV4Word(ip, from, i)
         && (var7 = ip.indexOf('.', var5 = i + 2)) > 0
         && isValidIpV4Word(ip, var5 - 1, var7)
         && (var8 = ip.indexOf('.', var6 = var7 + 2)) > 0
         && isValidIpV4Word(ip, var6 - 1, var8)
         && isValidIpV4Word(ip, var8 + 1, toExcluded);
   }

   private static boolean isValidIpV4Address0(CharSequence ip, int from, int toExcluded) {
      int len = toExcluded - from;
      int i;
      int var5;
      int var6;
      int var7;
      int var8;
      return len <= 15
         && len >= 7
         && (i = AsciiString.indexOf(ip, '.', from + 1)) > 0
         && isValidIpV4Word(ip, from, i)
         && (var7 = AsciiString.indexOf(ip, '.', var5 = i + 2)) > 0
         && isValidIpV4Word(ip, var5 - 1, var7)
         && (var8 = AsciiString.indexOf(ip, '.', var6 = var7 + 2)) > 0
         && isValidIpV4Word(ip, var6 - 1, var8)
         && isValidIpV4Word(ip, var8 + 1, toExcluded);
   }

   public static Inet6Address getByName(CharSequence ip) {
      return getByName(ip, true);
   }

   public static Inet6Address getByName(CharSequence ip, boolean ipv4Mapped) {
      byte[] bytes = getIPv6ByName(ip, ipv4Mapped);
      if (bytes == null) {
         return null;
      } else {
         try {
            return Inet6Address.getByAddress(null, bytes, -1);
         } catch (UnknownHostException var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   private static byte[] getIPv6ByName(CharSequence ip, boolean ipv4Mapped) {
      byte[] bytes = new byte[16];
      int ipLength = ip.length();
      int compressBegin = 0;
      int compressLength = 0;
      int currentIndex = 0;
      int value = 0;
      int begin = -1;
      int i = 0;
      int ipv6Separators = 0;
      int ipv4Separators = 0;

      boolean needsShift;
      for (needsShift = false; i < ipLength; i++) {
         char c = ip.charAt(i);
         switch (c) {
            case '.':
               ipv4Separators++;
               int tmp = i - begin;
               if (tmp > 3
                  || begin < 0
                  || ipv4Separators > 3
                  || ipv6Separators > 0 && currentIndex + compressLength < 12
                  || i + 1 >= ipLength
                  || currentIndex >= bytes.length
                  || ipv4Separators == 1
                     && (
                        !ipv4Mapped
                           || currentIndex != 0 && !isValidIPv4Mapped(bytes, currentIndex, compressBegin, compressLength)
                           || tmp == 3
                              && (!isValidNumericChar(ip.charAt(i - 1)) || !isValidNumericChar(ip.charAt(i - 2)) || !isValidNumericChar(ip.charAt(i - 3)))
                           || tmp == 2 && (!isValidNumericChar(ip.charAt(i - 1)) || !isValidNumericChar(ip.charAt(i - 2)))
                           || tmp == 1 && !isValidNumericChar(ip.charAt(i - 1))
                     )) {
                  return null;
               }

               value <<= 3 - tmp << 2;
               begin = (value & 15) * 100 + (value >> 4 & 15) * 10 + (value >> 8 & 15);
               if (begin < 0 || begin > 255) {
                  return null;
               }

               bytes[currentIndex++] = (byte)begin;
               value = 0;
               begin = -1;
               break;
            case ':':
               ipv6Separators++;
               if (i - begin > 4 || ipv4Separators > 0 || ipv6Separators > 8 || currentIndex + 1 >= bytes.length) {
                  return null;
               }

               value <<= 4 - (i - begin) << 2;
               if (compressLength > 0) {
                  compressLength -= 2;
               }

               bytes[currentIndex++] = (byte)((value & 15) << 4 | value >> 4 & 15);
               bytes[currentIndex++] = (byte)((value >> 8 & 15) << 4 | value >> 12 & 15);
               int tmp = i + 1;
               if (tmp < ipLength && ip.charAt(tmp) == ':') {
                  tmp++;
                  if (compressBegin != 0 || tmp < ipLength && ip.charAt(tmp) == ':') {
                     return null;
                  }

                  ipv6Separators++;
                  needsShift = ipv6Separators == 2 && value == 0;
                  compressBegin = currentIndex;
                  compressLength = bytes.length - currentIndex - 2;
                  i++;
               }

               value = 0;
               begin = -1;
               break;
            default:
               if (!isValidHexChar(c) || ipv4Separators > 0 && !isValidNumericChar(c)) {
                  return null;
               }

               if (begin < 0) {
                  begin = i;
               } else if (i - begin > 4) {
                  return null;
               }

               value += StringUtil.decodeHexNibble(c) << (i - begin << 2);
         }
      }

      boolean isCompressed = compressBegin > 0;
      if (ipv4Separators > 0) {
         if (begin > 0 && i - begin > 3 || ipv4Separators != 3 || currentIndex >= bytes.length) {
            return null;
         }

         if (ipv6Separators == 0) {
            compressLength = 12;
         } else {
            if (ipv6Separators < 2
               || (isCompressed || ipv6Separators != 6 || ip.charAt(0) == ':')
                  && (!isCompressed || ipv6Separators >= 8 || ip.charAt(0) == ':' && compressBegin > 2)) {
               return null;
            }

            compressLength -= 2;
         }

         value <<= 3 - (i - begin) << 2;
         begin = (value & 15) * 100 + (value >> 4 & 15) * 10 + (value >> 8 & 15);
         if (begin < 0 || begin > 255) {
            return null;
         }

         bytes[currentIndex++] = (byte)begin;
      } else {
         int tmpx = ipLength - 1;
         if (begin > 0 && i - begin > 4
            || ipv6Separators < 2
            || !isCompressed && (ipv6Separators + 1 != 8 || ip.charAt(0) == ':' || ip.charAt(tmpx) == ':')
            || isCompressed
               && (ipv6Separators > 8 || ipv6Separators == 8 && (compressBegin <= 2 && ip.charAt(0) != ':' || compressBegin >= 14 && ip.charAt(tmpx) != ':'))
            || currentIndex + 1 >= bytes.length
            || begin < 0 && ip.charAt(tmpx - 1) != ':'
            || compressBegin > 2 && ip.charAt(0) == ':') {
            return null;
         }

         if (begin >= 0 && i - begin <= 4) {
            value <<= 4 - (i - begin) << 2;
         }

         bytes[currentIndex++] = (byte)((value & 15) << 4 | value >> 4 & 15);
         bytes[currentIndex++] = (byte)((value >> 8 & 15) << 4 | value >> 12 & 15);
      }

      i = currentIndex + compressLength;
      if (!needsShift && i < bytes.length) {
         for (int var28 = 0; var28 < compressLength; var28++) {
            begin = var28 + compressBegin;
            currentIndex = begin + compressLength;
            if (currentIndex >= bytes.length) {
               break;
            }

            bytes[currentIndex] = bytes[begin];
            bytes[begin] = 0;
         }
      } else {
         if (i >= bytes.length) {
            compressBegin++;
         }

         for (int var27 = currentIndex; var27 < bytes.length; var27++) {
            for (begin = bytes.length - 1; begin >= compressBegin; begin--) {
               bytes[begin] = bytes[begin - 1];
            }

            bytes[begin] = 0;
            compressBegin++;
         }
      }

      if (ipv4Separators > 0) {
         bytes[10] = bytes[11] = -1;
      }

      return bytes;
   }

   public static String toSocketAddressString(InetSocketAddress addr) {
      String port = String.valueOf(addr.getPort());
      StringBuilder sb;
      if (addr.isUnresolved()) {
         String hostname = getHostname(addr);
         sb = newSocketAddressStringBuilder(hostname, port, !isValidIpV6Address(hostname));
      } else {
         InetAddress address = addr.getAddress();
         String hostString = toAddressString(address);
         sb = newSocketAddressStringBuilder(hostString, port, address instanceof Inet4Address);
      }

      return sb.append(':').append(port).toString();
   }

   public static String toSocketAddressString(String host, int port) {
      String portStr = String.valueOf(port);
      return newSocketAddressStringBuilder(host, portStr, !isValidIpV6Address(host)).append(':').append(portStr).toString();
   }

   private static StringBuilder newSocketAddressStringBuilder(String host, String port, boolean ipv4) {
      int hostLen = host.length();
      if (ipv4) {
         return new StringBuilder(hostLen + 1 + port.length()).append(host);
      } else {
         StringBuilder stringBuilder = new StringBuilder(hostLen + 3 + port.length());
         return hostLen > 1 && host.charAt(0) == '[' && host.charAt(hostLen - 1) == ']'
            ? stringBuilder.append(host)
            : stringBuilder.append('[').append(host).append(']');
      }
   }

   public static String toAddressString(InetAddress ip) {
      return toAddressString(ip, false);
   }

   public static String toAddressString(InetAddress ip, boolean ipv4Mapped) {
      if (ip instanceof Inet4Address) {
         return ip.getHostAddress();
      } else if (!(ip instanceof Inet6Address)) {
         throw new IllegalArgumentException("Unhandled type: " + ip);
      } else {
         return toAddressString(ip.getAddress(), 0, ipv4Mapped);
      }
   }

   private static String toAddressString(byte[] bytes, int offset, boolean ipv4Mapped) {
      int[] words = new int[8];
      int end = offset + words.length;

      for (int i = offset; i < end; i++) {
         words[i] = (bytes[i << 1] & 255) << 8 | bytes[(i << 1) + 1] & 255;
      }

      int currentStart = -1;
      int shortestStart = -1;
      int shortestLength = 0;

      int var13;
      for (var13 = 0; var13 < words.length; var13++) {
         if (words[var13] == 0) {
            if (currentStart < 0) {
               currentStart = var13;
            }
         } else if (currentStart >= 0) {
            int currentLength = var13 - currentStart;
            if (currentLength > shortestLength) {
               shortestStart = currentStart;
               shortestLength = currentLength;
            }

            currentStart = -1;
         }
      }

      if (currentStart >= 0) {
         int currentLength = var13 - currentStart;
         if (currentLength > shortestLength) {
            shortestStart = currentStart;
            shortestLength = currentLength;
         }
      }

      if (shortestLength == 1) {
         shortestLength = 0;
         shortestStart = -1;
      }

      int shortestEnd = shortestStart + shortestLength;
      StringBuilder b = new StringBuilder(39);
      if (shortestEnd < 0) {
         b.append(Integer.toHexString(words[0]));

         for (int var14 = 1; var14 < words.length; var14++) {
            b.append(':');
            b.append(Integer.toHexString(words[var14]));
         }
      } else {
         boolean isIpv4Mapped;
         if (!inRangeEndExclusive(0, shortestStart, shortestEnd)) {
            b.append(Integer.toHexString(words[0]));
            isIpv4Mapped = false;
         } else {
            b.append("::");
            isIpv4Mapped = ipv4Mapped && shortestEnd == 5 && words[5] == 65535;
         }

         for (int var15 = 1; var15 < words.length; var15++) {
            if (!inRangeEndExclusive(var15, shortestStart, shortestEnd)) {
               if (!inRangeEndExclusive(var15 - 1, shortestStart, shortestEnd)) {
                  if (isIpv4Mapped && var15 != 6) {
                     b.append('.');
                  } else {
                     b.append(':');
                  }
               }

               if (isIpv4Mapped && var15 > 5) {
                  b.append(words[var15] >> 8);
                  b.append('.');
                  b.append(words[var15] & 0xFF);
               } else {
                  b.append(Integer.toHexString(words[var15]));
               }
            } else if (!inRangeEndExclusive(var15 - 1, shortestStart, shortestEnd)) {
               b.append("::");
            }
         }
      }

      return b.toString();
   }

   public static String getHostname(InetSocketAddress addr) {
      return PlatformDependent.javaVersion() >= 7 ? addr.getHostString() : addr.getHostName();
   }

   private static boolean inRangeEndExclusive(int value, int start, int end) {
      return value >= start && value < end;
   }

   private NetUtil() {
   }

   static {
      logger.debug("-Djava.net.preferIPv4Stack: {}", IPV4_PREFERRED);
      logger.debug("-Djava.net.preferIPv6Addresses: {}", IPV6_ADDRESSES_PREFERRED);
      NetUtilInitializations.NetworkIfaceAndInetAddress loopback = NetUtilInitializations.determineLoopback(LOCALHOST4, LOCALHOST6);
      LOOPBACK_IF = loopback.iface();
      LOCALHOST = loopback.address();
   }
}
