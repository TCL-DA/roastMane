package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.ode.AbstractFieldIntegrator;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public abstract class AdaptiveStepsizeFieldIntegrator<T extends RealFieldElement<T>> extends AbstractFieldIntegrator<T> {
   protected double scalAbsoluteTolerance;
   protected double scalRelativeTolerance;
   protected double[] vecAbsoluteTolerance;
   protected double[] vecRelativeTolerance;
   protected int mainSetDimension;
   private T initialStep;
   private T minStep;
   private T maxStep;

   public AdaptiveStepsizeFieldIntegrator(
      Field<T> field, String name, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance
   ) {
      super(field, name);
      this.setStepSizeControl(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
      this.resetInternalState();
   }

   public AdaptiveStepsizeFieldIntegrator(
      Field<T> field, String name, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance
   ) {
      super(field, name);
      this.setStepSizeControl(minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
      this.resetInternalState();
   }

   public void setStepSizeControl(double minimalStep, double maximalStep, double absoluteTolerance, double relativeTolerance) {
      this.minStep = this.getField().getZero().add(FastMath.abs(minimalStep));
      this.maxStep = this.getField().getZero().add(FastMath.abs(maximalStep));
      this.initialStep = this.getField().getOne().negate();
      this.scalAbsoluteTolerance = absoluteTolerance;
      this.scalRelativeTolerance = relativeTolerance;
      this.vecAbsoluteTolerance = null;
      this.vecRelativeTolerance = null;
   }

   public void setStepSizeControl(double minimalStep, double maximalStep, double[] absoluteTolerance, double[] relativeTolerance) {
      this.minStep = this.getField().getZero().add(FastMath.abs(minimalStep));
      this.maxStep = this.getField().getZero().add(FastMath.abs(maximalStep));
      this.initialStep = this.getField().getOne().negate();
      this.scalAbsoluteTolerance = 0.0;
      this.scalRelativeTolerance = 0.0;
      this.vecAbsoluteTolerance = (double[])absoluteTolerance.clone();
      this.vecRelativeTolerance = (double[])relativeTolerance.clone();
   }

   public void setInitialStepSize(T initialStepSize) {
      if (!(initialStepSize.subtract(this.minStep).getReal() < 0.0) && !(initialStepSize.subtract(this.maxStep).getReal() > 0.0)) {
         this.initialStep = initialStepSize;
      } else {
         this.initialStep = this.getField().getOne().negate();
      }
   }

   @Override
   protected void sanityChecks(FieldODEState<T> eqn, T t) throws DimensionMismatchException, NumberIsTooSmallException {
      super.sanityChecks(eqn, t);
      this.mainSetDimension = eqn.getStateDimension();
      if (this.vecAbsoluteTolerance != null && this.vecAbsoluteTolerance.length != this.mainSetDimension) {
         throw new DimensionMismatchException(this.mainSetDimension, this.vecAbsoluteTolerance.length);
      } else if (this.vecRelativeTolerance != null && this.vecRelativeTolerance.length != this.mainSetDimension) {
         throw new DimensionMismatchException(this.mainSetDimension, this.vecRelativeTolerance.length);
      }
   }

   public T initializeStep(boolean forward, int order, T[] scale, FieldODEStateAndDerivative<T> state0, FieldEquationsMapper<T> mapper) throws MaxCountExceededException, DimensionMismatchException {
      if (this.initialStep.getReal() > 0.0) {
         return forward ? this.initialStep : this.initialStep.negate();
      } else {
         T[] y0 = mapper.mapState(state0);
         T[] yDot0 = mapper.mapDerivative(state0);
         T yOnScale2 = this.getField().getZero();
         T yDotOnScale2 = this.getField().getZero();

         for (int j = 0; j < scale.length; j++) {
            T ratio = y0[j].divide(scale[j]);
            yOnScale2 = yOnScale2.add(ratio.multiply(ratio));
            T ratioDot = yDot0[j].divide(scale[j]);
            yDotOnScale2 = yDotOnScale2.add(ratioDot.multiply(ratioDot));
         }

         T h = !(yOnScale2.getReal() < 1.0E-10) && !(yDotOnScale2.getReal() < 1.0E-10)
            ? yOnScale2.divide(yDotOnScale2).sqrt().multiply(0.01)
            : this.getField().getZero().add(1.0E-6);
         if (!forward) {
            h = h.negate();
         }

         T[] y1 = MathArrays.buildArray(this.getField(), y0.length);

         for (int j = 0; j < y0.length; j++) {
            y1[j] = y0[j].add(yDot0[j].multiply(h));
         }

         T[] yDot1 = this.computeDerivatives(state0.getTime().add(h), y1);
         T yDDotOnScale = this.getField().getZero();

         for (int j = 0; j < scale.length; j++) {
            T ratioDotDot = yDot1[j].subtract(yDot0[j]).divide(scale[j]);
            yDDotOnScale = yDDotOnScale.add(ratioDotDot.multiply(ratioDotDot));
         }

         yDDotOnScale = yDDotOnScale.sqrt().divide(h);
         T maxInv2 = MathUtils.max(yDotOnScale2.sqrt(), yDDotOnScale);
         T h1 = maxInv2.getReal() < 1.0E-15
            ? MathUtils.max(this.getField().getZero().add(1.0E-6), h.abs().multiply(0.001))
            : maxInv2.multiply(100).reciprocal().pow(1.0 / (double)order);
         h = MathUtils.min(h.abs().multiply(100), h1);
         h = MathUtils.max(h, state0.getTime().abs().multiply(1.0E-12));
         h = MathUtils.max(this.minStep, MathUtils.min(this.maxStep, h));
         if (!forward) {
            h = h.negate();
         }

         return h;
      }
   }

   protected T filterStep(T h, boolean forward, boolean acceptSmall) throws NumberIsTooSmallException {
      T filteredH = h;
      if (h.abs().subtract(this.minStep).getReal() < 0.0) {
         if (!acceptSmall) {
            throw new NumberIsTooSmallException(LocalizedFormats.MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION, h.abs().getReal(), this.minStep.getReal(), true);
         }

         filteredH = forward ? this.minStep : this.minStep.negate();
      }

      if (filteredH.subtract(this.maxStep).getReal() > 0.0) {
         filteredH = this.maxStep;
      } else if (filteredH.add(this.maxStep).getReal() < 0.0) {
         filteredH = this.maxStep.negate();
      }

      return filteredH;
   }

   protected void resetInternalState() {
      this.setStepStart(null);
      this.setStepSize(this.minStep.multiply(this.maxStep).sqrt());
   }

   public T getMinStep() {
      return this.minStep;
   }

   public T getMaxStep() {
      return this.maxStep;
   }
}
