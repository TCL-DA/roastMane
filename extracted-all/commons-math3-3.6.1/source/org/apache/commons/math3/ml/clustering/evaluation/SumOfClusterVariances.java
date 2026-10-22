package org.apache.commons.math3.ml.clustering.evaluation;

import java.util.List;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

public class SumOfClusterVariances<T extends Clusterable> extends ClusterEvaluator<T> {
   public SumOfClusterVariances(DistanceMeasure measure) {
      super(measure);
   }

   @Override
   public double score(List<? extends Cluster<T>> clusters) {
      double varianceSum = 0.0;

      for (Cluster<T> cluster : clusters) {
         if (!cluster.getPoints().isEmpty()) {
            Clusterable center = this.centroidOf(cluster);
            Variance stat = new Variance();

            for (T point : cluster.getPoints()) {
               stat.increment(this.distance(point, center));
            }

            varianceSum += stat.getResult();
         }
      }

      return varianceSum;
   }
}
