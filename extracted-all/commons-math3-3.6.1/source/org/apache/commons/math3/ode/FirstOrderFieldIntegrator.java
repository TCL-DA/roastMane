package org.apache.commons.math3.ode;

import java.util.Collection;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.events.FieldEventHandler;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;

public interface FirstOrderFieldIntegrator<T extends RealFieldElement<T>> {
   String getName();

   void addStepHandler(FieldStepHandler<T> var1);

   Collection<FieldStepHandler<T>> getStepHandlers();

   void clearStepHandlers();

   void addEventHandler(FieldEventHandler<T> var1, double var2, double var4, int var6);

   void addEventHandler(FieldEventHandler<T> var1, double var2, double var4, int var6, BracketedRealFieldUnivariateSolver<T> var7);

   Collection<FieldEventHandler<T>> getEventHandlers();

   void clearEventHandlers();

   FieldODEStateAndDerivative<T> getCurrentStepStart();

   T getCurrentSignedStepsize();

   void setMaxEvaluations(int var1);

   int getMaxEvaluations();

   int getEvaluations();

   FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> var1, FieldODEState<T> var2, T var3) throws NumberIsTooSmallException, MaxCountExceededException, NoBracketingException;
}
