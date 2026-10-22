package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

class ParameterizedWrapper implements ParameterizedODE {
   private final FirstOrderDifferentialEquations fode;

   ParameterizedWrapper(FirstOrderDifferentialEquations ode) {
      this.fode = ode;
   }

   public int getDimension() {
      return this.fode.getDimension();
   }

   public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
      this.fode.computeDerivatives(t, y, yDot);
   }

   @Override
   public Collection<String> getParametersNames() {
      return new ArrayList<>();
   }

   @Override
   public boolean isSupported(String name) {
      return false;
   }

   @Override
   public double getParameter(String name) throws UnknownParameterException {
      if (!this.isSupported(name)) {
         throw new UnknownParameterException(name);
      } else {
         return Double.NaN;
      }
   }

   @Override
   public void setParameter(String name, double value) {
   }
}
