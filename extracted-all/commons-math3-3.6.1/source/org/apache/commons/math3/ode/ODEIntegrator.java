package org.apache.commons.math3.ode;

import java.util.Collection;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;

public interface ODEIntegrator {
   String getName();

   void addStepHandler(StepHandler var1);

   Collection<StepHandler> getStepHandlers();

   void clearStepHandlers();

   void addEventHandler(EventHandler var1, double var2, double var4, int var6);

   void addEventHandler(EventHandler var1, double var2, double var4, int var6, UnivariateSolver var7);

   Collection<EventHandler> getEventHandlers();

   void clearEventHandlers();

   double getCurrentStepStart();

   double getCurrentSignedStepsize();

   void setMaxEvaluations(int var1);

   int getMaxEvaluations();

   int getEvaluations();
}
