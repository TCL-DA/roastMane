package javafx.beans.property;

import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.SetExpression;
import javafx.collections.ObservableSet;

public abstract class ReadOnlySetProperty<E> extends SetExpression<E> implements ReadOnlyProperty<ObservableSet<E>> {
   public void bindContentBidirectional(ObservableSet<E> var1) {
      Bindings.bindContentBidirectional(this, var1);
   }

   public void unbindContentBidirectional(Object var1) {
      Bindings.unbindContentBidirectional(this, var1);
   }

   public void bindContent(ObservableSet<E> var1) {
      Bindings.bindContent(this, var1);
   }

   public void unbindContent(Object var1) {
      Bindings.unbindContent(this, var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Set)) {
         return false;
      } else {
         Set var2 = (Set)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            try {
               return this.containsAll(var2);
            } catch (ClassCastException var4) {
               return false;
            } catch (NullPointerException var5) {
               return false;
            }
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;

      for (Object var3 : this) {
         if (var3 != null) {
            var1 += var3.hashCode();
         }
      }

      return var1;
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("ReadOnlySetProperty [");
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
