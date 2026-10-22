package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

class BoundaryBuilder<S extends Space> implements BSPTreeVisitor<S> {
   @Override
   public BSPTreeVisitor.Order visitOrder(BSPTree<S> node) {
      return BSPTreeVisitor.Order.PLUS_MINUS_SUB;
   }

   @Override
   public void visitInternalNode(BSPTree<S> node) {
      SubHyperplane<S> plusOutside = null;
      SubHyperplane<S> plusInside = null;
      NodesSet<S> splitters = null;
      Characterization<S> plusChar = new Characterization<>(node.getPlus(), node.getCut().copySelf());
      if (plusChar.touchOutside()) {
         Characterization<S> minusChar = new Characterization<>(node.getMinus(), plusChar.outsideTouching());
         if (minusChar.touchInside()) {
            plusOutside = minusChar.insideTouching();
            splitters = new NodesSet<>();
            splitters.addAll(minusChar.getInsideSplitters());
            splitters.addAll(plusChar.getOutsideSplitters());
         }
      }

      if (plusChar.touchInside()) {
         Characterization<S> minusChar = new Characterization<>(node.getMinus(), plusChar.insideTouching());
         if (minusChar.touchOutside()) {
            plusInside = minusChar.outsideTouching();
            if (splitters == null) {
               splitters = new NodesSet<>();
            }

            splitters.addAll(minusChar.getOutsideSplitters());
            splitters.addAll(plusChar.getInsideSplitters());
         }
      }

      if (splitters != null) {
         for (BSPTree<S> up = node.getParent(); up != null; up = up.getParent()) {
            splitters.add(up);
         }
      }

      node.setAttribute(new BoundaryAttribute<>(plusOutside, plusInside, splitters));
   }

   @Override
   public void visitLeafNode(BSPTree<S> node) {
   }
}
