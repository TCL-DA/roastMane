package com.sun.javafx.scene.layout.region;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.converter.EnumConverter;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.text.Font;

public final class RepeatStructConverter extends StyleConverter<ParsedValue<String, BackgroundRepeat>[][], RepeatStruct[]> {
   private static final RepeatStructConverter REPEAT_STRUCT_CONVERTER = new RepeatStructConverter();
   private final EnumConverter<BackgroundRepeat> repeatConverter = new EnumConverter<>(BackgroundRepeat.class);

   public static RepeatStructConverter getInstance() {
      return REPEAT_STRUCT_CONVERTER;
   }

   private RepeatStructConverter() {
   }

   public RepeatStruct[] convert(ParsedValue<ParsedValue<String, BackgroundRepeat>[][], RepeatStruct[]> var1, Font var2) {
      ParsedValue[][] var3 = (ParsedValue[][])var1.getValue();
      RepeatStruct[] var4 = new RepeatStruct[var3.length];

      for (int var5 = 0; var5 < var3.length; var5++) {
         ParsedValue[] var6 = var3[var5];
         BackgroundRepeat var7 = this.repeatConverter.convert(var6[0], null);
         BackgroundRepeat var8 = this.repeatConverter.convert(var6[1], null);
         var4[var5] = new RepeatStruct(var7, var8);
      }

      return var4;
   }

   @Override
   public String toString() {
      return "RepeatStructConverter";
   }
}
