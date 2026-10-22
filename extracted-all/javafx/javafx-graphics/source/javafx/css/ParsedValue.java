package javafx.css;

import javafx.scene.text.Font;

public class ParsedValue<V, T> {
   protected final V value;
   protected final StyleConverter<V, T> converter;

   public final V getValue() {
      return this.value;
   }

   public final StyleConverter<V, T> getConverter() {
      return this.converter;
   }

   public T convert(Font var1) {
      return (T)(this.converter != null ? this.converter.convert(this, var1) : this.value);
   }

   public boolean isContainsLookups() {
      return false;
   }

   public boolean isLookup() {
      return false;
   }

   protected ParsedValue(V var1, StyleConverter<V, T> var2) {
      this.value = (V)var1;
      this.converter = var2;
   }
}
