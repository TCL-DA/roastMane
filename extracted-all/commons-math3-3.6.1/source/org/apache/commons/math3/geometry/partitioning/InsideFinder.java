package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

class InsideFinder<S extends Space> {
   private final Region<S> region;
   private boolean plusFound;
   private boolean minusFound;

   InsideFinder(Region<S> region) {
      this.region = region;
      this.plusFound = false;
      this.minusFound = false;
   }

   public void recurseSides(BSPTree<S> node, SubHyperplane<S> sub) {
      if (node.getCut() == null) {
         if ((Boolean)node.getAttribute()) {
            this.plusFound = true;
            this.minusFound = true;
         }
      } else {
         Hyperplane<S> hyperplane = node.getCut().getHyperplane();
         SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
         switch (split.getSide()) {
            case PLUS:
               if (node.getCut().split(sub.getHyperplane()).getSide() == Side.PLUS) {
                  if (!this.region.isEmpty(node.getMinus())) {
                     this.plusFound = true;
                  }
               } else if (!this.region.isEmpty(node.getMinus())) {
                  this.minusFound = true;
               }

               if (!this.plusFound || !this.minusFound) {
                  this.recurseSides(node.getPlus(), sub);
               }
               break;
            case MINUS:
               if (node.getCut().split(sub.getHyperplane()).getSide() == Side.PLUS) {
                  if (!this.region.isEmpty(node.getPlus())) {
                     this.plusFound = true;
                  }
               } else if (!this.region.isEmpty(node.getPlus())) {
                  this.minusFound = true;
               }

               if (!this.plusFound || !this.minusFound) {
                  this.recurseSides(node.getMinus(), sub);
               }
               break;
            case BOTH:
               this.recurseSides(node.getPlus(), split.getPlus());
               if (!this.plusFound || !this.minusFound) {
                  this.recurseSides(node.getMinus(), split.getMinus());
               }
               break;
            default:
               if (node.getCut().getHyperplane().sameOrientationAs(sub.getHyperplane())) {
                  if (node.getPlus().getCut() != null || (Boolean)node.getPlus().getAttribute()) {
                     this.plusFound = true;
                  }

                  if (node.getMinus().getCut() != null || (Boolean)node.getMinus().getAttribute()) {
                     this.minusFound = true;
                  }
               } else {
                  if (node.getPlus().getCut() != null || (Boolean)node.getPlus().getAttribute()) {
                     this.minusFound = true;
                  }

                  if (node.getMinus().getCut() != null || (Boolean)node.getMinus().getAttribute()) {
                     this.plusFound = true;
                  }
               }
         }
      }
   }

   public boolean plusFound() {
      return this.plusFound;
   }

   public boolean minusFound() {
      return this.minusFound;
   }
}
