package org.glassfish.jersey.internal.guava;

import java.util.Objects;
import java.util.Map.Entry;

public abstract class ForwardingMapEntry<K, V> extends ForwardingObject implements Entry<K, V> {
   ForwardingMapEntry() {
   }

   protected abstract Entry<K, V> delegate();

   @Override
   public K getKey() {
      return this.delegate().getKey();
   }

   @Override
   public V getValue() {
      return this.delegate().getValue();
   }

   @Override
   public V setValue(V value) {
      return this.delegate().setValue(value);
   }

   @Override
   public boolean equals(Object object) {
      return this.delegate().equals(object);
   }

   @Override
   public int hashCode() {
      return this.delegate().hashCode();
   }

   boolean standardEquals(Object object) {
      if (!(object instanceof Entry)) {
         return false;
      } else {
         Entry<?, ?> that = (Entry<?, ?>)object;
         return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
      }
   }
}
