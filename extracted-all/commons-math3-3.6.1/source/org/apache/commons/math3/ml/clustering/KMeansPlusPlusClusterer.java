package org.apache.commons.math3.ml.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.util.MathUtils;

public class KMeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T> {
   private final int k;
   private final int maxIterations;
   private final RandomGenerator random;
   private final KMeansPlusPlusClusterer.EmptyClusterStrategy emptyStrategy;

   public KMeansPlusPlusClusterer(int k) {
      this(k, -1);
   }

   public KMeansPlusPlusClusterer(int k, int maxIterations) {
      this(k, maxIterations, new EuclideanDistance());
   }

   public KMeansPlusPlusClusterer(int k, int maxIterations, DistanceMeasure measure) {
      this(k, maxIterations, measure, new JDKRandomGenerator());
   }

   public KMeansPlusPlusClusterer(int k, int maxIterations, DistanceMeasure measure, RandomGenerator random) {
      this(k, maxIterations, measure, random, KMeansPlusPlusClusterer.EmptyClusterStrategy.LARGEST_VARIANCE);
   }

   public KMeansPlusPlusClusterer(
      int k, int maxIterations, DistanceMeasure measure, RandomGenerator random, KMeansPlusPlusClusterer.EmptyClusterStrategy emptyStrategy
   ) {
      super(measure);
      this.k = k;
      this.maxIterations = maxIterations;
      this.random = random;
      this.emptyStrategy = emptyStrategy;
   }

   public int getK() {
      return this.k;
   }

   public int getMaxIterations() {
      return this.maxIterations;
   }

   public RandomGenerator getRandomGenerator() {
      return this.random;
   }

   public KMeansPlusPlusClusterer.EmptyClusterStrategy getEmptyClusterStrategy() {
      return this.emptyStrategy;
   }

   @Override
   public List<CentroidCluster<T>> cluster(Collection<T> points) throws MathIllegalArgumentException, ConvergenceException {
      MathUtils.checkNotNull(points);
      if (points.size() < this.k) {
         throw new NumberIsTooSmallException(points.size(), this.k, false);
      } else {
         List<CentroidCluster<T>> clusters = this.chooseInitialCenters(points);
         int[] assignments = new int[points.size()];
         this.assignPointsToClusters(clusters, points, assignments);
         int max = this.maxIterations < 0 ? Integer.MAX_VALUE : this.maxIterations;

         for (int count = 0; count < max; count++) {
            boolean emptyCluster = false;
            List<CentroidCluster<T>> newClusters = new ArrayList<>();

            for (CentroidCluster<T> cluster : clusters) {
               Clusterable newCenter;
               if (cluster.getPoints().isEmpty()) {
                  switch (this.emptyStrategy) {
                     case LARGEST_VARIANCE:
                        newCenter = this.getPointFromLargestVarianceCluster(clusters);
                        break;
                     case LARGEST_POINTS_NUMBER:
                        newCenter = this.getPointFromLargestNumberCluster(clusters);
                        break;
                     case FARTHEST_POINT:
                        newCenter = this.getFarthestPoint(clusters);
                        break;
                     default:
                        throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
                  }

                  emptyCluster = true;
               } else {
                  newCenter = this.centroidOf(cluster.getPoints(), cluster.getCenter().getPoint().length);
               }

               newClusters.add(new CentroidCluster<>(newCenter));
            }

            int changes = this.assignPointsToClusters(newClusters, points, assignments);
            clusters = newClusters;
            if (changes == 0 && !emptyCluster) {
               return newClusters;
            }
         }

         return clusters;
      }
   }

   private int assignPointsToClusters(List<CentroidCluster<T>> clusters, Collection<T> points, int[] assignments) {
      int assignedDifferently = 0;
      int pointIndex = 0;

      for (T p : points) {
         int clusterIndex = this.getNearestCluster(clusters, p);
         if (clusterIndex != assignments[pointIndex]) {
            assignedDifferently++;
         }

         CentroidCluster<T> cluster = clusters.get(clusterIndex);
         cluster.addPoint(p);
         assignments[pointIndex++] = clusterIndex;
      }

      return assignedDifferently;
   }

