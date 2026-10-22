package org.apache.commons.math3.ode.events;

public interface EventHandler {
   void init(double var1, double[] var3, double var4);

   double g(double var1, double[] var3);

   EventHandler.Action eventOccurred(double var1, double[] var3, boolean var4);

   void resetState(double var1, double[] var3);

   public static enum Action {
      STOP,
      RESET_STATE,
      RESET_DERIVATIVES,
      CONTINUE;
   }
}
