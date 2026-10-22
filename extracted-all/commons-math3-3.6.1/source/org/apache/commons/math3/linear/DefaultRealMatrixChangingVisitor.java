package org.apache.commons.math3.linear;

public class DefaultRealMatrixChangingVisitor implements RealMatrixChangingVisitor {
   @Override
   public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
   }

   @Override
   public double visit(int row, int column, double value) {
      return value;
   }

   @Override
   public double end() {
      return 0.0;
   }
}
