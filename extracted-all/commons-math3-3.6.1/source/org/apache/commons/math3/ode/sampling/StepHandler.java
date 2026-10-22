package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;

public interface StepHandler {
   void init(double var1, double[] var3, double var4);

   void handleStep(StepInterpolator var1, boolean var2) throws MaxCountExceededException;
}
