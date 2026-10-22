package org.apache.commons.math3.linear;

public class DefaultRealMatrixPreservingVisitor implements RealMatrixPreservingVisitor {
   @Override
   public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
   }

   @Override
   public void visit(int row, int column, double value) {
   }

   @Override
   public double end() {
      return 0.0;
   }
}
