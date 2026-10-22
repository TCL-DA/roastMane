package com.sun.javafx.font;

import com.sun.javafx.geom.transform.BaseTransform;

public class PrismFontUtils {
   private PrismFontUtils() {
   }

   static Metrics getFontMetrics(PGFont var0) {
      FontStrike var1 = var0.getStrike(BaseTransform.IDENTITY_TRANSFORM, 0);
      return var1.getMetrics();
   }

   static double getCharWidth(PGFont var0, char var1) {
      FontStrike var2 = var0.getStrike(BaseTransform.IDENTITY_TRANSFORM, 0);
      double var3 = (double)var2.getCharAdvance(var1);
      if (var3 == 0.0) {
         var3 = (double)var0.getSize() / 2.0;
      }

      return var3;
   }
}
