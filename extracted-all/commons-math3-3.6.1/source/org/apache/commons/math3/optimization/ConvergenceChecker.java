package org.apache.commons.math3.optimization;

@Deprecated
public interface ConvergenceChecker<PAIR> {
   boolean converged(int var1, PAIR var2, PAIR var3);
}
