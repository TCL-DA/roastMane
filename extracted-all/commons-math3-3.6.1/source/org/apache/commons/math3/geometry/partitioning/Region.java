package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Region<S extends Space> {
   Region<S> buildNew(BSPTree<S> var1);

   Region<S> copySelf();

   boolean isEmpty();

   boolean isEmpty(BSPTree<S> var1);

   boolean isFull();

   boolean isFull(BSPTree<S> var1);

   boolean contains(Region<S> var1);

   Region.Location checkPoint(Point<S> var1);

   BoundaryProjection<S> projectToBoundary(Point<S> var1);

   BSPTree<S> getTree(boolean var1);

   double getBoundarySize();

   double getSize();

   Point<S> getBarycenter();

   @Deprecated
   Side side(Hyperplane<S> var1);

   SubHyperplane<S> intersection(SubHyperplane<S> var1);

   public static enum Location {
      INSIDE,
      OUTSIDE,
      BOUNDARY;
   }
}
