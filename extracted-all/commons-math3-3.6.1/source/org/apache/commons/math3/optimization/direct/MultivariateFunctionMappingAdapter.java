package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Logit;
import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

@Deprecated
public class MultivariateFunctionMappingAdapter implements MultivariateFunction {
   private final MultivariateFunction bounded;
   private final MultivariateFunctionMappingAdapter.Mapper[] mappers;

   public MultivariateFunctionMappingAdapter(MultivariateFunction bounded, double[] lower, double[] upper) {
      MathUtils.checkNotNull(lower);
      MathUtils.checkNotNull(upper);
      if (lower.length != upper.length) {
         throw new DimensionMismatchException(lower.length, upper.length);
      } else {
         for (int i = 0; i < lower.length; i++) {
            if (!(upper[i] >= lower[i])) {
               throw new NumberIsTooSmallException(upper[i], lower[i], true);
            }
         }

         this.bounded = bounded;
         this.mappers = new MultivariateFunctionMappingAdapter.Mapper[lower.length];

         for (int ix = 0; ix < this.mappers.length; ix++) {
            if (Double.isInfinite(lower[ix])) {
               if (Double.isInfinite(upper[ix])) {
                  this.mappers[ix] = new MultivariateFunctionMappingAdapter.NoBoundsMapper();
               } else {
                  this.mappers[ix] = new MultivariateFunctionMappingAdapter.UpperBoundMapper(upper[ix]);
               }
            } else if (Double.isInfinite(upper[ix])) {
               this.mappers[ix] = new MultivariateFunctionMappingAdapter.LowerBoundMapper(lower[ix]);
            } else {
               this.mappers[ix] = new MultivariateFunctionMappingAdapter.LowerUpperBoundMapper(lower[ix], upper[ix]);
            }
         }
      }
   }

   public double[] unboundedToBounded(double[] point) {
      double[] mapped = new double[this.mappers.length];

      for (int i = 0; i < this.mappers.length; i++) {
         mapped[i] = this.mappers[i].unboundedToBounded(point[i]);
      }

      return mapped;
   }

   public double[] boundedToUnbounded(double[] point) {
      double[] mapped = new double[this.mappers.length];

      for (int i = 0; i < this.mappers.length; i++) {
         mapped[i] = this.mappers[i].boundedToUnbounded(point[i]);
      }

      return mapped;
   }

   @Override
   public double value(double[] point) {
      return this.bounded.value(this.unboundedToBounded(point));
   }

   private static class LowerBoundMapper implements MultivariateFunctionMappingAdapter.Mapper {
      private final double lower;

      LowerBoundMapper(double lower) {
         this.lower = lower;
      }

      @Override
      public double unboundedToBounded(double y) {
         return this.lower + FastMath.exp(y);
      }

      @Override
      public double boundedToUnbounded(double x) {
         return FastMath.log(x - this.lower);
      }
   }

   private static class LowerUpperBoundMapper implements MultivariateFunctionMappingAdapter.Mapper {
      private final UnivariateFunction boundingFunction;
      private final UnivariateFunction unboundingFunction;

      LowerUpperBoundMapper(double lower, double upper) {
         this.boundingFunction = new Sigmoid(lower, upper);
         this.unboundingFunction = new Logit(lower, upper);
      }

      @Override
      public double unboundedToBounded(double y) {
         return this.boundingFunction.value(y);
      }

      @Override
      public double boundedToUnbounded(double x) {
         return this.unboundingFunction.value(x);
      }
   }

   private interface Mapper {
      double unboundedToBounded(double var1);

      double boundedToUnbounded(double var1);
   }

   private static class NoBoundsMapper implements MultivariateFunctionMappingAdapter.Mapper {
      NoBoundsMapper() {
      }

      @Override
      public double unboundedToBounded(double y) {
         return y;
      }

      @Override
      public double boundedToUnbounded(double x) {
         return x;
      }
   }

   private static class UpperBoundMapper implements MultivariateFunctionMappingAdapter.Mapper {
      private final double upper;

      UpperBoundMapper(double upper) {
         this.upper = upper;
      }

      @Override
      public double unboundedToBounded(double y) {
         return this.upper - FastMath.exp(-y);
      }

      @Override
      public double boundedToUnbounded(double x) {
         return -FastMath.log(this.upper - x);
      }
   }
}
