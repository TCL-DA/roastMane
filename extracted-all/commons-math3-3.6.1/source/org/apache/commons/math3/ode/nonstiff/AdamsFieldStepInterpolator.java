package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.util.MathArrays;

class AdamsFieldStepInterpolator<T extends RealFieldElement<T>> extends AbstractFieldStepInterpolator<T> {
   private T scalingH;
   private final FieldODEStateAndDerivative<T> reference;
   private final T[] scaled;
   private final Array2DRowFieldMatrix<T> nordsieck;

   AdamsFieldStepInterpolator(
      T stepSize,
      FieldODEStateAndDerivative<T> reference,
      T[] scaled,
      Array2DRowFieldMatrix<T> nordsieck,
      boolean isForward,
      FieldODEStateAndDerivative<T> globalPreviousState,
      FieldODEStateAndDerivative<T> globalCurrentState,
      FieldEquationsMapper<T> equationsMapper
   ) {
      this(stepSize, reference, scaled, nordsieck, isForward, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, equationsMapper);
   }

   private AdamsFieldStepInterpolator(
      T stepSize,
      FieldODEStateAndDerivative<T> reference,
      T[] scaled,
      Array2DRowFieldMatrix<T> nordsieck,
      boolean isForward,
      FieldODEStateAndDerivative<T> globalPreviousState,
      FieldODEStateAndDerivative<T> globalCurrentState,
      FieldODEStateAndDerivative<T> softPreviousState,
      FieldODEStateAndDerivative<T> softCurrentState,
      FieldEquationsMapper<T> equationsMapper
   ) {
      super(isForward, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, equationsMapper);
      this.scalingH = stepSize;
      this.reference = reference;
      this.scaled = (T[])((RealFieldElement[])scaled.clone());
      this.nordsieck = new Array2DRowFieldMatrix<>(nordsieck.getData(), false);
   }

   protected AdamsFieldStepInterpolator<T> create(
      boolean newForward,
      FieldODEStateAndDerivative<T> newGlobalPreviousState,
      FieldODEStateAndDerivative<T> newGlobalCurrentState,
      FieldODEStateAndDerivative<T> newSoftPreviousState,
      FieldODEStateAndDerivative<T> newSoftCurrentState,
      FieldEquationsMapper<T> newMapper
   ) {
      return new AdamsFieldStepInterpolator<>(
         this.scalingH,
         this.reference,
         this.scaled,
         this.nordsieck,
         newForward,
         newGlobalPreviousState,
         newGlobalCurrentState,
         newSoftPreviousState,
         newSoftCurrentState,
         newMapper
      );
   }

   @Override
   protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(
      FieldEquationsMapper<T> equationsMapper, T time, T theta, T thetaH, T oneMinusThetaH
   ) {
      return taylor(this.reference, time, this.scalingH, this.scaled, this.nordsieck);
   }

   public static <S extends RealFieldElement<S>> FieldODEStateAndDerivative<S> taylor(
      FieldODEStateAndDerivative<S> reference, S time, S stepSize, S[] scaled, Array2DRowFieldMatrix<S> nordsieck
   ) {
      S x = (S)time.subtract(reference.getTime());
      S normalizedAbscissa = (S)x.divide(stepSize);
      S[] stateVariation = (S[])MathArrays.buildArray(time.getField(), scaled.length);
      Arrays.fill(stateVariation, time.getField().getZero());
      S[] estimatedDerivatives = (S[])MathArrays.buildArray(time.getField(), scaled.length);
      Arrays.fill(estimatedDerivatives, time.getField().getZero());
      S[][] nData = (S[][])nordsieck.getDataRef();

      for (int i = nData.length - 1; i >= 0; i--) {
         int order = i + 2;
         S[] nDataI = nData[i];
         S power = (S)normalizedAbscissa.pow(order);

         for (int j = 0; j < nDataI.length; j++) {
            S d = (S)nDataI[j].multiply(power);
            stateVariation[j] = (S)stateVariation[j].add(d);
            estimatedDerivatives[j] = (S)estimatedDerivatives[j].add(d.multiply(order));
         }
      }

      S[] estimatedState = reference.getState();

      for (int j = 0; j < stateVariation.length; j++) {
         stateVariation[j] = (S)stateVariation[j].add(scaled[j].multiply(normalizedAbscissa));
         estimatedState[j] = (S)estimatedState[j].add(stateVariation[j]);
         estimatedDerivatives[j] = (S)estimatedDerivatives[j].add(scaled[j].multiply(normalizedAbscissa)).divide(x);
      }

      return new FieldODEStateAndDerivative<>(time, estimatedState, estimatedDerivatives);
   }
}
