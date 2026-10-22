package com.sun.javafx.scene.layout.region;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.text.Font;

public final class LayeredBackgroundPositionConverter extends StyleConverter<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]> {
   private static final LayeredBackgroundPositionConverter LAYERED_BACKGROUND_POSITION_CONVERTER = new LayeredBackgroundPositionConverter();

   public static LayeredBackgroundPositionConverter getInstance() {
      return LAYERED_BACKGROUND_POSITION_CONVERTER;
   }

   private LayeredBackgroundPositionConverter() {
   }

   public BackgroundPosition[] convert(ParsedValue<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      BackgroundPosition[] var4 = new BackgroundPosition[var3.length];

      for (int var5 = 0; var5 < var3.length; var5++) {
         var4[var5] = (BackgroundPosition)var3[var5].convert(var2);
      }

      return var4;
   }

   @Override
   public String toString() {
      return "LayeredBackgroundPositionConverter";
   }
}
