package org.controlsfx.control.spreadsheet;

import javafx.util.StringConverter;

public abstract class StringConverterWithFormat<T> extends StringConverter<T> {
   protected StringConverter<T> myConverter;

   public StringConverterWithFormat() {
   }

   public StringConverterWithFormat(StringConverter<T> specificStringConverter) {
      this.myConverter = specificStringConverter;
   }

   public String toStringFormat(T value, String format) {
      return this.toString(value);
   }
}
