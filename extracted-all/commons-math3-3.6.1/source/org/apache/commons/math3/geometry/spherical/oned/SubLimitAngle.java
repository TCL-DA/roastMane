package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

public class SubLimitAngle extends AbstractSubHyperplane<Sphere1D, Sphere1D> {
   public SubLimitAngle(Hyperplane<Sphere1D> hyperplane, Region<Sphere1D> remainingRegion) {
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
   protected AbstractSubHyperplane<Sphere1D, Sphere1D> buildNew(Hyperplane<Sphere1D> hyperplane, Region<Sphere1D> remainingRegion) {
      return new SubLimitAngle(hyperplane, remainingRegion);
   }

   @Override
   public SubHyperplane.SplitSubHyperplane<Sphere1D> split(Hyperplane<Sphere1D> hyperplane) {
      double global = hyperplane.getOffset(((LimitAngle)this.getHyperplane()).getLocation());
      return global < -1.0E-10 ? new SubHyperplane.SplitSubHyperplane<>(null, this) : new SubHyperplane.SplitSubHyperplane<>(this, null);
   }
}
