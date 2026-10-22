package org.apache.commons.math3.ml.neuralnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.util.Pair;

public class MapUtils {
   private MapUtils() {
   }

   public static Neuron findBest(double[] features, Iterable<Neuron> neurons, DistanceMeasure distance) {
      Neuron best = null;
      double min = Double.POSITIVE_INFINITY;

      for (Neuron n : neurons) {
         double d = distance.compute(n.getFeatures(), features);
         if (d < min) {
            min = d;
            best = n;
         }
      }

      return best;
   }

   public static Pair<Neuron, Neuron> findBestAndSecondBest(double[] features, Iterable<Neuron> neurons, DistanceMeasure distance) {
      Neuron[] best = new Neuron[]{null, null};
      double[] min = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};

      for (Neuron n : neurons) {
         double d = distance.compute(n.getFeatures(), features);
         if (d < min[0]) {
            min[1] = min[0];
            best[1] = best[0];
            min[0] = d;
            best[0] = n;
         } else if (d < min[1]) {
            min[1] = d;
            best[1] = n;
         }
      }

      return new Pair<>(best[0], best[1]);
   }

   public static Neuron[] sort(double[] features, Iterable<Neuron> neurons, DistanceMeasure distance) {
      List<MapUtils.PairNeuronDouble> list = new ArrayList<>();

      for (Neuron n : neurons) {
         double d = distance.compute(n.getFeatures(), features);
         list.add(new MapUtils.PairNeuronDouble(n, d));
      }

      Collections.sort(list, MapUtils.PairNeuronDouble.COMPARATOR);
      int len = list.size();
      Neuron[] sorted = new Neuron[len];

      for (int i = 0; i < len; i++) {
         sorted[i] = list.get(i).getNeuron();
      }

      return sorted;
   }

   public static double[][] computeU(NeuronSquareMesh2D map, DistanceMeasure distance) {
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
               d += distance.compute(features, n.getFeatures());
            }

            uMatrix[i][j] = d / (double)count;
         }
      }

      return uMatrix;
   }

   public static int[][] computeHitHistogram(Iterable<double[]> data, NeuronSquareMesh2D map, DistanceMeasure distance) {
      HashMap<Neuron, Integer> hit = new HashMap<>();
      Network net = map.getNetwork();

      for (double[] f : data) {
         Neuron best = findBest(f, net, distance);
         Integer count = hit.get(best);
         if (count == null) {
            hit.put(best, 1);
         } else {
            hit.put(best, count + 1);
         }
      }

      int numRows = map.getNumberOfRows();
      int numCols = map.getNumberOfColumns();
      int[][] histo = new int[numRows][numCols];

      for (int i = 0; i < numRows; i++) {
         for (int j = 0; j < numCols; j++) {
            Neuron neuron = map.getNeuron(i, j);
            Integer count = hit.get(neuron);
            if (count == null) {
               histo[i][j] = 0;
            } else {
               histo[i][j] = count;
            }
         }
      }

      return histo;
   }

   public static double computeQuantizationError(Iterable<double[]> data, Iterable<Neuron> neurons, DistanceMeasure distance) {
      double d = 0.0;
      int count = 0;

      for (double[] f : data) {
         count++;
         d += distance.compute(f, findBest(f, neurons, distance).getFeatures());
      }

      if (count == 0) {
         throw new NoDataException();
      } else {
         return d / (double)count;
      }
   }

   public static double computeTopographicError(Iterable<double[]> data, Network net, DistanceMeasure distance) {
      int notAdjacentCount = 0;
      int count = 0;

      for (double[] f : data) {
         count++;
         Pair<Neuron, Neuron> p = findBestAndSecondBest(f, net, distance);
         if (!net.getNeighbours(p.getFirst()).contains(p.getSecond())) {
            notAdjacentCount++;
         }
      }

      if (count == 0) {
         throw new NoDataException();
      } else {
         return (double)notAdjacentCount / (double)count;
      }
   }

   private static class PairNeuronDouble {
      static final Comparator<MapUtils.PairNeuronDouble> COMPARATOR = new Comparator<MapUtils.PairNeuronDouble>() {
         public int compare(MapUtils.PairNeuronDouble o1, MapUtils.PairNeuronDouble o2) {
            return Double.compare(o1.value, o2.value);
         }
      };
      private final Neuron neuron;
      private final double value;

      PairNeuronDouble(Neuron neuron, double value) {
         this.neuron = neuron;
         this.value = value;
      }

      public Neuron getNeuron() {
         return this.neuron;
      }
   }
}
