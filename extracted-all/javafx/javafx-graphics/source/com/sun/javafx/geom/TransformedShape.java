package com.sun.javafx.geom;

import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;

public abstract class TransformedShape extends Shape {
   protected final Shape delegate;
   private Shape cachedTransformedShape;

   public static TransformedShape transformedShape(Shape var0, BaseTransform var1) {
      return (TransformedShape)(var1.isTranslateOrIdentity()
         ? translatedShape(var0, var1.getMxt(), var1.getMyt())
         : new TransformedShape.General(var0, var1.copy()));
   }

   public static TransformedShape translatedShape(Shape var0, double var1, double var3) {
      return new TransformedShape.Translate(var0, (float)var1, (float)var3);
   }

   protected TransformedShape(Shape var1) {
      this.delegate = var1;
   }

   public Shape getDelegateNoClone() {
      return this.delegate;
   }

   public abstract BaseTransform getTransformNoClone();

   public abstract BaseTransform adjust(BaseTransform var1);

   protected Point2D untransform(float var1, float var2) {
      Point2D var3 = new Point2D(var1, var2);

      try {
         var3 = this.getTransformNoClone().inverseTransform(var3, var3);
      } catch (NoninvertibleTransformException var5) {
      }

      return var3;
   }

   protected BaseBounds untransformedBounds(float var1, float var2, float var3, float var4) {
      RectBounds var5 = new RectBounds(var1, var2, var1 + var3, var2 + var4);

      try {
         return this.getTransformNoClone().inverseTransform(var5, var5);
      } catch (NoninvertibleTransformException var7) {
         return var5.makeEmpty();
      }
   }

   @Override
   public RectBounds getBounds() {
      float[] var1 = new float[4];
      Shape.accumulate(var1, this.delegate, this.getTransformNoClone());
      return new RectBounds(var1[0], var1[1], var1[2], var1[3]);
   }

   @Override
   public boolean contains(float var1, float var2) {
      return this.delegate.contains(this.untransform(var1, var2));
   }

   private Shape getCachedTransformedShape() {
      if (this.cachedTransformedShape == null) {
         this.cachedTransformedShape = this.copy();
      }

      return this.cachedTransformedShape;
   }

   @Override
   public boolean intersects(float var1, float var2, float var3, float var4) {
      return this.getCachedTransformedShape().intersects(var1, var2, var3, var4);
   }

   @Override
   public boolean contains(float var1, float var2, float var3, float var4) {
      return this.getCachedTransformedShape().contains(var1, var2, var3, var4);
   }

   @Override
   public PathIterator getPathIterator(BaseTransform var1) {
      return this.delegate.getPathIterator(this.adjust(var1));
   }

   @Override
   public PathIterator getPathIterator(BaseTransform var1, float var2) {
      return this.delegate.getPathIterator(this.adjust(var1), var2);
   }

   @Override
   public Shape copy() {
      return this.getTransformNoClone().createTransformedShape(this.delegate);
   }

   static final class General extends TransformedShape {
      BaseTransform transform;

      General(Shape var1, BaseTransform var2) {
         super(var1);
         this.transform = var2;
      }

      @Override
      public BaseTransform getTransformNoClone() {
         return this.transform;
      }

      @Override
      public BaseTransform adjust(BaseTransform var1) {
         return var1 != null && !var1.isIdentity() ? var1.copy().deriveWithConcatenation(this.transform) : this.transform.copy();
      }
   }

   static final class Translate extends TransformedShape {
      private final float tx;
      private final float ty;
      private BaseTransform cachedTx;

      public Translate(Shape var1, float var2, float var3) {
         super(var1);
         this.tx = var2;
         this.ty = var3;
      }

      @Override
      public BaseTransform getTransformNoClone() {
         if (this.cachedTx == null) {
            this.cachedTx = BaseTransform.getTranslateInstance((double)this.tx, (double)this.ty);
         }

         return this.cachedTx;
      }

      @Override
      public BaseTransform adjust(BaseTransform var1) {
         return var1 != null && !var1.isIdentity()
            ? var1.copy().deriveWithTranslation((double)this.tx, (double)this.ty)
            : BaseTransform.getTranslateInstance((double)this.tx, (double)this.ty);
      }

      @Override
      public RectBounds getBounds() {
         RectBounds var1 = this.delegate.getBounds();
         var1.setBounds(var1.getMinX() + this.tx, var1.getMinY() + this.ty, var1.getMaxX() + this.tx, var1.getMaxY() + this.ty);
         return var1;
      }

      @Override
      public boolean contains(float var1, float var2) {
         return this.delegate.contains(var1 - this.tx, var2 - this.ty);
      }

      @Override
      public boolean intersects(float var1, float var2, float var3, float var4) {
         return this.delegate.intersects(var1 - this.tx, var2 - this.ty, var3, var4);
      }

      @Override
      public boolean contains(float var1, float var2, float var3, float var4) {
         return this.delegate.contains(var1 - this.tx, var2 - this.ty, var3, var4);
      }
   }
}
