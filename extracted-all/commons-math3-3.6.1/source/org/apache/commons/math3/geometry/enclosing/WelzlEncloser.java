package org.apache.commons.math3.geometry.enclosing;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public class WelzlEncloser<S extends Space, P extends Point<S>> implements Encloser<S, P> {
   private final double tolerance;
   private final SupportBallGenerator<S, P> generator;

   public WelzlEncloser(double tolerance, SupportBallGenerator<S, P> generator) {
      this.tolerance = tolerance;
      this.generator = generator;
   }

   @Override
   public EnclosingBall<S, P> enclose(Iterable<P> points) {
      return points != null && points.iterator().hasNext() ? this.pivotingBall(points) : this.generator.ballOnSupport(new ArrayList<>());
   }

   private EnclosingBall<S, P> pivotingBall(Iterable<P> points) {
      P first = points.iterator().next();
      List<P> extreme = new ArrayList<>(first.getSpace().getDimension() + 1);
      List<P> support = new ArrayList<>(first.getSpace().getDimension() + 1);
      extreme.add(first);
      EnclosingBall<S, P> ball = this.moveToFrontBall(extreme, extreme.size(), support);

      while (true) {
         P farthest = this.selectFarthest(points, ball);
         if (ball.contains(farthest, this.tolerance)) {
            return ball;
         }

         support.clear();
         support.add(farthest);
         EnclosingBall<S, P> savedBall = ball;
         ball = this.moveToFrontBall(extreme, extreme.size(), support);
         if (ball.getRadius() < savedBall.getRadius()) {
            throw new MathInternalError();
         }

         extreme.add(0, farthest);
         extreme.subList(ball.getSupportSize(), extreme.size()).clear();
      }
   }

   private EnclosingBall<S, P> moveToFrontBall(List<P> extreme, int nbExtreme, List<P> support) {
      EnclosingBall<S, P> ball = this.generator.ballOnSupport(support);
      if (ball.getSupportSize() <= ball.getCenter().getSpace().getDimension()) {
         for (int i = 0; i < nbExtreme; i++) {
            P pi = extreme.get(i);
            if (!ball.contains(pi, this.tolerance)) {
               support.add(pi);
               ball = this.moveToFrontBall(extreme, i, support);
               support.remove(support.size() - 1);

               for (int j = i; j > 0; j--) {
                  extreme.set(j, extreme.get(j - 1));
               }

               extreme.set(0, pi);
            }
         }
      }

      return ball;
   }

   public P selectFarthest(Iterable<P> points, EnclosingBall<S, P> ball) {
      P center = ball.getCenter();
      P farthest = null;
      double dMax = -1.0;

      for (P point : points) {
         double d = point.distance(center);
         if (d > dMax) {
            farthest = point;
            dMax = d;
         }
      }

      return farthest;
   }
}
