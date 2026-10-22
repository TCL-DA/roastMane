package org.apache.commons.math3.ode.sampling;

public class DummyStepHandler implements StepHandler {
   private DummyStepHandler() {
   }

   public static DummyStepHandler getInstance() {
      return DummyStepHandler.LazyHolder.INSTANCE;
   }

   @Override
   public void init(double t0, double[] y0, double t) {
   }

   @Override
   public void handleStep(StepInterpolator interpolator, boolean isLast) {
   }

   private Object readResolve() {
      return DummyStepHandler.LazyHolder.INSTANCE;
   }

   private static class LazyHolder {
      private static final DummyStepHandler INSTANCE = new DummyStepHandler();
   }
}
