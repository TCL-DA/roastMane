package javafx.css.converter;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public final class ColorConverter extends StyleConverter<String, Color> {
   public static StyleConverter<String, Color> getInstance() {
      return ColorConverter.Holder.COLOR_INSTANCE;
   }

   private ColorConverter() {
   }

   public Color convert(ParsedValue<String, Color> var1, Font var2) {
      Object var3 = var1.getValue();
      if (var3 == null) {
         return null;
      } else if (var3 instanceof Color) {
         return (Color)var3;
      } else {
         if (var3 instanceof String) {
            String var4 = (String)var3;
            if (var4.isEmpty() || "null".equals(var4)) {
               return null;
            }

            try {
               return Color.web((String)var3);
            } catch (IllegalArgumentException var6) {
            }
         }

         System.err.println("not a color: " + var1);
         return Color.BLACK;
      }
   }

   @Override
   public String toString() {
      return "ColorConverter";
   }

   private static class Holder {
      static final ColorConverter COLOR_INSTANCE = new ColorConverter();
   }
}
