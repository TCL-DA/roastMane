package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.util.FastMath;

public class Max implements BivariateFunction {
   @Override
   public double value(double x, double y) {
      return FastMath.max(x, y);
   }
}
