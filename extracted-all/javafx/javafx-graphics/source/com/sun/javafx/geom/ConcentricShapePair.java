package com.sun.javafx.geom;

import com.sun.javafx.geom.transform.BaseTransform;

public final class ConcentricShapePair extends ShapePair {
   private final Shape outer;
   private final Shape inner;

   public ConcentricShapePair(Shape var1, Shape var2) {
      this.outer = var1;
      this.inner = var2;
   }

   @Override
   public int getCombinationType() {
      return 1;
   }

   @Override
   public Shape getOuterShape() {
      return this.outer;
   }

   @Override
   public Shape getInnerShape() {
      return this.inner;
   }

   @Override
   public Shape copy() {
      return new ConcentricShapePair(this.outer.copy(), this.inner.copy());
   }

   @Override
   public boolean contains(float var1, float var2) {
      return this.outer.contains(var1, var2) && !this.inner.contains(var1, var2);
   }

   @Override
   public boolean intersects(float var1, float var2, float var3, float var4) {
      return this.outer.intersects(var1, var2, var3, var4) && !this.inner.contains(var1, var2, var3, var4);
   }

   @Override
   public boolean contains(float var1, float var2, float var3, float var4) {
      return this.outer.contains(var1, var2, var3, var4) && !this.inner.intersects(var1, var2, var3, var4);
   }

   @Override
   public RectBounds getBounds() {
      return this.outer.getBounds();
   }

   @Override
   public PathIterator getPathIterator(BaseTransform var1) {
      return new ConcentricShapePair.PairIterator(this.outer.getPathIterator(var1), this.inner.getPathIterator(var1));
   }

   @Override
   public PathIterator getPathIterator(BaseTransform var1, float var2) {
      return new ConcentricShapePair.PairIterator(this.outer.getPathIterator(var1, var2), this.inner.getPathIterator(var1, var2));
   }

   static class PairIterator implements PathIterator {
      PathIterator outer;
      PathIterator inner;

      PairIterator(PathIterator var1, PathIterator var2) {
         this.outer = var1;
         this.inner = var2;
      }

      @Override
      public int getWindingRule() {
         return 0;
      }

      @Override
      public int currentSegment(float[] var1) {
         return this.outer.isDone() ? this.inner.currentSegment(var1) : this.outer.currentSegment(var1);
      }

      @Override
      public boolean isDone() {
         return this.outer.isDone() && this.inner.isDone();
      }

      @Override
      public void next() {
         if (this.outer.isDone()) {
            this.inner.next();
         } else {
            this.outer.next();
         }
      }
   }
}
