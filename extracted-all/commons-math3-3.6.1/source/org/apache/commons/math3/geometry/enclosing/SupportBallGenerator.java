package org.apache.commons.math3.geometry.enclosing;

import java.util.List;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface SupportBallGenerator<S extends Space, P extends Point<S>> {
   EnclosingBall<S, P> ballOnSupport(List<P> var1);
}
