package org.apache.commons.collections4.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;

public abstract class AbstractLinkedMap<K, V> extends AbstractHashedMap<K, V> implements OrderedMap<K, V> {
   transient AbstractLinkedMap.LinkEntry<K, V> header;

   protected AbstractLinkedMap() {
   }

   protected AbstractLinkedMap(int initialCapacity, float loadFactor, int threshold) {
      super(initialCapacity, loadFactor, threshold);
   }

   protected AbstractLinkedMap(int initialCapacity) {
      super(initialCapacity);
   }

   protected AbstractLinkedMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   protected AbstractLinkedMap(Map<? extends K, ? extends V> map) {
      super(map);
   }

   @Override
   protected void init() {
      this.header = this.createEntry(null, -1, null, null);
      this.header.before = this.header.after = this.header;
   }

   @Override
   public boolean containsValue(Object value) {
      if (value == null) {
         for (AbstractLinkedMap.LinkEntry<K, V> entry = this.header.after; entry != this.header; entry = entry.after) {
            if (entry.getValue() == null) {
               return true;
            }
         }
      } else {
         for (AbstractLinkedMap.LinkEntry<K, V> entryx = this.header.after; entryx != this.header; entryx = entryx.after) {
            if (this.isEqualValue(value, entryx.getValue())) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void clear() {
      super.clear();
      this.header.before = this.header.after = this.header;
   }

   @Override
   public K firstKey() {
      if (this.size == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return this.header.after.getKey();
      }
   }

   @Override
   public K lastKey() {
      if (this.size == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return this.header.before.getKey();
      }
   }

   @Override
   public K nextKey(Object key) {
      AbstractLinkedMap.LinkEntry<K, V> entry = this.getEntry(key);
      return entry != null && entry.after != this.header ? entry.after.getKey() : null;
   }

   protected AbstractLinkedMap.LinkEntry<K, V> getEntry(Object key) {
      return (AbstractLinkedMap.LinkEntry<K, V>)super.getEntry(key);
   }

   @Override
   public K previousKey(Object key) {
      AbstractLinkedMap.LinkEntry<K, V> entry = this.getEntry(key);
      return entry != null && entry.before != this.header ? entry.before.getKey() : null;
   }

   protected AbstractLinkedMap.LinkEntry<K, V> getEntry(int index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index " + index + " is less than zero");
      } else if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index " + index + " is invalid for size " + this.size);
      } else {
         AbstractLinkedMap.LinkEntry<K, V> entry;
         if (index < this.size / 2) {
            entry = this.header.after;

            for (int currentIndex = 0; currentIndex < index; currentIndex++) {
               entry = entry.after;
            }
         } else {
            entry = this.header;

            for (int currentIndex = this.size; currentIndex > index; currentIndex--) {
               entry = entry.before;
            }
         }

         return entry;
      }
   }

   @Override
   protected void addEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex) {
      AbstractLinkedMap.LinkEntry<K, V> link = (AbstractLinkedMap.LinkEntry<K, V>)entry;
      link.after = this.header;
      link.before = this.header.before;
      this.header.before.after = link;
      this.header.before = link;
      this.data[hashIndex] = link;
   }

