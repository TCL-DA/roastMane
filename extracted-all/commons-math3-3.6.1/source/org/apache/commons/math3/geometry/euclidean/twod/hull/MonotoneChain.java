package org.apache.commons.math3.geometry.euclidean.twod.hull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class MonotoneChain extends AbstractConvexHullGenerator2D {
   public MonotoneChain() {
      this(false);
   }

   public MonotoneChain(boolean includeCollinearPoints) {
      super(includeCollinearPoints);
   }

   public MonotoneChain(boolean includeCollinearPoints, double tolerance) {
      super(includeCollinearPoints, tolerance);
   }

   @Override
   public Collection<Vector2D> findHullVertices(Collection<Vector2D> points) {
      List<Vector2D> pointsSortedByXAxis = new ArrayList<>(points);
      Collections.sort(pointsSortedByXAxis, new Comparator<Vector2D>() {
         public int compare(Vector2D o1, Vector2D o2) {
            double tolerance = MonotoneChain.this.getTolerance();
            int diff = Precision.compareTo(o1.getX(), o2.getX(), tolerance);
            return diff == 0 ? Precision.compareTo(o1.getY(), o2.getY(), tolerance) : diff;
         }
      });
      List<Vector2D> lowerHull = new ArrayList<>();

      for (Vector2D p : pointsSortedByXAxis) {
         this.updateHull(p, lowerHull);
      }

      List<Vector2D> upperHull = new ArrayList<>();

      for (int idx = pointsSortedByXAxis.size() - 1; idx >= 0; idx--) {
         Vector2D p = pointsSortedByXAxis.get(idx);
         this.updateHull(p, upperHull);
      }

      List<Vector2D> hullVertices = new ArrayList<>(lowerHull.size() + upperHull.size() - 2);

      for (int idx = 0; idx < lowerHull.size() - 1; idx++) {
         hullVertices.add(lowerHull.get(idx));
      }

      for (int idx = 0; idx < upperHull.size() - 1; idx++) {
         hullVertices.add(upperHull.get(idx));
      }

      if (hullVertices.isEmpty() && !lowerHull.isEmpty()) {
         hullVertices.add(lowerHull.get(0));
      }

      return hullVertices;
   }

   private void updateHull(Vector2D point, List<Vector2D> hull) {
      double tolerance = this.getTolerance();
      if (hull.size() == 1) {
         Vector2D p1 = hull.get(0);
         if (p1.distance(point) < tolerance) {
            return;
         }
      }

      while (hull.size() >= 2) {
         int size = hull.size();
         Vector2D p1 = hull.get(size - 2);
         Vector2D p2 = hull.get(size - 1);
         double offset = new Line(p1, p2, tolerance).getOffset(point);
         if (FastMath.abs(offset) < tolerance) {
            double distanceToCurrent = p1.distance(point);
            if (!(distanceToCurrent < tolerance) && !(p2.distance(point) < tolerance)) {
               double distanceToLast = p1.distance(p2);
               if (this.isIncludeCollinearPoints()) {
                  int index = distanceToCurrent < distanceToLast ? size - 1 : size;
                  hull.add(index, point);
               } else if (distanceToCurrent > distanceToLast) {
                  hull.remove(size - 1);
                  hull.add(point);
               }

               return;
            }

            return;
         }

         if (!(offset > 0.0)) {
            break;
         }

         hull.remove(size - 1);
      }

      hull.add(point);
   }
}
