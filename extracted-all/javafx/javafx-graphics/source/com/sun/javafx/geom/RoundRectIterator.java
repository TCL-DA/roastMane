package com.sun.javafx.geom;

import com.sun.javafx.geom.transform.BaseTransform;
import java.util.NoSuchElementException;

class RoundRectIterator implements PathIterator {
   double x;
   double y;
   double w;
   double h;
   double aw;
   double ah;
   BaseTransform transform;
   int index;
   private static final double angle = Math.PI / 4;
   private static final double a = 1.0 - Math.cos(Math.PI / 4);
   private static final double b = Math.tan(Math.PI / 4);
   private static final double c = Math.sqrt(1.0 + b * b) - 1.0 + a;
   private static final double cv = 1.3333333333333333 * a * b / c;
   private static final double acv = (1.0 - cv) / 2.0;
   private static final double[][] ctrlpts = new double[][]{
      {0.0, 0.0, 0.0, 0.5},
      {0.0, 0.0, 1.0, -0.5},
      {0.0, 0.0, 1.0, -acv, 0.0, acv, 1.0, 0.0, 0.0, 0.5, 1.0, 0.0},
      {1.0, -0.5, 1.0, 0.0},
      {1.0, -acv, 1.0, 0.0, 1.0, 0.0, 1.0, -acv, 1.0, 0.0, 1.0, -0.5},
      {1.0, 0.0, 0.0, 0.5},
      {1.0, 0.0, 0.0, acv, 1.0, -acv, 0.0, 0.0, 1.0, -0.5, 0.0, 0.0},
      {0.0, 0.5, 0.0, 0.0},
      {0.0, acv, 0.0, 0.0, 0.0, 0.0, 0.0, acv, 0.0, 0.0, 0.0, 0.5},
      new double[0]
   };
   private static final int[] types = new int[]{0, 1, 3, 1, 3, 1, 3, 1, 3, 4};

   RoundRectIterator(RoundRectangle2D var1, BaseTransform var2) {
      this.x = (double)var1.x;
      this.y = (double)var1.y;
      this.w = (double)var1.width;
      this.h = (double)var1.height;
      this.aw = Math.min(this.w, (double)Math.abs(var1.arcWidth));
      this.ah = Math.min(this.h, (double)Math.abs(var1.arcHeight));
      this.transform = var2;
      if (this.aw < 0.0 || this.ah < 0.0) {
         this.index = ctrlpts.length;
      }
   }

   @Override
   public int getWindingRule() {
      return 1;
   }

   @Override
   public boolean isDone() {
      return this.index >= ctrlpts.length;
   }

   @Override
   public void next() {
      this.index++;
      if (this.index < ctrlpts.length && this.aw == 0.0 && this.ah == 0.0 && types[this.index] == 3) {
         this.index++;
      }
   }

   @Override
   public int currentSegment(float[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("roundrect iterator out of bounds");
      } else {
         double[] var2 = ctrlpts[this.index];
         int var3 = 0;

         for (byte var4 = 0; var4 < var2.length; var4 += 4) {
            var1[var3++] = (float)(this.x + var2[var4 + 0] * this.w + var2[var4 + 1] * this.aw);
            var1[var3++] = (float)(this.y + var2[var4 + 2] * this.h + var2[var4 + 3] * this.ah);
         }

         if (this.transform != null) {
            this.transform.transform(var1, 0, var1, 0, var3 / 2);
         }

         return types[this.index];
      }
   }
}
