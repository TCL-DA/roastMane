package com.ghgande.j2mod.modbus.util;

import com.ghgande.j2mod.modbus.io.BytesOutputStream;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusUtil {
   private static final Logger logger = LoggerFactory.getLogger(ModbusUtil.class);
   private static final short[] auchCRCHi = new short[]{
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64,
      1,
      192,
      128,
      65,
      1,
      192,
      128,
      65,
      0,
      193,
      129,
      64
   };
   private static final short[] auchCRCLo = new short[]{
      0,
      192,
      193,
      1,
      195,
      3,
      2,
      194,
      198,
      6,
      7,
      199,
      5,
      197,
      196,
      4,
      204,
      12,
      13,
      205,
      15,
      207,
      206,
      14,
      10,
      202,
      203,
      11,
      201,
      9,
      8,
      200,
      216,
      24,
      25,
      217,
      27,
      219,
      218,
      26,
      30,
      222,
      223,
      31,
      221,
      29,
      28,
      220,
      20,
      212,
      213,
      21,
      215,
      23,
      22,
      214,
      210,
      18,
      19,
      211,
      17,
      209,
      208,
      16,
      240,
      48,
      49,
      241,
      51,
      243,
      242,
      50,
      54,
      246,
      247,
      55,
      245,
      53,
      52,
      244,
      60,
      252,
      253,
      61,
      255,
      63,
      62,
      254,
      250,
      58,
      59,
      251,
      57,
      249,
      248,
      56,
      40,
      232,
      233,
      41,
      235,
      43,
      42,
      234,
      238,
      46,
      47,
      239,
      45,
      237,
      236,
      44,
      228,
      36,
      37,
      229,
      39,
      231,
      230,
      38,
      34,
      226,
      227,
      35,
      225,
      33,
      32,
      224,
      160,
      96,
      97,
      161,
      99,
      163,
      162,
      98,
      102,
      166,
      167,
      103,
      165,
      101,
      100,
      164,
      108,
      172,
      173,
      109,
      175,
      111,
      110,
      174,
      170,
      106,
      107,
      171,
      105,
      169,
      168,
      104,
      120,
      184,
      185,
      121,
      187,
      123,
      122,
      186,
      190,
      126,
      127,
      191,
      125,
      189,
      188,
      124,
      180,
      116,
      117,
      181,
      119,
      183,
      182,
      118,
      114,
      178,
      179,
      115,
      177,
      113,
      112,
      176,
      80,
      144,
      145,
      81,
      147,
      83,
      82,
      146,
      150,
      86,
      87,
      151,
      85,
      149,
      148,
      84,
      156,
      92,
      93,
      157,
      95,
      159,
      158,
      94,
      90,
      154,
      155,
      91,
      153,
      89,
      88,
      152,
      136,
      72,
      73,
      137,
      75,
      139,
      138,
      74,
      78,
      142,
      143,
      79,
      141,
      77,
      76,
      140,
      68,
      132,
      133,
      69,
      135,
      71,
      70,
      134,
      130,
      66,
      67,
      131,
      65,
      129,
      128,
      64
   };

   private ModbusUtil() {
   }

   public static String toHex(ModbusMessage msg) {
      BytesOutputStream byteOutputStream = new BytesOutputStream(256);
      String ret = "-1";

      try {
         msg.writeTo(byteOutputStream);
         ret = toHex(byteOutputStream.getBuffer(), 0, byteOutputStream.size());
      } catch (IOException var4) {
         logger.debug("Hex conversion error {}", var4);
      }

      return ret;
   }

   public static String toHex(byte[] data) {
      return toHex(data, 0, data.length);
   }

   public static String toHex(byte[] data, int off, int end) {
      StringBuilder buf = new StringBuilder(data.length * 2);
      if (end > data.length) {
         end = data.length;
      }

      for (int i = off; i < end; i++) {
         if ((data[i] & 255) < 16) {
            buf.append("0");
         }

         buf.append(Long.toString((long)(data[i] & 255), 16).toUpperCase());
         if (i < end - 1) {
            buf.append(" ");
         }
      }

      return buf.toString();
   }

   public static byte[] toHex(int i) {
      StringBuilder buf = new StringBuilder(2);
      if ((i & 0xFF) < 16) {
         buf.append("0");
      }

      buf.append(Long.toString((long)(i & 0xFF), 16).toUpperCase());

      try {
         return buf.toString().getBytes("US-ASCII");
      } catch (Exception var3) {
         logger.debug("Problem converting bytes to string - {}", var3.getMessage());
         return null;
      }
   }

   public static int registerToUnsignedShort(byte[] bytes) {
      return (bytes[0] & 0xFF) << 8 | bytes[1] & 0xFF;
   }

   public static byte[] unsignedShortToRegister(int v) {
      return new byte[]{(byte)(0xFF & v >> 8), (byte)(0xFF & v)};
   }

   public static short registerToShort(byte[] bytes) {
      return (short)(bytes[0] << 8 | bytes[1] & 255);
   }

   public static short registerToShort(byte[] bytes, int idx) {
      return (short)(bytes[idx] << 8 | bytes[idx + 1] & 255);
   }

   public static byte[] shortToRegister(short s) {
      return new byte[]{(byte)(0xFF & s >> 8), (byte)(255 & s)};
   }

   public static int registersToInt(byte[] bytes) {
      return (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | bytes[3] & 0xFF;
   }

   public static byte[] intToRegisters(int v) {
      return new byte[]{(byte)(0xFF & v >> 24), (byte)(0xFF & v >> 16), (byte)(0xFF & v >> 8), (byte)(0xFF & v)};
   }

   public static long registersToLong(byte[] bytes) {
      return (long)(bytes[0] & 255) << 56
         | (long)(bytes[1] & 255) << 48
         | (long)(bytes[2] & 255) << 40
         | (long)(bytes[3] & 255) << 32
         | (long)(bytes[4] & 255) << 24
         | (long)(bytes[5] & 255) << 16
         | (long)(bytes[6] & 255) << 8
         | (long)(bytes[7] & 255);
   }

   public static byte[] longToRegisters(long v) {
      return new byte[]{
         (byte)((int)(255L & v >> 56)),
         (byte)((int)(255L & v >> 48)),
         (byte)((int)(255L & v >> 40)),
         (byte)((int)(255L & v >> 32)),
         (byte)((int)(255L & v >> 24)),
         (byte)((int)(255L & v >> 16)),
         (byte)((int)(255L & v >> 8)),
         (byte)((int)(255L & v))
      };
   }

   public static float registersToFloat(byte[] bytes) {
      return Float.intBitsToFloat((bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255);
   }

   public static byte[] floatToRegisters(float f) {
      return intToRegisters(Float.floatToIntBits(f));
   }

   public static double registersToDouble(byte[] bytes) {
      return Double.longBitsToDouble(
         (long)(bytes[0] & 255) << 56
            | (long)(bytes[1] & 255) << 48
            | (long)(bytes[2] & 255) << 40
            | (long)(bytes[3] & 255) << 32
            | (long)(bytes[4] & 255) << 24
            | (long)(bytes[5] & 255) << 16
            | (long)(bytes[6] & 255) << 8
            | (long)(bytes[7] & 255)
      );
   }

   public static byte[] doubleToRegisters(double d) {
      return longToRegisters(Double.doubleToLongBits(d));
   }

   public static int unsignedByteToInt(byte b) {
      return b & 0xFF;
   }

   public static byte lowByte(int wd) {
      return Integer.valueOf(0xFF & wd).byteValue();
   }

   public static byte hiByte(int wd) {
      return Integer.valueOf(0xFF & wd >> 8).byteValue();
   }

   public static int makeWord(int hibyte, int lowbyte) {
      int hi = 0xFF & hibyte;
      int low = 0xFF & lowbyte;
      return hi << 8 | low;
   }

   public static int[] calculateCRC(byte[] data, int offset, int len) {
      int[] crc = new int[]{255, 255};

      for (int i = offset; i < len && i < data.length; i++) {
         int nextByte = 255 & data[i];
         int uIndex = crc[0] ^ nextByte;
         crc[0] = crc[1] ^ auchCRCHi[uIndex];
         crc[1] = auchCRCLo[uIndex];
      }

      return crc;
   }

   public static boolean isBlank(String value) {
      return value == null || value.isEmpty();
   }

   public static boolean isBlank(List<Object> list) {
      return list == null || list.isEmpty();
   }

   public static boolean isBlank(Object[] list) {
      return list == null || list.length == 0;
   }

   public static void sleep(long time) {
      try {
         Thread.sleep(time);
      } catch (InterruptedException var3) {
         logger.warn("Backout sleep timer has been interrupted");
      }
   }
}
