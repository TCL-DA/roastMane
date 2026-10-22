package org.apache.commons.math3.geometry.partitioning.utilities;

import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class OrderedTuple implements Comparable<OrderedTuple> {
   private static final long SIGN_MASK = Long.MIN_VALUE;
   private static final long EXPONENT_MASK = 9218868437227405312L;
   private static final long MANTISSA_MASK = 4503599627370495L;
   private static final long IMPLICIT_ONE = 4503599627370496L;
   private double[] components;
   private int offset;
   private int lsb;
   private long[] encoding;
   private boolean posInf;
   private boolean negInf;
   private boolean nan;

   public OrderedTuple(double... components) {
      this.components = (double[])components.clone();
      int msb = Integer.MIN_VALUE;
      this.lsb = Integer.MAX_VALUE;
      this.posInf = false;
      this.negInf = false;
      this.nan = false;

      for (int i = 0; i < components.length; i++) {
         if (Double.isInfinite(components[i])) {
            if (components[i] < 0.0) {
               this.negInf = true;
            } else {
               this.posInf = true;
            }
         } else if (Double.isNaN(components[i])) {
            this.nan = true;
         } else {
            long b = Double.doubleToLongBits(components[i]);
            long m = mantissa(b);
            if (m != 0L) {
               int e = exponent(b);
               msb = FastMath.max(msb, e + computeMSB(m));
               this.lsb = FastMath.min(this.lsb, e + computeLSB(m));
            }
         }
      }

      if (this.posInf && this.negInf) {
         this.posInf = false;
         this.negInf = false;
         this.nan = true;
      }

      if (this.lsb <= msb) {
         this.encode(msb + 16);
      } else {
         this.encoding = new long[]{0L};
      }
   }

   private void encode(int minOffset) {
      this.offset = minOffset + 31;
      this.offset = this.offset - this.offset % 32;
      if (this.encoding == null || this.encoding.length != 1 || this.encoding[0] != 0L) {
         int neededBits = this.offset + 1 - this.lsb;
         int neededLongs = (neededBits + 62) / 63;
         this.encoding = new long[this.components.length * neededLongs];
         int eIndex = 0;
         int shift = 62;
         long word = 0L;

         for (int k = this.offset; eIndex < this.encoding.length; k--) {
            for (int vIndex = 0; vIndex < this.components.length; vIndex++) {
               if (this.getBit(vIndex, k) != 0) {
                  word |= 1L << shift;
               }

               if (shift-- == 0) {
                  this.encoding[eIndex++] = word;
                  word = 0L;
                  shift = 62;
               }
            }
         }
      }
   }

   public int compareTo(OrderedTuple ot) {
      if (this.components.length == ot.components.length) {
         if (this.nan) {
            return 1;
         } else if (ot.nan) {
            return -1;
         } else if (!this.negInf && !ot.posInf) {
            if (!this.posInf && !ot.negInf) {
               if (this.offset < ot.offset) {
                  this.encode(ot.offset);
               } else if (this.offset > ot.offset) {
                  ot.encode(this.offset);
               }

               int limit = FastMath.min(this.encoding.length, ot.encoding.length);

               for (int i = 0; i < limit; i++) {
                  if (this.encoding[i] < ot.encoding[i]) {
                     return -1;
                  }

                  if (this.encoding[i] > ot.encoding[i]) {
                     return 1;
                  }
               }

               if (this.encoding.length < ot.encoding.length) {
                  return -1;
               } else {
                  return this.encoding.length > ot.encoding.length ? 1 : 0;
               }
            } else {
               return 1;
            }
         } else {
            return -1;
         }
      } else {
         return this.components.length - ot.components.length;
      }
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return other instanceof OrderedTuple ? this.compareTo((OrderedTuple)other) == 0 : false;
      }
   }

   @Override
   public int hashCode() {
      int multiplier = 37;
      int trueHash = 97;
      int falseHash = 71;
      int hash = Arrays.hashCode(this.components);
      hash = hash * 37 + this.offset;
      hash = hash * 37 + this.lsb;
      hash = hash * 37 + (this.posInf ? 97 : 71);
      hash = hash * 37 + (this.negInf ? 97 : 71);
      return hash * 37 + (this.nan ? 97 : 71);
   }

   public double[] getComponents() {
      return (double[])this.components.clone();
   }

   private static long sign(long bits) {
      return bits & Long.MIN_VALUE;
   }

   private static int exponent(long bits) {
      return (int)((bits & 9218868437227405312L) >> 52) - 1075;
   }

   private static long mantissa(long bits) {
      return (bits & 9218868437227405312L) == 0L ? (bits & 4503599627370495L) << 1 : 4503599627370496L | bits & 4503599627370495L;
   }

   private static int computeMSB(long l) {
      long ll = l;
      long mask = 4294967295L;
      int scale = 32;

      int msb;
      for (msb = 0; scale != 0; mask >>= scale) {
         if ((ll & mask) != ll) {
            msb |= scale;
            ll >>= scale;
         }

         scale >>= 1;
      }

      return msb;
   }

   private static int computeLSB(long l) {
      long ll = l;
      long mask = -4294967296L;
      int scale = 32;

      int lsb;
      for (lsb = 0; scale != 0; mask >>= scale) {
         if ((ll & mask) == ll) {
            lsb |= scale;
            ll >>= scale;
         }

         scale >>= 1;
      }

      return lsb;
   }

   private int getBit(int i, int k) {
      long bits = Double.doubleToLongBits(this.components[i]);
      int e = exponent(bits);
      if (k < e || k > this.offset) {
         return 0;
      } else if (k == this.offset) {
         return sign(bits) == 0L ? 1 : 0;
      } else if (k > e + 52) {
         return sign(bits) == 0L ? 0 : 1;
      } else {
         long m = sign(bits) == 0L ? mantissa(bits) : -mantissa(bits);
         return (int)(m >> k - e & 1L);
      }
   }
}
