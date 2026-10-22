package org.apache.commons.math3.ml.neuralnet.sofm.util;

import org.apache.commons.math3.analysis.function.Logistic;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

public class QuasiSigmoidDecayFunction {
   private final Logistic sigmoid;
   private final double scale;

   public QuasiSigmoidDecayFunction(double initValue, double slope, long numCall) {
      if (initValue <= 0.0) {
         throw new NotStrictlyPositiveException(initValue);
      } else if (slope >= 0.0) {
         throw new NumberIsTooLargeException(slope, 0, false);
      } else if (numCall <= 1L) {
         throw new NotStrictlyPositiveException(numCall);
      } else {
         double m = (double)numCall;
         double b = 4.0 * slope / initValue;
         double q = 1.0;
         double a = 0.0;
         double n = 1.0;
         this.sigmoid = new Logistic(initValue, m, b, 1.0, 0.0, 1.0);
         double y0 = this.sigmoid.value(0.0);
         this.scale = initValue / y0;
      }
   }

   public double value(long numCall) {
      return this.scale * this.sigmoid.value((double)numCall);
   }
}
