package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.list.UnmodifiableList;

public class ListOrderedMap<K, V> extends AbstractMapDecorator<K, V> implements OrderedMap<K, V>, Serializable {
   private static final long serialVersionUID = 2728177751851003750L;
   private final List<K> insertOrder = new ArrayList<>();

   public static <K, V> ListOrderedMap<K, V> listOrderedMap(Map<K, V> map) {
      return new ListOrderedMap<>(map);
   }

   public ListOrderedMap() {
      this(new HashMap<>());
   }

   protected ListOrderedMap(Map<K, V> map) {
      super(map);
      this.insertOrder.addAll(this.decorated().keySet());
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
      return new ListOrderedMap.ListOrderedMapIterator<>(this);
   }

   @Override
   public K firstKey() {
      if (this.size() == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return this.insertOrder.get(0);
      }
   }

   @Override
   public K lastKey() {
      if (this.size() == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return this.insertOrder.get(this.size() - 1);
      }
   }

   @Override
   public K nextKey(Object key) {
      int index = this.insertOrder.indexOf(key);
      return index >= 0 && index < this.size() - 1 ? this.insertOrder.get(index + 1) : null;
   }

   @Override
   public K previousKey(Object key) {
      int index = this.insertOrder.indexOf(key);
      return index > 0 ? this.insertOrder.get(index - 1) : null;
   }

