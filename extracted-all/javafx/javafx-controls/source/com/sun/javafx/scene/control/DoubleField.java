package com.sun.javafx.scene.control;

import com.sun.javafx.scene.control.skin.DoubleFieldSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Skin;

public class DoubleField extends InputField {
   private DoubleProperty value = new SimpleDoubleProperty(this, "value");

   public final double getValue() {
      return this.value.get();
   }

   public final void setValue(double var1) {
      this.value.set(var1);
   }

   public final DoubleProperty valueProperty() {
      return this.value;
   }

   public DoubleField() {
      this.getStyleClass().setAll(new String[]{"double-field"});
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new DoubleFieldSkin(this);
   }
}
