package com.sun.javafx.scene.layout.region;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.text.Font;

public final class LayeredBackgroundSizeConverter extends StyleConverter<ParsedValue<ParsedValue[], BackgroundSize>[], BackgroundSize[]> {
   private static final LayeredBackgroundSizeConverter LAYERED_BACKGROUND_SIZE_CONVERTER = new LayeredBackgroundSizeConverter();

   public static LayeredBackgroundSizeConverter getInstance() {
      return LAYERED_BACKGROUND_SIZE_CONVERTER;
   }

   private LayeredBackgroundSizeConverter() {
   }

   public BackgroundSize[] convert(ParsedValue<ParsedValue<ParsedValue[], BackgroundSize>[], BackgroundSize[]> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      BackgroundSize[] var4 = new BackgroundSize[var3.length];

      for (int var5 = 0; var5 < var3.length; var5++) {
         var4[var5] = (BackgroundSize)var3[var5].convert(var2);
      }

      return var4;
   }
}
