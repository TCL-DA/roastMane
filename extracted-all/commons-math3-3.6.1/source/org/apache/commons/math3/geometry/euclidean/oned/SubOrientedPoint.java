package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

public class SubOrientedPoint extends AbstractSubHyperplane<Euclidean1D, Euclidean1D> {
   public SubOrientedPoint(Hyperplane<Euclidean1D> hyperplane, Region<Euclidean1D> remainingRegion) {
      super(hyperplane, remainingRegion);
   }

   @Override
   public double getSize() {
      return 0.0;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   protected AbstractSubHyperplane<Euclidean1D, Euclidean1D> buildNew(Hyperplane<Euclidean1D> hyperplane, Region<Euclidean1D> remainingRegion) {
      return new SubOrientedPoint(hyperplane, remainingRegion);
   }

   @Override
   public SubHyperplane.SplitSubHyperplane<Euclidean1D> split(Hyperplane<Euclidean1D> hyperplane) {
      double global = hyperplane.getOffset(((OrientedPoint)this.getHyperplane()).getLocation());
      if (global < -1.0E-10) {
         return new SubHyperplane.SplitSubHyperplane<>(null, this);
      } else {
         return global > 1.0E-10 ? new SubHyperplane.SplitSubHyperplane<>(this, null) : new SubHyperplane.SplitSubHyperplane<>(null, null);
      }
   }
}
