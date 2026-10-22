package org.glassfish.jersey.internal.guava;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

abstract class AbstractMultimap<K, V> implements Multimap<K, V> {
   private transient Collection<Entry<K, V>> entries;
   private transient Set<K> keySet;
   private transient Collection<V> values;
   private transient Map<K, Collection<V>> asMap;

   @Override
   public boolean containsValue(Object value) {
      for (Collection<V> collection : this.asMap().values()) {
         if (collection.contains(value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsEntry(Object key, Object value) {
      Collection<V> collection = this.asMap().get(key);
      return collection != null && collection.contains(value);
   }

   @Override
   public boolean remove(Object key, Object value) {
      Collection<V> collection = this.asMap().get(key);
      return collection != null && collection.remove(value);
   }

   @Override
   public boolean put(K key, V value) {
      return this.get(key).add(value);
   }

   @Override
   public boolean putAll(K key, Iterable<? extends V> values) {
      Preconditions.checkNotNull(values);
      if (values instanceof Collection) {
         Collection<? extends V> valueCollection = (Collection<? extends V>)values;
         return !valueCollection.isEmpty() && this.get(key).addAll(valueCollection);
      } else {
         Iterator<? extends V> valueItr = values.iterator();
         return valueItr.hasNext() && Iterators.addAll(this.get(key), valueItr);
      }
   }

   @Override
   public Collection<Entry<K, V>> entries() {
      Collection<Entry<K, V>> result = this.entries;
      return result == null ? (this.entries = this.createEntries()) : result;
   }

   private Collection<Entry<K, V>> createEntries() {
      return (Collection<Entry<K, V>>)(this instanceof SetMultimap ? new AbstractMultimap.EntrySet() : new AbstractMultimap.Entries());
   }

   abstract Iterator<Entry<K, V>> entryIterator();

   @Override
   public Set<K> keySet() {
      Set<K> result = this.keySet;
      return result == null ? (this.keySet = this.createKeySet()) : result;
   }

   Set<K> createKeySet() {
      return new Maps.KeySet<>(this.asMap());
   }

   @Override
   public Collection<V> values() {
      Collection<V> result = this.values;
      return result == null ? (this.values = this.createValues()) : result;
   }

   private Collection<V> createValues() {
      return new AbstractMultimap.Values();
   }

   Iterator<V> valueIterator() {
      return Maps.valueIterator(this.entries().iterator());
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      Map<K, Collection<V>> result = this.asMap;
      return result == null ? (this.asMap = this.createAsMap()) : result;
   }

   abstract Map<K, Collection<V>> createAsMap();

   @Override
   public boolean equals(Object object) {
      return Multimaps.equalsImpl(this, object);
   }

   @Override
   public int hashCode() {
      return this.asMap().hashCode();
   }

   @Override
   public String toString() {
      return this.asMap().toString();
   }

   private class Entries extends Multimaps.Entries<K, V> {
      private Entries() {
      }

      @Override
      Multimap<K, V> multimap() {
         return AbstractMultimap.this;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return AbstractMultimap.this.entryIterator();
      }
   }

   private class EntrySet extends AbstractMultimap<K, V>.Entries implements Set<Entry<K, V>> {
      private EntrySet() {
      }

      @Override
      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }

      @Override
      public boolean equals(Object obj) {
         return Sets.equalsImpl(this, obj);
      }
   }

   private class Values extends AbstractCollection<V> {
      private Values() {
      }

      @Override
      public Iterator<V> iterator() {
         return AbstractMultimap.this.valueIterator();
      }

      @Override
      public int size() {
         return AbstractMultimap.this.size();
      }

      @Override
      public boolean contains(Object o) {
         return AbstractMultimap.this.containsValue(o);
      }

      @Override
      public void clear() {
         AbstractMultimap.this.clear();
      }
   }
}
