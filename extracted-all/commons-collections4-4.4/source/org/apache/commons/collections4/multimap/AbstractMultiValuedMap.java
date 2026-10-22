package org.apache.commons.collections4.multimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.keyvalue.UnmodifiableMapEntry;
import org.apache.commons.collections4.multiset.AbstractMultiSet;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;

public abstract class AbstractMultiValuedMap<K, V> implements MultiValuedMap<K, V> {
   private transient Collection<V> valuesView;
   private transient AbstractMultiValuedMap<K, V>.EntryValues entryValuesView;
   private transient MultiSet<K> keysMultiSetView;
   private transient AbstractMultiValuedMap<K, V>.AsMap asMapView;
   private transient Map<K, Collection<V>> map;

   protected AbstractMultiValuedMap() {
   }

   protected AbstractMultiValuedMap(Map<K, ? extends Collection<V>> map) {
      if (map == null) {
         throw new NullPointerException("Map must not be null.");
      } else {
         this.map = (Map<K, Collection<V>>)map;
      }
   }

   protected Map<K, ? extends Collection<V>> getMap() {
      return this.map;
   }

   protected void setMap(Map<K, ? extends Collection<V>> map) {
      this.map = (Map<K, Collection<V>>)map;
   }

   protected abstract Collection<V> createCollection();

   @Override
   public boolean containsKey(Object key) {
      return this.getMap().containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.values().contains(value);
   }

   @Override
   public boolean containsMapping(Object key, Object value) {
      Collection<V> coll = (Collection<V>)this.getMap().get(key);
      return coll != null && coll.contains(value);
   }

   @Override
   public Collection<Entry<K, V>> entries() {
      return this.entryValuesView != null ? this.entryValuesView : (this.entryValuesView = new AbstractMultiValuedMap.EntryValues());
   }

   @Override
   public Collection<V> get(K key) {
      return this.wrappedCollection(key);
   }

   Collection<V> wrappedCollection(K key) {
      return new AbstractMultiValuedMap.WrappedCollection(key);
   }

   @Override
   public Collection<V> remove(Object key) {
      return CollectionUtils.emptyIfNull((Collection<V>)this.getMap().remove(key));
   }

   @Override
   public boolean removeMapping(Object key, Object value) {
      Collection<V> coll = (Collection<V>)this.getMap().get(key);
      if (coll == null) {
         return false;
      } else {
         boolean changed = coll.remove(value);
         if (coll.isEmpty()) {
            this.getMap().remove(key);
         }

         return changed;
      }
   }

   @Override
   public boolean isEmpty() {
      return this.getMap().isEmpty();
   }

   @Override
   public Set<K> keySet() {
      return this.getMap().keySet();
   }

   @Override
   public int size() {
      int size = 0;

      for (Collection<V> col : this.getMap().values()) {
         size += col.size();
      }

      return size;
   }

   @Override
   public Collection<V> values() {
      Collection<V> vs = this.valuesView;
      return vs != null ? vs : (this.valuesView = new AbstractMultiValuedMap.Values());
   }

   @Override
   public void clear() {
      this.getMap().clear();
   }

   @Override
   public boolean put(K key, V value) {
      Collection<V> coll = (Collection<V>)this.getMap().get(key);
      if (coll == null) {
         coll = this.createCollection();
         if (coll.add(value)) {
            this.map.put(key, coll);
            return true;
         } else {
            return false;
         }
      } else {
         return coll.add(value);
      }
   }

