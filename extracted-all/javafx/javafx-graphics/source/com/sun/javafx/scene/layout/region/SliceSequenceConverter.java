package com.sun.javafx.scene.layout.region;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public final class SliceSequenceConverter extends StyleConverter<ParsedValue<ParsedValue[], BorderImageSlices>[], BorderImageSlices[]> {
   private static final SliceSequenceConverter BORDER_IMAGE_SLICE_SEQUENCE_CONVERTER = new SliceSequenceConverter();

   public static SliceSequenceConverter getInstance() {
      return BORDER_IMAGE_SLICE_SEQUENCE_CONVERTER;
   }

   public BorderImageSlices[] convert(ParsedValue<ParsedValue<ParsedValue[], BorderImageSlices>[], BorderImageSlices[]> var1, Font var2) {
      ParsedValue[] var3 = (ParsedValue[])var1.getValue();
      BorderImageSlices[] var4 = new BorderImageSlices[var3.length];

      for (int var5 = 0; var5 < var3.length; var5++) {
         var4[var5] = BorderImageSliceConverter.getInstance().convert(var3[var5], var2);
      }

      return var4;
   }

   @Override
   public String toString() {
      return "BorderImageSliceSequenceConverter";
   }
}
