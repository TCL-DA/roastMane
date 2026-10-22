package org.apache.commons.math3.ml.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.util.MathUtils;

public class DBSCANClusterer<T extends Clusterable> extends Clusterer<T> {
   private final double eps;
   private final int minPts;

   public DBSCANClusterer(double eps, int minPts) throws NotPositiveException {
      this(eps, minPts, new EuclideanDistance());
   }

   public DBSCANClusterer(double eps, int minPts, DistanceMeasure measure) throws NotPositiveException {
      super(measure);
      if (eps < 0.0) {
         throw new NotPositiveException(eps);
      } else if (minPts < 0) {
         throw new NotPositiveException(minPts);
      } else {
         this.eps = eps;
         this.minPts = minPts;
      }
   }

   public double getEps() {
      return this.eps;
   }

   public int getMinPts() {
      return this.minPts;
   }

   @Override
   public List<Cluster<T>> cluster(Collection<T> points) throws NullArgumentException {
      MathUtils.checkNotNull(points);
      List<Cluster<T>> clusters = new ArrayList<>();
      Map<Clusterable, DBSCANClusterer.PointStatus> visited = new HashMap<>();

      for (T point : points) {
         if (visited.get(point) == null) {
            List<T> neighbors = this.getNeighbors(point, points);
            if (neighbors.size() >= this.minPts) {
               Cluster<T> cluster = new Cluster<>();
               clusters.add(this.expandCluster(cluster, point, neighbors, points, visited));
            } else {
               visited.put(point, DBSCANClusterer.PointStatus.NOISE);
            }
         }
      }

      return clusters;
   }

   private Cluster<T> expandCluster(Cluster<T> cluster, T point, List<T> neighbors, Collection<T> points, Map<Clusterable, DBSCANClusterer.PointStatus> visited) {
      cluster.addPoint(point);
      visited.put(point, DBSCANClusterer.PointStatus.PART_OF_CLUSTER);
      List<T> seeds = new ArrayList<>(neighbors);

      for (int index = 0; index < seeds.size(); index++) {
         T current = seeds.get(index);
         DBSCANClusterer.PointStatus pStatus = visited.get(current);
         if (pStatus == null) {
            List<T> currentNeighbors = this.getNeighbors(current, points);
            if (currentNeighbors.size() >= this.minPts) {
               seeds = this.merge(seeds, currentNeighbors);
            }
         }

         if (pStatus != DBSCANClusterer.PointStatus.PART_OF_CLUSTER) {
            visited.put(current, DBSCANClusterer.PointStatus.PART_OF_CLUSTER);
            cluster.addPoint(current);
         }
      }

      return cluster;
   }

   private List<T> getNeighbors(T point, Collection<T> points) {
      List<T> neighbors = new ArrayList<>();

      for (T neighbor : points) {
         if (point != neighbor && this.distance(neighbor, point) <= this.eps) {
            neighbors.add(neighbor);
         }
      }

      return neighbors;
   }

   private List<T> merge(List<T> one, List<T> two) {
      Set<T> oneSet = new HashSet<>(one);

      for (T item : two) {
         if (!oneSet.contains(item)) {
            one.add(item);
         }
      }

      return one;
   }

   private static enum PointStatus {
      NOISE,
      PART_OF_CLUSTER;
   }
}
