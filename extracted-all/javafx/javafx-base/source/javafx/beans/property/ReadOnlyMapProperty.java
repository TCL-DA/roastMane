package javafx.beans.property;

import java.util.Map;
import java.util.Map.Entry;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.MapExpression;
import javafx.collections.ObservableMap;

public abstract class ReadOnlyMapProperty<K, V> extends MapExpression<K, V> implements ReadOnlyProperty<ObservableMap<K, V>> {
   public void bindContentBidirectional(ObservableMap<K, V> var1) {
      Bindings.bindContentBidirectional(this, var1);
   }

   public void unbindContentBidirectional(Object var1) {
      Bindings.unbindContentBidirectional(this, var1);
   }

   public void bindContent(ObservableMap<K, V> var1) {
      Bindings.bindContent(this, var1);
   }

   public void unbindContent(Object var1) {
      Bindings.unbindContent(this, var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Map)) {
         return false;
      } else {
         Map var2 = (Map)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            try {
               for (Entry var4 : this.entrySet()) {
                  Object var5 = var4.getKey();
                  Object var6 = var4.getValue();
                  if (var6 == null) {
                     if (var2.get(var5) != null || !var2.containsKey(var5)) {
                        return false;
                     }
                  } else if (!var6.equals(var2.get(var5))) {
                     return false;
                  }
               }

               return true;
            } catch (ClassCastException var7) {
               return false;
            } catch (NullPointerException var8) {
               return false;
            }
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;

      for (Entry var3 : this.entrySet()) {
         var1 += var3.hashCode();
      }

      return var1;
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("ReadOnlyMapProperty [");
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
