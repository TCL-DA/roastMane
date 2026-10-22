package org.glassfish.jersey.internal.util.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.glassfish.jersey.internal.LocalizationMessages;

public class KeyComparatorHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {
   private static final long serialVersionUID = 3000273665665137463L;
   static final int DEFAULT_INITIAL_CAPACITY = 16;
   static final int MAXIMUM_CAPACITY = 1073741824;
   static final float DEFAULT_LOAD_FACTOR = 0.75F;
   transient KeyComparatorHashMap.Entry<K, V>[] table;
   transient int size;
   int threshold;
   final float loadFactor;
   transient volatile int modCount;
   final KeyComparator<K> keyComparator;
   static final Object NULL_KEY = new Object();
   private transient Set<java.util.Map.Entry<K, V>> entrySet = null;

   public KeyComparatorHashMap(int initialCapacity, float loadFactor, KeyComparator<K> keyComparator) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException(LocalizationMessages.ILLEGAL_INITIAL_CAPACITY(initialCapacity));
      } else {
         if (initialCapacity > 1073741824) {
            initialCapacity = 1073741824;
         }

         if (!(loadFactor <= 0.0F) && !Float.isNaN(loadFactor)) {
            int capacity = 1;

            while (capacity < initialCapacity) {
               capacity <<= 1;
            }

            this.loadFactor = loadFactor;
            this.threshold = (int)((float)capacity * loadFactor);
            this.table = new KeyComparatorHashMap.Entry[capacity];
            this.keyComparator = keyComparator;
            this.init();
         } else {
            throw new IllegalArgumentException(LocalizationMessages.ILLEGAL_LOAD_FACTOR(loadFactor));
         }
      }
   }

   public KeyComparatorHashMap(int initialCapacity, KeyComparator<K> keyComparator) {
      this(initialCapacity, 0.75F, keyComparator);
   }

   public KeyComparatorHashMap(KeyComparator<K> keyComparator) {
      this(16, 0.75F, keyComparator);
   }

   public KeyComparatorHashMap(Map<? extends K, ? extends V> m, KeyComparator<K> keyComparator) {
      this(Math.max((int)((float)m.size() / 0.75F) + 1, 16), 0.75F, keyComparator);
      this.putAllForCreate(m);
   }

   public int getModCount() {
      return this.modCount;
   }

   void init() {
   }

   static <T> T maskNull(T key) {
      return (T)(key == null ? NULL_KEY : key);
   }

   static <T> boolean isNull(T key) {
      return key == NULL_KEY;
   }

   static <T> T unmaskNull(T key) {
      return key == NULL_KEY ? null : key;
   }

   static int hash(Object x) {
      int h = x.hashCode();
      h += ~(h << 9);
      h ^= h >>> 14;
      h += h << 4;
      return h ^ h >>> 10;
   }

   static boolean eq(Object x, Object y) {
      return x == y || x.equals(y);
   }

   static int indexFor(int h, int length) {
      return h & length - 1;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   int keyComparatorHash(K k) {
      return isNull(k) ? this.hash(k.hashCode()) : this.hash(this.keyComparator.hash(k));
   }

   int hash(int h) {
      h += ~(h << 9);
      h ^= h >>> 14;
      h += h << 4;
      return h ^ h >>> 10;
   }

   boolean keyComparatorEq(K x, K y) {
      if (isNull(x)) {
         return x == y;
      } else {
         return isNull(y) ? x == y : x == y || this.keyComparator.equals(x, y);
      }
   }

   @Override
   public V get(Object key) {
      K k = maskNull((K)key);
      int hash = this.keyComparatorHash(k);
      int i = indexFor(hash, this.table.length);

      for (KeyComparatorHashMap.Entry<K, V> e = this.table[i]; e != null; e = e.next) {
         if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
            return e.value;
         }
      }

      return null;
   }

   @Override
   public boolean containsKey(Object key) {
      K k = maskNull((K)key);
      int hash = this.keyComparatorHash(k);
      int i = indexFor(hash, this.table.length);

      for (KeyComparatorHashMap.Entry<K, V> e = this.table[i]; e != null; e = e.next) {
         if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
            return true;
         }
      }

      return false;
   }

   KeyComparatorHashMap.Entry<K, V> getEntry(K key) {
      K k = maskNull(key);
      int hash = this.keyComparatorHash(k);
      int i = indexFor(hash, this.table.length);
      KeyComparatorHashMap.Entry<K, V> e = this.table[i];

      while (e != null && (e.hash != hash || !this.keyComparatorEq(k, e.key))) {
         e = e.next;
      }

      return e;
   }

   @Override
   public V put(K key, V value) {
      K k = maskNull(key);
      int hash = this.keyComparatorHash(k);
      int i = indexFor(hash, this.table.length);

      for (KeyComparatorHashMap.Entry<K, V> e = this.table[i]; e != null; e = e.next) {
         if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
            V oldValue = e.value;
            e.value = value;
            e.recordAccess(this);
            return oldValue;
         }
      }

      this.modCount++;
      this.addEntry(hash, k, value, i);
      return null;
   }

   private void putForCreate(K key, V value) {
      K k = maskNull(key);
      int hash = this.keyComparatorHash(k);
      int i = indexFor(hash, this.table.length);

      for (KeyComparatorHashMap.Entry<K, V> e = this.table[i]; e != null; e = e.next) {
         if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
            e.value = value;
            return;
         }
      }

      this.createEntry(hash, k, value, i);
   }

   private void putAllForCreate(Map<? extends K, ? extends V> m) {
      for (java.util.Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
         this.putForCreate((K)e.getKey(), (V)e.getValue());
      }
   }

   void resize(int newCapacity) {
      KeyComparatorHashMap.Entry<K, V>[] oldTable = this.table;
      int oldCapacity = oldTable.length;
      if (oldCapacity == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         KeyComparatorHashMap.Entry<K, V>[] newTable = new KeyComparatorHashMap.Entry[newCapacity];
         this.transfer(newTable);
         this.table = newTable;
         this.threshold = (int)((float)newCapacity * this.loadFactor);
      }
   }

   void transfer(KeyComparatorHashMap.Entry<K, V>[] newTable) {
      KeyComparatorHashMap.Entry<K, V>[] src = this.table;
      int newCapacity = newTable.length;

      for (int j = 0; j < src.length; j++) {
         KeyComparatorHashMap.Entry<K, V> e = src[j];
         if (e != null) {
            src[j] = null;

            while (true) {
               KeyComparatorHashMap.Entry<K, V> next = e.next;
               int i = indexFor(e.hash, newCapacity);
               e.next = newTable[i];
               newTable[i] = e;
               e = next;
               if (next == null) {
                  break;
               }
            }
         }
      }
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      int numKeysToBeAdded = m.size();
      if (numKeysToBeAdded != 0) {
         if (numKeysToBeAdded > this.threshold) {
            int targetCapacity = (int)((float)numKeysToBeAdded / this.loadFactor + 1.0F);
            if (targetCapacity > 1073741824) {
               targetCapacity = 1073741824;
            }

            int newCapacity = this.table.length;

            while (newCapacity < targetCapacity) {
               newCapacity <<= 1;
            }

            if (newCapacity > this.table.length) {
               this.resize(newCapacity);
            }
         }

         for (java.util.Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put((K)e.getKey(), (V)e.getValue());
         }
      }
   }

   @Override
   public V remove(Object key) {
      KeyComparatorHashMap.Entry<K, V> e = this.removeEntryForKey(key);
      return e == null ? null : e.value;
   }

   KeyComparatorHashMap.Entry<K, V> removeEntryForKey(Object key) {
      K k = maskNull((K)key);
      int hash = this.keyComparatorHash(k);
      int i = indexFor(hash, this.table.length);
      KeyComparatorHashMap.Entry<K, V> prev = this.table[i];
      KeyComparatorHashMap.Entry<K, V> e = prev;

      while (e != null) {
         KeyComparatorHashMap.Entry<K, V> next = e.next;
         if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
            this.modCount++;
            this.size--;
            if (prev == e) {
               this.table[i] = next;
            } else {
               prev.next = next;
            }

            e.recordRemoval(this);
            return e;
         }

         prev = e;
         e = next;
      }

      return e;
   }

   KeyComparatorHashMap.Entry<K, V> removeMapping(Object o) {
      if (!(o instanceof java.util.Map.Entry)) {
         return null;
      } else {
         java.util.Map.Entry<K, V> entry = (java.util.Map.Entry<K, V>)o;
         K k = maskNull(entry.getKey());
         int hash = this.keyComparatorHash(k);
         int i = indexFor(hash, this.table.length);
         KeyComparatorHashMap.Entry<K, V> prev = this.table[i];
         KeyComparatorHashMap.Entry<K, V> e = prev;

         while (e != null) {
            KeyComparatorHashMap.Entry<K, V> next = e.next;
            if (e.hash == hash && e.equals(entry)) {
               this.modCount++;
               this.size--;
               if (prev == e) {
                  this.table[i] = next;
               } else {
                  prev.next = next;
               }

               e.recordRemoval(this);
               return e;
            }

            prev = e;
            e = next;
         }

         return e;
      }
   }

   @Override
   public void clear() {
      this.modCount++;
      KeyComparatorHashMap.Entry[] tab = this.table;

      for (int i = 0; i < tab.length; i++) {
         tab[i] = null;
      }

      this.size = 0;
   }

   @Override
   public boolean containsValue(Object value) {
      if (value == null) {
         return this.containsNullValue();
      } else {
         KeyComparatorHashMap.Entry[] tab = this.table;

         for (int i = 0; i < tab.length; i++) {
            for (KeyComparatorHashMap.Entry e = tab[i]; e != null; e = e.next) {
               if (value.equals(e.value)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean containsNullValue() {
      KeyComparatorHashMap.Entry[] tab = this.table;

      for (KeyComparatorHashMap.Entry aTab : tab) {
         for (KeyComparatorHashMap.Entry e = aTab; e != null; e = e.next) {
            if (e.value == null) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public Object clone() {
      KeyComparatorHashMap<K, V> result = null;

      try {
         result = (KeyComparatorHashMap<K, V>)super.clone();
         result.table = new KeyComparatorHashMap.Entry[this.table.length];
         result.entrySet = null;
         result.modCount = 0;
         result.size = 0;
         result.init();
         result.putAllForCreate(this);
      } catch (CloneNotSupportedException var3) {
      }

      return result;
   }

   void addEntry(int hash, K key, V value, int bucketIndex) {
      KeyComparatorHashMap.Entry<K, V> e = this.table[bucketIndex];
      this.table[bucketIndex] = new KeyComparatorHashMap.Entry<>(hash, key, value, e);
      if (this.size++ >= this.threshold) {
         this.resize(2 * this.table.length);
      }
   }

   void createEntry(int hash, K key, V value, int bucketIndex) {
      KeyComparatorHashMap.Entry<K, V> e = this.table[bucketIndex];
      this.table[bucketIndex] = new KeyComparatorHashMap.Entry<>(hash, key, value, e);
      this.size++;
   }

   Iterator<K> newKeyIterator() {
      return new KeyComparatorHashMap.KeyIterator();
   }

   Iterator<V> newValueIterator() {
      return new KeyComparatorHashMap.ValueIterator();
   }

   Iterator<java.util.Map.Entry<K, V>> newEntryIterator() {
      return new KeyComparatorHashMap.EntryIterator();
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet() {
      Set<java.util.Map.Entry<K, V>> es = this.entrySet;
      return es != null ? es : (this.entrySet = new KeyComparatorHashMap.EntrySet());
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      Iterator<java.util.Map.Entry<K, V>> i = this.entrySet().iterator();
      s.defaultWriteObject();
      s.writeInt(this.table.length);
      s.writeInt(this.size);

      while (i.hasNext()) {
         java.util.Map.Entry<K, V> e = i.next();
         s.writeObject(e.getKey());
         s.writeObject(e.getValue());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      int numBuckets = s.readInt();
      this.table = new KeyComparatorHashMap.Entry[numBuckets];
      this.init();
      int ss = s.readInt();

      for (int i = 0; i < ss; i++) {
         K key = (K)s.readObject();
         V value = (V)s.readObject();
         this.putForCreate(key, value);
      }
   }

   int capacity() {
      return this.table.length;
   }

   float loadFactor() {
      return this.loadFactor;
   }

   static class Entry<K, V> implements java.util.Map.Entry<K, V> {
      final K key;
      V value;
      final int hash;
      KeyComparatorHashMap.Entry<K, V> next;

      Entry(int h, K k, V v, KeyComparatorHashMap.Entry<K, V> n) {
         this.value = v;
         this.next = n;
         this.key = k;
         this.hash = h;
      }

      @Override
      public K getKey() {
         return KeyComparatorHashMap.unmaskNull(this.key);
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public V setValue(V newValue) {
         V oldValue = this.value;
         this.value = newValue;
         return oldValue;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry e = (java.util.Map.Entry)o;
            Object k1 = this.getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || k1 != null && k1.equals(k2)) {
               Object v1 = this.getValue();
               Object v2 = e.getValue();
               if (v1 == v2 || v1 != null && v1.equals(v2)) {
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public int hashCode() {
         return (this.key == KeyComparatorHashMap.NULL_KEY ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      @Override
      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }

      void recordAccess(KeyComparatorHashMap<K, V> m) {
      }

      void recordRemoval(KeyComparatorHashMap<K, V> m) {
      }
   }

   private class EntryIterator extends KeyComparatorHashMap<K, V>.HashIterator<java.util.Map.Entry<K, V>> {
      private EntryIterator() {
      }

      public java.util.Map.Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   private class EntrySet extends AbstractSet {
      private EntrySet() {
      }

      @Override
      public Iterator iterator() {
         return KeyComparatorHashMap.this.newEntryIterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<K, V> e = (java.util.Map.Entry<K, V>)o;
            KeyComparatorHashMap.Entry<K, V> candidate = KeyComparatorHashMap.this.getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
         }
      }

      @Override
      public boolean remove(Object o) {
         return KeyComparatorHashMap.this.removeMapping(o) != null;
      }

      @Override
      public int size() {
         return KeyComparatorHashMap.this.size;
      }

      @Override
      public void clear() {
         KeyComparatorHashMap.this.clear();
      }
   }

   private abstract class HashIterator<E> implements Iterator<E> {
      KeyComparatorHashMap.Entry<K, V> next;
      int expectedModCount = KeyComparatorHashMap.this.modCount;
      int index;
      KeyComparatorHashMap.Entry<K, V> current;

      HashIterator() {
         KeyComparatorHashMap.Entry<K, V>[] t = KeyComparatorHashMap.this.table;
         int i = t.length;
         KeyComparatorHashMap.Entry<K, V> n = null;
         if (KeyComparatorHashMap.this.size != 0) {
            while (i > 0) {
               if ((n = t[--i]) != null) {
                  break;
               }
            }
         }

         this.next = n;
         this.index = i;
      }

      @Override
      public boolean hasNext() {
         return this.next != null;
      }

      KeyComparatorHashMap.Entry<K, V> nextEntry() {
         if (KeyComparatorHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            KeyComparatorHashMap.Entry<K, V> e = this.next;
            if (e == null) {
               throw new NoSuchElementException();
            } else {
               KeyComparatorHashMap.Entry<K, V> n = e.next;
               KeyComparatorHashMap.Entry<K, V>[] t = KeyComparatorHashMap.this.table;
               int i = this.index;

               while (n == null && i > 0) {
                  n = t[--i];
               }

               this.index = i;
               this.next = n;
               return this.current = e;
            }
         }
      }

      @Override
      public void remove() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else if (KeyComparatorHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            K k = this.current.key;
            this.current = null;
            KeyComparatorHashMap.this.removeEntryForKey(k);
            this.expectedModCount = KeyComparatorHashMap.this.modCount;
         }
      }
   }

   private class KeyIterator extends KeyComparatorHashMap<K, V>.HashIterator<K> {
      private KeyIterator() {
      }

      @Override
      public K next() {
         return (K)this.nextEntry().getKey();
      }
   }

   private class ValueIterator extends KeyComparatorHashMap<K, V>.HashIterator<V> {
      private ValueIterator() {
      }

      @Override
      public V next() {
         return this.nextEntry().value;
      }
   }
}
