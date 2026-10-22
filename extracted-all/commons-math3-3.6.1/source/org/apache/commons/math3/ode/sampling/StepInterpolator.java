package org.apache.commons.math3.ode.sampling;

import java.io.Externalizable;
import org.apache.commons.math3.exception.MaxCountExceededException;

public interface StepInterpolator extends Externalizable {
   double getPreviousTime();

   double getCurrentTime();

   double getInterpolatedTime();

   void setInterpolatedTime(double var1);

   double[] getInterpolatedState() throws MaxCountExceededException;

   double[] getInterpolatedDerivatives() throws MaxCountExceededException;

   double[] getInterpolatedSecondaryState(int var1) throws MaxCountExceededException;

   double[] getInterpolatedSecondaryDerivatives(int var1) throws MaxCountExceededException;

   boolean isForward();

   StepInterpolator copy() throws MaxCountExceededException;
}
