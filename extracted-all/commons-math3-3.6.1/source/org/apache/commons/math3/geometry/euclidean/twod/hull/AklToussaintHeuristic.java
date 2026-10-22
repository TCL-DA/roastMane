package org.apache.commons.math3.geometry.euclidean.twod.hull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public final class AklToussaintHeuristic {
   private AklToussaintHeuristic() {
   }

   public static Collection<Vector2D> reducePoints(Collection<Vector2D> points) {
      int size = 0;
      Vector2D minX = null;
      Vector2D maxX = null;
      Vector2D minY = null;
      Vector2D maxY = null;

      for (Vector2D p : points) {
         if (minX == null || p.getX() < minX.getX()) {
            minX = p;
         }

         if (maxX == null || p.getX() > maxX.getX()) {
            maxX = p;
         }

         if (minY == null || p.getY() < minY.getY()) {
            minY = p;
         }

         if (maxY == null || p.getY() > maxY.getY()) {
            maxY = p;
         }

         size++;
      }

      if (size < 4) {
         return points;
      } else {
         List<Vector2D> quadrilateral = buildQuadrilateral(minY, maxX, maxY, minX);
         if (quadrilateral.size() < 3) {
            return points;
         } else {
            List<Vector2D> reducedPoints = new ArrayList<>(quadrilateral);

            for (Vector2D p : points) {
               if (!insideQuadrilateral(p, quadrilateral)) {
                  reducedPoints.add(p);
               }
            }

            return reducedPoints;
         }
      }
   }

   private static List<Vector2D> buildQuadrilateral(Vector2D... points) {
      List<Vector2D> quadrilateral = new ArrayList<>();

      for (Vector2D p : points) {
         if (!quadrilateral.contains(p)) {
            quadrilateral.add(p);
         }
      }

      return quadrilateral;
   }

   private static boolean insideQuadrilateral(Vector2D point, List<Vector2D> quadrilateralPoints) {
      Vector2D p1 = quadrilateralPoints.get(0);
      Vector2D p2 = quadrilateralPoints.get(1);
      if (!point.equals(p1) && !point.equals(p2)) {
         double last = point.crossProduct(p1, p2);
         int size = quadrilateralPoints.size();

         for (int i = 1; i < size; i++) {
            p1 = p2;
            p2 = quadrilateralPoints.get(i + 1 == size ? 0 : i + 1);
            if (point.equals(p1) || point.equals(p2)) {
               return true;
            }

            if (last * point.crossProduct(p1, p2) < 0.0) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }
}
