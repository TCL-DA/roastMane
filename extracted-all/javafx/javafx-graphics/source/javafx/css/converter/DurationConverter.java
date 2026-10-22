package javafx.css.converter;

import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
import javafx.util.Duration;

public final class DurationConverter extends StyleConverter<ParsedValue<?, Size>, Duration> {
   public static StyleConverter<ParsedValue<?, Size>, Duration> getInstance() {
      return DurationConverter.Holder.INSTANCE;
   }

   private DurationConverter() {
   }

   public Duration convert(ParsedValue<ParsedValue<?, Size>, Duration> var1, Font var2) {
      ParsedValue var3 = (ParsedValue)var1.getValue();
      Size var4 = (Size)var3.convert(var2);
      double var5 = var4.getValue();
      Duration var7 = null;
      if (var5 < Double.POSITIVE_INFINITY) {
         switch (var4.getUnits()) {
            case S:
               var7 = Duration.seconds(var5);
               break;
            case MS:
               var7 = Duration.millis(var5);
               break;
            default:
               var7 = Duration.UNKNOWN;
         }
      } else {
         var7 = Duration.INDEFINITE;
      }

      return var7;
   }

   @Override
   public String toString() {
      return "DurationConverter";
   }

   private static class Holder {
      static final DurationConverter INSTANCE = new DurationConverter();
   }
}
