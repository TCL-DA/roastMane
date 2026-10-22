package com.sun.javafx.scene.layout.region;

import javafx.scene.layout.BorderWidths;

public class BorderImageSlices {
   public static final BorderImageSlices EMPTY = new BorderImageSlices(BorderWidths.EMPTY, false);
   public static final BorderImageSlices DEFAULT = new BorderImageSlices(BorderWidths.FULL, false);
   public BorderWidths widths;
   public boolean filled;

   public BorderImageSlices(BorderWidths var1, boolean var2) {
      this.widths = var1;
      this.filled = var2;
   }
}
