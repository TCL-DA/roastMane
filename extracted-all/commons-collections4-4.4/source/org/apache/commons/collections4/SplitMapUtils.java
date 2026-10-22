package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.set.UnmodifiableSet;

public class SplitMapUtils {
   private SplitMapUtils() {
   }

   public static <K, V> IterableMap<K, V> readableMap(Get<K, V> get) {
      if (get == null) {
         throw new NullPointerException("Get must not be null");
      } else if (get instanceof Map) {
         return get instanceof IterableMap ? (IterableMap)get : MapUtils.iterableMap((Map<K, V>)get);
      } else {
         return new SplitMapUtils.WrappedGet<>(get);
      }
   }

   public static <K, V> Map<K, V> writableMap(Put<K, V> put) {
      if (put == null) {
         throw new NullPointerException("Put must not be null");
      } else {
         return (Map<K, V>)(put instanceof Map ? (Map)put : new SplitMapUtils.WrappedPut<>(put));
      }
   }

   private static class WrappedGet<K, V> implements IterableMap<K, V>, Unmodifiable {
      private final Get<K, V> get;

      private WrappedGet(Get<K, V> get) {
         this.get = get;
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(Object key) {
         return this.get.containsKey(key);
      }

      @Override
      public boolean containsValue(Object value) {
         return this.get.containsValue(value);
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return UnmodifiableEntrySet.unmodifiableEntrySet(this.get.entrySet());
      }

      @Override
      public boolean equals(Object arg0) {
         return arg0 == this ? true : arg0 instanceof SplitMapUtils.WrappedGet && ((SplitMapUtils.WrappedGet)arg0).get.equals(this.get);
      }

      @Override
      public V get(Object key) {
         return this.get.get(key);
      }

      @Override
      public int hashCode() {
         return "WrappedGet".hashCode() << 4 | this.get.hashCode();
      }

      @Override
      public boolean isEmpty() {
         return this.get.isEmpty();
      }

      @Override
      public Set<K> keySet() {
         return UnmodifiableSet.unmodifiableSet(this.get.keySet());
      }

      @Override
      public V put(K key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> t) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V remove(Object key) {
         return this.get.remove(key);
      }

      @Override
      public int size() {
         return this.get.size();
      }

      @Override
      public Collection<V> values() {
         return UnmodifiableCollection.unmodifiableCollection(this.get.values());
      }

      @Override
      public MapIterator<K, V> mapIterator() {
         MapIterator<K, V> it;
         if (this.get instanceof IterableGet) {
            it = ((IterableGet)this.get).mapIterator();
         } else {
            it = new EntrySetToMapIteratorAdapter<>(this.get.entrySet());
         }

         return UnmodifiableMapIterator.unmodifiableMapIterator(it);
      }
   }

   private static class WrappedPut<K, V> implements Map<K, V>, Put<K, V> {
      private final Put<K, V> put;

      private WrappedPut(Put<K, V> put) {
         this.put = put;
      }

      @Override
      public void clear() {
         this.put.clear();
      }

      @Override
      public boolean containsKey(Object key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsValue(Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this ? true : obj instanceof SplitMapUtils.WrappedPut && ((SplitMapUtils.WrappedPut)obj).put.equals(this.put);
      }

      @Override
      public V get(Object key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int hashCode() {
         return "WrappedPut".hashCode() << 4 | this.put.hashCode();
      }

      @Override
      public boolean isEmpty() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Set<K> keySet() {
         throw new UnsupportedOperationException();
      }

      @Override
      public V put(K key, V value) {
         return (V)this.put.put(key, value);
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> t) {
         this.put.putAll(t);
      }

      @Override
      public V remove(Object key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<V> values() {
         throw new UnsupportedOperationException();
      }
   }
}
