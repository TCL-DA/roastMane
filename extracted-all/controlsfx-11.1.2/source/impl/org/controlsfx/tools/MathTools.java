package impl.org.controlsfx.tools;

import java.util.Objects;

public class MathTools {
   public static boolean isInInterval(double lowerBound, double value, double upperBound) {
      return lowerBound <= value && value <= upperBound;
   }

   public static double inInterval(double lowerBound, double value, double upperBound) {
      if (value < lowerBound) {
         return lowerBound;
      } else {
         return upperBound < value ? upperBound : value;
      }
   }

   public static double min(double... values) {
      Objects.requireNonNull(values, "The specified value array must not be null.");
      if (values.length == 0) {
         throw new IllegalArgumentException("The specified value array must contain at least one element.");
      } else {
         double min = Double.MAX_VALUE;

         for (double value : values) {
            min = Math.min(value, min);
         }

         return min;
      }
   }
}
