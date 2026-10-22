package javafx.css.converter;

import com.sun.javafx.util.Utils;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public final class StringConverter extends StyleConverter<String, String> {
   public static StyleConverter<String, String> getInstance() {
      return StringConverter.Holder.INSTANCE;
   }

   private StringConverter() {
   }

   public String convert(ParsedValue<String, String> var1, Font var2) {
      String var3 = (String)var1.getValue();
      return var3 == null ? null : Utils.convertUnicode(var3);
   }

   @Override
   public String toString() {
      return "StringConverter";
   }

   private static class Holder {
      static final StringConverter INSTANCE = new StringConverter();
      static final StringConverter.SequenceConverter SEQUENCE_INSTANCE = new StringConverter.SequenceConverter();
   }

   public static final class SequenceConverter extends StyleConverter<ParsedValue<String, String>[], String[]> {
      public static StringConverter.SequenceConverter getInstance() {
         return StringConverter.Holder.SEQUENCE_INSTANCE;
      }

      private SequenceConverter() {
      }

      public String[] convert(ParsedValue<ParsedValue<String, String>[], String[]> var1, Font var2) {
         ParsedValue[] var3 = (ParsedValue[])var1.getValue();
         String[] var4 = new String[var3.length];

         for (int var5 = 0; var5 < var3.length; var5++) {
            var4[var5] = StringConverter.getInstance().convert(var3[var5], var2);
         }

         return var4;
      }

      @Override
      public String toString() {
         return "String.SequenceConverter";
      }
   }
}
