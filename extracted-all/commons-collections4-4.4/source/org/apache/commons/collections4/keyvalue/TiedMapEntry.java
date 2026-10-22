package org.apache.commons.collections4.keyvalue;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections4.KeyValue;

public class TiedMapEntry<K, V> implements Entry<K, V>, KeyValue<K, V>, Serializable {
   private static final long serialVersionUID = -8453869361373831205L;
   private final Map<K, V> map;
   private final K key;

   public TiedMapEntry(Map<K, V> map, K key) {
      this.map = map;
      this.key = key;
   }

   @Override
   public K getKey() {
      return this.key;
   }

   @Override
   public V getValue() {
      return this.map.get(this.key);
   }

   @Override
   public V setValue(V value) {
      if (value == this) {
         throw new IllegalArgumentException("Cannot set value to this map entry");
      } else {
         return this.map.put(this.key, value);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Entry)) {
         return false;
      } else {
         Entry<?, ?> other = (Entry<?, ?>)obj;
         Object value = this.getValue();
         return (this.key == null ? other.getKey() == null : this.key.equals(other.getKey()))
            && (value == null ? other.getValue() == null : value.equals(other.getValue()));
      }
   }

   @Override
   public int hashCode() {
      Object value = this.getValue();
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (value == null ? 0 : value.hashCode());
   }

   @Override
   public String toString() {
      return this.getKey() + "=" + this.getValue();
   }
}
