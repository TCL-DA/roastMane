package com.sun.javafx.scene.layout.region;

import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;

public final class CornerRadiiConverter extends StyleConverter<ParsedValue<ParsedValue<?, Size>[][], CornerRadii>[], CornerRadii[]> {
   private static final CornerRadiiConverter INSTANCE = new CornerRadiiConverter();

   public static CornerRadiiConverter getInstance() {
      return INSTANCE;
   }

   private CornerRadiiConverter() {
   }

   public CornerRadii[] convert(ParsedValue<ParsedValue<ParsedValue<?, Size>[][], CornerRadii>[], CornerRadii[]> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      CornerRadii[] var4 = new CornerRadii[var3.length];

      for (int var5 = 0; var5 < var3.length; var5++) {
         ParsedValue[][] var6 = (ParsedValue[][])var3[var5].getValue();
         Size var7 = (Size)var6[0][0].convert(var2);
         Size var8 = (Size)var6[0][1].convert(var2);
         Size var9 = (Size)var6[0][2].convert(var2);
         Size var10 = (Size)var6[0][3].convert(var2);
         Size var11 = (Size)var6[1][0].convert(var2);
         Size var12 = (Size)var6[1][1].convert(var2);
         Size var13 = (Size)var6[1][2].convert(var2);
         Size var14 = (Size)var6[1][3].convert(var2);
         var4[var5] = new CornerRadii(
            var7.pixels(var2),
            var11.pixels(var2),
            var12.pixels(var2),
            var8.pixels(var2),
            var9.pixels(var2),
            var13.pixels(var2),
            var14.pixels(var2),
            var10.pixels(var2),
            var7.getUnits() == SizeUnits.PERCENT,
            var11.getUnits() == SizeUnits.PERCENT,
            var12.getUnits() == SizeUnits.PERCENT,
            var8.getUnits() == SizeUnits.PERCENT,
            var9.getUnits() == SizeUnits.PERCENT,
            var13.getUnits() == SizeUnits.PERCENT,
            var13.getUnits() == SizeUnits.PERCENT,
            var10.getUnits() == SizeUnits.PERCENT
         );
      }

      return var4;
   }
}
