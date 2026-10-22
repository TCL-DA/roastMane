package org.apache.commons.math3.optim;

public class InitialGuess implements OptimizationData {
   private final double[] init;

   public InitialGuess(double[] startPoint) {
      this.init = (double[])startPoint.clone();
   }

   public double[] getInitialGuess() {
      return (double[])this.init.clone();
   }
}
