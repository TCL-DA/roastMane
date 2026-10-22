package javafx.beans.binding;

import com.sun.javafx.binding.StringFormatter;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

public abstract class StringExpression implements ObservableStringValue {
   public String getValue() {
      return this.get();
   }

   public final String getValueSafe() {
      String var1 = this.get();
      return var1 == null ? "" : var1;
   }

   public static StringExpression stringExpression(ObservableValue<?> var0) {
      if (var0 == null) {
         throw new NullPointerException("Value must be specified.");
      } else {
         return StringFormatter.convert(var0);
      }
   }

   public StringExpression concat(Object var1) {
      return Bindings.concat(this, var1);
   }

   public BooleanBinding isEqualTo(ObservableStringValue var1) {
      return Bindings.equal(this, var1);
   }

   public BooleanBinding isEqualTo(String var1) {
      return Bindings.equal(this, var1);
   }

   public BooleanBinding isNotEqualTo(ObservableStringValue var1) {
      return Bindings.notEqual(this, var1);
   }

   public BooleanBinding isNotEqualTo(String var1) {
      return Bindings.notEqual(this, var1);
   }

   public BooleanBinding isEqualToIgnoreCase(ObservableStringValue var1) {
      return Bindings.equalIgnoreCase(this, var1);
   }

   public BooleanBinding isEqualToIgnoreCase(String var1) {
      return Bindings.equalIgnoreCase(this, var1);
   }

   public BooleanBinding isNotEqualToIgnoreCase(ObservableStringValue var1) {
      return Bindings.notEqualIgnoreCase(this, var1);
   }

   public BooleanBinding isNotEqualToIgnoreCase(String var1) {
      return Bindings.notEqualIgnoreCase(this, var1);
   }

   public BooleanBinding greaterThan(ObservableStringValue var1) {
      return Bindings.greaterThan(this, var1);
   }

   public BooleanBinding greaterThan(String var1) {
      return Bindings.greaterThan(this, var1);
   }

   public BooleanBinding lessThan(ObservableStringValue var1) {
      return Bindings.lessThan(this, var1);
   }

   public BooleanBinding lessThan(String var1) {
      return Bindings.lessThan(this, var1);
   }

   public BooleanBinding greaterThanOrEqualTo(ObservableStringValue var1) {
      return Bindings.greaterThanOrEqual(this, var1);
   }

   public BooleanBinding greaterThanOrEqualTo(String var1) {
      return Bindings.greaterThanOrEqual(this, var1);
   }

   public BooleanBinding lessThanOrEqualTo(ObservableStringValue var1) {
      return Bindings.lessThanOrEqual(this, var1);
   }

   public BooleanBinding lessThanOrEqualTo(String var1) {
      return Bindings.lessThanOrEqual(this, var1);
   }

   public BooleanBinding isNull() {
      return Bindings.isNull(this);
   }

   public BooleanBinding isNotNull() {
      return Bindings.isNotNull(this);
   }

   public IntegerBinding length() {
      return Bindings.length(this);
   }

   public BooleanBinding isEmpty() {
      return Bindings.isEmpty(this);
   }

   public BooleanBinding isNotEmpty() {
      return Bindings.isNotEmpty(this);
   }
}
