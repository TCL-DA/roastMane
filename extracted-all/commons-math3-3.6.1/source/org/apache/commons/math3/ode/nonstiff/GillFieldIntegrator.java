package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

public class GillFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T> {
   public GillFieldIntegrator(Field<T> field, T step) {
      super(field, "Gill", step);
   }

   @Override
   public T[] getC() {
      T[] c = MathArrays.buildArray(this.getField(), 3);
      c[0] = this.fraction(1, 2);
      c[1] = c[0];
      c[2] = this.getField().getOne();
      return c;
   }

   @Override
   public T[][] getA() {
      T two = this.getField().getZero().add(2.0);
      T sqrtTwo = two.sqrt();
      T[][] a = MathArrays.buildArray(this.getField(), 3, -1);

      for (int i = 0; i < a.length; i++) {
         a[i] = MathArrays.buildArray(this.getField(), i + 1);
      }

      a[0][0] = this.fraction(1, 2);
      a[1][0] = sqrtTwo.subtract(1.0).multiply(0.5);
      a[1][1] = sqrtTwo.subtract(2.0).multiply(-0.5);
      a[2][0] = this.getField().getZero();
      a[2][1] = sqrtTwo.multiply(-0.5);
      a[2][2] = sqrtTwo.add(2.0).multiply(0.5);
      return a;
   }

   @Override
   public T[] getB() {
      T two = this.getField().getZero().add(2.0);
      T sqrtTwo = two.sqrt();
      T[] b = MathArrays.buildArray(this.getField(), 4);
      b[0] = this.fraction(1, 6);
      b[1] = sqrtTwo.subtract(2.0).divide(-6.0);
      b[2] = sqrtTwo.add(2.0).divide(6.0);
      b[3] = b[0];
      return b;
   }

   protected GillFieldStepInterpolator<T> createInterpolator(
      boolean forward,
      T[][] yDotK,
      FieldODEStateAndDerivative<T> globalPreviousState,
      FieldODEStateAndDerivative<T> globalCurrentState,
      FieldEquationsMapper<T> mapper
   ) {
      return new GillFieldStepInterpolator<>(
         this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper
      );
   }
}
