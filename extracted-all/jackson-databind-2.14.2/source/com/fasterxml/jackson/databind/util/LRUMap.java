package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap;
import java.io.Serializable;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

public class LRUMap<K, V> implements LookupCache<K, V>, Serializable {
   private static final long serialVersionUID = 2L;
   protected final int _initialEntries;
   protected final int _maxEntries;
   protected final transient PrivateMaxEntriesMap<K, V> _map;

   public LRUMap(int initialEntries, int maxEntries) {
      this._initialEntries = initialEntries;
      this._maxEntries = maxEntries;
      this._map = new PrivateMaxEntriesMap.Builder<K, V>().initialCapacity(initialEntries).maximumCapacity((long)maxEntries).concurrencyLevel(4).build();
   }

   @Override
   public V put(K key, V value) {
      return this._map.put(key, value);
   }

   @Override
   public V putIfAbsent(K key, V value) {
      return this._map.putIfAbsent(key, value);
   }

   @Override
   public V get(Object key) {
      return this._map.get(key);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   @Override
   public int size() {
      return this._map.size();
   }

   public void contents(BiConsumer<K, V> consumer) {
      for (Entry<K, V> entry : this._map.entrySet()) {
         consumer.accept(entry.getKey(), entry.getValue());
      }
   }

   protected Object readResolve() {
      return new LRUMap(this._initialEntries, this._maxEntries);
   }
}
