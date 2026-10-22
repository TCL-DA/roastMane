package com.sun.javafx.geom;

import com.sun.javafx.geom.transform.BaseTransform;
import java.util.Enumeration;
import java.util.Vector;

public class Area extends Shape {
   private static final Vector EmptyCurves = new Vector();
   private Vector curves;
   private RectBounds cachedBounds;

   public Area() {
      this.curves = EmptyCurves;
   }

   public Area(Shape var1) {
      if (var1 instanceof Area) {
         this.curves = ((Area)var1).curves;
      } else {
         this.curves = pathToCurves(var1.getPathIterator(null));
      }
   }

   public Area(PathIterator var1) {
      this.curves = pathToCurves(var1);
   }

   private static Vector pathToCurves(PathIterator var0) {
      Vector var1 = new Vector();
      int var2 = var0.getWindingRule();
      float[] var3 = new float[6];
      double[] var4 = new double[23];
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = 0.0;

      double var11;
      for (var11 = 0.0; !var0.isDone(); var0.next()) {
         switch (var0.currentSegment(var3)) {
            case 0:
               Curve.insertLine(var1, var9, var11, var5, var7);
               var9 = var5 = (double)var3[0];
               var11 = var7 = (double)var3[1];
               Curve.insertMove(var1, var5, var7);
               break;
            case 1:
               double var19 = (double)var3[0];
               double var21 = (double)var3[1];
               Curve.insertLine(var1, var9, var11, var19, var21);
               var9 = var19;
               var11 = var21;
               break;
            case 2:
               double var18 = (double)var3[2];
               double var20 = (double)var3[3];
               Curve.insertQuad(var1, var4, var9, var11, (double)var3[0], (double)var3[1], (double)var3[2], (double)var3[3]);
               var9 = var18;
               var11 = var20;
               break;
            case 3:
               double var13 = (double)var3[4];
               double var15 = (double)var3[5];
               Curve.insertCubic(var1, var4, var9, var11, (double)var3[0], (double)var3[1], (double)var3[2], (double)var3[3], (double)var3[4], (double)var3[5]);
               var9 = var13;
               var11 = var15;
               break;
            case 4:
               Curve.insertLine(var1, var9, var11, var5, var7);
               var9 = var5;
               var11 = var7;
         }
      }

      Curve.insertLine(var1, var9, var11, var5, var7);
      Object var17;
      if (var2 == 0) {
         var17 = new AreaOp.EOWindOp();
      } else {
         var17 = new AreaOp.NZWindOp();
      }

      return ((AreaOp)var17).calculate(var1, EmptyCurves);
   }

   public void add(Area var1) {
      this.curves = new AreaOp.AddOp().calculate(this.curves, var1.curves);
      this.invalidateBounds();
   }

   public void subtract(Area var1) {
      this.curves = new AreaOp.SubOp().calculate(this.curves, var1.curves);
      this.invalidateBounds();
   }

   public void intersect(Area var1) {
      this.curves = new AreaOp.IntOp().calculate(this.curves, var1.curves);
      this.invalidateBounds();
   }

   public void exclusiveOr(Area var1) {
      this.curves = new AreaOp.XorOp().calculate(this.curves, var1.curves);
      this.invalidateBounds();
   }

   public void reset() {
      this.curves = new Vector();
      this.invalidateBounds();
   }

   public boolean isEmpty() {
      return this.curves.size() == 0;
   }

   public boolean isPolygonal() {
      Enumeration var1 = this.curves.elements();

      while (var1.hasMoreElements()) {
         if (((Curve)var1.nextElement()).getOrder() > 1) {
            return false;
         }
      }

      return true;
   }

