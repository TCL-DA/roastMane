package javafx.scene.layout;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.layout.region.BorderImageSlices;
import com.sun.javafx.scene.layout.region.Margins;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import java.util.Map;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

class BorderConverter extends StyleConverter<ParsedValue[], Border> {
   private static final BorderConverter BORDER_IMAGE_CONVERTER = new BorderConverter();

   public static BorderConverter getInstance() {
      return BORDER_IMAGE_CONVERTER;
   }

   private BorderConverter() {
   }

   public Border convert(Map<CssMetaData<? extends Styleable, ?>, Object> var1) {
      Paint[][] var2 = (Paint[][])var1.get(Border.BORDER_COLOR);
      BorderStrokeStyle[][] var3 = (BorderStrokeStyle[][])var1.get(Border.BORDER_STYLE);
      String[] var4 = (String[])var1.get(Border.BORDER_IMAGE_SOURCE);
      boolean var5 = var2 != null && var2.length > 0 || var3 != null && var3.length > 0;
      boolean var6 = var4 != null && var4.length > 0;
      if (!var5 && !var6) {
         return null;
      } else {
         BorderStroke[] var7 = null;
         if (var5) {
            int var8 = var2 != null ? var2.length - 1 : -1;
            int var9 = var3 != null ? var3.length - 1 : -1;
            int var10 = (var8 >= var9 ? var8 : var9) + 1;
            Object var11 = var1.get(Border.BORDER_WIDTH);
            Margins[] var12 = var11 == null ? new Margins[0] : (Margins[])var11;
            int var13 = var12.length - 1;
            var11 = var1.get(Border.BORDER_RADIUS);
            CornerRadii[] var14 = var11 == null ? new CornerRadii[0] : (CornerRadii[])var11;
            int var15 = var14.length - 1;
            var11 = var1.get(Border.BORDER_INSETS);
            Insets[] var16 = var11 == null ? new Insets[0] : (Insets[])var11;
            int var17 = var16.length - 1;

            for (int var18 = 0; var18 < var10; var18++) {
               BorderStrokeStyle[] var19;
               if (var9 < 0) {
                  var19 = new BorderStrokeStyle[4];
                  var19[0] = var19[1] = var19[2] = var19[3] = BorderStrokeStyle.SOLID;
               } else {
                  var19 = var3[var18 <= var9 ? var18 : var9];
               }

               if (var19[0] != BorderStrokeStyle.NONE
                  || var19[1] != BorderStrokeStyle.NONE
                  || var19[2] != BorderStrokeStyle.NONE
                  || var19[3] != BorderStrokeStyle.NONE) {
                  Paint[] var20;
                  if (var8 < 0) {
                     var20 = new Paint[4];
                     var20[0] = var20[1] = var20[2] = var20[3] = Color.BLACK;
                  } else {
                     var20 = var2[var18 <= var8 ? var18 : var8];
                  }

                  if (var7 == null) {
                     var7 = new BorderStroke[var10];
                  }

                  Margins var21 = var12.length == 0 ? null : var12[var18 <= var13 ? var18 : var13];
                  CornerRadii var22 = var14.length == 0 ? CornerRadii.EMPTY : var14[var18 <= var15 ? var18 : var15];
                  Insets var23 = var16.length == 0 ? null : var16[var18 <= var17 ? var18 : var17];
                  var7[var18] = new BorderStroke(
                     var20[0],
                     var20[1],
                     var20[2],
                     var20[3],
                     var19[0],
                     var19[1],
                     var19[2],
                     var19[3],
                     var22,
                     var21 == null ? BorderStroke.DEFAULT_WIDTHS : new BorderWidths(var21.getTop(), var21.getRight(), var21.getBottom(), var21.getLeft()),
                     var23
                  );
               }
            }
         }

         BorderImage[] var25 = null;
         if (var6) {
            var25 = new BorderImage[var4.length];
            Object var26 = var1.get(Border.BORDER_IMAGE_REPEAT);
            RepeatStruct[] var30 = var26 == null ? new RepeatStruct[0] : (RepeatStruct[])var26;
            int var33 = var30.length - 1;
            var26 = var1.get(Border.BORDER_IMAGE_SLICE);
            BorderImageSlices[] var34 = var26 == null ? new BorderImageSlices[0] : (BorderImageSlices[])var26;
            int var35 = var34.length - 1;
            var26 = var1.get(Border.BORDER_IMAGE_WIDTH);
            BorderWidths[] var36 = var26 == null ? new BorderWidths[0] : (BorderWidths[])var26;
            int var37 = var36.length - 1;
            var26 = var1.get(Border.BORDER_IMAGE_INSETS);
            Insets[] var38 = var26 == null ? new Insets[0] : (Insets[])var26;
            int var39 = var38.length - 1;

            for (int var40 = 0; var40 < var4.length; var40++) {
               if (var4[var40] != null) {
                  BorderRepeat var41 = BorderRepeat.STRETCH;
                  BorderRepeat var42 = BorderRepeat.STRETCH;
                  if (var30.length > 0) {
                     RepeatStruct var43 = var30[var40 <= var33 ? var40 : var33];
                     switch (var43.repeatX) {
                        case SPACE:
                           var41 = BorderRepeat.SPACE;
                           break;
                        case ROUND:
                           var41 = BorderRepeat.ROUND;
                           break;
                        case REPEAT:
                           var41 = BorderRepeat.REPEAT;
                           break;
                        case NO_REPEAT:
                           var41 = BorderRepeat.STRETCH;
                     }

                     switch (var43.repeatY) {
                        case SPACE:
                           var42 = BorderRepeat.SPACE;
                           break;
                        case ROUND:
                           var42 = BorderRepeat.ROUND;
                           break;
                        case REPEAT:
                           var42 = BorderRepeat.REPEAT;
                           break;
                        case NO_REPEAT:
                           var42 = BorderRepeat.STRETCH;
                     }
                  }

                  BorderImageSlices var44 = var34.length > 0 ? var34[var40 <= var35 ? var40 : var35] : BorderImageSlices.DEFAULT;
                  Insets var45 = var38.length > 0 ? var38[var40 <= var39 ? var40 : var39] : Insets.EMPTY;
                  BorderWidths var46 = var36.length > 0 ? var36[var40 <= var37 ? var40 : var37] : BorderWidths.DEFAULT;
                  Image var24 = StyleManager.getInstance().getCachedImage(var4[var40]);
                  var25[var40] = new BorderImage(var24, var46, var45, var44.widths, var44.filled, var41, var42);
               }
            }
         }

         return var7 == null && var25 == null ? null : new Border(var7, var25);
      }
   }

   @Override
   public String toString() {
      return "BorderConverter";
   }
}
