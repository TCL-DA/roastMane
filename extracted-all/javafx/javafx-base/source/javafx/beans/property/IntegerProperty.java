package javafx.beans.property;

import com.sun.javafx.binding.BidirectionalBinding;
import com.sun.javafx.binding.Logging;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.value.WritableIntegerValue;

public abstract class IntegerProperty extends ReadOnlyIntegerProperty implements Property<Number>, WritableIntegerValue {
   @Override
   public void setValue(Number var1) {
      if (var1 == null) {
         Logging.getLogger().fine("Attempt to set integer property to null, using default value instead.", new NullPointerException());
         this.set(0);
      } else {
         this.set(var1.intValue());
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
      StringBuilder var3 = new StringBuilder("IntegerProperty [");
      if (var1 != null) {
         var3.append("bean: ").append(var1).append(", ");
      }

      if (var2 != null && !var2.equals("")) {
         var3.append("name: ").append(var2).append(", ");
      }

      var3.append("value: ").append(this.get()).append("]");
      return var3.toString();
   }

   public static IntegerProperty integerProperty(final Property<Integer> var0) {
      Objects.requireNonNull(var0, "Property cannot be null");
      return new IntegerPropertyBase() {
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

   public ObjectProperty<Integer> asObject() {
      return new ObjectPropertyBase<Integer>() {
         {
            BidirectionalBinding.bindNumber(this, IntegerProperty.this);
         }

         @Override
         public Object getBean() {
            return null;
         }

         @Override
         public String getName() {
            return IntegerProperty.this.getName();
         }
      };
   }
}