   @Override
   public boolean putAll(Map<? extends K, ? extends V> map) {
      if (map == null) {
         throw new NullPointerException("Map must not be null.");
      } else {
         boolean changed = false;

         for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            changed |= this.put((K)entry.getKey(), (V)entry.getValue());
         }

         return changed;
      }
   }

   @Override
   public boolean putAll(MultiValuedMap<? extends K, ? extends V> map) {
      if (map == null) {
         throw new NullPointerException("Map must not be null.");
      } else {
         boolean changed = false;

         for (Entry<? extends K, ? extends V> entry : map.entries()) {
            changed |= this.put((K)entry.getKey(), (V)entry.getValue());
         }

         return changed;
      }
   }

   @Override
   public MultiSet<K> keys() {
      if (this.keysMultiSetView == null) {
         this.keysMultiSetView = UnmodifiableMultiSet.unmodifiableMultiSet(new AbstractMultiValuedMap.KeysMultiSet());
      }

      return this.keysMultiSetView;
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      return this.asMapView != null ? this.asMapView : (this.asMapView = new AbstractMultiValuedMap.AsMap(this.map));
   }

   @Override
   public boolean putAll(K key, Iterable<? extends V> values) {
      if (values == null) {
         throw new NullPointerException("Values must not be null.");
      } else if (values instanceof Collection) {
         Collection<? extends V> valueCollection = (Collection<? extends V>)values;
         return !valueCollection.isEmpty() && this.get(key).addAll(valueCollection);
      } else {
         Iterator<? extends V> it = values.iterator();
         return it.hasNext() && CollectionUtils.addAll(this.get(key), it);
      }
   }

   @Override
   public MapIterator<K, V> mapIterator() {
      return (MapIterator<K, V>)(this.size() == 0 ? EmptyMapIterator.emptyMapIterator() : new AbstractMultiValuedMap.MultiValuedMapIterator());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof MultiValuedMap ? this.asMap().equals(((MultiValuedMap)obj).asMap()) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.getMap().hashCode();
   }

   @Override
   public String toString() {
      return this.getMap().toString();
   }

   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.map.size());

      for (Entry<K, Collection<V>> entry : this.map.entrySet()) {
         out.writeObject(entry.getKey());
         out.writeInt(entry.getValue().size());

         for (V value : entry.getValue()) {
            out.writeObject(value);
         }
      }
   }

   protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      int entrySize = in.readInt();

      for (int i = 0; i < entrySize; i++) {
         K key = (K)in.readObject();
         Collection<V> values = this.get(key);
         int valueSize = in.readInt();

         for (int j = 0; j < valueSize; j++) {
            V value = (V)in.readObject();
            values.add(value);
         }
      }
   }

   private class AsMap extends AbstractMap<K, Collection<V>> {
      final transient Map<K, Collection<V>> decoratedMap;

      AsMap(Map<K, Collection<V>> map) {
         this.decoratedMap = map;
      }

      @Override
      public Set<Entry<K, Collection<V>>> entrySet() {
         return new AbstractMultiValuedMap.AsMap.AsMapEntrySet();
      }

      @Override
      public boolean containsKey(Object key) {
         return this.decoratedMap.containsKey(key);
      }

      public Collection<V> get(Object key) {
         Collection<V> collection = this.decoratedMap.get(key);
         return collection == null ? null : AbstractMultiValuedMap.this.wrappedCollection((K)key);
      }

      @Override
      public Set<K> keySet() {
         return AbstractMultiValuedMap.this.keySet();
      }

      @Override
      public int size() {
         return this.decoratedMap.size();
      }

      public Collection<V> remove(Object key) {
         Collection<V> collection = this.decoratedMap.remove(key);
         if (collection == null) {
            return null;
         } else {
            Collection<V> output = AbstractMultiValuedMap.this.createCollection();
            output.addAll(collection);
            collection.clear();
            return output;
         }
      }

      @Override
      public boolean equals(Object object) {
         return this == object || this.decoratedMap.equals(object);
      }

      @Override
      public int hashCode() {
         return this.decoratedMap.hashCode();
      }

      @Override
      public String toString() {
         return this.decoratedMap.toString();
      }

      @Override
      public void clear() {
         AbstractMultiValuedMap.this.clear();
      }

      class AsMapEntrySet extends AbstractSet<Entry<K, Collection<V>>> {
         @Override
         public Iterator<Entry<K, Collection<V>>> iterator() {
            return AsMap.this.new AsMapEntrySetIterator(AsMap.this.decoratedMap.entrySet().iterator());
         }

         @Override
         public int size() {
            return AsMap.this.size();
         }

         @Override
         public void clear() {
            AsMap.this.clear();
         }

         @Override
         public boolean contains(Object o) {
            return AsMap.this.decoratedMap.entrySet().contains(o);
         }

         @Override
         public boolean remove(Object o) {
            if (!this.contains(o)) {
               return false;
            } else {
               Entry<?, ?> entry = (Entry<?, ?>)o;
               AbstractMultiValuedMap.this.remove(entry.getKey());
               return true;
            }
         }
      }

      class AsMapEntrySetIterator extends AbstractIteratorDecorator<Entry<K, Collection<V>>> {
         AsMapEntrySetIterator(Iterator<Entry<K, Collection<V>>> iterator) {
            super(iterator);
         }

         public Entry<K, Collection<V>> next() {
            Entry<K, Collection<V>> entry = (Entry<K, Collection<V>>)super.next();
            K key = entry.getKey();
            return new UnmodifiableMapEntry<>(key, AbstractMultiValuedMap.this.wrappedCollection(key));
         }
      }
   }

   private class EntryValues extends AbstractCollection<Entry<K, V>> {
      private EntryValues() {
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return new LazyIteratorChain<Entry<K, V>>() {
            final Collection<K> keysCol = new ArrayList<>(AbstractMultiValuedMap.this.getMap().keySet());
            final Iterator<K> keyIterator = AbstractMultiValuedMap.EntryValues.super.keysCol.iterator();

            @Override
            protected Iterator<? extends Entry<K, V>> nextIterator(int count) {
               if (!this.keyIterator.hasNext()) {
                  return null;
               } else {
                  final K key = this.keyIterator.next();
                  Transformer<V, Entry<K, V>> entryTransformer = new Transformer<V, Entry<K, V>>() {
                     public Entry<K, V> transform(V input) {
                        return AbstractMultiValuedMap.this.new MultiValuedMapEntry(key, input);
                     }
                  };
                  return new TransformIterator<>(AbstractMultiValuedMap.this.new ValuesIterator(key), entryTransformer);
               }
            }
         };
      }

      @Override
      public int size() {
         return AbstractMultiValuedMap.this.size();
      }
   }

   private class KeysMultiSet extends AbstractMultiSet<K> {
      private KeysMultiSet() {
      }

      @Override
      public boolean contains(Object o) {
         return AbstractMultiValuedMap.this.getMap().containsKey(o);
      }

      @Override
      public boolean isEmpty() {
         return AbstractMultiValuedMap.this.getMap().isEmpty();
      }

      @Override
      public int size() {
         return AbstractMultiValuedMap.this.size();
      }

      @Override
      protected int uniqueElements() {
         return AbstractMultiValuedMap.this.getMap().size();
      }

      @Override
      public int getCount(Object object) {
         int count = 0;
         Collection<V> col = (Collection<V>)AbstractMultiValuedMap.this.getMap().get(object);
         if (col != null) {
            count = col.size();
         }

         return count;
      }

      @Override
      protected Iterator<MultiSet.Entry<K>> createEntrySetIterator() {
         AbstractMultiValuedMap<K, V>.KeysMultiSet.MapEntryTransformer transformer = new AbstractMultiValuedMap.KeysMultiSet.MapEntryTransformer();
         return IteratorUtils.transformedIterator(AbstractMultiValuedMap.this.map.entrySet().iterator(), transformer);
      }

      private final class MapEntryTransformer implements Transformer<Map.Entry<K, Collection<V>>, MultiSet.Entry<K>> {
         private MapEntryTransformer() {
         }

         public MultiSet.Entry<K> transform(final Map.Entry<K, Collection<V>> mapEntry) {
            return new AbstractMultiSet.AbstractEntry<K>() {
               @Override
               public K getElement() {
                  return mapEntry.getKey();
               }

               @Override
               public int getCount() {
                  return mapEntry.getValue().size();
               }
            };
         }
      }
   }

   private class MultiValuedMapEntry extends AbstractMapEntry<K, V> {
      public MultiValuedMapEntry(K key, V value) {
         super(key, value);
      }

      @Override
      public V setValue(V value) {
         throw new UnsupportedOperationException();
      }
   }

   private class MultiValuedMapIterator implements MapIterator<K, V> {
      private final Iterator<Entry<K, V>> it;
      private Entry<K, V> current = null;

      public MultiValuedMapIterator() {
         this.it = AbstractMultiValuedMap.this.entries().iterator();
      }

      @Override
      public boolean hasNext() {
         return this.it.hasNext();
      }

      @Override
      public K next() {
         this.current = this.it.next();
         return this.current.getKey();
      }

      @Override
      public K getKey() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return this.current.getKey();
         }
      }

      @Override
      public V getValue() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return this.current.getValue();
         }
      }

      @Override
      public void remove() {
         this.it.remove();
      }

      @Override
      public V setValue(V value) {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return this.current.setValue(value);
         }
      }
   }

   private class Values extends AbstractCollection<V> {
      private Values() {
      }

      @Override
      public Iterator<V> iterator() {
         IteratorChain<V> chain = new IteratorChain<>();

         for (K k : AbstractMultiValuedMap.this.keySet()) {
            chain.addIterator(AbstractMultiValuedMap.this.new ValuesIterator(k));
         }

         return chain;
      }

      @Override
      public int size() {
         return AbstractMultiValuedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractMultiValuedMap.this.clear();
      }
   }

   private class ValuesIterator implements Iterator<V> {
      private final Object key;
      private final Collection<V> values;
      private final Iterator<V> iterator;

      public ValuesIterator(Object key) {
         this.key = key;
         this.values = (Collection<V>)AbstractMultiValuedMap.this.getMap().get(key);
         this.iterator = this.values.iterator();
      }

      @Override
      public void remove() {
         this.iterator.remove();
         if (this.values.isEmpty()) {
            AbstractMultiValuedMap.this.remove(this.key);
         }
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Override
      public V next() {
         return this.iterator.next();
      }
   }

   class WrappedCollection implements Collection<V> {
      protected final K key;

      public WrappedCollection(K key) {
         this.key = key;
      }

      protected Collection<V> getMapping() {
         return (Collection<V>)AbstractMultiValuedMap.this.getMap().get(this.key);
      }

      @Override
      public boolean add(V value) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            coll = AbstractMultiValuedMap.this.createCollection();
            AbstractMultiValuedMap.this.map.put(this.key, coll);
         }

         return coll.add(value);
      }

      @Override
      public boolean addAll(Collection<? extends V> other) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            coll = AbstractMultiValuedMap.this.createCollection();
            AbstractMultiValuedMap.this.map.put(this.key, coll);
         }

         return coll.addAll(other);
      }

      @Override
      public void clear() {
         Collection<V> coll = this.getMapping();
         if (coll != null) {
            coll.clear();
            AbstractMultiValuedMap.this.remove(this.key);
         }
      }

      @Override
      public Iterator<V> iterator() {
         Collection<V> coll = this.getMapping();
         return (Iterator<V>)(coll == null ? IteratorUtils.EMPTY_ITERATOR : AbstractMultiValuedMap.this.new ValuesIterator(this.key));
      }

      @Override
      public int size() {
         Collection<V> coll = this.getMapping();
         return coll == null ? 0 : coll.size();
      }

      @Override
      public boolean contains(Object obj) {
         Collection<V> coll = this.getMapping();
         return coll != null && coll.contains(obj);
      }

      @Override
      public boolean containsAll(Collection<?> other) {
         Collection<V> coll = this.getMapping();
         return coll != null && coll.containsAll(other);
      }

      @Override
      public boolean isEmpty() {
         Collection<V> coll = this.getMapping();
         return coll == null || coll.isEmpty();
      }

      @Override
      public boolean remove(Object item) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            return false;
         } else {
            boolean result = coll.remove(item);
            if (coll.isEmpty()) {
               AbstractMultiValuedMap.this.remove(this.key);
            }

            return result;
         }
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            return false;
         } else {
            boolean result = coll.removeAll(c);
            if (coll.isEmpty()) {
               AbstractMultiValuedMap.this.remove(this.key);
            }

            return result;
         }
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            return false;
         } else {
            boolean result = coll.retainAll(c);
            if (coll.isEmpty()) {
               AbstractMultiValuedMap.this.remove(this.key);
            }

            return result;
         }
      }

      @Override
      public Object[] toArray() {
         Collection<V> coll = this.getMapping();
         return coll == null ? CollectionUtils.EMPTY_COLLECTION.toArray() : coll.toArray();
      }

      @Override
      public <T> T[] toArray(T[] a) {
         Collection<V> coll = this.getMapping();
         return (T[])(coll == null ? CollectionUtils.EMPTY_COLLECTION.toArray(a) : coll.toArray(a));
      }

      @Override
      public String toString() {
         Collection<V> coll = this.getMapping();
         return coll == null ? CollectionUtils.EMPTY_COLLECTION.toString() : coll.toString();
      }
   }
}
