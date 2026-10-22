package org.girod.javafx.svgimage.tosvg.utils;

import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;

public class CSSProperty {
   private StyleableProperty<?> prop = null;
   private Object value = null;

   public CSSProperty(StyleableProperty<?> prop, Object value) {
      this.prop = prop;
      this.value = value;
   }

   public StyleableProperty<?> getProperty() {
      return this.prop;
   }

   public StyleOrigin getStyleOrigin() {
      return this.prop.getStyleOrigin();
   }

   public Object getValue() {
      return this.value;
   }
}
