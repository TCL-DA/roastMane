package org.apache.commons.math3.optim.linear;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class SimplexSolver extends LinearOptimizer {
   static final int DEFAULT_ULPS = 10;
   static final double DEFAULT_CUT_OFF = 1.0E-10;
   private static final double DEFAULT_EPSILON = 1.0E-6;
   private final double epsilon;
   private final int maxUlps;
   private final double cutOff;
   private PivotSelectionRule pivotSelection;
   private SolutionCallback solutionCallback;

   public SimplexSolver() {
      this(1.0E-6, 10, 1.0E-10);
   }

   public SimplexSolver(double epsilon) {
      this(epsilon, 10, 1.0E-10);
   }

   public SimplexSolver(double epsilon, int maxUlps) {
      this(epsilon, maxUlps, 1.0E-10);
   }

   public SimplexSolver(double epsilon, int maxUlps, double cutOff) {
      this.epsilon = epsilon;
      this.maxUlps = maxUlps;
      this.cutOff = cutOff;
      this.pivotSelection = PivotSelectionRule.DANTZIG;
   }

   @Override
   public PointValuePair optimize(OptimizationData... optData) throws TooManyIterationsException {
      return super.optimize(optData);
   }

   @Override
   protected void parseOptimizationData(OptimizationData... optData) {
      super.parseOptimizationData(optData);
      this.solutionCallback = null;

      for (OptimizationData data : optData) {
         if (data instanceof SolutionCallback) {
            this.solutionCallback = (SolutionCallback)data;
         } else if (data instanceof PivotSelectionRule) {
            this.pivotSelection = (PivotSelectionRule)data;
         }
      }
   }

   private Integer getPivotColumn(SimplexTableau tableau) {
      double minValue = 0.0;
      Integer minPos = null;

      for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
         double entry = tableau.getEntry(0, i);
         if (entry < minValue) {
            minValue = entry;
            minPos = i;
            if (this.pivotSelection == PivotSelectionRule.BLAND && this.isValidPivotColumn(tableau, i)) {
               break;
            }
         }
      }

      return minPos;
   }

   private boolean isValidPivotColumn(SimplexTableau tableau, int col) {
      for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
         double entry = tableau.getEntry(i, col);
         if (Precision.compareTo(entry, 0.0, this.cutOff) > 0) {
            return true;
         }
      }

      return false;
   }

   private Integer getPivotRow(SimplexTableau tableau, int col) {
      List<Integer> minRatioPositions = new ArrayList<>();
      double minRatio = Double.MAX_VALUE;

      for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
         double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
         double entry = tableau.getEntry(i, col);
         if (Precision.compareTo(entry, 0.0, this.cutOff) > 0) {
            double ratio = FastMath.abs(rhs / entry);
            int cmp = Double.compare(ratio, minRatio);
            if (cmp == 0) {
               minRatioPositions.add(i);
            } else if (cmp < 0) {
               minRatio = ratio;
               minRatioPositions.clear();
               minRatioPositions.add(i);
            }
         }
      }

      if (minRatioPositions.size() == 0) {
         return null;
      } else if (minRatioPositions.size() <= 1) {
         return minRatioPositions.get(0);
      } else {
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

         Integer minRow = null;
         int minIndex = tableau.getWidth();

         for (Integer row : minRatioPositions) {
            int basicVar = tableau.getBasicVariable(row);
            if (basicVar < minIndex) {
               minIndex = basicVar;
               minRow = row;
            }
         }

         return minRow;
      }
   }

   protected void doIteration(SimplexTableau tableau) throws TooManyIterationsException, UnboundedSolutionException {
      this.incrementIterationCount();
      Integer pivotCol = this.getPivotColumn(tableau);
      Integer pivotRow = this.getPivotRow(tableau, pivotCol);
      if (pivotRow == null) {
         throw new UnboundedSolutionException();
      } else {
         tableau.performRowOperations(pivotCol, pivotRow);
      }
   }

   protected void solvePhase1(SimplexTableau tableau) throws TooManyIterationsException, UnboundedSolutionException, NoFeasibleSolutionException {
      if (tableau.getNumArtificialVariables() != 0) {
         while (!tableau.isOptimal()) {
            this.doIteration(tableau);
         }

         if (!Precision.equals(tableau.getEntry(0, tableau.getRhsOffset()), 0.0, this.epsilon)) {
            throw new NoFeasibleSolutionException();
         }
      }
   }

   public PointValuePair doOptimize() throws TooManyIterationsException, UnboundedSolutionException, NoFeasibleSolutionException {
      if (this.solutionCallback != null) {
         this.solutionCallback.setTableau(null);
      }

      SimplexTableau tableau = new SimplexTableau(
         this.getFunction(), this.getConstraints(), this.getGoalType(), this.isRestrictedToNonNegative(), this.epsilon, this.maxUlps
      );
      this.solvePhase1(tableau);
      tableau.dropPhase1Objective();
      if (this.solutionCallback != null) {
         this.solutionCallback.setTableau(tableau);
      }

      while (!tableau.isOptimal()) {
         this.doIteration(tableau);
      }

      PointValuePair solution = tableau.getSolution();
      if (this.isRestrictedToNonNegative()) {
         double[] coeff = solution.getPoint();

         for (int i = 0; i < coeff.length; i++) {
            if (Precision.compareTo(coeff[i], 0.0, this.epsilon) < 0) {
               throw new NoFeasibleSolutionException();
            }
         }
      }

      return solution;
   }
}
