package org.apache.commons.collections4.splitmap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.IterableGet;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;

public class AbstractIterableGetMapDecorator<K, V> implements IterableGet<K, V> {
   transient Map<K, V> map;

   public AbstractIterableGetMapDecorator(Map<K, V> map) {
      if (map == null) {
         throw new NullPointerException("Map must not be null.");
      } else {
         this.map = map;
      }
   }

   protected AbstractIterableGetMapDecorator() {
   }

   protected Map<K, V> decorated() {
      return this.map;
   }

   @Override
   public boolean containsKey(Object key) {
      return this.decorated().containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.decorated().containsValue(value);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.decorated().entrySet();
   }

   @Override
   public V get(Object key) {
      return this.decorated().get(key);
   }

   @Override
   public V remove(Object key) {
      return this.decorated().remove(key);
   }

   @Override
   public boolean isEmpty() {
      return this.decorated().isEmpty();
   }

   @Override
   public Set<K> keySet() {
      return this.decorated().keySet();
   }

   @Override
   public int size() {
      return this.decorated().size();
   }

   @Override
   public Collection<V> values() {
      return this.decorated().values();
   }

   @Override
   public MapIterator<K, V> mapIterator() {
      return new EntrySetToMapIteratorAdapter<>(this.entrySet());
   }

   @Override
   public boolean equals(Object object) {
      return object == this ? true : this.decorated().equals(object);
   }

   @Override
   public int hashCode() {
      return this.decorated().hashCode();
   }

   @Override
   public String toString() {
      return this.decorated().toString();
   }
}
