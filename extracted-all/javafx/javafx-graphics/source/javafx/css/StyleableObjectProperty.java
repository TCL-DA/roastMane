package javafx.css;

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ObservableValue;

public abstract class StyleableObjectProperty<T> extends ObjectPropertyBase<T> implements StyleableProperty<T> {
   private StyleOrigin origin = null;

   public StyleableObjectProperty() {
   }

   public StyleableObjectProperty(T var1) {
      super(var1);
   }

   @Override
   public void applyStyle(StyleOrigin var1, T var2) {
      this.set((T)var2);
      this.origin = var1;
   }

   public void bind(ObservableValue<? extends T> var1) {
      super.bind(var1);
      this.origin = StyleOrigin.USER;
   }

   public void set(T var1) {
      super.set(var1);
      this.origin = StyleOrigin.USER;
   }

   @Override
   public StyleOrigin getStyleOrigin() {
      return this.origin;
   }
}
