package com.sun.javafx.scene.layout.region;

import com.sun.javafx.css.ParsedValueImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

public class BorderStyleConverter extends StyleConverter<ParsedValue[], BorderStrokeStyle> {
   public static final ParsedValueImpl<ParsedValue[], Number[]> NONE = new ParsedValueImpl<>(null, null);
   public static final ParsedValueImpl<ParsedValue[], Number[]> HIDDEN = new ParsedValueImpl<>(null, null);
   public static final ParsedValueImpl<ParsedValue[], Number[]> DOTTED = new ParsedValueImpl<>(null, null);
   public static final ParsedValueImpl<ParsedValue[], Number[]> DASHED = new ParsedValueImpl<>(null, null);
   public static final ParsedValueImpl<ParsedValue[], Number[]> SOLID = new ParsedValueImpl<>(null, null);
   private static final BorderStyleConverter BORDER_STYLE_CONVERTER = new BorderStyleConverter();

   public static BorderStyleConverter getInstance() {
      return BORDER_STYLE_CONVERTER;
   }

   private BorderStyleConverter() {
   }

   public BorderStrokeStyle convert(ParsedValue<ParsedValue[], BorderStrokeStyle> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      ParsedValue var4 = var3[0];
      boolean var5 = var3[1] == null && var3[2] == null && var3[3] == null && var3[4] == null && var3[5] == null;
      if (NONE == var4) {
         return BorderStrokeStyle.NONE;
      } else if (DOTTED == var4 && var5) {
         return BorderStrokeStyle.DOTTED;
      } else if (DASHED == var4 && var5) {
         return BorderStrokeStyle.DASHED;
      } else if (SOLID == var4 && var5) {
         return BorderStrokeStyle.SOLID;
      } else {
         ParsedValue[] var6 = (ParsedValue[])var3[0].getValue();
         Object var7;
         if (var6 == null) {
            if (DOTTED == var4) {
               var7 = BorderStrokeStyle.DOTTED.getDashArray();
            } else if (DASHED == var4) {
               var7 = BorderStrokeStyle.DASHED.getDashArray();
            } else if (SOLID == var4) {
               var7 = BorderStrokeStyle.SOLID.getDashArray();
            } else {
               var7 = Collections.emptyList();
            }
         } else {
            var7 = new ArrayList(var6.length);

            for (int var8 = 0; var8 < var6.length; var8++) {
               Size var9 = (Size)var6[var8].convert(var2);
               var7.add(var9.pixels(var2));
            }
         }

         double var16 = var3[1] != null ? (Double)var3[1].convert(var2) : 0.0;
         StrokeType var10 = var3[2] != null ? (StrokeType)var3[2].convert(var2) : StrokeType.INSIDE;
         StrokeLineJoin var11 = var3[3] != null ? (StrokeLineJoin)var3[3].convert(var2) : StrokeLineJoin.MITER;
         double var12 = var3[4] != null ? (Double)var3[4].convert(var2) : 10.0;
         StrokeLineCap var14 = var3[5] != null ? (StrokeLineCap)var3[5].convert(var2) : (DOTTED == var4 ? StrokeLineCap.ROUND : StrokeLineCap.BUTT);
         BorderStrokeStyle var15 = new BorderStrokeStyle(var10, var11, var14, var12, var16, (List<Double>)var7);
         return BorderStrokeStyle.SOLID.equals(var15) ? BorderStrokeStyle.SOLID : var15;
      }
   }

   @Override
   public String toString() {
      return "BorderStyleConverter";
   }
}
