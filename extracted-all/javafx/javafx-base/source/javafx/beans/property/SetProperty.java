package javafx.beans.property;

import javafx.beans.binding.Bindings;
import javafx.beans.value.WritableSetValue;
import javafx.collections.ObservableSet;

public abstract class SetProperty<E> extends ReadOnlySetProperty<E> implements Property<ObservableSet<E>>, WritableSetValue<E> {
   public void setValue(ObservableSet<E> var1) {
      this.set(var1);
   }

   @Override
   public void bindBidirectional(Property<ObservableSet<E>> var1) {
      Bindings.bindBidirectional(this, var1);
   }

   @Override
   public void unbindBidirectional(Property<ObservableSet<E>> var1) {
      Bindings.unbindBidirectional(this, var1);
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("SetProperty [");
      if (var1 != null) {
         var3.append("bean: ").append(var1).append(", ");
      }

      if (var2 != null && !var2.equals("")) {
         var3.append("name: ").append(var2).append(", ");
      }

      var3.append("value: ").append(this.get()).append("]");
      return var3.toString();
   }
}
