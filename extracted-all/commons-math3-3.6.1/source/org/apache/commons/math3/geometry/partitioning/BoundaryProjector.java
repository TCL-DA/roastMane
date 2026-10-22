package org.apache.commons.math3.geometry.partitioning;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.util.FastMath;

class BoundaryProjector<S extends Space, T extends Space> implements BSPTreeVisitor<S> {
   private final Point<S> original;
   private Point<S> projected;
   private BSPTree<S> leaf;
   private double offset;

   BoundaryProjector(Point<S> original) {
      this.original = original;
      this.projected = null;
      this.leaf = null;
      this.offset = Double.POSITIVE_INFINITY;
   }

   @Override
   public BSPTreeVisitor.Order visitOrder(BSPTree<S> node) {
      return node.getCut().getHyperplane().getOffset(this.original) <= 0.0 ? BSPTreeVisitor.Order.MINUS_SUB_PLUS : BSPTreeVisitor.Order.PLUS_SUB_MINUS;
   }

   @Override
   public void visitInternalNode(BSPTree<S> node) {
      Hyperplane<S> hyperplane = node.getCut().getHyperplane();
      double signedOffset = hyperplane.getOffset(this.original);
      if (FastMath.abs(signedOffset) < this.offset) {
         Point<S> regular = hyperplane.project(this.original);
         List<Region<T>> boundaryParts = this.boundaryRegions(node);
         boolean regularFound = false;

         for (Region<T> part : boundaryParts) {
            if (!regularFound && this.belongsToPart(regular, hyperplane, part)) {
               this.projected = regular;
               this.offset = FastMath.abs(signedOffset);
               regularFound = true;
            }
         }

         if (!regularFound) {
            for (Region<T> partx : boundaryParts) {
               Point<S> spI = this.singularProjection(regular, hyperplane, partx);
               if (spI != null) {
                  double distance = this.original.distance(spI);
                  if (distance < this.offset) {
                     this.projected = spI;
                     this.offset = distance;
                  }
               }
            }
         }
      }
   }

   @Override
   public void visitLeafNode(BSPTree<S> node) {
      if (this.leaf == null) {
         this.leaf = node;
      }
   }

   public BoundaryProjection<S> getProjection() {
      this.offset = FastMath.copySign(this.offset, this.leaf.getAttribute() ? -1.0 : 1.0);
      return new BoundaryProjection<>(this.original, this.projected, this.offset);
   }

   private List<Region<T>> boundaryRegions(BSPTree<S> node) {
      List<Region<T>> regions = new ArrayList<>(2);
      BoundaryAttribute<S> ba = (BoundaryAttribute<S>)node.getAttribute();
      this.addRegion(ba.getPlusInside(), regions);
      this.addRegion(ba.getPlusOutside(), regions);
      return regions;
   }

   private void addRegion(SubHyperplane<S> sub, List<Region<T>> list) {
      if (sub != null) {
         Region<T> region = ((AbstractSubHyperplane)sub).getRemainingRegion();
         if (region != null) {
            list.add(region);
         }
      }
   }

   private boolean belongsToPart(Point<S> point, Hyperplane<S> hyperplane, Region<T> part) {
      Embedding<S, T> embedding = (Embedding<S, T>)hyperplane;
      return part.checkPoint(embedding.toSubSpace(point)) != Region.Location.OUTSIDE;
   }

   private Point<S> singularProjection(Point<S> point, Hyperplane<S> hyperplane, Region<T> part) {
      Embedding<S, T> embedding = (Embedding<S, T>)hyperplane;
      BoundaryProjection<T> bp = part.projectToBoundary(embedding.toSubSpace(point));
      return bp.getProjected() == null ? null : embedding.toSpace(bp.getProjected());
   }
}
