package org.apache.commons.math3.stat.clustering;

import java.util.Collection;

@Deprecated
public interface Clusterable<T> {
   double distanceFrom(T var1);

   T centroidOf(Collection<T> var1);
}
