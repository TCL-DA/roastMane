package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableOrderedMap<K, V> extends AbstractOrderedMapDecorator<K, V> implements Unmodifiable, Serializable {
   private static final long serialVersionUID = 8136428161720526266L;

   public static <K, V> OrderedMap<K, V> unmodifiableOrderedMap(OrderedMap<? extends K, ? extends V> map) {
      return (OrderedMap<K, V>)(map instanceof Unmodifiable ? map : new UnmodifiableOrderedMap<>(map));
   }

   private UnmodifiableOrderedMap(OrderedMap<? extends K, ? extends V> map) {
      super((OrderedMap<K, V>)map);
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
   public OrderedMapIterator<K, V> mapIterator() {
      OrderedMapIterator<K, V> it = this.decorated().mapIterator();
      return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator(it);
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
   public Collection<V> values() {
      Collection<V> coll = super.values();
      return UnmodifiableCollection.unmodifiableCollection(coll);
   }
}
