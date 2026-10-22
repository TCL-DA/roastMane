package com.sun.javafx.fxml.builder.web;

import java.util.Arrays;
import java.util.Collection;
import javafx.scene.Parent;

@Deprecated
public abstract class ParentBuilder<B extends ParentBuilder<B>> extends NodeBuilder<B> {
   private int __set;
   private Collection<? extends String> stylesheets;

   protected ParentBuilder() {
   }

   public void applyTo(Parent var1) {
      super.applyTo(var1);
      int var2 = this.__set;
      if ((var2 & 2) != 0) {
         var1.getStylesheets().addAll(this.stylesheets);
      }
   }

   public B stylesheets(Collection<? extends String> var1) {
      this.stylesheets = var1;
      this.__set |= 2;
      return (B)this;
   }

   public B stylesheets(String... var1) {
      return this.stylesheets(Arrays.asList(var1));
   }
}
