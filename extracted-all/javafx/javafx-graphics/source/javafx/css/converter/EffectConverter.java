package javafx.css.converter;

import java.util.Map;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class EffectConverter extends StyleConverter<ParsedValue[], Effect> {
   private static Map<ParsedValue<ParsedValue[], Effect>, Effect> cache;

   public static StyleConverter<ParsedValue[], Effect> getInstance() {
      return EffectConverter.Holder.EFFECT_CONVERTER;
   }

   public Effect convert(ParsedValue<ParsedValue[], Effect> var1, Font var2) {
      throw new IllegalArgumentException("Parsed value is not an Effect");
   }

   protected EffectConverter() {
   }

   @Override
   public String toString() {
      return "EffectConverter";
   }

   public static void clearCache() {
      if (cache != null) {
         cache.clear();
      }
   }

   public static final class DropShadowConverter extends EffectConverter {
      public static EffectConverter.DropShadowConverter getInstance() {
         return EffectConverter.Holder.DROP_SHADOW_INSTANCE;
      }

      private DropShadowConverter() {
      }

      @Override
      public Effect convert(ParsedValue<ParsedValue[], Effect> var1, Font var2) {
         Effect var3 = super.getCachedValue(var1);
         if (var3 != null) {
            return var3;
         } else {
            ParsedValue[] var4 = (ParsedValue[])var1.getValue();
            BlurType var5 = (BlurType)var4[0].convert(var2);
            Color var6 = (Color)var4[1].convert(var2);
            Double var7 = ((Size)var4[2].convert(var2)).pixels(var2);
            Double var8 = ((Size)var4[3].convert(var2)).pixels(var2);
            Double var9 = ((Size)var4[4].convert(var2)).pixels(var2);
            Double var10 = ((Size)var4[5].convert(var2)).pixels(var2);
            DropShadow var11 = new DropShadow();
            if (var5 != null) {
               var11.setBlurType(var5);
            }

            if (var6 != null) {
               var11.setColor(var6);
            }

            if (var8 != null) {
               var11.setSpread(var8);
            }

            if (var7 != null) {
               var11.setRadius(var7);
            }

            if (var9 != null) {
               var11.setOffsetX(var9);
            }

            if (var10 != null) {
               var11.setOffsetY(var10);
            }

            super.cacheValue(var1, var11);
            return var11;
         }
      }

      @Override
      public String toString() {
         return "DropShadowConverter";
      }
   }

   private static class Holder {
      static final EffectConverter EFFECT_CONVERTER = new EffectConverter();
      static final EffectConverter.DropShadowConverter DROP_SHADOW_INSTANCE = new EffectConverter.DropShadowConverter();
      static final EffectConverter.InnerShadowConverter INNER_SHADOW_INSTANCE = new EffectConverter.InnerShadowConverter();
   }

   public static final class InnerShadowConverter extends EffectConverter {
      public static EffectConverter.InnerShadowConverter getInstance() {
         return EffectConverter.Holder.INNER_SHADOW_INSTANCE;
      }

      private InnerShadowConverter() {
      }

      @Override
      public Effect convert(ParsedValue<ParsedValue[], Effect> var1, Font var2) {
         Effect var3 = super.getCachedValue(var1);
         if (var3 != null) {
            return var3;
         } else {
            ParsedValue[] var4 = (ParsedValue[])var1.getValue();
            BlurType var5 = (BlurType)var4[0].convert(var2);
            Color var6 = (Color)var4[1].convert(var2);
            Double var7 = ((Size)var4[2].convert(var2)).pixels(var2);
            Double var8 = ((Size)var4[3].convert(var2)).pixels(var2);
            Double var9 = ((Size)var4[4].convert(var2)).pixels(var2);
            Double var10 = ((Size)var4[5].convert(var2)).pixels(var2);
            InnerShadow var11 = new InnerShadow();
            if (var5 != null) {
               var11.setBlurType(var5);
            }

            if (var6 != null) {
               var11.setColor(var6);
            }

            if (var7 != null) {
               var11.setRadius(var7);
            }

            if (var8 != null) {
               var11.setChoke(var8);
            }

            if (var9 != null) {
               var11.setOffsetX(var9);
            }

            if (var10 != null) {
               var11.setOffsetY(var10);
            }

            super.cacheValue(var1, var11);
            return var11;
         }
      }

      @Override
      public String toString() {
         return "InnerShadowConverter";
      }
   }
}
