package com.sun.javafx.css;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.StyleableProperty;
import javafx.scene.Node;

public class SubCssMetaData<T> extends CssMetaData<Node, T> {
   public SubCssMetaData(String var1, StyleConverter var2, T var3) {
      super(var1, var2, (T)var3);
   }

   public SubCssMetaData(String var1, StyleConverter var2) {
      super(var1, var2);
   }

   public boolean isSettable(Node var1) {
      return false;
   }

   public StyleableProperty<T> getStyleableProperty(Node var1) {
      return null;
   }
}