   protected AbstractLinkedMap.LinkEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
      return new AbstractLinkedMap.LinkEntry<>(next, hashCode, this.convertKey(key), value);
   }

   @Override
   protected void removeEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex, AbstractHashedMap.HashEntry<K, V> previous) {
      AbstractLinkedMap.LinkEntry<K, V> link = (AbstractLinkedMap.LinkEntry<K, V>)entry;
      link.before.after = link.after;
      link.after.before = link.before;
      link.after = null;
      link.before = null;
      super.removeEntry(entry, hashIndex, previous);
   }

   protected AbstractLinkedMap.LinkEntry<K, V> entryBefore(AbstractLinkedMap.LinkEntry<K, V> entry) {
      return entry.before;
   }

   protected AbstractLinkedMap.LinkEntry<K, V> entryAfter(AbstractLinkedMap.LinkEntry<K, V> entry) {
      return entry.after;
   }

   @Override
   public OrderedMapIterator<K, V> mapIterator() {
      return (OrderedMapIterator<K, V>)(this.size == 0 ? EmptyOrderedMapIterator.emptyOrderedMapIterator() : new AbstractLinkedMap.LinkMapIterator<>(this));
   }

   @Override
   protected Iterator<Entry<K, V>> createEntrySetIterator() {
      return (Iterator<Entry<K, V>>)(this.size() == 0 ? EmptyOrderedIterator.emptyOrderedIterator() : new AbstractLinkedMap.EntrySetIterator<>(this));
   }

   @Override
   protected Iterator<K> createKeySetIterator() {
      return (Iterator<K>)(this.size() == 0 ? EmptyOrderedIterator.emptyOrderedIterator() : new AbstractLinkedMap.KeySetIterator<>(this));
   }

   @Override
   protected Iterator<V> createValuesIterator() {
      return (Iterator<V>)(this.size() == 0 ? EmptyOrderedIterator.emptyOrderedIterator() : new AbstractLinkedMap.ValuesIterator<>(this));
   }

   protected static class EntrySetIterator<K, V>
      extends AbstractLinkedMap.LinkIterator<K, V>
      implements OrderedIterator<Entry<K, V>>,
      ResettableIterator<Entry<K, V>> {
      protected EntrySetIterator(AbstractLinkedMap<K, V> parent) {
         super(parent);
      }

      public Entry<K, V> next() {
         return super.nextEntry();
      }

      public Entry<K, V> previous() {
         return super.previousEntry();
      }
   }

   protected static class KeySetIterator<K> extends AbstractLinkedMap.LinkIterator<K, Object> implements OrderedIterator<K>, ResettableIterator<K> {
      protected KeySetIterator(AbstractLinkedMap<K, ?> parent) {
         super((AbstractLinkedMap<K, Object>)parent);
      }

      @Override
      public K next() {
         return super.nextEntry().getKey();
      }

      @Override
      public K previous() {
         return super.previousEntry().getKey();
      }
   }

   protected static class LinkEntry<K, V> extends AbstractHashedMap.HashEntry<K, V> {
      protected AbstractLinkedMap.LinkEntry<K, V> before;
      protected AbstractLinkedMap.LinkEntry<K, V> after;

      protected LinkEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, Object key, V value) {
         super(next, hashCode, key, value);
      }
   }

   protected abstract static class LinkIterator<K, V> {
      protected final AbstractLinkedMap<K, V> parent;
      protected AbstractLinkedMap.LinkEntry<K, V> last;
      protected AbstractLinkedMap.LinkEntry<K, V> next;
      protected int expectedModCount;

      protected LinkIterator(AbstractLinkedMap<K, V> parent) {
         this.parent = parent;
         this.next = parent.header.after;
         this.expectedModCount = parent.modCount;
      }

      public boolean hasNext() {
         return this.next != this.parent.header;
      }

      public boolean hasPrevious() {
         return this.next.before != this.parent.header;
      }

      protected AbstractLinkedMap.LinkEntry<K, V> nextEntry() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else if (this.next == this.parent.header) {
            throw new NoSuchElementException("No next() entry in the iteration");
         } else {
            this.last = this.next;
            this.next = this.next.after;
            return this.last;
         }
      }

      protected AbstractLinkedMap.LinkEntry<K, V> previousEntry() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            AbstractLinkedMap.LinkEntry<K, V> previous = this.next.before;
            if (previous == this.parent.header) {
               throw new NoSuchElementException("No previous() entry in the iteration");
            } else {
               this.next = previous;
               this.last = previous;
               return this.last;
            }
         }
      }

      protected AbstractLinkedMap.LinkEntry<K, V> currentEntry() {
         return this.last;
      }

      public void remove() {
         if (this.last == null) {
            throw new IllegalStateException("remove() can only be called once after next()");
         } else if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.parent.remove(this.last.getKey());
            this.last = null;
            this.expectedModCount = this.parent.modCount;
         }
      }

      public void reset() {
         this.last = null;
         this.next = this.parent.header.after;
      }

      @Override
      public String toString() {
         return this.last != null ? "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]" : "Iterator[]";
      }
   }

   protected static class LinkMapIterator<K, V> extends AbstractLinkedMap.LinkIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
      protected LinkMapIterator(AbstractLinkedMap<K, V> parent) {
         super(parent);
      }

      @Override
      public K next() {
         return super.nextEntry().getKey();
      }

      @Override
      public K previous() {
         return super.previousEntry().getKey();
      }

      @Override
      public K getKey() {
         AbstractLinkedMap.LinkEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return current.getKey();
         }
      }

      @Override
      public V getValue() {
         AbstractLinkedMap.LinkEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return current.getValue();
         }
      }

      @Override
      public V setValue(V value) {
         AbstractLinkedMap.LinkEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return current.setValue(value);
         }
      }
   }

   protected static class ValuesIterator<V> extends AbstractLinkedMap.LinkIterator<Object, V> implements OrderedIterator<V>, ResettableIterator<V> {
      protected ValuesIterator(AbstractLinkedMap<?, V> parent) {
         super((AbstractLinkedMap<Object, V>)parent);
      }

      @Override
      public V next() {
         return super.nextEntry().getValue();
      }

      @Override
      public V previous() {
         return super.previousEntry().getValue();
      }
   }
}
