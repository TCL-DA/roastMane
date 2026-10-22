package javafx.css.converter;

import com.sun.javafx.util.Utils;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public final class LadderConverter extends StyleConverter<ParsedValue[], Color> {
   public static LadderConverter getInstance() {
      return LadderConverter.Holder.INSTANCE;
   }

   private LadderConverter() {
   }

   public Color convert(ParsedValue<ParsedValue[], Color> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      Color var4 = (Color)var3[0].convert(var2);
      Stop[] var5 = new Stop[var3.length - 1];

      for (int var6 = 1; var6 < var3.length; var6++) {
         var5[var6 - 1] = (Stop)var3[var6].convert(var2);
      }

      return Utils.ladder(var4, var5);
   }

   @Override
   public String toString() {
      return "LadderConverter";
   }

   private static class Holder {
      static final LadderConverter INSTANCE = new LadderConverter();
   }
}
