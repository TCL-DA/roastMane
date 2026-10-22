package org.apache.commons.math3.ml.neuralnet.twod.util;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.util.Pair;

public class TopographicErrorHistogram implements MapDataVisualization {
   private final DistanceMeasure distance;
   private final boolean relativeCount;

   public TopographicErrorHistogram(boolean relativeCount, DistanceMeasure distance) {
      this.relativeCount = relativeCount;
      this.distance = distance;
   }

   @Override
   public double[][] computeImage(NeuronSquareMesh2D map, Iterable<double[]> data) {
      int nR = map.getNumberOfRows();
      int nC = map.getNumberOfColumns();
      Network net = map.getNetwork();
      LocationFinder finder = new LocationFinder(map);
      int[][] hit = new int[nR][nC];
      double[][] error = new double[nR][nC];

      for (double[] sample : data) {
         Pair<Neuron, Neuron> p = MapUtils.findBestAndSecondBest(sample, map, this.distance);
         Neuron best = p.getFirst();
         LocationFinder.Location loc = finder.getLocation(best);
         int row = loc.getRow();
         int col = loc.getColumn();
         hit[row][col]++;
         if (!net.getNeighbours(best).contains(p.getSecond())) {
            error[row][col]++;
         }
      }

      if (this.relativeCount) {
         for (int r = 0; r < nR; r++) {
            for (int c = 0; c < nC; c++) {
               error[r][c] = error[r][c] / (double)hit[r][c];
            }
         }
      }

      return error;
   }
}
