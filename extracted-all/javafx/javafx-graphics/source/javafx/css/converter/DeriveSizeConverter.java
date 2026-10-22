package javafx.css.converter;

import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public final class DeriveSizeConverter extends StyleConverter<ParsedValue<Size, Size>[], Size> {
   public static DeriveSizeConverter getInstance() {
      return DeriveSizeConverter.Holder.INSTANCE;
   }

   private DeriveSizeConverter() {
   }

   public Size convert(ParsedValue<ParsedValue<Size, Size>[], Size> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      double var4 = ((Size)var3[0].convert(var2)).pixels(var2);
      double var6 = ((Size)var3[1].convert(var2)).pixels(var2);
      return new Size(var4 + var6, SizeUnits.PX);
   }

   @Override
   public String toString() {
      return "DeriveSizeConverter";
   }

   private static class Holder {
      static final DeriveSizeConverter INSTANCE = new DeriveSizeConverter();
   }
}
