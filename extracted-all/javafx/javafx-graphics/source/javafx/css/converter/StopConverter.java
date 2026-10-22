package javafx.css.converter;

import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public final class StopConverter extends StyleConverter<ParsedValue[], Stop> {
   public static StopConverter getInstance() {
      return StopConverter.Holder.INSTANCE;
   }

   private StopConverter() {
   }

   public Stop convert(ParsedValue<ParsedValue[], Stop> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      Double var4 = ((Size)var3[0].convert(var2)).pixels(var2);
      Color var5 = (Color)var3[1].convert(var2);
      return new Stop(var4, var5);
   }

   @Override
   public String toString() {
      return "StopConverter";
   }

   private static class Holder {
      static final StopConverter INSTANCE = new StopConverter();
   }
}
