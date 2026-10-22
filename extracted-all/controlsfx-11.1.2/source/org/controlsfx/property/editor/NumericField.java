package org.controlsfx.property.editor;

import java.math.BigInteger;
import javafx.beans.binding.NumberExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;

class NumericField extends TextField {
   private final NumericField.NumericValidator<? extends Number> value;

   public NumericField(Class<? extends Number> cls) {
      if (cls != byte.class
         && cls != Byte.class
         && cls != short.class
         && cls != Short.class
         && cls != int.class
         && cls != Integer.class
         && cls != long.class
         && cls != Long.class
         && cls != BigInteger.class) {
         this.value = new NumericField.DoubleValidator(this);
      } else {
         this.value = new NumericField.LongValidator(this);
      }

      this.focusedProperty().addListener((observable, oldValue, newValue) -> {
         if (!newValue) {
            this.value.setValue(this.value.toNumber(this.getText()));
         }
      });
   }

   public final ObservableValue<Number> valueProperty() {
      return this.value;
   }

   public void replaceText(int start, int end, String text) {
      if (this.replaceValid(start, end, text)) {
         super.replaceText(start, end, text);
      }
   }

   public void replaceSelection(String text) {
      IndexRange range = this.getSelection();
      if (this.replaceValid(range.getStart(), range.getEnd(), text)) {
         super.replaceSelection(text);
      }
   }

   protected void setValue(Number value) {
      this.value.setValue(value);
   }

   private Boolean replaceValid(int start, int end, String fragment) {
      try {
         String newText = this.getText().substring(0, start) + fragment + this.getText().substring(end);
         if (newText.isEmpty()) {
            return true;
         } else {
            this.value.toNumber(newText);
            return true;
         }
      } catch (Throwable var5) {
         return false;
      }
   }

   static class DoubleValidator extends SimpleDoubleProperty implements NumericField.NumericValidator<Double> {
      private NumericField field;

      public DoubleValidator(NumericField field) {
         super(field, "value", 0.0);
         this.field = field;
      }

      protected void invalidated() {
         this.field.setText(Double.toString(this.get()));
      }

      public Double toNumber(String s) {
         if (s != null && !s.trim().isEmpty()) {
            String d = s.trim();
            if (!d.endsWith("f") && !d.endsWith("d") && !d.endsWith("F") && !d.endsWith("D")) {
               return new Double(d);
            } else {
               throw new NumberFormatException("There should be no alpha symbols");
            }
         } else {
            return 0.0;
         }
      }
   }

   static class LongValidator extends SimpleLongProperty implements NumericField.NumericValidator<Long> {
      private NumericField field;

      public LongValidator(NumericField field) {
         super(field, "value", 0L);
         this.field = field;
      }

      protected void invalidated() {
         this.field.setText(Long.toString(this.get()));
      }

      public Long toNumber(String s) {
         if (s != null && !s.trim().isEmpty()) {
            String d = s.trim();
            return new Long(d);
         } else {
            return 0L;
         }
      }
   }

   private interface NumericValidator<T extends Number> extends NumberExpression {
      void setValue(Number var1);

      T toNumber(String var1);
   }
}
