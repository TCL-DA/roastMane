package javafx.css.converter;

import com.sun.javafx.util.Utils;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public final class DeriveColorConverter extends StyleConverter<ParsedValue[], Color> {
   public static DeriveColorConverter getInstance() {
      return DeriveColorConverter.Holder.INSTANCE;
   }

   private DeriveColorConverter() {
   }

   public Color convert(ParsedValue<ParsedValue[], Color> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      Color var4 = (Color)var3[0].convert(var2);
      Size var5 = (Size)var3[1].convert(var2);
      return Utils.deriveColor(var4, var5.pixels(var2));
   }

   @Override
   public String toString() {
      return "DeriveColorConverter";
   }

   private static class Holder {
      static final DeriveColorConverter INSTANCE = new DeriveColorConverter();
   }
}
