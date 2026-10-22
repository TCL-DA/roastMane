package org.glassfish.jersey.internal.util.collection;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class KeyComparatorLinkedHashMap<K, V> extends KeyComparatorHashMap<K, V> implements Map<K, V> {
   private static final long serialVersionUID = 3801124242820219131L;
   private transient KeyComparatorLinkedHashMap.Entry<K, V> header;
   private final boolean accessOrder;

   public KeyComparatorLinkedHashMap(int initialCapacity, float loadFactor, KeyComparator<K> keyComparator) {
      super(initialCapacity, loadFactor, keyComparator);
      this.accessOrder = false;
   }

   public KeyComparatorLinkedHashMap(int initialCapacity, KeyComparator<K> keyComparator) {
      super(initialCapacity, keyComparator);
      this.accessOrder = false;
   }

   public KeyComparatorLinkedHashMap(KeyComparator<K> keyComparator) {
      super(keyComparator);
      this.accessOrder = false;
   }

   public KeyComparatorLinkedHashMap(Map<? extends K, ? extends V> m, KeyComparator<K> keyComparator) {
      super(m, keyComparator);
      this.accessOrder = false;
   }

   public KeyComparatorLinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder, KeyComparator<K> keyComparator) {
      super(initialCapacity, loadFactor, keyComparator);
      this.accessOrder = accessOrder;
   }

   @Override
   void init() {
      this.header = new KeyComparatorLinkedHashMap.Entry<>(-1, null, null, null);
      this.header.before = this.header.after = this.header;
   }

   @Override
   void transfer(KeyComparatorHashMap.Entry[] newTable) {
      int newCapacity = newTable.length;

      for (KeyComparatorLinkedHashMap.Entry<K, V> e = this.header.after; e != this.header; e = e.after) {
         int index = indexFor(e.hash, newCapacity);
         e.next = newTable[index];
         newTable[index] = e;
      }
   }

   @Override
   public boolean containsValue(Object value) {
      if (value == null) {
         for (KeyComparatorLinkedHashMap.Entry e = this.header.after; e != this.header; e = e.after) {
            if (e.value == null) {
               return true;
            }
         }
      } else {
         for (KeyComparatorLinkedHashMap.Entry ex = this.header.after; ex != this.header; ex = ex.after) {
            if (value.equals(ex.value)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public V get(Object key) {
      KeyComparatorLinkedHashMap.Entry<K, V> e = (KeyComparatorLinkedHashMap.Entry<K, V>)this.getEntry((K)key);
      if (e == null) {
         return null;
      } else {
         e.recordAccess(this);
         return e.value;
      }
   }

   @Override
   public void clear() {
      super.clear();
      this.header.before = this.header.after = this.header;
   }

   @Override
   Iterator<K> newKeyIterator() {
      return new KeyComparatorLinkedHashMap.KeyIterator();
   }

   @Override
   Iterator<V> newValueIterator() {
      return new KeyComparatorLinkedHashMap.ValueIterator();
   }

   @Override
   Iterator<java.util.Map.Entry<K, V>> newEntryIterator() {
      return new KeyComparatorLinkedHashMap.EntryIterator();
   }

   @Override
   void addEntry(int hash, K key, V value, int bucketIndex) {
      this.createEntry(hash, key, value, bucketIndex);
      KeyComparatorLinkedHashMap.Entry<K, V> eldest = this.header.after;
      if (this.removeEldestEntry(eldest)) {
         this.removeEntryForKey(eldest.key);
      } else if (this.size >= this.threshold) {
         this.resize(2 * this.table.length);
      }
   }

   @Override
   void createEntry(int hash, K key, V value, int bucketIndex) {
      KeyComparatorHashMap.Entry<K, V> old = this.table[bucketIndex];
      KeyComparatorLinkedHashMap.Entry<K, V> e = new KeyComparatorLinkedHashMap.Entry<>(hash, key, value, old);
      this.table[bucketIndex] = e;
      e.addBefore(this.header);
      this.size++;
   }

   protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
      return false;
   }

   private static class Entry<K, V> extends KeyComparatorHashMap.Entry<K, V> {
      KeyComparatorLinkedHashMap.Entry<K, V> before;
      KeyComparatorLinkedHashMap.Entry<K, V> after;

      Entry(int hash, K key, V value, KeyComparatorHashMap.Entry<K, V> next) {
         super(hash, key, value, next);
      }

      private void remove() {
         this.before.after = this.after;
         this.after.before = this.before;
      }

      private void addBefore(KeyComparatorLinkedHashMap.Entry<K, V> existingEntry) {
         this.after = existingEntry;
         this.before = existingEntry.before;
         this.before.after = this;
         this.after.before = this;
      }

      @Override
      void recordAccess(KeyComparatorHashMap<K, V> m) {
         KeyComparatorLinkedHashMap<K, V> lm = (KeyComparatorLinkedHashMap<K, V>)m;
         if (lm.accessOrder) {
            lm.modCount++;
            this.remove();
            this.addBefore(lm.header);
         }
      }

      @Override
      void recordRemoval(KeyComparatorHashMap<K, V> m) {
         this.remove();
      }

      @Override
      public boolean equals(Object obj) {
         return super.equals(obj);
      }

      @Override
      public int hashCode() {
         return super.hashCode();
      }
   }

   private class EntryIterator extends KeyComparatorLinkedHashMap<K, V>.LinkedHashIterator<java.util.Map.Entry<K, V>> {
      private EntryIterator() {
      }

      public java.util.Map.Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   private class KeyIterator extends KeyComparatorLinkedHashMap<K, V>.LinkedHashIterator<K> {
      private KeyIterator() {
      }

      @Override
      public K next() {
         return (K)this.nextEntry().getKey();
      }
   }

   private abstract class LinkedHashIterator<T> implements Iterator<T> {
      KeyComparatorLinkedHashMap.Entry<K, V> nextEntry = KeyComparatorLinkedHashMap.this.header.after;
      KeyComparatorLinkedHashMap.Entry<K, V> lastReturned = null;
      int expectedModCount = KeyComparatorLinkedHashMap.this.modCount;

      private LinkedHashIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.nextEntry != KeyComparatorLinkedHashMap.this.header;
      }

      @Override
      public void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else if (KeyComparatorLinkedHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            KeyComparatorLinkedHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
            this.expectedModCount = KeyComparatorLinkedHashMap.this.modCount;
         }
      }

      KeyComparatorLinkedHashMap.Entry<K, V> nextEntry() {
         if (KeyComparatorLinkedHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else if (this.nextEntry == KeyComparatorLinkedHashMap.this.header) {
            throw new NoSuchElementException();
         } else {
            KeyComparatorLinkedHashMap.Entry<K, V> e = this.lastReturned = this.nextEntry;
            this.nextEntry = e.after;
            return e;
         }
      }
   }

   private class ValueIterator extends KeyComparatorLinkedHashMap<K, V>.LinkedHashIterator<V> {
      private ValueIterator() {
      }

      @Override
      public V next() {
         return this.nextEntry().value;
      }
   }
}