   private List<CentroidCluster<T>> chooseInitialCenters(Collection<T> points) {
      List<T> pointList = Collections.unmodifiableList(new ArrayList<>(points));
      int numPoints = pointList.size();
      boolean[] taken = new boolean[numPoints];
      List<CentroidCluster<T>> resultSet = new ArrayList<>();
      int firstPointIndex = this.random.nextInt(numPoints);
      T firstPoint = pointList.get(firstPointIndex);
      resultSet.add(new CentroidCluster<>(firstPoint));
      taken[firstPointIndex] = true;
      double[] minDistSquared = new double[numPoints];

      for (int i = 0; i < numPoints; i++) {
         if (i != firstPointIndex) {
            double d = this.distance(firstPoint, pointList.get(i));
            minDistSquared[i] = d * d;
         }
      }

      while (resultSet.size() < this.k) {
         double distSqSum = 0.0;

         for (int ix = 0; ix < numPoints; ix++) {
            if (!taken[ix]) {
               distSqSum += minDistSquared[ix];
            }
         }

         double r = this.random.nextDouble() * distSqSum;
         int nextPointIndex = -1;
         double sum = 0.0;

         for (int ixx = 0; ixx < numPoints; ixx++) {
            if (!taken[ixx]) {
               sum += minDistSquared[ixx];
               if (sum >= r) {
                  nextPointIndex = ixx;
                  break;
               }
            }
         }

         if (nextPointIndex == -1) {
            for (int ixxx = numPoints - 1; ixxx >= 0; ixxx--) {
               if (!taken[ixxx]) {
                  nextPointIndex = ixxx;
                  break;
               }
            }
         }

         if (nextPointIndex < 0) {
            break;
         }

         T p = pointList.get(nextPointIndex);
         resultSet.add(new CentroidCluster<>(p));
         taken[nextPointIndex] = true;
         if (resultSet.size() < this.k) {
            for (int j = 0; j < numPoints; j++) {
               if (!taken[j]) {
                  double d = this.distance(p, pointList.get(j));
                  double d2 = d * d;
                  if (d2 < minDistSquared[j]) {
                     minDistSquared[j] = d2;
                  }
               }
            }
         }
      }

      return resultSet;
   }

   private T getPointFromLargestVarianceCluster(Collection<CentroidCluster<T>> clusters) throws ConvergenceException {
      double maxVariance = Double.NEGATIVE_INFINITY;
      Cluster<T> selected = null;

      for (CentroidCluster<T> cluster : clusters) {
         if (!cluster.getPoints().isEmpty()) {
            Clusterable center = cluster.getCenter();
            Variance stat = new Variance();

            for (T point : cluster.getPoints()) {
               stat.increment(this.distance(point, center));
            }

            double variance = stat.getResult();
            if (variance > maxVariance) {
               maxVariance = variance;
               selected = cluster;
            }
         }
      }

      if (selected == null) {
         throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
      } else {
         List<T> selectedPoints = selected.getPoints();
         return selectedPoints.remove(this.random.nextInt(selectedPoints.size()));
      }
   }

   private T getPointFromLargestNumberCluster(Collection<? extends Cluster<T>> clusters) throws ConvergenceException {
      int maxNumber = 0;
      Cluster<T> selected = null;

      for (Cluster<T> cluster : clusters) {
         int number = cluster.getPoints().size();
         if (number > maxNumber) {
            maxNumber = number;
            selected = cluster;
         }
      }

      if (selected == null) {
         throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
      } else {
         List<T> selectedPoints = selected.getPoints();
         return selectedPoints.remove(this.random.nextInt(selectedPoints.size()));
      }
   }

   private T getFarthestPoint(Collection<CentroidCluster<T>> clusters) throws ConvergenceException {
      double maxDistance = Double.NEGATIVE_INFINITY;
      Cluster<T> selectedCluster = null;
      int selectedPoint = -1;

      for (CentroidCluster<T> cluster : clusters) {
         Clusterable center = cluster.getCenter();
         List<T> points = cluster.getPoints();

         for (int i = 0; i < points.size(); i++) {
            double distance = this.distance(points.get(i), center);
            if (distance > maxDistance) {
               maxDistance = distance;
               selectedCluster = cluster;
               selectedPoint = i;
            }
         }
      }

      if (selectedCluster == null) {
         throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
      } else {
         return selectedCluster.getPoints().remove(selectedPoint);
      }
   }

   private int getNearestCluster(Collection<CentroidCluster<T>> clusters, T point) {
      double minDistance = Double.MAX_VALUE;
      int clusterIndex = 0;
      int minCluster = 0;

      for (CentroidCluster<T> c : clusters) {
         double distance = this.distance(point, c.getCenter());
         if (distance < minDistance) {
            minDistance = distance;
            minCluster = clusterIndex;
         }

         clusterIndex++;
      }

      return minCluster;
   }

   private Clusterable centroidOf(Collection<T> points, int dimension) {
      double[] centroid = new double[dimension];

      for (T p : points) {
         double[] point = p.getPoint();

         for (int i = 0; i < centroid.length; i++) {
            centroid[i] += point[i];
         }
      }

      for (int i = 0; i < centroid.length; i++) {
         centroid[i] /= (double)points.size();
      }

      return new DoublePoint(centroid);
   }

   public static enum EmptyClusterStrategy {
      LARGEST_VARIANCE,
      LARGEST_POINTS_NUMBER,
      FARTHEST_POINT,
      ERROR;
   }
}
