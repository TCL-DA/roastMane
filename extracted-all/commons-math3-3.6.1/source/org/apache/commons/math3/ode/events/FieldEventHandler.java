package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

public interface FieldEventHandler<T extends RealFieldElement<T>> {
   void init(FieldODEStateAndDerivative<T> var1, T var2);

   T g(FieldODEStateAndDerivative<T> var1);

   Action eventOccurred(FieldODEStateAndDerivative<T> var1, boolean var2);

   FieldODEState<T> resetState(FieldODEStateAndDerivative<T> var1);
}
