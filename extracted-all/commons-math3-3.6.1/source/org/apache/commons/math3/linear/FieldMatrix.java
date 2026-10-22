package org.apache.commons.math3.linear;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;

public interface FieldMatrix<T extends FieldElement<T>> extends AnyMatrix {
   Field<T> getField();

   FieldMatrix<T> createMatrix(int var1, int var2) throws NotStrictlyPositiveException;

   FieldMatrix<T> copy();

   FieldMatrix<T> add(FieldMatrix<T> var1) throws MatrixDimensionMismatchException;

   FieldMatrix<T> subtract(FieldMatrix<T> var1) throws MatrixDimensionMismatchException;

   FieldMatrix<T> scalarAdd(T var1);

   FieldMatrix<T> scalarMultiply(T var1);

   FieldMatrix<T> multiply(FieldMatrix<T> var1) throws DimensionMismatchException;

   FieldMatrix<T> preMultiply(FieldMatrix<T> var1) throws DimensionMismatchException;

   FieldMatrix<T> power(int var1) throws NonSquareMatrixException, NotPositiveException;

   T[][] getData();

   FieldMatrix<T> getSubMatrix(int var1, int var2, int var3, int var4) throws NumberIsTooSmallException, OutOfRangeException;

   FieldMatrix<T> getSubMatrix(int[] var1, int[] var2) throws NoDataException, NullArgumentException, OutOfRangeException;

   void copySubMatrix(int var1, int var2, int var3, int var4, T[][] var5) throws MatrixDimensionMismatchException, NumberIsTooSmallException, OutOfRangeException;

   void copySubMatrix(int[] var1, int[] var2, T[][] var3) throws MatrixDimensionMismatchException, NoDataException, NullArgumentException, OutOfRangeException;

   void setSubMatrix(T[][] var1, int var2, int var3) throws DimensionMismatchException, OutOfRangeException, NoDataException, NullArgumentException;

   FieldMatrix<T> getRowMatrix(int var1) throws OutOfRangeException;

   void setRowMatrix(int var1, FieldMatrix<T> var2) throws MatrixDimensionMismatchException, OutOfRangeException;

   FieldMatrix<T> getColumnMatrix(int var1) throws OutOfRangeException;

   void setColumnMatrix(int var1, FieldMatrix<T> var2) throws MatrixDimensionMismatchException, OutOfRangeException;

   FieldVector<T> getRowVector(int var1) throws OutOfRangeException;

   void setRowVector(int var1, FieldVector<T> var2) throws MatrixDimensionMismatchException, OutOfRangeException;

   FieldVector<T> getColumnVector(int var1) throws OutOfRangeException;

   void setColumnVector(int var1, FieldVector<T> var2) throws MatrixDimensionMismatchException, OutOfRangeException;

   T[] getRow(int var1) throws OutOfRangeException;

   void setRow(int var1, T[] var2) throws MatrixDimensionMismatchException, OutOfRangeException;

   T[] getColumn(int var1) throws OutOfRangeException;

   void setColumn(int var1, T[] var2) throws MatrixDimensionMismatchException, OutOfRangeException;

   T getEntry(int var1, int var2) throws OutOfRangeException;

   void setEntry(int var1, int var2, T var3) throws OutOfRangeException;

   void addToEntry(int var1, int var2, T var3) throws OutOfRangeException;

   void multiplyEntry(int var1, int var2, T var3) throws OutOfRangeException;

   FieldMatrix<T> transpose();

   T getTrace() throws NonSquareMatrixException;

   T[] operate(T[] var1) throws DimensionMismatchException;

   FieldVector<T> operate(FieldVector<T> var1) throws DimensionMismatchException;

   T[] preMultiply(T[] var1) throws DimensionMismatchException;

   FieldVector<T> preMultiply(FieldVector<T> var1) throws DimensionMismatchException;

   T walkInRowOrder(FieldMatrixChangingVisitor<T> var1);

   T walkInRowOrder(FieldMatrixPreservingVisitor<T> var1);

   T walkInRowOrder(FieldMatrixChangingVisitor<T> var1, int var2, int var3, int var4, int var5) throws OutOfRangeException, NumberIsTooSmallException;

   T walkInRowOrder(FieldMatrixPreservingVisitor<T> var1, int var2, int var3, int var4, int var5) throws OutOfRangeException, NumberIsTooSmallException;

   T walkInColumnOrder(FieldMatrixChangingVisitor<T> var1);

   T walkInColumnOrder(FieldMatrixPreservingVisitor<T> var1);

   T walkInColumnOrder(FieldMatrixChangingVisitor<T> var1, int var2, int var3, int var4, int var5) throws NumberIsTooSmallException, OutOfRangeException;

   T walkInColumnOrder(FieldMatrixPreservingVisitor<T> var1, int var2, int var3, int var4, int var5) throws NumberIsTooSmallException, OutOfRangeException;

   T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> var1);

   T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> var1);

   T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> var1, int var2, int var3, int var4, int var5) throws NumberIsTooSmallException, OutOfRangeException;

   T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> var1, int var2, int var3, int var4, int var5) throws NumberIsTooSmallException, OutOfRangeException;
}