   public boolean isRectangular() {
      int var1 = this.curves.size();
      if (var1 == 0) {
         return true;
      } else if (var1 > 3) {
         return false;
      } else {
         Curve var2 = (Curve)this.curves.get(1);
         Curve var3 = (Curve)this.curves.get(2);
         if (var2.getOrder() != 1 || var3.getOrder() != 1) {
            return false;
         } else {
            return var2.getXTop() != var2.getXBot() || var3.getXTop() != var3.getXBot()
               ? false
               : var2.getYTop() == var3.getYTop() && var2.getYBot() == var3.getYBot();
         }
      }
   }

   public boolean isSingular() {
      if (this.curves.size() < 3) {
         return true;
      } else {
         Enumeration var1 = this.curves.elements();
         var1.nextElement();

         while (var1.hasMoreElements()) {
            if (((Curve)var1.nextElement()).getOrder() == 0) {
               return false;
            }
         }

         return true;
      }
   }

   private void invalidateBounds() {
      this.cachedBounds = null;
   }

   private RectBounds getCachedBounds() {
      if (this.cachedBounds != null) {
         return this.cachedBounds;
      } else {
         RectBounds var1 = new RectBounds();
         if (this.curves.size() > 0) {
            Curve var2 = (Curve)this.curves.get(0);
            var1.setBounds((float)var2.getX0(), (float)var2.getY0(), 0.0F, 0.0F);

            for (int var3 = 1; var3 < this.curves.size(); var3++) {
               ((Curve)this.curves.get(var3)).enlarge(var1);
            }
         }

         return this.cachedBounds = var1;
      }
   }

   @Override
   public RectBounds getBounds() {
      return new RectBounds(this.getCachedBounds());
   }

   public boolean isEquivalent(Area var1) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         Vector var2 = new AreaOp.XorOp().calculate(this.curves, var1.curves);
         return var2.isEmpty();
      }
   }

   public void transform(BaseTransform var1) {
      if (var1 == null) {
         throw new NullPointerException("transform must not be null");
      } else {
         this.curves = pathToCurves(this.getPathIterator(var1));
         this.invalidateBounds();
      }
   }

   public Area createTransformedArea(BaseTransform var1) {
      Area var2 = new Area(this);
      var2.transform(var1);
      return var2;
   }

   @Override
   public boolean contains(float var1, float var2) {
      if (!this.getCachedBounds().contains(var1, var2)) {
         return false;
      } else {
         Enumeration var3 = this.curves.elements();
         int var4 = 0;

         while (var3.hasMoreElements()) {
            Curve var5 = (Curve)var3.nextElement();
            var4 += var5.crossingsFor((double)var1, (double)var2);
         }

         return (var4 & 1) == 1;
      }
   }

   @Override
   public boolean contains(Point2D var1) {
      return this.contains(var1.x, var1.y);
   }

   @Override
   public boolean contains(float var1, float var2, float var3, float var4) {
      if (!(var3 < 0.0F) && !(var4 < 0.0F)) {
         if (this.getCachedBounds().contains(var1, var2) && this.getCachedBounds().contains(var1 + var3, var2 + var4)) {
            Crossings var5 = Crossings.findCrossings(this.curves, (double)var1, (double)var2, (double)(var1 + var3), (double)(var2 + var4));
            return var5 != null && var5.covers((double)var2, (double)(var2 + var4));
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean intersects(float var1, float var2, float var3, float var4) {
      if (!(var3 < 0.0F) && !(var4 < 0.0F)) {
         if (!this.getCachedBounds().intersects(var1, var2, var3, var4)) {
            return false;
         } else {
            Crossings var5 = Crossings.findCrossings(this.curves, (double)var1, (double)var2, (double)(var1 + var3), (double)(var2 + var4));
            return var5 == null || !var5.isEmpty();
         }
      } else {
         return false;
      }
   }

   @Override
   public PathIterator getPathIterator(BaseTransform var1) {
      return new AreaIterator(this.curves, var1);
   }

   @Override
   public PathIterator getPathIterator(BaseTransform var1, float var2) {
      return new FlatteningPathIterator(this.getPathIterator(var1), var2);
   }

   public Area copy() {
      return new Area(this);
   }
}
