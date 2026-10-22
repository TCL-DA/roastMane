package com.fasterxml.jackson.core.io.doubleparser;

class FastFloatMath {
   private static final int FLOAT_EXPONENT_BIAS = 127;
   private static final int FLOAT_SIGNIFICAND_WIDTH = 24;
   private static final int FLOAT_MIN_EXPONENT_POWER_OF_TEN = -45;
   private static final int FLOAT_MAX_EXPONENT_POWER_OF_TEN = 38;
   private static final int FLOAT_MIN_EXPONENT_POWER_OF_TWO = -126;
   private static final int FLOAT_MAX_EXPONENT_POWER_OF_TWO = 127;
   private static final float[] FLOAT_POWER_OF_TEN = new float[]{
      1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F
   };

   private FastFloatMath() {
   }

   static float decFloatLiteralToFloat(boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
      if (significand == 0L) {
         return isNegative ? -0.0F : 0.0F;
      } else {
         float result;
         if (isSignificandTruncated) {
            if (-45 <= exponentOfTruncatedSignificand && exponentOfTruncatedSignificand <= 38) {
               float withoutRounding = tryDecToFloatWithFastAlgorithm(isNegative, significand, exponentOfTruncatedSignificand);
               float roundedUp = tryDecToFloatWithFastAlgorithm(isNegative, significand + 1L, exponentOfTruncatedSignificand);
               if (!Float.isNaN(withoutRounding) && roundedUp == withoutRounding) {
                  return withoutRounding;
               }
            }

            result = Float.NaN;
         } else if (-45 <= exponent && exponent <= 38) {
            result = tryDecToFloatWithFastAlgorithm(isNegative, significand, exponent);
         } else {
            result = Float.NaN;
         }

         return result;
      }
   }

   static float hexFloatLiteralToFloat(boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
      if (significand == 0L) {
         return isNegative ? -0.0F : 0.0F;
      } else {
         float result;
         if (isSignificandTruncated) {
            if (-126 <= exponentOfTruncatedSignificand && exponentOfTruncatedSignificand <= 127) {
               float withoutRounding = tryHexToFloatWithFastAlgorithm(isNegative, significand, exponentOfTruncatedSignificand);
               float roundedUp = tryHexToFloatWithFastAlgorithm(isNegative, significand + 1L, exponentOfTruncatedSignificand);
               if (!Double.isNaN((double)withoutRounding) && roundedUp == withoutRounding) {
                  return withoutRounding;
               }
            }

            result = Float.NaN;
         } else if (-126 <= exponent && exponent <= 127) {
            result = tryHexToFloatWithFastAlgorithm(isNegative, significand, exponent);
         } else {
            result = Float.NaN;
         }

         return result;
      }
   }

   static float tryDecToFloatWithFastAlgorithm(boolean isNegative, long digits, int power) {
      if (-10 <= power && power <= 10 && Long.compareUnsigned(digits, 16777215L) <= 0) {
         float d = (float)digits;
         if (power < 0) {
            d /= FLOAT_POWER_OF_TEN[-power];
         } else {
            d *= FLOAT_POWER_OF_TEN[power];
         }

         return isNegative ? -d : d;
      } else {
         long factorMantissa = FastDoubleMath.MANTISSA_64[power - -325];
         long exponent = (217706L * (long)power >> 16) + 127L + 64L;
         int lz = Long.numberOfLeadingZeros(digits);
         digits <<= lz;
         FastDoubleMath.UInt128 product = FastDoubleMath.fullMultiplication(digits, factorMantissa);
         long lower = product.low;
         long upper = product.high;
         if ((upper & 274877906943L) == 274877906943L && Long.compareUnsigned(lower + digits, lower) < 0) {
            long factor_mantissa_low = FastDoubleMath.MANTISSA_128[power - -325];
            product = FastDoubleMath.fullMultiplication(digits, factor_mantissa_low);
            long product_low = product.low;
            long product_middle2 = product.high;
            long product_high = upper;
            long product_middle = lower + product_middle2;
            if (Long.compareUnsigned(product_middle, lower) < 0) {
               product_high = upper + 1L;
            }

            if (product_middle + 1L == 0L
               && (product_high & 549755813887L) == 549755813887L
               && product_low + (long)Long.compareUnsigned(digits, product_low) < 0L) {
               return Float.NaN;
            }

            upper = product_high;
         }

         long upperbit = upper >>> 63;
         long mantissa = upper >>> (int)(upperbit + 38L);
         lz += (int)(1L ^ upperbit);
         if ((upper & 274877906943L) != 274877906943L && ((upper & 274877906943L) != 0L || (mantissa & 3L) != 1L)) {
            mantissa = ++mantissa >>> 1;
            if (mantissa >= 16777216L) {
               mantissa = 8388608L;
               lz--;
            }

            mantissa &= -8388609L;
            long real_exponent = exponent - (long)lz;
            if (real_exponent >= 1L && real_exponent <= 254L) {
               int bits = (int)(mantissa | real_exponent << 23 | (isNegative ? 2147483648L : 0L));
               return Float.intBitsToFloat(bits);
            } else {
               return Float.NaN;
            }
         } else {
            return Float.NaN;
         }
      }
   }

   static float tryHexToFloatWithFastAlgorithm(boolean isNegative, long digits, int power) {
      if (digits == 0L || power < -180) {
         return isNegative ? -0.0F : 0.0F;
      } else if (power > 127) {
         return isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
      } else if (Long.compareUnsigned(digits, 9007199254740991L) <= 0) {
         float d = (float)digits;
         d *= Math.scalb(1.0F, power);
         if (isNegative) {
            d = -d;
         }

         return d;
      } else {
         return Float.NaN;
      }
   }
}
