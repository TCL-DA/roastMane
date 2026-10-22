package org.apache.commons.math3.ml.neuralnet.twod.util;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;

public class QuantizationError implements MapDataVisualization {
   private final DistanceMeasure distance;

   public QuantizationError(DistanceMeasure distance) {
      this.distance = distance;
   }

   @Override
   public double[][] computeImage(NeuronSquareMesh2D map, Iterable<double[]> data) {
      int nR = map.getNumberOfRows();
      int nC = map.getNumberOfColumns();
      LocationFinder finder = new LocationFinder(map);
      int[][] hit = new int[nR][nC];
      double[][] error = new double[nR][nC];

      for (double[] sample : data) {
         Neuron best = MapUtils.findBest(sample, map, this.distance);
         LocationFinder.Location loc = finder.getLocation(best);
         int row = loc.getRow();
         int col = loc.getColumn();
         hit[row][col]++;
         error[row][col] = error[row][col] + this.distance.compute(sample, best.getFeatures());
      }

      for (int r = 0; r < nR; r++) {
         for (int c = 0; c < nC; c++) {
            int count = hit[r][c];
            if (count != 0) {
               error[r][c] = error[r][c] / (double)count;
            }
         }
      }

      return error;
   }
}
