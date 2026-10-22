package javafx.css.converter;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.Cursor;
import javafx.scene.text.Font;

public final class CursorConverter extends StyleConverter<String, Cursor> {
   public static StyleConverter<String, Cursor> getInstance() {
      return CursorConverter.Holder.INSTANCE;
   }

   private CursorConverter() {
   }

   public Cursor convert(ParsedValue<String, Cursor> var1, Font var2) {
      String var3 = (String)var1.getValue();
      if (var3 != null) {
         int var4 = var3.indexOf("Cursor.");
         if (var4 > -1) {
            var3 = var3.substring(var4 + "Cursor.".length());
         }

         var3 = var3.replace('-', '_').toUpperCase();
      }

      try {
         return Cursor.cursor(var3);
      } catch (NullPointerException | IllegalArgumentException var5) {
         return Cursor.DEFAULT;
      }
   }

   @Override
   public String toString() {
      return "CursorConverter";
   }

   private static class Holder {
      static final CursorConverter INSTANCE = new CursorConverter();
   }
}
