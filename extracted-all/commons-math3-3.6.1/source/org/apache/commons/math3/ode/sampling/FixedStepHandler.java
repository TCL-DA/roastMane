package org.apache.commons.math3.ode.sampling;

public interface FixedStepHandler {
   void init(double var1, double[] var3, double var4);

   void handleStep(double var1, double[] var3, double[] var4, boolean var5);
}
