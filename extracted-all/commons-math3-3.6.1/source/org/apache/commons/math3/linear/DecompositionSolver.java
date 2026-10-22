package org.apache.commons.math3.linear;

public interface DecompositionSolver {
   RealVector solve(RealVector var1) throws SingularMatrixException;

   RealMatrix solve(RealMatrix var1) throws SingularMatrixException;

   boolean isNonSingular();

   RealMatrix getInverse() throws SingularMatrixException;
}
