package org.apache.commons.math3.geometry.euclidean.twod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

class NestedLoops {
   private Vector2D[] loop;
   private List<NestedLoops> surrounded;
   private Region<Euclidean2D> polygon;
   private boolean originalIsClockwise;
   private final double tolerance;

   NestedLoops(double tolerance) {
      this.surrounded = new ArrayList<>();
      this.tolerance = tolerance;
   }

   private NestedLoops(Vector2D[] loop, double tolerance) throws MathIllegalArgumentException {
      if (loop[0] == null) {
         throw new MathIllegalArgumentException(LocalizedFormats.OUTLINE_BOUNDARY_LOOP_OPEN);
      } else {
         this.loop = loop;
         this.surrounded = new ArrayList<>();
         this.tolerance = tolerance;
         ArrayList<SubHyperplane<Euclidean2D>> edges = new ArrayList<>();
         Vector2D current = loop[loop.length - 1];

         for (int i = 0; i < loop.length; i++) {
            Vector2D previous = current;
            current = loop[i];
            Line line = new Line(previous, current, tolerance);
            IntervalsSet region = new IntervalsSet(line.toSubSpace(previous).getX(), line.toSubSpace(current).getX(), tolerance);
            edges.add(new SubLine(line, region));
         }

         this.polygon = new PolygonsSet(edges, tolerance);
         if (Double.isInfinite(this.polygon.getSize())) {
            this.polygon = new RegionFactory<Euclidean2D>().getComplement(this.polygon);
            this.originalIsClockwise = false;
         } else {
            this.originalIsClockwise = true;
         }
      }
   }

   public void add(Vector2D[] bLoop) throws MathIllegalArgumentException {
      this.add(new NestedLoops(bLoop, this.tolerance));
   }

   private void add(NestedLoops node) throws MathIllegalArgumentException {
      for (NestedLoops child : this.surrounded) {
         if (child.polygon.contains(node.polygon)) {
            child.add(node);
            return;
         }
      }

      Iterator<NestedLoops> iterator = this.surrounded.iterator();

      while (iterator.hasNext()) {
         NestedLoops childx = iterator.next();
         if (node.polygon.contains(childx.polygon)) {
            node.surrounded.add(childx);
            iterator.remove();
         }
      }

      RegionFactory<Euclidean2D> factory = new RegionFactory<>();

      for (NestedLoops childx : this.surrounded) {
         if (!factory.intersection(node.polygon, childx.polygon).isEmpty()) {
            throw new MathIllegalArgumentException(LocalizedFormats.CROSSING_BOUNDARY_LOOPS);
         }
      }

      this.surrounded.add(node);
   }

   public void correctOrientation() {
      for (NestedLoops child : this.surrounded) {
         child.setClockWise(true);
      }
   }

   private void setClockWise(boolean clockwise) {
      if (this.originalIsClockwise ^ clockwise) {
         int min = -1;
         int max = this.loop.length;

         while (true) {
            min++;
            if (min >= --max) {
               break;
            }

            Vector2D tmp = this.loop[min];
            this.loop[min] = this.loop[max];
            this.loop[max] = tmp;
         }
      }

      for (NestedLoops child : this.surrounded) {
         child.setClockWise(!clockwise);
      }
   }
}
