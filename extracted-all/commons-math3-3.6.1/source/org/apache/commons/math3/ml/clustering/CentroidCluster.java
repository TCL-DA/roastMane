package org.apache.commons.math3.ml.clustering;

public class CentroidCluster<T extends Clusterable> extends Cluster<T> {
   private static final long serialVersionUID = -3075288519071812288L;
   private final Clusterable center;

   public CentroidCluster(Clusterable center) {
      this.center = center;
   }

   public Clusterable getCenter() {
      return this.center;
   }
}
