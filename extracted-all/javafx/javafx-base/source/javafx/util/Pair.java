package javafx.util;

import java.io.Serializable;
import javafx.beans.NamedArg;

public class Pair<K, V> implements Serializable {
   private K key;
   private V value;

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   public Pair(@NamedArg("key") K var1, @NamedArg("value") V var2) {
      this.key = (K)var1;
      this.value = (V)var2;
   }

   @Override
   public String toString() {
      return this.key + "=" + this.value;
   }

   @Override
   public int hashCode() {
      int var1 = 7;
      var1 = 31 * var1 + (this.key != null ? this.key.hashCode() : 0);
      return 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Pair)) {
         return false;
      } else {
         Pair var2 = (Pair)var1;
         if (this.key != null ? this.key.equals(var2.key) : var2.key == null) {
            return this.value != null ? this.value.equals(var2.value) : var2.value == null;
         } else {
            return false;
         }
      }
   }
}
