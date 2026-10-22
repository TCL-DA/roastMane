package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Hyperplane<S extends Space> {
   Hyperplane<S> copySelf();

   double getOffset(Point<S> var1);

   Point<S> project(Point<S> var1);

   double getTolerance();

   boolean sameOrientationAs(Hyperplane<S> var1);

   SubHyperplane<S> wholeHyperplane();

   Region<S> wholeSpace();
}
