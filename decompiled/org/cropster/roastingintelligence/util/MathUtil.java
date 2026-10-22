package org.cropster.roastingintelligence.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class MathUtil {
   private MathUtil() {
   }

   public static long roundLong(long value, long tens) {
      if (tens > 0L && tens % 10L == 0L) {
         return (value + tens / 2L) / tens * tens;
      } else {
         throw new IllegalArgumentException("tens need to be a multiple of and >= 10");
      }
   }

   public static long floorLong(long value, long tens) {
      if (tens > 0L && tens % 10L == 0L) {
         return value / tens * tens;
      } else {
         throw new IllegalArgumentException("tens need to be a multiple of and >= 10");
      }
   }

   public static double roundHalfToEven(double value, int precision) {
      if (precision < 0) {
         throw new IllegalArgumentException("precision has to be equal to or greater than 0");
      } else {
         double factor = Math.pow(10.0, (double)precision);
         return Math.rint(value * factor) / factor;
      }
   }

   public static double roundHalfToEven(double value) {
      return roundHalfToEven(value, 0);
   }

   public static BigDecimal d(String val) {
      return new BigDecimal(val);
   }

   public static BigDecimal stripTrailingFractionZeros(BigDecimal bd) {
      BigDecimal result = bd.stripTrailingZeros();
      if (result.scale() < 0) {
         result = result.setScale(0);
      }

      return result;
   }

   public static BigDecimal[] distribute(BigDecimal amount, BigDecimal[] percentages) {
      BigDecimal remainingAmount = amount;
      BigDecimal[] distributedAmounts = new BigDecimal[percentages.length];

      for (int i = 0; i < percentages.length; i++) {
         BigDecimal percentage = percentages[i];
         BigDecimal partialAmount = remainingAmount;
         if (BigDecimal.ZERO.compareTo(remainingAmount) < 0 && i < percentages.length - 1) {
            BigDecimal share = percentage.multiply(amount);
            if (share.compareTo(remainingAmount) < 0) {
               partialAmount = share.setScale(3, RoundingMode.HALF_EVEN);
            }
         }

         remainingAmount = remainingAmount.subtract(partialAmount);
         distributedAmounts[i] = stripTrailingFractionZeros(partialAmount);
      }

      return distributedAmounts;
   }

   public static List<BigDecimal> distribute(BigDecimal amount, Collection<BigDecimal> percentages) {
      BigDecimal[] percentageArray = percentages.toArray(new BigDecimal[percentages.size()]);
      BigDecimal[] amountArray = distribute(amount, percentageArray);
      return Arrays.asList(amountArray);
   }
}
