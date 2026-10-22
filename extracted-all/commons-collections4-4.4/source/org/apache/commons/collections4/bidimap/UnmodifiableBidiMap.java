package org.apache.commons.collections4.bidimap;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableBidiMap<K, V> extends AbstractBidiMapDecorator<K, V> implements Unmodifiable {
   private UnmodifiableBidiMap<V, K> inverse;

   public static <K, V> BidiMap<K, V> unmodifiableBidiMap(BidiMap<? extends K, ? extends V> map) {
      return (BidiMap<K, V>)(map instanceof Unmodifiable ? map : new UnmodifiableBidiMap<>(map));
   }

   private UnmodifiableBidiMap(BidiMap<? extends K, ? extends V> map) {
      super((BidiMap<K, V>)map);
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      Set<Entry<K, V>> set = super.entrySet();
      return UnmodifiableEntrySet.unmodifiableEntrySet(set);
   }

   @Override
   public Set<K> keySet() {
      Set<K> set = super.keySet();
      return UnmodifiableSet.unmodifiableSet(set);
   }

   @Override
   public Set<V> values() {
      Set<V> set = super.values();
      return UnmodifiableSet.unmodifiableSet(set);
   }

   @Override
   public K removeValue(Object value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MapIterator<K, V> mapIterator() {
      MapIterator<K, V> it = this.decorated().mapIterator();
      return UnmodifiableMapIterator.unmodifiableMapIterator(it);
   }

   @Override
   public synchronized BidiMap<V, K> inverseBidiMap() {
      if (this.inverse == null) {
         this.inverse = new UnmodifiableBidiMap<>(this.decorated().inverseBidiMap());
         this.inverse.inverse = this;
      }

      return this.inverse;
   }
}
