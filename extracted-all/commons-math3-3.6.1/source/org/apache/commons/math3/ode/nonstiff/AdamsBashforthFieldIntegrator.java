package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

public class AdamsBashforthFieldIntegrator<T extends RealFieldElement<T>> extends AdamsFieldIntegrator<T> {
   private static final String METHOD_NAME = "Adams-Bashforth";

   public AdamsBashforthFieldIntegrator(Field<T> field, int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) throws NumberIsTooSmallException {
      super(field, "Adams-Bashforth", nSteps, nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
   }

   public AdamsBashforthFieldIntegrator(
      Field<T> field, int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance
   ) throws IllegalArgumentException {
      super(field, "Adams-Bashforth", nSteps, nSteps, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
   }

   private T errorEstimation(T[] previousState, T[] predictedState, T[] predictedScaled, FieldMatrix<T> predictedNordsieck) {
      T error = this.getField().getZero();

      for (int i = 0; i < this.mainSetDimension; i++) {
         T yScale = predictedState[i].abs();
         T tol = this.vecAbsoluteTolerance == null
            ? yScale.multiply(this.scalRelativeTolerance).add(this.scalAbsoluteTolerance)
            : yScale.multiply(this.vecRelativeTolerance[i]).add(this.vecAbsoluteTolerance[i]);
         T variation = this.getField().getZero();
         int sign = predictedNordsieck.getRowDimension() % 2 == 0 ? -1 : 1;

         for (int k = predictedNordsieck.getRowDimension() - 1; k >= 0; k--) {
            variation = variation.add(predictedNordsieck.getEntry(k, i).multiply(sign));
            sign = -sign;
         }

         variation = variation.subtract(predictedScaled[i]);
         T ratio = predictedState[i].subtract(previousState[i]).add(variation).divide(tol);
         error = error.add(ratio.multiply(ratio));
      }

      return error.divide((double)this.mainSetDimension).sqrt();
   }

   @Override
   public FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> equations, FieldODEState<T> initialState, T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
      this.sanityChecks(initialState, finalTime);
      T t0 = initialState.getTime();
      T[] y = equations.getMapper().mapState(initialState);
      this.setStepStart(this.initIntegration(equations, t0, y, finalTime));
      boolean forward = finalTime.subtract(initialState.getTime()).getReal() > 0.0;
      this.start(equations, this.getStepStart(), finalTime);
      FieldODEStateAndDerivative<T> stepStart = this.getStepStart();
      FieldODEStateAndDerivative<T> stepEnd = AdamsFieldStepInterpolator.taylor(
         stepStart, stepStart.getTime().add(this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck
      );
      this.setIsLastStep(false);

      do {
         T[] predictedY = null;
         T[] predictedScaled = MathArrays.buildArray(this.getField(), y.length);
         Array2DRowFieldMatrix<T> predictedNordsieck = null;
         T error = this.getField().getZero().add(10.0);

         while (error.subtract(1.0).getReal() >= 0.0) {
            predictedY = stepEnd.getState();
            T[] yDot = this.computeDerivatives(stepEnd.getTime(), predictedY);

            for (int j = 0; j < predictedScaled.length; j++) {
               predictedScaled[j] = this.getStepSize().multiply(yDot[j]);
            }

            predictedNordsieck = this.updateHighOrderDerivativesPhase1(this.nordsieck);
            this.updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, predictedNordsieck);
            error = this.errorEstimation(y, predictedY, predictedScaled, predictedNordsieck);
            if (error.subtract(1.0).getReal() >= 0.0) {
               T factor = this.computeStepGrowShrinkFactor(error);
               this.rescale(this.filterStep(this.getStepSize().multiply(factor), forward, false));
               stepEnd = AdamsFieldStepInterpolator.taylor(
                  this.getStepStart(), this.getStepStart().getTime().add(this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck
               );
            }
         }

         this.setStepStart(
            this.acceptStep(
               new AdamsFieldStepInterpolator<>(
                  this.getStepSize(), stepEnd, predictedScaled, predictedNordsieck, forward, this.getStepStart(), stepEnd, equations.getMapper()
               ),
               finalTime
            )
         );
         this.scaled = predictedScaled;
         this.nordsieck = predictedNordsieck;
         if (!this.isLastStep()) {
            System.arraycopy(predictedY, 0, y, 0, y.length);
            if (this.resetOccurred()) {
               this.start(equations, this.getStepStart(), finalTime);
            }

            T factor = this.computeStepGrowShrinkFactor(error);
            T scaledH = this.getStepSize().multiply(factor);
            T nextT = this.getStepStart().getTime().add(scaledH);
            boolean nextIsLast = forward ? nextT.subtract(finalTime).getReal() >= 0.0 : nextT.subtract(finalTime).getReal() <= 0.0;
            T hNew = this.filterStep(scaledH, forward, nextIsLast);
            T filteredNextT = this.getStepStart().getTime().add(hNew);
            boolean filteredNextIsLast = forward ? filteredNextT.subtract(finalTime).getReal() >= 0.0 : filteredNextT.subtract(finalTime).getReal() <= 0.0;
            if (filteredNextIsLast) {
               hNew = finalTime.subtract(this.getStepStart().getTime());
            }

            this.rescale(hNew);
            stepEnd = AdamsFieldStepInterpolator.taylor(
               this.getStepStart(), this.getStepStart().getTime().add(this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck
            );
         }
      } while (!this.isLastStep());

      FieldODEStateAndDerivative<T> finalState = this.getStepStart();
      this.setStepStart(null);
      this.setStepSize(null);
      return finalState;
   }
}
