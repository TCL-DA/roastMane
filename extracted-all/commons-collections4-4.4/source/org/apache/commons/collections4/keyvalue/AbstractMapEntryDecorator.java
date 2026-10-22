package org.apache.commons.collections4.keyvalue;

import java.util.Map.Entry;
import org.apache.commons.collections4.KeyValue;

public abstract class AbstractMapEntryDecorator<K, V> implements Entry<K, V>, KeyValue<K, V> {
   private final Entry<K, V> entry;

   public AbstractMapEntryDecorator(Entry<K, V> entry) {
      if (entry == null) {
         throw new NullPointerException("Map Entry must not be null.");
      } else {
         this.entry = entry;
      }
   }

   protected Entry<K, V> getMapEntry() {
      return this.entry;
   }

   @Override
   public K getKey() {
      return this.entry.getKey();
   }

   @Override
   public V getValue() {
      return this.entry.getValue();
   }

   @Override
   public V setValue(V object) {
      return this.entry.setValue(object);
   }

   @Override
   public boolean equals(Object object) {
      return object == this ? true : this.entry.equals(object);
   }

   @Override
   public int hashCode() {
      return this.entry.hashCode();
   }

   @Override
   public String toString() {
      return this.entry.toString();
   }
}
