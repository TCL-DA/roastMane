package com.sun.javafx.geom;

import com.sun.javafx.geom.transform.BaseTransform;

public class GeneralShapePair extends ShapePair {
   private final Shape outer;
   private final Shape inner;
   private final int combinationType;

   public GeneralShapePair(Shape var1, Shape var2, int var3) {
      this.outer = var1;
      this.inner = var2;
      this.combinationType = var3;
   }

   @Override
   public final int getCombinationType() {
      return this.combinationType;
   }

   @Override
   public final Shape getOuterShape() {
      return this.outer;
   }

   @Override
   public final Shape getInnerShape() {
      return this.inner;
   }

   @Override
   public Shape copy() {
      return new GeneralShapePair(this.outer.copy(), this.inner.copy(), this.combinationType);
   }

   @Override
   public boolean contains(float var1, float var2) {
      return this.combinationType == 4
         ? this.outer.contains(var1, var2) && this.inner.contains(var1, var2)
         : this.outer.contains(var1, var2) && !this.inner.contains(var1, var2);
   }

   @Override
   public boolean intersects(float var1, float var2, float var3, float var4) {
      return this.combinationType == 4
         ? this.outer.intersects(var1, var2, var3, var4) && this.inner.intersects(var1, var2, var3, var4)
         : this.outer.intersects(var1, var2, var3, var4) && !this.inner.contains(var1, var2, var3, var4);
   }

   @Override
   public boolean contains(float var1, float var2, float var3, float var4) {
      return this.combinationType == 4
         ? this.outer.contains(var1, var2, var3, var4) && this.inner.contains(var1, var2, var3, var4)
         : this.outer.contains(var1, var2, var3, var4) && !this.inner.intersects(var1, var2, var3, var4);
   }

   @Override
   public RectBounds getBounds() {
      RectBounds var1 = this.outer.getBounds();
      if (this.combinationType == 4) {
         var1.intersectWith(this.inner.getBounds());
      }

      return var1;
   }

   @Override
   public PathIterator getPathIterator(BaseTransform var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public PathIterator getPathIterator(BaseTransform var1, float var2) {
      return new FlatteningPathIterator(this.getPathIterator(var1), var2);
   }
}
