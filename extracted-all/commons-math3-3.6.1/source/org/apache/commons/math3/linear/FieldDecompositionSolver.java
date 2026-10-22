package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public interface FieldDecompositionSolver<T extends FieldElement<T>> {
   FieldVector<T> solve(FieldVector<T> var1);

   FieldMatrix<T> solve(FieldMatrix<T> var1);

   boolean isNonSingular();

   FieldMatrix<T> getInverse();
}
