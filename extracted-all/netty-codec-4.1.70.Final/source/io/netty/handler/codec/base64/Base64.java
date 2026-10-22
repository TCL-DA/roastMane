package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteOrder;

public final class Base64 {
   private static final int MAX_LINE_LENGTH = 76;
   private static final byte EQUALS_SIGN = 61;
   private static final byte NEW_LINE = 10;
   private static final byte WHITE_SPACE_ENC = -5;
   private static final byte EQUALS_SIGN_ENC = -1;

   private static byte[] alphabet(Base64Dialect dialect) {
      return ((Base64Dialect)ObjectUtil.checkNotNull(dialect, "dialect")).alphabet;
   }

   private static byte[] decodabet(Base64Dialect dialect) {
      return ((Base64Dialect)ObjectUtil.checkNotNull(dialect, "dialect")).decodabet;
   }

   private static boolean breakLines(Base64Dialect dialect) {
      return ((Base64Dialect)ObjectUtil.checkNotNull(dialect, "dialect")).breakLinesByDefault;
   }

   public static ByteBuf encode(ByteBuf src) {
      return encode(src, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf src, Base64Dialect dialect) {
      return encode(src, breakLines(dialect), dialect);
   }

   public static ByteBuf encode(ByteBuf src, boolean breakLines) {
      return encode(src, breakLines, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf src, boolean breakLines, Base64Dialect dialect) {
      ObjectUtil.checkNotNull(src, "src");
      ByteBuf dest = encode(src, src.readerIndex(), src.readableBytes(), breakLines, dialect);
      src.readerIndex(src.writerIndex());
      return dest;
   }

   public static ByteBuf encode(ByteBuf src, int off, int len) {
      return encode(src, off, len, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf src, int off, int len, Base64Dialect dialect) {
      return encode(src, off, len, breakLines(dialect), dialect);
   }

   public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines) {
      return encode(src, off, len, breakLines, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect) {
      return encode(src, off, len, breakLines, dialect, src.alloc());
   }

   public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect, ByteBufAllocator allocator) {
      ObjectUtil.checkNotNull(src, "src");
      ObjectUtil.checkNotNull(dialect, "dialect");
      ByteBuf dest = allocator.buffer(encodedBufferSize(len, breakLines)).order(src.order());
      byte[] alphabet = alphabet(dialect);
      int d = 0;
      int e = 0;
      int len2 = len - 2;

      for (int lineLength = 0; d < len2; e += 4) {
         encode3to4(src, d + off, 3, dest, e, alphabet);
         lineLength += 4;
         if (breakLines && lineLength == 76) {
            dest.setByte(e + 4, 10);
            e++;
            lineLength = 0;
         }

         d += 3;
      }

      if (d < len) {
         encode3to4(src, d + off, len - d, dest, e, alphabet);
         e += 4;
      }

      if (e > 1 && dest.getByte(e - 1) == 10) {
         e--;
      }

      return dest.slice(0, e);
   }

   private static void encode3to4(ByteBuf src, int srcOffset, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
      if (src.order() == ByteOrder.BIG_ENDIAN) {
         int inBuff;
         switch (numSigBytes) {
            case 1:
               inBuff = toInt(src.getByte(srcOffset));
               break;
            case 2:
               inBuff = toIntBE(src.getShort(srcOffset));
               break;
            default:
               inBuff = numSigBytes <= 0 ? 0 : toIntBE(src.getMedium(srcOffset));
         }

         encode3to4BigEndian(inBuff, numSigBytes, dest, destOffset, alphabet);
      } else {
         int inBuff;
         switch (numSigBytes) {
            case 1:
               inBuff = toInt(src.getByte(srcOffset));
               break;
            case 2:
               inBuff = toIntLE(src.getShort(srcOffset));
               break;
            default:
               inBuff = numSigBytes <= 0 ? 0 : toIntLE(src.getMedium(srcOffset));
         }

         encode3to4LittleEndian(inBuff, numSigBytes, dest, destOffset, alphabet);
      }
   }

   static int encodedBufferSize(int len, boolean breakLines) {
      long len43 = ((long)len << 2) / 3L;
      long ret = len43 + 3L & -4L;
      if (breakLines) {
         ret += len43 / 76L;
      }

      return ret < 2147483647L ? (int)ret : Integer.MAX_VALUE;
   }

   private static int toInt(byte value) {
      return (value & 0xFF) << 16;
   }

   private static int toIntBE(short value) {
      return (value & 0xFF00) << 8 | (value & 0xFF) << 8;
   }

   private static int toIntLE(short value) {
      return (value & 0xFF) << 16 | value & 0xFF00;
   }

   private static int toIntBE(int mediumValue) {
      return mediumValue & 0xFF0000 | mediumValue & 0xFF00 | mediumValue & 0xFF;
   }

   private static int toIntLE(int mediumValue) {
      return (mediumValue & 0xFF) << 16 | mediumValue & 0xFF00 | (mediumValue & 0xFF0000) >>> 16;
   }

   private static void encode3to4BigEndian(int inBuff, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
      switch (numSigBytes) {
         case 1:
            dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 63] << 16 | 15616 | 61);
            break;
         case 2:
            dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 63] << 16 | alphabet[inBuff >>> 6 & 63] << 8 | 61);
            break;
         case 3:
            dest.setInt(
               destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 63] << 16 | alphabet[inBuff >>> 6 & 63] << 8 | alphabet[inBuff & 63]
            );
      }
   }

   private static void encode3to4LittleEndian(int inBuff, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
      switch (numSigBytes) {
         case 1:
            dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 63] << 8 | 3997696 | 1023410176);
            break;
         case 2:
            dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 63] << 8 | alphabet[inBuff >>> 6 & 63] << 16 | 1023410176);
            break;
         case 3:
            dest.setInt(
               destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 63] << 8 | alphabet[inBuff >>> 6 & 63] << 16 | alphabet[inBuff & 63] << 24
            );
      }
   }

   public static ByteBuf decode(ByteBuf src) {
      return decode(src, Base64Dialect.STANDARD);
   }

   public static ByteBuf decode(ByteBuf src, Base64Dialect dialect) {
      ObjectUtil.checkNotNull(src, "src");
      ByteBuf dest = decode(src, src.readerIndex(), src.readableBytes(), dialect);
      src.readerIndex(src.writerIndex());
      return dest;
   }

   public static ByteBuf decode(ByteBuf src, int off, int len) {
      return decode(src, off, len, Base64Dialect.STANDARD);
   }

   public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect) {
      return decode(src, off, len, dialect, src.alloc());
   }

   public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect, ByteBufAllocator allocator) {
      ObjectUtil.checkNotNull(src, "src");
      ObjectUtil.checkNotNull(dialect, "dialect");
      return new Base64.Decoder().decode(src, off, len, allocator, dialect);
   }

   static int decodedBufferSize(int len) {
      return len - (len >>> 2);
   }

   private Base64() {
   }

   private static final class Decoder implements ByteProcessor {
      private final byte[] b4 = new byte[4];
      private int b4Posn;
      private byte[] decodabet;
      private int outBuffPosn;
      private ByteBuf dest;

      private Decoder() {
      }

      ByteBuf decode(ByteBuf src, int off, int len, ByteBufAllocator allocator, Base64Dialect dialect) {
         this.dest = allocator.buffer(Base64.decodedBufferSize(len)).order(src.order());
         this.decodabet = Base64.decodabet(dialect);

         try {
            src.forEachByte(off, len, this);
            return this.dest.slice(0, this.outBuffPosn);
         } catch (Throwable var7) {
            this.dest.release();
            PlatformDependent.throwException(var7);
            return null;
         }
      }

      public boolean process(byte value) throws Exception {
         if (value > 0) {
            byte sbiDecode = this.decodabet[value];
            if (sbiDecode >= -5) {
               if (sbiDecode >= -1) {
                  this.b4[this.b4Posn++] = value;
                  if (this.b4Posn > 3) {
                     this.outBuffPosn = this.outBuffPosn + decode4to3(this.b4, this.dest, this.outBuffPosn, this.decodabet);
                     this.b4Posn = 0;
                     return value != 61;
                  }
               }

               return true;
            }
         }

         throw new IllegalArgumentException("invalid Base64 input character: " + (short)(value & 255) + " (decimal)");
      }

      private static int decode4to3(byte[] src, ByteBuf dest, int destOffset, byte[] decodabet) {
         byte src0 = src[0];
         byte src1 = src[1];
         byte src2 = src[2];
         if (src2 == 61) {
            int decodedValue;
            try {
               decodedValue = (decodabet[src0] & 255) << 2 | (decodabet[src1] & 255) >>> 4;
            } catch (IndexOutOfBoundsException var11) {
               throw new IllegalArgumentException("not encoded in Base64");
            }

            dest.setByte(destOffset, decodedValue);
            return 1;
         } else {
            byte src3 = src[3];
            if (src3 == 61) {
               byte b1 = decodabet[src1];

               int decodedValue;
               try {
                  if (dest.order() == ByteOrder.BIG_ENDIAN) {
                     decodedValue = ((decodabet[src0] & 63) << 2 | (b1 & 240) >> 4) << 8 | (b1 & 15) << 4 | (decodabet[src2] & 252) >>> 2;
                  } else {
                     decodedValue = (decodabet[src0] & 63) << 2 | (b1 & 240) >> 4 | ((b1 & 15) << 4 | (decodabet[src2] & 252) >>> 2) << 8;
                  }
               } catch (IndexOutOfBoundsException var12) {
                  throw new IllegalArgumentException("not encoded in Base64");
               }

               dest.setShort(destOffset, decodedValue);
               return 2;
            } else {
               int decodedValue;
               try {
                  if (dest.order() == ByteOrder.BIG_ENDIAN) {
                     decodedValue = (decodabet[src0] & 63) << 18 | (decodabet[src1] & 255) << 12 | (decodabet[src2] & 255) << 6 | decodabet[src3] & 255;
                  } else {
                     byte b1 = decodabet[src1];
                     byte b2 = decodabet[src2];
                     decodedValue = (decodabet[src0] & 63) << 2
                        | (b1 & 15) << 12
                        | (b1 & 240) >>> 4
                        | (b2 & 3) << 22
                        | (b2 & 252) << 6
                        | (decodabet[src3] & 255) << 16;
                  }
               } catch (IndexOutOfBoundsException var13) {
                  throw new IllegalArgumentException("not encoded in Base64");
               }

               dest.setMedium(destOffset, decodedValue);
               return 3;
            }
         }
      }
   }
}
