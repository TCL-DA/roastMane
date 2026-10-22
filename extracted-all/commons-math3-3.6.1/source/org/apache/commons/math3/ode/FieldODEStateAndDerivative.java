package org.apache.commons.math3.ode;

import org.apache.commons.math3.RealFieldElement;

public class FieldODEStateAndDerivative<T extends RealFieldElement<T>> extends FieldODEState<T> {
   private final T[] derivative;
   private final T[][] secondaryDerivative;

   public FieldODEStateAndDerivative(T time, T[] state, T[] derivative) {
      this(time, state, derivative, (T[][])((RealFieldElement[][])null), (T[][])((RealFieldElement[][])null));
   }

   public FieldODEStateAndDerivative(T time, T[] state, T[] derivative, T[][] secondaryState, T[][] secondaryDerivative) {
      super(time, state, secondaryState);
      this.derivative = (T[])((RealFieldElement[])derivative.clone());
      this.secondaryDerivative = this.copy(time.getField(), secondaryDerivative);
   }

   public T[] getDerivative() {
      return (T[])((RealFieldElement[])this.derivative.clone());
   }

   public T[] getSecondaryDerivative(int index) {
      return (T[])(index == 0 ? (RealFieldElement[])this.derivative.clone() : (RealFieldElement[])this.secondaryDerivative[index - 1].clone());
   }
}
