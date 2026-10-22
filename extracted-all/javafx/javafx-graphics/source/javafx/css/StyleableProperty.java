package javafx.css;

import javafx.beans.value.WritableValue;

public interface StyleableProperty<T> extends WritableValue<T> {
   void applyStyle(StyleOrigin var1, T var2);

   StyleOrigin getStyleOrigin();

   CssMetaData<? extends Styleable, T> getCssMetaData();
}
