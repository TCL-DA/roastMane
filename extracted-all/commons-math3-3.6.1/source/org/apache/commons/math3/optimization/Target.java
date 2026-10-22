package org.apache.commons.math3.optimization;

@Deprecated
public class Target implements OptimizationData {
   private final double[] target;

   public Target(double[] observations) {
      this.target = (double[])observations.clone();
   }

   public double[] getTarget() {
      return (double[])this.target.clone();
   }
}
