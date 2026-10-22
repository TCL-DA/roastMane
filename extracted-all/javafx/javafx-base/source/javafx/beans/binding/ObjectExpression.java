package javafx.beans.binding;

import com.sun.javafx.binding.StringFormatter;
import java.util.Locale;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class ObjectExpression<T> implements ObservableObjectValue<T> {
   @Override
   public T getValue() {
      return this.get();
   }

   public static <T> ObjectExpression<T> objectExpression(final ObservableObjectValue<T> var0) {
      if (var0 == null) {
         throw new NullPointerException("Value must be specified.");
      } else {
         return (ObjectExpression<T>)(var0 instanceof ObjectExpression ? (ObjectExpression)var0 : new ObjectBinding<T>() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected T computeValue() {
               return (T)var0.get();
            }

            @Override
            public ObservableList<ObservableObjectValue<T>> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         });
      }
   }

   public BooleanBinding isEqualTo(ObservableObjectValue<?> var1) {
      return Bindings.equal(this, var1);
   }

   public BooleanBinding isEqualTo(Object var1) {
      return Bindings.equal(this, var1);
   }

   public BooleanBinding isNotEqualTo(ObservableObjectValue<?> var1) {
      return Bindings.notEqual(this, var1);
   }

   public BooleanBinding isNotEqualTo(Object var1) {
      return Bindings.notEqual(this, var1);
   }

   public BooleanBinding isNull() {
      return Bindings.isNull(this);
   }

   public BooleanBinding isNotNull() {
      return Bindings.isNotNull(this);
   }

   public StringBinding asString() {
      return (StringBinding)StringFormatter.convert(this);
   }

   public StringBinding asString(String var1) {
      return (StringBinding)Bindings.format(var1, this);
   }

   public StringBinding asString(Locale var1, String var2) {
      return (StringBinding)Bindings.format(var1, var2, this);
   }
}