   @Override
   public V put(K key, V value) {
      if (this.decorated().containsKey(key)) {
         return this.decorated().put(key, value);
      } else {
         V result = this.decorated().put(key, value);
         this.insertOrder.add(key);
         return result;
      }
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
         this.put((K)entry.getKey(), (V)entry.getValue());
      }
   }

   public void putAll(int index, Map<? extends K, ? extends V> map) {
      if (index >= 0 && index <= this.insertOrder.size()) {
         for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            K key = (K)entry.getKey();
            boolean contains = this.containsKey(key);
            this.put(index, (K)entry.getKey(), (V)entry.getValue());
            if (!contains) {
               index++;
            } else {
               index = this.indexOf(entry.getKey()) + 1;
            }
         }
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.insertOrder.size());
      }
   }

   @Override
   public V remove(Object key) {
      V result = null;
      if (this.decorated().containsKey(key)) {
         result = this.decorated().remove(key);
         this.insertOrder.remove(key);
      }

      return result;
   }

   @Override
   public void clear() {
      this.decorated().clear();
      this.insertOrder.clear();
   }

   @Override
   public Set<K> keySet() {
      return new ListOrderedMap.KeySetView<>(this);
   }

   public List<K> keyList() {
      return UnmodifiableList.unmodifiableList(this.insertOrder);
   }

   @Override
   public Collection<V> values() {
      return new ListOrderedMap.ValuesView<>(this);
   }

   public List<V> valueList() {
      return new ListOrderedMap.ValuesView<>(this);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return new ListOrderedMap.EntrySetView<>(this, this.insertOrder);
   }

   @Override
   public String toString() {
      if (this.isEmpty()) {
         return "{}";
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append('{');
         boolean first = true;

         for (Entry<K, V> entry : this.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if (first) {
               first = false;
            } else {
               buf.append(", ");
            }

            buf.append(key == this ? "(this Map)" : key);
            buf.append('=');
            buf.append(value == this ? "(this Map)" : value);
         }

         buf.append('}');
         return buf.toString();
      }
   }

   public K get(int index) {
      return this.insertOrder.get(index);
   }

   public V getValue(int index) {
      return this.get(this.insertOrder.get(index));
   }

   public int indexOf(Object key) {
      return this.insertOrder.indexOf(key);
   }

   public V setValue(int index, V value) {
      K key = this.insertOrder.get(index);
      return this.put(key, value);
   }

   public V put(int index, K key, V value) {
      if (index >= 0 && index <= this.insertOrder.size()) {
         Map<K, V> m = this.decorated();
         if (m.containsKey(key)) {
            V result = m.remove(key);
            int pos = this.insertOrder.indexOf(key);
            this.insertOrder.remove(pos);
            if (pos < index) {
               index--;
            }

            this.insertOrder.add(index, key);
            m.put(key, value);
            return result;
         } else {
            this.insertOrder.add(index, key);
            m.put(key, value);
            return null;
         }
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.insertOrder.size());
      }
   }

   public V remove(int index) {
      return this.remove(this.get(index));
   }

   public List<K> asList() {
      return this.keyList();
   }

   static class EntrySetView<K, V> extends AbstractSet<Entry<K, V>> {
      private final ListOrderedMap<K, V> parent;
      private final List<K> insertOrder;
      private Set<Entry<K, V>> entrySet;

      public EntrySetView(ListOrderedMap<K, V> parent, List<K> insertOrder) {
         this.parent = parent;
         this.insertOrder = insertOrder;
      }

      private Set<Entry<K, V>> getEntrySet() {
         if (this.entrySet == null) {
            this.entrySet = this.parent.decorated().entrySet();
         }

         return this.entrySet;
      }

      @Override
      public int size() {
         return this.parent.size();
      }

      @Override
      public boolean isEmpty() {
         return this.parent.isEmpty();
      }

      @Override
      public boolean contains(Object obj) {
         return this.getEntrySet().contains(obj);
      }

      @Override
      public boolean containsAll(Collection<?> coll) {
         return this.getEntrySet().containsAll(coll);
      }

      @Override
      public boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         } else if (this.getEntrySet().contains(obj)) {
            Object key = ((Entry)obj).getKey();
            this.parent.remove(key);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void clear() {
         this.parent.clear();
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this ? true : this.getEntrySet().equals(obj);
      }

      @Override
      public int hashCode() {
         return this.getEntrySet().hashCode();
      }

      @Override
      public String toString() {
         return this.getEntrySet().toString();
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return new ListOrderedMap.ListOrderedIterator<>(this.parent, this.insertOrder);
      }
   }

   static class KeySetView<K> extends AbstractSet<K> {
      private final ListOrderedMap<K, Object> parent;

      KeySetView(ListOrderedMap<K, ?> parent) {
         this.parent = (ListOrderedMap<K, Object>)parent;
      }

      @Override
      public int size() {
         return this.parent.size();
      }

      @Override
      public boolean contains(Object value) {
         return this.parent.containsKey(value);
      }

      @Override
      public void clear() {
         this.parent.clear();
      }

      @Override
      public Iterator<K> iterator() {
         return new AbstractUntypedIteratorDecorator<Entry<K, Object>, K>(this.parent.entrySet().iterator()) {
            @Override
            public K next() {
               return this.getIterator().next().getKey();
            }
         };
      }
   }

   static class ListOrderedIterator<K, V> extends AbstractUntypedIteratorDecorator<K, Entry<K, V>> {
      private final ListOrderedMap<K, V> parent;
      private K last = (K)null;

      ListOrderedIterator(ListOrderedMap<K, V> parent, List<K> insertOrder) {
         super(insertOrder.iterator());
         this.parent = parent;
      }

      public Entry<K, V> next() {
         this.last = this.getIterator().next();
         return new ListOrderedMap.ListOrderedMapEntry<>(this.parent, this.last);
      }

      @Override
      public void remove() {
         super.remove();
         this.parent.decorated().remove(this.last);
      }
   }

   static class ListOrderedMapEntry<K, V> extends AbstractMapEntry<K, V> {
      private final ListOrderedMap<K, V> parent;

      ListOrderedMapEntry(ListOrderedMap<K, V> parent, K key) {
         super(key, null);
         this.parent = parent;
      }

      @Override
      public V getValue() {
         return this.parent.get(this.getKey());
      }

      @Override
      public V setValue(V value) {
         return this.parent.decorated().put(this.getKey(), value);
      }
   }

   static class ListOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
      private final ListOrderedMap<K, V> parent;
      private ListIterator<K> iterator;
      private K last = (K)null;
      private boolean readable = false;

      ListOrderedMapIterator(ListOrderedMap<K, V> parent) {
         this.parent = parent;
         this.iterator = parent.insertOrder.listIterator();
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Override
      public K next() {
         this.last = this.iterator.next();
         this.readable = true;
         return this.last;
      }

      @Override
      public boolean hasPrevious() {
         return this.iterator.hasPrevious();
      }

      @Override
      public K previous() {
         this.last = this.iterator.previous();
         this.readable = true;
         return this.last;
      }

      @Override
      public void remove() {
         if (!this.readable) {
            throw new IllegalStateException("remove() can only be called once after next()");
         } else {
            this.iterator.remove();
            this.parent.map.remove(this.last);
            this.readable = false;
         }
      }

      @Override
      public K getKey() {
         if (!this.readable) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return this.last;
         }
      }

      @Override
      public V getValue() {
         if (!this.readable) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return this.parent.get(this.last);
         }
      }

      @Override
      public V setValue(V value) {
         if (!this.readable) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return this.parent.map.put(this.last, value);
         }
      }

      @Override
      public void reset() {
         this.iterator = this.parent.insertOrder.listIterator();
         this.last = null;
         this.readable = false;
      }

      @Override
      public String toString() {
         return this.readable ? "Iterator[" + this.getKey() + "=" + this.getValue() + "]" : "Iterator[]";
      }
   }

   static class ValuesView<V> extends AbstractList<V> {
      private final ListOrderedMap<Object, V> parent;

      ValuesView(ListOrderedMap<?, V> parent) {
         this.parent = (ListOrderedMap<Object, V>)parent;
      }

      @Override
      public int size() {
         return this.parent.size();
      }

      @Override
      public boolean contains(Object value) {
         return this.parent.containsValue(value);
      }

      @Override
      public void clear() {
         this.parent.clear();
      }

      @Override
      public Iterator<V> iterator() {
         return new AbstractUntypedIteratorDecorator<Entry<Object, V>, V>(this.parent.entrySet().iterator()) {
            @Override
            public V next() {
               return this.getIterator().next().getValue();
            }
         };
      }

      @Override
      public V get(int index) {
         return this.parent.getValue(index);
      }

      @Override
      public V set(int index, V value) {
         return this.parent.setValue(index, value);
      }

      @Override
      public V remove(int index) {
         return this.parent.remove(index);
      }
   }
}
