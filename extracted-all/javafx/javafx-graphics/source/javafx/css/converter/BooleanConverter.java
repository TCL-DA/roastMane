package javafx.css.converter;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public final class BooleanConverter extends StyleConverter<String, Boolean> {
   public static StyleConverter<String, Boolean> getInstance() {
      return BooleanConverter.Holder.INSTANCE;
   }

   private BooleanConverter() {
   }

   public Boolean convert(ParsedValue<String, Boolean> var1, Font var2) {
      String var3 = (String)var1.getValue();
      return Boolean.valueOf(var3);
   }

   @Override
   public String toString() {
      return "BooleanConverter";
   }

   private static class Holder {
      static final BooleanConverter INSTANCE = new BooleanConverter();
   }
}
