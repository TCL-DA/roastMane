package org.apache.commons.math3.geometry.enclosing;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Encloser<S extends Space, P extends Point<S>> {
   EnclosingBall<S, P> enclose(Iterable<P> var1);
}
