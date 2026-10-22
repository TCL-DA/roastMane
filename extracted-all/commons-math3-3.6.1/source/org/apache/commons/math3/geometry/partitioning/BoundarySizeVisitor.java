package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

class BoundarySizeVisitor<S extends Space> implements BSPTreeVisitor<S> {
   private double boundarySize = 0.0;

   @Override
   public BSPTreeVisitor.Order visitOrder(BSPTree<S> node) {
      return BSPTreeVisitor.Order.MINUS_SUB_PLUS;
   }

   @Override
   public void visitInternalNode(BSPTree<S> node) {
      BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
      if (attribute.getPlusOutside() != null) {
         this.boundarySize = this.boundarySize + attribute.getPlusOutside().getSize();
      }

      if (attribute.getPlusInside() != null) {
         this.boundarySize = this.boundarySize + attribute.getPlusInside().getSize();
      }
   }

   @Override
   public void visitLeafNode(BSPTree<S> node) {
   }

   public double getSize() {
      return this.boundarySize;
   }
}
