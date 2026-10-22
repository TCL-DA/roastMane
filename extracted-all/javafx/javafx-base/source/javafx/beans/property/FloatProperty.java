package javafx.beans.property;

import com.sun.javafx.binding.BidirectionalBinding;
import com.sun.javafx.binding.Logging;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.value.WritableFloatValue;

public abstract class FloatProperty extends ReadOnlyFloatProperty implements Property<Number>, WritableFloatValue {
   @Override
   public void setValue(Number var1) {
      if (var1 == null) {
         Logging.getLogger().fine("Attempt to set float property to null, using default value instead.", new NullPointerException());
         this.set(0.0F);
      } else {
         this.set(var1.floatValue());
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
      StringBuilder var3 = new StringBuilder("FloatProperty [");
      if (var1 != null) {
         var3.append("bean: ").append(var1).append(", ");
      }

      if (var2 != null && !var2.equals("")) {
         var3.append("name: ").append(var2).append(", ");
      }

      var3.append("value: ").append(this.get()).append("]");
      return var3.toString();
   }

   public static FloatProperty floatProperty(final Property<Float> var0) {
      Objects.requireNonNull(var0, "Property cannot be null");
      return new FloatPropertyBase() {
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

   public ObjectProperty<Float> asObject() {
      return new ObjectPropertyBase<Float>() {
         {
            BidirectionalBinding.bindNumber(this, FloatProperty.this);
         }

         @Override
         public Object getBean() {
            return null;
         }

         @Override
         public String getName() {
            return FloatProperty.this.getName();
         }
      };
   }
}
