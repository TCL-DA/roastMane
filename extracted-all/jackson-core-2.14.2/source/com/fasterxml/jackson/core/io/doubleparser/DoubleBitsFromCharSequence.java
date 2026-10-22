package com.fasterxml.jackson.core.io.doubleparser;

final class DoubleBitsFromCharSequence extends AbstractFloatingPointBitsFromCharSequence {
   public DoubleBitsFromCharSequence() {
   }

   @Override
   long nan() {
      return Double.doubleToRawLongBits(Double.NaN);
   }

   @Override
   long negativeInfinity() {
      return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
   }

   @Override
   long positiveInfinity() {
      return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
   }

   @Override
   long valueOfFloatLiteral(
      CharSequence str,
      int startIndex,
      int endIndex,
      boolean isNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand
   ) {
      double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(str.subSequence(startIndex, endIndex).toString()) : d);
   }

   @Override
   long valueOfHexLiteral(
      CharSequence str,
      int startIndex,
      int endIndex,
      boolean isNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand
   ) {
      double d = FastDoubleMath.tryHexFloatToDoubleTruncated(
         isNegative, significand, (long)exponent, isSignificandTruncated, (long)exponentOfTruncatedSignificand
      );
      return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(str.subSequence(startIndex, endIndex).toString()) : d);
   }
}
