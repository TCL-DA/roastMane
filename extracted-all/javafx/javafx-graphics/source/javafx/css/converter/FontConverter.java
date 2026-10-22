package javafx.css.converter;

import com.sun.javafx.util.Utils;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public final class FontConverter extends StyleConverter<ParsedValue[], Font> {
   public static StyleConverter<ParsedValue[], Font> getInstance() {
      return FontConverter.Holder.INSTANCE;
   }

   private FontConverter() {
   }

   public Font convert(ParsedValue<ParsedValue[], Font> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      Font var4 = var2 != null ? var2 : Font.getDefault();
      String var5 = var3[0] != null ? Utils.stripQuotes((String)var3[0].convert(var4)) : var4.getFamily();
      double var6 = var4.getSize();
      if (var3[1] != null) {
         ParsedValue var8 = (ParsedValue)var3[1].getValue();
         Size var9 = (Size)var8.convert(var4);
         var6 = var9.pixels(var4.getSize(), var4);
      }

      FontWeight var11 = var3[2] != null ? (FontWeight)var3[2].convert(var4) : FontWeight.NORMAL;
      FontPosture var12 = var3[3] != null ? (FontPosture)var3[3].convert(var4) : FontPosture.REGULAR;
      return Font.font(var5, var11, var12, var6);
   }

   public Font convert(Map<CssMetaData<? extends Styleable, ?>, Object> var1) {
      Font var2 = Font.getDefault();
      double var3 = var2.getSize();
      String var5 = var2.getFamily();
      FontWeight var6 = FontWeight.NORMAL;
      FontPosture var7 = FontPosture.REGULAR;

      for (Entry var9 : var1.entrySet()) {
         Object var10 = var9.getValue();
         if (var10 != null) {
            String var11 = ((CssMetaData)var9.getKey()).getProperty();
            if (var11.endsWith("font-size")) {
               var3 = ((Number)var10).doubleValue();
            } else if (var11.endsWith("font-family")) {
               var5 = Utils.stripQuotes((String)var10);
            } else if (var11.endsWith("font-weight")) {
               var6 = (FontWeight)var10;
            } else if (var11.endsWith("font-style")) {
               var7 = (FontPosture)var10;
            }
         }
      }

      return Font.font(var5, var6, var7, var3);
   }

   @Override
   public String toString() {
      return "FontConverter";
   }

   public static final class FontSizeConverter extends StyleConverter<ParsedValue<?, Size>, Number> {
      public static FontConverter.FontSizeConverter getInstance() {
         return FontConverter.FontSizeConverter.Holder.INSTANCE;
      }

      private FontSizeConverter() {
      }

      public Number convert(ParsedValue<ParsedValue<?, Size>, Number> var1, Font var2) {
         ParsedValue var3 = (ParsedValue)var1.getValue();
         return ((Size)var3.convert(var2)).pixels(var2.getSize(), var2);
      }

      @Override
      public String toString() {
         return "FontConverter.FontSizeConverter";
      }

      private static class Holder {
         static final FontConverter.FontSizeConverter INSTANCE = new FontConverter.FontSizeConverter();
      }
   }

   public static final class FontStyleConverter extends StyleConverter<String, FontPosture> {
      public static FontConverter.FontStyleConverter getInstance() {
         return FontConverter.FontStyleConverter.Holder.INSTANCE;
      }

      private FontStyleConverter() {
      }

      public FontPosture convert(ParsedValue<String, FontPosture> var1, Font var2) {
         Object var3 = var1.getValue();
         FontPosture var4 = null;
         if (var3 instanceof String) {
            try {
               String var5 = ((String)var3).toUpperCase(Locale.ROOT);
               var4 = Enum.valueOf(FontPosture.class, var5);
            } catch (IllegalArgumentException var6) {
               var4 = FontPosture.REGULAR;
            } catch (NullPointerException var7) {
               var4 = FontPosture.REGULAR;
            }
         } else if (var3 instanceof FontPosture) {
            var4 = (FontPosture)var3;
         }

         return var4;
      }

      @Override
      public String toString() {
         return "FontConverter.StyleConverter";
      }

      private static class Holder {
         static final FontConverter.FontStyleConverter INSTANCE = new FontConverter.FontStyleConverter();
      }
   }

   public static final class FontWeightConverter extends StyleConverter<String, FontWeight> {
      public static FontConverter.FontWeightConverter getInstance() {
         return FontConverter.FontWeightConverter.Holder.INSTANCE;
      }

      private FontWeightConverter() {
      }

      public FontWeight convert(ParsedValue<String, FontWeight> var1, Font var2) {
         Object var3 = var1.getValue();
         FontWeight var4 = null;
         if (var3 instanceof String) {
            try {
               String var5 = ((String)var3).toUpperCase(Locale.ROOT);
               var4 = Enum.valueOf(FontWeight.class, var5);
            } catch (IllegalArgumentException var6) {
               var4 = FontWeight.NORMAL;
            } catch (NullPointerException var7) {
               var4 = FontWeight.NORMAL;
            }
         } else if (var3 instanceof FontWeight) {
            var4 = (FontWeight)var3;
         }

         return var4;
      }

      @Override
      public String toString() {
         return "FontConverter.WeightConverter";
      }

      private static class Holder {
         static final FontConverter.FontWeightConverter INSTANCE = new FontConverter.FontWeightConverter();
      }
   }

   private static class Holder {
      static final FontConverter INSTANCE = new FontConverter();
   }
}
