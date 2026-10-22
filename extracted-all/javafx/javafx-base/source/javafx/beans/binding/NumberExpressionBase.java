package javafx.beans.binding;

import com.sun.javafx.binding.StringFormatter;
import java.util.Locale;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableNumberValue;

public abstract class NumberExpressionBase implements NumberExpression {
   public static <S extends Number> NumberExpressionBase numberExpression(ObservableNumberValue var0) {
      if (var0 == null) {
         throw new NullPointerException("Value must be specified.");
      } else {
         NumberExpressionBase var1 = (NumberExpressionBase)(
            var0 instanceof NumberExpressionBase
               ? var0
               : (
                  var0 instanceof ObservableIntegerValue
                     ? IntegerExpression.integerExpression((ObservableIntegerValue)var0)
                     : (
                        var0 instanceof ObservableDoubleValue
                           ? DoubleExpression.doubleExpression((ObservableDoubleValue)var0)
                           : (
                              var0 instanceof ObservableFloatValue
                                 ? FloatExpression.floatExpression((ObservableFloatValue)var0)
                                 : (var0 instanceof ObservableLongValue ? LongExpression.longExpression((ObservableLongValue)var0) : null)
                           )
                     )
               )
         );
         if (var1 != null) {
            return var1;
         } else {
            throw new IllegalArgumentException("Unsupported Type");
         }
      }
   }

   @Override
   public NumberBinding add(ObservableNumberValue var1) {
      return Bindings.add(this, var1);
   }

   @Override
   public NumberBinding subtract(ObservableNumberValue var1) {
      return Bindings.subtract(this, var1);
   }

   @Override
   public NumberBinding multiply(ObservableNumberValue var1) {
      return Bindings.multiply(this, var1);
   }

   @Override
   public NumberBinding divide(ObservableNumberValue var1) {
      return Bindings.divide(this, var1);
   }

   @Override
   public BooleanBinding isEqualTo(ObservableNumberValue var1) {
      return Bindings.equal(this, var1);
   }

   @Override
   public BooleanBinding isEqualTo(ObservableNumberValue var1, double var2) {
      return Bindings.equal(this, var1, var2);
   }

   @Override
   public BooleanBinding isEqualTo(double var1, double var3) {
      return Bindings.equal(this, var1, var3);
   }

   @Override
   public BooleanBinding isEqualTo(float var1, double var2) {
      return Bindings.equal(this, var1, var2);
   }

   @Override
   public BooleanBinding isEqualTo(long var1) {
      return Bindings.equal(this, var1);
   }

   @Override
   public BooleanBinding isEqualTo(long var1, double var3) {
      return Bindings.equal(this, var1, var3);
   }

   @Override
   public BooleanBinding isEqualTo(int var1) {
      return Bindings.equal(this, var1);
   }

   @Override
   public BooleanBinding isEqualTo(int var1, double var2) {
      return Bindings.equal(this, var1, var2);
   }

   @Override
   public BooleanBinding isNotEqualTo(ObservableNumberValue var1) {
      return Bindings.notEqual(this, var1);
   }

   @Override
   public BooleanBinding isNotEqualTo(ObservableNumberValue var1, double var2) {
      return Bindings.notEqual(this, var1, var2);
   }

   @Override
   public BooleanBinding isNotEqualTo(double var1, double var3) {
      return Bindings.notEqual(this, var1, var3);
   }

   @Override
   public BooleanBinding isNotEqualTo(float var1, double var2) {
      return Bindings.notEqual(this, var1, var2);
   }

   @Override
   public BooleanBinding isNotEqualTo(long var1) {
      return Bindings.notEqual(this, var1);
   }

   @Override
   public BooleanBinding isNotEqualTo(long var1, double var3) {
      return Bindings.notEqual(this, var1, var3);
   }

   @Override
   public BooleanBinding isNotEqualTo(int var1) {
      return Bindings.notEqual(this, var1);
   }

   @Override
   public BooleanBinding isNotEqualTo(int var1, double var2) {
      return Bindings.notEqual(this, var1, var2);
   }

   @Override
   public BooleanBinding greaterThan(ObservableNumberValue var1) {
      return Bindings.greaterThan(this, var1);
   }

   @Override
   public BooleanBinding greaterThan(double var1) {
      return Bindings.greaterThan(this, var1);
   }

   @Override
   public BooleanBinding greaterThan(float var1) {
      return Bindings.greaterThan(this, var1);
   }

   @Override
   public BooleanBinding greaterThan(long var1) {
      return Bindings.greaterThan(this, var1);
   }

   @Override
   public BooleanBinding greaterThan(int var1) {
      return Bindings.greaterThan(this, var1);
   }

   @Override
   public BooleanBinding lessThan(ObservableNumberValue var1) {
      return Bindings.lessThan(this, var1);
   }

   @Override
   public BooleanBinding lessThan(double var1) {
      return Bindings.lessThan(this, var1);
   }

   @Override
   public BooleanBinding lessThan(float var1) {
      return Bindings.lessThan(this, var1);
   }

   @Override
   public BooleanBinding lessThan(long var1) {
      return Bindings.lessThan(this, var1);
   }

   @Override
   public BooleanBinding lessThan(int var1) {
      return Bindings.lessThan(this, var1);
   }

   @Override
   public BooleanBinding greaterThanOrEqualTo(ObservableNumberValue var1) {
      return Bindings.greaterThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding greaterThanOrEqualTo(double var1) {
      return Bindings.greaterThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding greaterThanOrEqualTo(float var1) {
      return Bindings.greaterThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding greaterThanOrEqualTo(long var1) {
      return Bindings.greaterThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding greaterThanOrEqualTo(int var1) {
      return Bindings.greaterThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding lessThanOrEqualTo(ObservableNumberValue var1) {
      return Bindings.lessThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding lessThanOrEqualTo(double var1) {
      return Bindings.lessThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding lessThanOrEqualTo(float var1) {
      return Bindings.lessThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding lessThanOrEqualTo(long var1) {
      return Bindings.lessThanOrEqual(this, var1);
   }

   @Override
   public BooleanBinding lessThanOrEqualTo(int var1) {
      return Bindings.lessThanOrEqual(this, var1);
   }

   @Override
   public StringBinding asString() {
      return (StringBinding)StringFormatter.convert(this);
   }

   @Override
   public StringBinding asString(String var1) {
      return (StringBinding)Bindings.format(var1, this);
   }

   @Override
   public StringBinding asString(Locale var1, String var2) {
      return (StringBinding)Bindings.format(var1, var2, this);
   }
}
