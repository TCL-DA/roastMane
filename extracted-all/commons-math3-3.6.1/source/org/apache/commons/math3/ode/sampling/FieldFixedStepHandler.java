package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

public interface FieldFixedStepHandler<T extends RealFieldElement<T>> {
   void init(FieldODEStateAndDerivative<T> var1, T var2);

   void handleStep(FieldODEStateAndDerivative<T> var1, boolean var2);
}
