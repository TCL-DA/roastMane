package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

public interface FieldStepInterpolator<T extends RealFieldElement<T>> {
   FieldODEStateAndDerivative<T> getPreviousState();

   FieldODEStateAndDerivative<T> getCurrentState();

   FieldODEStateAndDerivative<T> getInterpolatedState(T var1);

   boolean isForward();
}
