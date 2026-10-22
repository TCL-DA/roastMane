package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Embedding<S extends Space, T extends Space> {
   Point<T> toSubSpace(Point<S> var1);

   Point<S> toSpace(Point<T> var1);
}
