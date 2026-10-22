package org.apache.commons.math3.ml.neuralnet.twod.util;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;

public class HitHistogram implements MapDataVisualization {
   private final DistanceMeasure distance;
   private final boolean normalizeCount;

   public HitHistogram(boolean normalizeCount, DistanceMeasure distance) {
      this.normalizeCount = normalizeCount;
      this.distance = distance;
   }

   @Override
   public double[][] computeImage(NeuronSquareMesh2D map, Iterable<double[]> data) {
      int nR = map.getNumberOfRows();
      int nC = map.getNumberOfColumns();
      LocationFinder finder = new LocationFinder(map);
      int numSamples = 0;
      double[][] hit = new double[nR][nC];

      for (double[] sample : data) {
         Neuron best = MapUtils.findBest(sample, map, this.distance);
         LocationFinder.Location loc = finder.getLocation(best);
         int row = loc.getRow();
         int col = loc.getColumn();
         hit[row][col]++;
         numSamples++;
      }

      if (this.normalizeCount) {
         for (int r = 0; r < nR; r++) {
            for (int c = 0; c < nC; c++) {
               hit[r][c] = hit[r][c] / (double)numSamples;
            }
         }
      }

      return hit;
   }
}
