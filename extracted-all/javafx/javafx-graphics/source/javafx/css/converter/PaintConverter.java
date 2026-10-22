package javafx.css.converter;

import com.sun.javafx.css.StyleManager;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.scene.image.Image;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public final class PaintConverter extends StyleConverter<ParsedValue<?, Paint>, Paint> {
   public static StyleConverter<ParsedValue<?, Paint>, Paint> getInstance() {
      return PaintConverter.Holder.INSTANCE;
   }

   private PaintConverter() {
   }

   public Paint convert(ParsedValue<ParsedValue<?, Paint>, Paint> var1, Font var2) {
      Object var3 = var1.getValue();
      return var3 instanceof Paint ? (Paint)var3 : (Paint)((ParsedValue)var1.getValue()).convert(var2);
   }

   @Override
   public String toString() {
      return "PaintConverter";
   }

   private static class Holder {
      static final PaintConverter INSTANCE = new PaintConverter();
      static final PaintConverter.SequenceConverter SEQUENCE_INSTANCE = new PaintConverter.SequenceConverter();
      static final PaintConverter.LinearGradientConverter LINEAR_GRADIENT_INSTANCE = new PaintConverter.LinearGradientConverter();
      static final PaintConverter.ImagePatternConverter IMAGE_PATTERN_INSTANCE = new PaintConverter.ImagePatternConverter();
      static final PaintConverter.RepeatingImagePatternConverter REPEATING_IMAGE_PATTERN_INSTANCE = new PaintConverter.RepeatingImagePatternConverter();
      static final PaintConverter.RadialGradientConverter RADIAL_GRADIENT_INSTANCE = new PaintConverter.RadialGradientConverter();
   }

   public static final class ImagePatternConverter extends StyleConverter<ParsedValue[], Paint> {
      public static PaintConverter.ImagePatternConverter getInstance() {
         return PaintConverter.Holder.IMAGE_PATTERN_INSTANCE;
      }

      private ImagePatternConverter() {
      }

      public Paint convert(ParsedValue<ParsedValue[], Paint> var1, Font var2) {
         Paint var3 = (Paint)super.getCachedValue(var1);
         if (var3 != null) {
            return var3;
         } else {
            ParsedValue[] var4 = (ParsedValue[])var1.getValue();
            ParsedValue var5 = var4[0];
            String var6 = (String)var5.convert(var2);
            if (var4.length == 1) {
               return new ImagePattern(StyleManager.getInstance().getCachedImage(var6));
            } else {
               Size var7 = (Size)var4[1].convert(var2);
               Size var8 = (Size)var4[2].convert(var2);
               Size var9 = (Size)var4[3].convert(var2);
               Size var10 = (Size)var4[4].convert(var2);
               boolean var11 = var4.length < 6 ? true : (Boolean)var4[5].getValue();
               ImagePattern var12 = new ImagePattern(new Image(var6), var7.getValue(), var8.getValue(), var9.getValue(), var10.getValue(), var11);
               super.cacheValue(var1, var12);
               return var12;
            }
         }
      }

      @Override
      public String toString() {
         return "ImagePatternConverter";
      }
   }

   public static final class LinearGradientConverter extends StyleConverter<ParsedValue[], Paint> {
      public static PaintConverter.LinearGradientConverter getInstance() {
         return PaintConverter.Holder.LINEAR_GRADIENT_INSTANCE;
      }

      private LinearGradientConverter() {
      }

      public Paint convert(ParsedValue<ParsedValue[], Paint> var1, Font var2) {
         Paint var3 = (Paint)super.getCachedValue(var1);
         if (var3 != null) {
            return var3;
         } else {
            ParsedValue[] var4 = (ParsedValue[])var1.getValue();
            int var5 = 0;
            Size var6 = (Size)var4[var5++].convert(var2);
            Size var7 = (Size)var4[var5++].convert(var2);
            Size var8 = (Size)var4[var5++].convert(var2);
            Size var9 = (Size)var4[var5++].convert(var2);
            boolean var10 = var6.getUnits() == SizeUnits.PERCENT
               && var6.getUnits() == var7.getUnits()
               && var6.getUnits() == var8.getUnits()
               && var6.getUnits() == var9.getUnits();
            CycleMethod var11 = (CycleMethod)var4[var5++].convert(var2);
            Stop[] var12 = new Stop[var4.length - var5];

            for (int var13 = var5; var13 < var4.length; var13++) {
               var12[var13 - var5] = (Stop)var4[var13].convert(var2);
            }

            LinearGradient var14 = new LinearGradient(var6.pixels(var2), var7.pixels(var2), var8.pixels(var2), var9.pixels(var2), var10, var11, var12);
            super.cacheValue(var1, var14);
            return var14;
         }
      }

      @Override
      public String toString() {
         return "LinearGradientConverter";
      }
   }

   public static final class RadialGradientConverter extends StyleConverter<ParsedValue[], Paint> {
      public static PaintConverter.RadialGradientConverter getInstance() {
         return PaintConverter.Holder.RADIAL_GRADIENT_INSTANCE;
      }

      private RadialGradientConverter() {
      }

      public Paint convert(ParsedValue<ParsedValue[], Paint> var1, Font var2) {
         Paint var3 = (Paint)super.getCachedValue(var1);
         if (var3 != null) {
            return var3;
         } else {
            ParsedValue[] var4 = (ParsedValue[])var1.getValue();
            int var5 = 0;
            Size var6 = var4[var5++] != null ? (Size)var4[var5 - 1].convert(var2) : null;
            Size var7 = var4[var5++] != null ? (Size)var4[var5 - 1].convert(var2) : null;
            Size var8 = var4[var5++] != null ? (Size)var4[var5 - 1].convert(var2) : null;
            Size var9 = var4[var5++] != null ? (Size)var4[var5 - 1].convert(var2) : null;
            Size var10 = (Size)var4[var5++].convert(var2);
            boolean var11 = var10.getUnits().equals(SizeUnits.PERCENT);
            boolean var12 = var8 != null ? var11 == var8.getUnits().equals(SizeUnits.PERCENT) : true;
            var12 = var12 && var9 != null ? var11 == var9.getUnits().equals(SizeUnits.PERCENT) : true;
            if (!var12) {
               throw new IllegalArgumentException("units do not agree");
            } else {
               CycleMethod var13 = (CycleMethod)var4[var5++].convert(var2);
               Stop[] var14 = new Stop[var4.length - var5];

               for (int var15 = var5; var15 < var4.length; var15++) {
                  var14[var15 - var5] = (Stop)var4[var15].convert(var2);
               }

               double var25 = 0.0;
               if (var6 != null) {
                  var25 = var6.pixels(var2);
                  if (var6.getUnits().equals(SizeUnits.PERCENT)) {
                     var25 = var25 * 360.0 % 360.0;
                  }
               }

               RadialGradient var17 = new RadialGradient(
                  var25,
                  var7 != null ? var7.pixels() : 0.0,
                  var8 != null ? var8.pixels() : 0.0,
                  var9 != null ? var9.pixels() : 0.0,
                  var10 != null ? var10.pixels() : 1.0,
                  var11,
                  var13,
                  var14
               );
               super.cacheValue(var1, var17);
               return var17;
            }
         }
      }

      @Override
      public String toString() {
         return "RadialGradientConverter";
      }
   }

   public static final class RepeatingImagePatternConverter extends StyleConverter<ParsedValue[], Paint> {
      public static PaintConverter.RepeatingImagePatternConverter getInstance() {
         return PaintConverter.Holder.REPEATING_IMAGE_PATTERN_INSTANCE;
      }

      private RepeatingImagePatternConverter() {
      }

      public Paint convert(ParsedValue<ParsedValue[], Paint> var1, Font var2) {
         Paint var3 = (Paint)super.getCachedValue(var1);
         if (var3 != null) {
            return var3;
         } else {
            ParsedValue[] var4 = (ParsedValue[])var1.getValue();
            ParsedValue var5 = var4[0];
            String var6 = (String)var5.convert(var2);
            if (var6 == null) {
               return null;
            } else {
               Image var7 = new Image(var6);
               ImagePattern var8 = new ImagePattern(var7, 0.0, 0.0, var7.getWidth(), var7.getHeight(), false);
               super.cacheValue(var1, var8);
               return var8;
            }
         }
      }

      @Override
      public String toString() {
         return "RepeatingImagePatternConverter";
      }
   }

   public static final class SequenceConverter extends StyleConverter<ParsedValue<?, Paint>[], Paint[]> {
      public static PaintConverter.SequenceConverter getInstance() {
         return PaintConverter.Holder.SEQUENCE_INSTANCE;
      }

      private SequenceConverter() {
      }

      public Paint[] convert(ParsedValue<ParsedValue<?, Paint>[], Paint[]> var1, Font var2) {
         ParsedValue[] var3 = (ParsedValue[])var1.getValue();
         Paint[] var4 = new Paint[var3.length];

         for (int var5 = 0; var5 < var3.length; var5++) {
            var4[var5] = (Paint)var3[var5].convert(var2);
         }

         return var4;
      }

      @Override
      public String toString() {
         return "Paint.SequenceConverter";
      }
   }
}
