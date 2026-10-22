package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.EntrySetMapIterator;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableMap<K, V> extends AbstractMapDecorator<K, V> implements Unmodifiable, Serializable {
   private static final long serialVersionUID = 2737023427269031941L;

   public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> map) {
      return (Map<K, V>)(map instanceof Unmodifiable ? map : new UnmodifiableMap<>(map));
   }

   private UnmodifiableMap(Map<? extends K, ? extends V> map) {
      super((Map<K, V>)map);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.map = (Map<K, V>)in.readObject();
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
   public MapIterator<K, V> mapIterator() {
      if (this.map instanceof IterableMap) {
         MapIterator<K, V> it = ((IterableMap)this.map).mapIterator();
         return UnmodifiableMapIterator.unmodifiableMapIterator(it);
      } else {
         MapIterator<K, V> it = new EntrySetMapIterator<>(this.map);
         return UnmodifiableMapIterator.unmodifiableMapIterator(it);
      }
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
   public Collection<V> values() {
      Collection<V> coll = super.values();
      return UnmodifiableCollection.unmodifiableCollection(coll);
   }
}
