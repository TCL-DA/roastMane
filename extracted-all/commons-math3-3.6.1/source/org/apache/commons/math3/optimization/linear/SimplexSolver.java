package org.apache.commons.math3.optimization.linear;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.util.Precision;

@Deprecated
public class SimplexSolver extends AbstractLinearOptimizer {
   private static final double DEFAULT_EPSILON = 1.0E-6;
   private static final int DEFAULT_ULPS = 10;
   private final double epsilon;
   private final int maxUlps;

   public SimplexSolver() {
      this(1.0E-6, 10);
   }

   public SimplexSolver(double epsilon, int maxUlps) {
      this.epsilon = epsilon;
      this.maxUlps = maxUlps;
   }

   private Integer getPivotColumn(SimplexTableau tableau) {
      double minValue = 0.0;
      Integer minPos = null;

      for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
         double entry = tableau.getEntry(0, i);
         if (entry < minValue) {
            minValue = entry;
            minPos = i;
         }
      }

      return minPos;
   }

   private Integer getPivotRow(SimplexTableau tableau, int col) {
      List<Integer> minRatioPositions = new ArrayList<>();
      double minRatio = Double.MAX_VALUE;

      for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
         double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
         double entry = tableau.getEntry(i, col);
         if (Precision.compareTo(entry, 0.0, this.maxUlps) > 0) {
            double ratio = rhs / entry;
            int cmp = Double.compare(ratio, minRatio);
            if (cmp == 0) {
               minRatioPositions.add(i);
            } else if (cmp < 0) {
               minRatio = ratio;
               minRatioPositions = new ArrayList<>();
               minRatioPositions.add(i);
            }
         }
      }

      if (minRatioPositions.size() == 0) {
         return null;
      } else {
         if (minRatioPositions.size() > 1) {
            if (tableau.getNumArtificialVariables() > 0) {
               for (Integer row : minRatioPositions) {
                  for (int ix = 0; ix < tableau.getNumArtificialVariables(); ix++) {
                     int column = ix + tableau.getArtificialVariableOffset();
                     double entry = tableau.getEntry(row, column);
                     if (Precision.equals(entry, 1.0, this.maxUlps) && row.equals(tableau.getBasicRow(column))) {
                        return row;
                     }
                  }
               }
            }

            if (this.getIterations() < this.getMaxIterations() / 2) {
               Integer minRow = null;
               int minIndex = tableau.getWidth();
               int varStart = tableau.getNumObjectiveFunctions();
               int varEnd = tableau.getWidth() - 1;

               for (Integer row : minRatioPositions) {
                  for (int ixx = varStart; ixx < varEnd && !row.equals(minRow); ixx++) {
                     Integer basicRow = tableau.getBasicRow(ixx);
                     if (basicRow != null && basicRow.equals(row) && ixx < minIndex) {
                        minIndex = ixx;
                        minRow = row;
                     }
                  }
               }

               return minRow;
            }
         }

         return minRatioPositions.get(0);
      }
   }

   protected void doIteration(SimplexTableau tableau) throws MaxCountExceededException, UnboundedSolutionException {
      this.incrementIterationsCounter();
      Integer pivotCol = this.getPivotColumn(tableau);
      Integer pivotRow = this.getPivotRow(tableau, pivotCol);
      if (pivotRow == null) {
         throw new UnboundedSolutionException();
      } else {
         double pivotVal = tableau.getEntry(pivotRow, pivotCol);
         tableau.divideRow(pivotRow, pivotVal);

         for (int i = 0; i < tableau.getHeight(); i++) {
            if (i != pivotRow) {
               double multiplier = tableau.getEntry(i, pivotCol);
               tableau.subtractRow(i, pivotRow, multiplier);
            }
         }
      }
   }

   protected void solvePhase1(SimplexTableau tableau) throws MaxCountExceededException, UnboundedSolutionException, NoFeasibleSolutionException {
      if (tableau.getNumArtificialVariables() != 0) {
         while (!tableau.isOptimal()) {
            this.doIteration(tableau);
         }

         if (!Precision.equals(tableau.getEntry(0, tableau.getRhsOffset()), 0.0, this.epsilon)) {
            throw new NoFeasibleSolutionException();
         }
      }
   }

   @Override
   public PointValuePair doOptimize() throws MaxCountExceededException, UnboundedSolutionException, NoFeasibleSolutionException {
      SimplexTableau tableau = new SimplexTableau(
         this.getFunction(), this.getConstraints(), this.getGoalType(), this.restrictToNonNegative(), this.epsilon, this.maxUlps
      );
      this.solvePhase1(tableau);
      tableau.dropPhase1Objective();

      while (!tableau.isOptimal()) {
         this.doIteration(tableau);
      }

      return tableau.getSolution();
   }
}
