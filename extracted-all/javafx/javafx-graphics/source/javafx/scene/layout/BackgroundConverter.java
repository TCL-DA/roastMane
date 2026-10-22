package javafx.scene.layout;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import java.util.Map;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

class BackgroundConverter extends StyleConverter<ParsedValue[], Background> {
   static final StyleConverter<ParsedValue[], Background> INSTANCE = new BackgroundConverter();

   public Background convert(Map<CssMetaData<? extends Styleable, ?>, Object> var1) {
      Paint[] var2 = (Paint[])var1.get(Background.BACKGROUND_COLOR);
      String[] var3 = (String[])var1.get(Background.BACKGROUND_IMAGE);
      boolean var4 = var2 != null && var2.length > 0;
      boolean var5 = var3 != null && var3.length > 0;
      if (!var4 && !var5) {
         return null;
      } else {
         BackgroundFill[] var6 = null;
         if (var4) {
            var6 = new BackgroundFill[var2.length];
            Object var7 = var1.get(Background.BACKGROUND_INSETS);
            Insets[] var8 = var7 == null ? new Insets[0] : (Insets[])var7;
            var7 = var1.get(Background.BACKGROUND_RADIUS);
            CornerRadii[] var9 = var7 == null ? new CornerRadii[0] : (CornerRadii[])var7;
            int var10 = var8.length - 1;
            int var11 = var9.length - 1;

            for (int var12 = 0; var12 < var2.length; var12++) {
               Insets var13 = var8.length > 0 ? var8[var12 <= var10 ? var12 : var10] : Insets.EMPTY;
               CornerRadii var14 = var9.length > 0 ? var9[var12 <= var11 ? var12 : var11] : CornerRadii.EMPTY;
               var6[var12] = new BackgroundFill(var2[var12], var14, var13);
            }
         }

         BackgroundImage[] var21 = null;
         if (var5) {
            var21 = new BackgroundImage[var3.length];
            Object var22 = var1.get(Background.BACKGROUND_REPEAT);
            RepeatStruct[] var25 = var22 == null ? new RepeatStruct[0] : (RepeatStruct[])var22;
            var22 = var1.get(Background.BACKGROUND_POSITION);
            BackgroundPosition[] var26 = var22 == null ? new BackgroundPosition[0] : (BackgroundPosition[])var22;
            var22 = var1.get(Background.BACKGROUND_SIZE);
            BackgroundSize[] var27 = var22 == null ? new BackgroundSize[0] : (BackgroundSize[])var22;
            int var28 = var25.length - 1;
            int var29 = var26.length - 1;
            int var30 = var27.length - 1;

            for (int var15 = 0; var15 < var3.length; var15++) {
               if (var3[var15] != null) {
                  Image var16 = StyleManager.getInstance().getCachedImage(var3[var15]);
                  if (var16 != null) {
                     RepeatStruct var17 = var25.length > 0 ? var25[var15 <= var28 ? var15 : var28] : null;
                     BackgroundPosition var18 = var26.length > 0 ? var26[var15 <= var29 ? var15 : var29] : null;
                     BackgroundSize var19 = var27.length > 0 ? var27[var15 <= var30 ? var15 : var30] : null;
                     var21[var15] = new BackgroundImage(var16, var17 == null ? null : var17.repeatX, var17 == null ? null : var17.repeatY, var18, var19);
                  }
               }
            }
         }

         return new Background(var6, var21);
      }
   }
}
