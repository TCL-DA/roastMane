package javafx.beans.property;

import com.sun.javafx.binding.BidirectionalBinding;
import com.sun.javafx.binding.Logging;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.value.WritableBooleanValue;

public abstract class BooleanProperty extends ReadOnlyBooleanProperty implements Property<Boolean>, WritableBooleanValue {
   @Override
   public void setValue(Boolean var1) {
      if (var1 == null) {
         Logging.getLogger().fine("Attempt to set boolean property to null, using default value instead.", new NullPointerException());
         this.set(false);
      } else {
         this.set(var1);
      }
   }

   @Override
   public void bindBidirectional(Property<Boolean> var1) {
      Bindings.bindBidirectional(this, var1);
   }

   @Override
   public void unbindBidirectional(Property<Boolean> var1) {
      Bindings.unbindBidirectional(this, var1);
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("BooleanProperty [");
      if (var1 != null) {
         var3.append("bean: ").append(var1).append(", ");
      }

      if (var2 != null && !var2.equals("")) {
         var3.append("name: ").append(var2).append(", ");
      }

      var3.append("value: ").append(this.get()).append("]");
      return var3.toString();
   }

   public static BooleanProperty booleanProperty(final Property<Boolean> var0) {
      Objects.requireNonNull(var0, "Property cannot be null");
      return (BooleanProperty)(var0 instanceof BooleanProperty ? (BooleanProperty)var0 : new BooleanPropertyBase() {
         {
            BidirectionalBinding.bind(this, var0);
         }

         @Override
         public Object getBean() {
            return null;
         }

         @Override
         public String getName() {
            return var0.getName();
         }
      });
   }

   public ObjectProperty<Boolean> asObject() {
      return new ObjectPropertyBase<Boolean>() {
         {
            BidirectionalBinding.bind(this, BooleanProperty.this);
         }

         @Override
         public Object getBean() {
            return null;
         }

         @Override
         public String getName() {
            return BooleanProperty.this.getName();
         }
      };
   }
}
