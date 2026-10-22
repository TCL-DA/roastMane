package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.BivariateFunction;

public class Subtract implements BivariateFunction {
   @Override
   public double value(double x, double y) {
      return x - y;
   }
}
