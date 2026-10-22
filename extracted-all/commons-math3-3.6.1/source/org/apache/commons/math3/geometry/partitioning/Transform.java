package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Transform<S extends Space, T extends Space> {
   Point<S> apply(Point<S> var1);

   Hyperplane<S> apply(Hyperplane<S> var1);

   SubHyperplane<T> apply(SubHyperplane<T> var1, Hyperplane<S> var2, Hyperplane<S> var3);
}
