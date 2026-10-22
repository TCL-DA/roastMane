package org.glassfish.jersey.internal.guava;

import java.util.Objects;
import java.util.Map.Entry;

final class RemovalNotification<K, V> implements Entry<K, V> {
   private static final long serialVersionUID = 0L;
   private final K key;
   private final V value;

   RemovalNotification(K key, V value, RemovalCause cause) {
      this.key = key;
      this.value = value;
   }

   @Override
   public K getKey() {
      return this.key;
   }

   @Override
   public V getValue() {
      return this.value;
   }

   @Override
   public final V setValue(V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof Entry)) {
         return false;
      } else {
         Entry<?, ?> that = (Entry<?, ?>)object;
         return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
      }
   }

   @Override
   public int hashCode() {
      K k = this.getKey();
      V v = this.getValue();
      return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
   }

   @Override
   public String toString() {
      return this.getKey() + "=" + this.getValue();
   }
}
