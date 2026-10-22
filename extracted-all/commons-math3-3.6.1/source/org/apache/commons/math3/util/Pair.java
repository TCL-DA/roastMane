package org.apache.commons.math3.util;

public class Pair<K, V> {
   private final K key;
   private final V value;

   public Pair(K k, V v) {
      this.key = k;
      this.value = v;
   }

   public Pair(Pair<? extends K, ? extends V> entry) {
      this((K)entry.getKey(), (V)entry.getValue());
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   public K getFirst() {
      return this.key;
   }

   public V getSecond() {
      return this.value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Pair)) {
         return false;
      } else {
         Pair<?, ?> oP = (Pair<?, ?>)o;
         return (this.key == null ? oP.key == null : this.key.equals(oP.key)) && (this.value == null ? oP.value == null : this.value.equals(oP.value));
      }
   }

   @Override
   public int hashCode() {
      int result = this.key == null ? 0 : this.key.hashCode();
      int h = this.value == null ? 0 : this.value.hashCode();
      return 37 * result + h ^ h >>> 16;
   }

   @Override
   public String toString() {
      return "[" + this.getKey() + ", " + this.getValue() + "]";
   }

   public static <K, V> Pair<K, V> create(K k, V v) {
      return new Pair<>(k, v);
   }
}
