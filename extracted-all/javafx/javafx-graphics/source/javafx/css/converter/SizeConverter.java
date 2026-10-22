package javafx.css.converter;

import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public final class SizeConverter extends StyleConverter<ParsedValue<?, Size>, Number> {
   public static StyleConverter<ParsedValue<?, Size>, Number> getInstance() {
      return SizeConverter.Holder.INSTANCE;
   }

   private SizeConverter() {
   }

   public Number convert(ParsedValue<ParsedValue<?, Size>, Number> var1, Font var2) {
      ParsedValue var3 = (ParsedValue)var1.getValue();
      return ((Size)var3.convert(var2)).pixels(var2);
   }

   @Override
   public String toString() {
      return "SizeConverter";
   }

   private static class Holder {
      static final SizeConverter INSTANCE = new SizeConverter();
      static final SizeConverter.SequenceConverter SEQUENCE_INSTANCE = new SizeConverter.SequenceConverter();
   }

   public static final class SequenceConverter extends StyleConverter<ParsedValue[], Number[]> {
      public static SizeConverter.SequenceConverter getInstance() {
         return SizeConverter.Holder.SEQUENCE_INSTANCE;
      }

      private SequenceConverter() {
      }

      public Number[] convert(ParsedValue<ParsedValue[], Number[]> var1, Font var2) {
         ParsedValue[] var3 = (ParsedValue[])var1.getValue();
         Number[] var4 = new Number[var3.length];

         for (int var5 = 0; var5 < var3.length; var5++) {
            var4[var5] = ((Size)var3[var5].convert(var2)).pixels(var2);
         }

         return var4;
      }

      @Override
      public String toString() {
         return "Size.SequenceConverter";
      }
   }
}
