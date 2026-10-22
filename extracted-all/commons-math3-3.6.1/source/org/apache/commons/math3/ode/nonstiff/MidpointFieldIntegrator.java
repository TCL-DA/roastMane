package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

public class MidpointFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T> {
   public MidpointFieldIntegrator(Field<T> field, T step) {
      super(field, "midpoint", step);
   }

   @Override
   public T[] getC() {
      T[] c = MathArrays.buildArray(this.getField(), 1);
      c[0] = this.getField().getOne().multiply(0.5);
      return c;
   }

   @Override
   public T[][] getA() {
      T[][] a = MathArrays.buildArray(this.getField(), 1, 1);
      a[0][0] = this.fraction(1, 2);
      return a;
   }

   @Override
   public T[] getB() {
      T[] b = MathArrays.buildArray(this.getField(), 2);
      b[0] = this.getField().getZero();
      b[1] = this.getField().getOne();
      return b;
   }

   protected MidpointFieldStepInterpolator<T> createInterpolator(
      boolean forward,
      T[][] yDotK,
      FieldODEStateAndDerivative<T> globalPreviousState,
      FieldODEStateAndDerivative<T> globalCurrentState,
      FieldEquationsMapper<T> mapper
   ) {
      return new MidpointFieldStepInterpolator<>(
         this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper
      );
   }
}
