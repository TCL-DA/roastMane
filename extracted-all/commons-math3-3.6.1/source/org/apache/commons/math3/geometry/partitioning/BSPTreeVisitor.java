package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public interface BSPTreeVisitor<S extends Space> {
   BSPTreeVisitor.Order visitOrder(BSPTree<S> var1);

   void visitInternalNode(BSPTree<S> var1);

   void visitLeafNode(BSPTree<S> var1);

   public static enum Order {
      PLUS_MINUS_SUB,
      PLUS_SUB_MINUS,
      MINUS_PLUS_SUB,
      MINUS_SUB_PLUS,
      SUB_PLUS_MINUS,
      SUB_MINUS_PLUS;
   }
}
