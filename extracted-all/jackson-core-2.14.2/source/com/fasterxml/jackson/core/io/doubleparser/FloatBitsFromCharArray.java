package com.fasterxml.jackson.core.io.doubleparser;

class FloatBitsFromCharArray extends AbstractFloatingPointBitsFromCharArray {
   public FloatBitsFromCharArray() {
   }

   @Override
   long nan() {
      return (long)Float.floatToRawIntBits(Float.NaN);
   }

   @Override
   long negativeInfinity() {
      return (long)Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
   }

   @Override
   long positiveInfinity() {
      return (long)Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
   }

   @Override
   long valueOfFloatLiteral(
      char[] str,
      int startIndex,
      int endIndex,
      boolean isNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand
   ) {
      float result = FastFloatMath.decFloatLiteralToFloat(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return Float.isNaN(result)
         ? (long)Float.floatToRawIntBits(Float.parseFloat(new String(str, startIndex, endIndex - startIndex)))
         : (long)Float.floatToRawIntBits(result);
   }

   @Override
   long valueOfHexLiteral(
      char[] str,
      int startIndex,
      int endIndex,
      boolean isNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand
   ) {
      float d = FastFloatMath.hexFloatLiteralToFloat(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return (long)Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(new String(str, startIndex, endIndex - startIndex)) : d);
   }
}
