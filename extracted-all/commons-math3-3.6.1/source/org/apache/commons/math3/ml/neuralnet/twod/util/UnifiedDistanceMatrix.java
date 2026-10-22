package org.apache.commons.math3.ml.neuralnet.twod.util;

import java.util.Collection;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;

public class UnifiedDistanceMatrix implements MapVisualization {
   private final boolean individualDistances;
   private final DistanceMeasure distance;

   public UnifiedDistanceMatrix(boolean individualDistances, DistanceMeasure distance) {
      this.individualDistances = individualDistances;
      this.distance = distance;
   }

   @Override
   public double[][] computeImage(NeuronSquareMesh2D map) {
      return this.individualDistances ? this.individualDistances(map) : this.averageDistances(map);
   }

   private double[][] individualDistances(NeuronSquareMesh2D map) {
      int numRows = map.getNumberOfRows();
      int numCols = map.getNumberOfColumns();
      double[][] uMatrix = new double[numRows * 2 + 1][numCols * 2 + 1];

      for (int i = 0; i < numRows; i++) {
         int iR = 2 * i + 1;

         for (int j = 0; j < numCols; j++) {
            int jR = 2 * j + 1;
            double[] current = map.getNeuron(i, j).getFeatures();
            Neuron neighbour = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.CENTER);
            if (neighbour != null) {
               uMatrix[iR][jR + 1] = this.distance.compute(current, neighbour.getFeatures());
            }

            neighbour = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.CENTER, NeuronSquareMesh2D.VerticalDirection.DOWN);
            if (neighbour != null) {
               uMatrix[iR + 1][jR] = this.distance.compute(current, neighbour.getFeatures());
            }
         }
      }

      for (int i = 0; i < numRows; i++) {
         int iR = 2 * i + 1;

         for (int j = 0; j < numCols; j++) {
            int jRx = 2 * j + 1;
            Neuron currentx = map.getNeuron(i, j);
            Neuron right = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.CENTER);
            Neuron bottom = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.CENTER, NeuronSquareMesh2D.VerticalDirection.DOWN);
            Neuron bottomRight = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.DOWN);
            double current2BottomRight = bottomRight == null ? 0.0 : this.distance.compute(currentx.getFeatures(), bottomRight.getFeatures());
            double right2Bottom = right != null && bottom != null ? this.distance.compute(right.getFeatures(), bottom.getFeatures()) : 0.0;
            uMatrix[iR + 1][jRx + 1] = 0.5 * (current2BottomRight + right2Bottom);
         }
      }

      int lastRow = uMatrix.length - 1;
      uMatrix[0] = uMatrix[lastRow];
      int lastCol = uMatrix[0].length - 1;

      for (int r = 0; r < lastRow; r++) {
         uMatrix[r][0] = uMatrix[r][lastCol];
      }

      return uMatrix;
   }

   private double[][] averageDistances(NeuronSquareMesh2D map) {
      int numRows = map.getNumberOfRows();
      int numCols = map.getNumberOfColumns();
      double[][] uMatrix = new double[numRows][numCols];
      Network net = map.getNetwork();

      for (int i = 0; i < numRows; i++) {
         for (int j = 0; j < numCols; j++) {
            Neuron neuron = map.getNeuron(i, j);
            Collection<Neuron> neighbours = net.getNeighbours(neuron);
            double[] features = neuron.getFeatures();
            double d = 0.0;
            int count = 0;

            for (Neuron n : neighbours) {
               count++;
               d += this.distance.compute(features, n.getFeatures());
            }

            uMatrix[i][j] = d / (double)count;
         }
      }

      return uMatrix;
   }
}
