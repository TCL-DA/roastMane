package javafx.beans.property;

import com.sun.javafx.binding.BidirectionalBinding;
import com.sun.javafx.binding.Logging;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.value.WritableDoubleValue;

public abstract class DoubleProperty extends ReadOnlyDoubleProperty implements Property<Number>, WritableDoubleValue {
   @Override
   public void setValue(Number var1) {
      if (var1 == null) {
         Logging.getLogger().fine("Attempt to set double property to null, using default value instead.", new NullPointerException());
         this.set(0.0);
      } else {
         this.set(var1.doubleValue());
      }
   }

   @Override
   public void bindBidirectional(Property<Number> var1) {
      Bindings.bindBidirectional(this, var1);
   }

   @Override
   public void unbindBidirectional(Property<Number> var1) {
      Bindings.unbindBidirectional(this, var1);
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("DoubleProperty [");
      if (var1 != null) {
         var3.append("bean: ").append(var1).append(", ");
      }

      if (var2 != null && !var2.equals("")) {
         var3.append("name: ").append(var2).append(", ");
      }

      var3.append("value: ").append(this.get()).append("]");
      return var3.toString();
   }

   public static DoubleProperty doubleProperty(final Property<Double> var0) {
      Objects.requireNonNull(var0, "Property cannot be null");
      return new DoublePropertyBase() {
         {
            BidirectionalBinding.bindNumber(this, var0);
         }

         @Override
         public Object getBean() {
            return null;
         }

         @Override
         public String getName() {
            return var0.getName();
         }
      };
   }

   public ObjectProperty<Double> asObject() {
      return new ObjectPropertyBase<Double>() {
         {
            BidirectionalBinding.bindNumber(this, DoubleProperty.this);
         }

         @Override
         public Object getBean() {
            return null;
         }

         @Override
         public String getName() {
            return DoubleProperty.this.getName();
         }
      };
   }
}
