package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;

public abstract class AbstractReferenceMap<K, V> extends AbstractHashedMap<K, V> {
   private AbstractReferenceMap.ReferenceStrength keyType;
   private AbstractReferenceMap.ReferenceStrength valueType;
   private boolean purgeValues;
   private transient ReferenceQueue<Object> queue;

   protected AbstractReferenceMap() {
   }

   protected AbstractReferenceMap(
      AbstractReferenceMap.ReferenceStrength keyType, AbstractReferenceMap.ReferenceStrength valueType, int capacity, float loadFactor, boolean purgeValues
   ) {
      super(capacity, loadFactor);
      this.keyType = keyType;
      this.valueType = valueType;
      this.purgeValues = purgeValues;
   }

   @Override
   protected void init() {
      this.queue = new ReferenceQueue<>();
   }

   @Override
   public int size() {
      this.purgeBeforeRead();
      return super.size();
   }

   @Override
   public boolean isEmpty() {
      this.purgeBeforeRead();
      return super.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      this.purgeBeforeRead();
      Entry<K, V> entry = this.getEntry(key);
      return entry == null ? false : entry.getValue() != null;
   }

   @Override
   public boolean containsValue(Object value) {
      this.purgeBeforeRead();
      return value == null ? false : super.containsValue(value);
   }

   @Override
   public V get(Object key) {
      this.purgeBeforeRead();
      Entry<K, V> entry = this.getEntry(key);
      return entry == null ? null : entry.getValue();
   }

   @Override
   public V put(K key, V value) {
      if (key == null) {
         throw new NullPointerException("null keys not allowed");
      } else if (value == null) {
         throw new NullPointerException("null values not allowed");
      } else {
         this.purgeBeforeWrite();
         return super.put(key, value);
      }
   }

   @Override
   public V remove(Object key) {
      if (key == null) {
         return null;
      } else {
         this.purgeBeforeWrite();
         return super.remove(key);
      }
   }

   @Override
   public void clear() {
      super.clear();

      while (this.queue.poll() != null) {
      }
   }

   @Override
   public MapIterator<K, V> mapIterator() {
      return new AbstractReferenceMap.ReferenceMapIterator<>(this);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new AbstractReferenceMap.ReferenceEntrySet<>(this);
      }

      return this.entrySet;
   }

   @Override
   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = new AbstractReferenceMap.ReferenceKeySet<>(this);
      }

      return this.keySet;
   }

   @Override
   public Collection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceMap.ReferenceValues<>(this);
      }

      return this.values;
   }

   protected void purgeBeforeRead() {
      this.purge();
   }

   protected void purgeBeforeWrite() {
      this.purge();
   }

   protected void purge() {
      for (Reference<?> ref = this.queue.poll(); ref != null; ref = this.queue.poll()) {
         this.purge(ref);
      }
   }

   protected void purge(Reference<?> ref) {
      int hash = ref.hashCode();
      int index = this.hashIndex(hash, this.data.length);
      AbstractHashedMap.HashEntry<K, V> previous = null;

      for (AbstractHashedMap.HashEntry<K, V> entry = this.data[index]; entry != null; entry = entry.next) {
         AbstractReferenceMap.ReferenceEntry<K, V> refEntry = (AbstractReferenceMap.ReferenceEntry<K, V>)entry;
         if (refEntry.purge(ref)) {
            if (previous == null) {
               this.data[index] = entry.next;
            } else {
               previous.next = entry.next;
            }

            this.size--;
            refEntry.onPurge();
            return;
         }

         previous = entry;
      }
   }

   @Override
   protected AbstractHashedMap.HashEntry<K, V> getEntry(Object key) {
      return key == null ? null : super.getEntry(key);
   }

   protected int hashEntry(Object key, Object value) {
      return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
   }

   @Override
   protected boolean isEqualKey(Object key1, Object key2) {
      key2 = this.keyType == AbstractReferenceMap.ReferenceStrength.HARD ? key2 : ((Reference)key2).get();
      return key1 == key2 || key1.equals(key2);
   }

   protected AbstractReferenceMap.ReferenceEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
      return new AbstractReferenceMap.ReferenceEntry<>(this, next, hashCode, key, value);
   }

   @Override
   protected Iterator<Entry<K, V>> createEntrySetIterator() {
      return new AbstractReferenceMap.ReferenceEntrySetIterator<>(this);
   }

   @Override
   protected Iterator<K> createKeySetIterator() {
      return new AbstractReferenceMap.ReferenceKeySetIterator<>(this);
   }

   @Override
   protected Iterator<V> createValuesIterator() {
      return new AbstractReferenceMap.ReferenceValuesIterator<>(this);
   }

   @Override
   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.keyType.value);
      out.writeInt(this.valueType.value);
      out.writeBoolean(this.purgeValues);
      out.writeFloat(this.loadFactor);
      out.writeInt(this.data.length);
      MapIterator<K, V> it = this.mapIterator();

      while (it.hasNext()) {
         out.writeObject(it.next());
         out.writeObject(it.getValue());
      }

      out.writeObject(null);
   }

   @Override
   protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      this.keyType = AbstractReferenceMap.ReferenceStrength.resolve(in.readInt());
      this.valueType = AbstractReferenceMap.ReferenceStrength.resolve(in.readInt());
      this.purgeValues = in.readBoolean();
      this.loadFactor = in.readFloat();
      int capacity = in.readInt();
      this.init();
      this.data = new AbstractHashedMap.HashEntry[capacity];
      this.threshold = this.calculateThreshold(this.data.length, this.loadFactor);

      while (true) {
         K key = (K)in.readObject();
         if (key == null) {
            return;
         }

         V value = (V)in.readObject();
         this.put(key, value);
      }
   }

   protected boolean isKeyType(AbstractReferenceMap.ReferenceStrength type) {
      return this.keyType == type;
   }

   protected boolean isValueType(AbstractReferenceMap.ReferenceStrength type) {
      return this.valueType == type;
   }

   static class ReferenceBaseIterator<K, V> {
      final AbstractReferenceMap<K, V> parent;
      int index;
      AbstractReferenceMap.ReferenceEntry<K, V> entry;
      AbstractReferenceMap.ReferenceEntry<K, V> previous;
      K currentKey;
      K nextKey;
      V currentValue;
      V nextValue;
      int expectedModCount;

      public ReferenceBaseIterator(AbstractReferenceMap<K, V> parent) {
         this.parent = parent;
         this.index = parent.size() != 0 ? parent.data.length : 0;
         this.expectedModCount = parent.modCount;
      }

      public boolean hasNext() {
         this.checkMod();

         while (this.nextNull()) {
            AbstractReferenceMap.ReferenceEntry<K, V> e = this.entry;
            int i = this.index;

            while (e == null && i > 0) {
               e = (AbstractReferenceMap.ReferenceEntry<K, V>)this.parent.data[--i];
            }

            this.entry = e;
            this.index = i;
            if (e == null) {
               this.currentKey = null;
               this.currentValue = null;
               return false;
            }

            this.nextKey = e.getKey();
            this.nextValue = e.getValue();
            if (this.nextNull()) {
               this.entry = this.entry.next();
            }
         }

         return true;
      }

      private void checkMod() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      private boolean nextNull() {
         return this.nextKey == null || this.nextValue == null;
      }

      protected AbstractReferenceMap.ReferenceEntry<K, V> nextEntry() {
         this.checkMod();
         if (this.nextNull() && !this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.previous = this.entry;
            this.entry = this.entry.next();
            this.currentKey = this.nextKey;
            this.currentValue = this.nextValue;
            this.nextKey = null;
            this.nextValue = null;
            return this.previous;
         }
      }

      protected AbstractReferenceMap.ReferenceEntry<K, V> currentEntry() {
         this.checkMod();
         return this.previous;
      }

      public void remove() {
         this.checkMod();
         if (this.previous == null) {
            throw new IllegalStateException();
         } else {
            this.parent.remove(this.currentKey);
            this.previous = null;
            this.currentKey = null;
            this.currentValue = null;
            this.expectedModCount = this.parent.modCount;
         }
      }
   }

   protected static class ReferenceEntry<K, V> extends AbstractHashedMap.HashEntry<K, V> {
      private final AbstractReferenceMap<K, V> parent;

      public ReferenceEntry(AbstractReferenceMap<K, V> parent, AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
         super(next, hashCode, null, null);
         this.parent = parent;
         this.key = this.toReference(parent.keyType, key, hashCode);
         this.value = this.toReference(parent.valueType, value, hashCode);
      }

      @Override
      public K getKey() {
         return (K)(this.parent.keyType == AbstractReferenceMap.ReferenceStrength.HARD ? this.key : ((Reference)this.key).get());
      }

      @Override
      public V getValue() {
         return (V)(this.parent.valueType == AbstractReferenceMap.ReferenceStrength.HARD ? this.value : ((Reference)this.value).get());
      }

      @Override
      public V setValue(V obj) {
         V old = this.getValue();
         if (this.parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD) {
            ((Reference)this.value).clear();
         }

         this.value = this.toReference(this.parent.valueType, obj, this.hashCode);
         return old;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> entry = (Entry<?, ?>)obj;
            Object entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            return entryKey != null && entryValue != null
               ? this.parent.isEqualKey(entryKey, this.key) && this.parent.isEqualValue(entryValue, this.getValue())
               : false;
         }
      }

      @Override
      public int hashCode() {
         return this.parent.hashEntry(this.getKey(), this.getValue());
      }

      protected <T> Object toReference(AbstractReferenceMap.ReferenceStrength type, T referent, int hash) {
         if (type == AbstractReferenceMap.ReferenceStrength.HARD) {
            return referent;
         } else if (type == AbstractReferenceMap.ReferenceStrength.SOFT) {
            return new AbstractReferenceMap.SoftRef<>(hash, referent, this.parent.queue);
         } else if (type == AbstractReferenceMap.ReferenceStrength.WEAK) {
            return new AbstractReferenceMap.WeakRef<>(hash, referent, this.parent.queue);
         } else {
            throw new Error();
         }
      }

      protected void onPurge() {
      }

      protected boolean purge(Reference<?> ref) {
         boolean r = this.parent.keyType != AbstractReferenceMap.ReferenceStrength.HARD && this.key == ref;
         r = r || this.parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD && this.value == ref;
         if (r) {
            if (this.parent.keyType != AbstractReferenceMap.ReferenceStrength.HARD) {
               ((Reference)this.key).clear();
            }

            if (this.parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD) {
               ((Reference)this.value).clear();
            } else if (this.parent.purgeValues) {
               this.nullValue();
            }
         }

         return r;
      }

      protected AbstractReferenceMap.ReferenceEntry<K, V> next() {
         return (AbstractReferenceMap.ReferenceEntry<K, V>)this.next;
      }

      protected void nullValue() {
         this.value = null;
      }
   }

   static class ReferenceEntrySet<K, V> extends AbstractHashedMap.EntrySet<K, V> {
      protected ReferenceEntrySet(AbstractHashedMap<K, V> parent) {
         super(parent);
      }

      @Override
      public Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      @Override
      public <T> T[] toArray(T[] arr) {
         ArrayList<Entry<K, V>> list = new ArrayList<>(this.size());

         for (Entry<K, V> entry : this) {
            list.add(new DefaultMapEntry<>(entry));
         }

         return (T[])list.toArray(arr);
      }
   }

   static class ReferenceEntrySetIterator<K, V> extends AbstractReferenceMap.ReferenceBaseIterator<K, V> implements Iterator<Entry<K, V>> {
      public ReferenceEntrySetIterator(AbstractReferenceMap<K, V> parent) {
         super(parent);
      }

      public Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   static class ReferenceKeySet<K> extends AbstractHashedMap.KeySet<K> {
      protected ReferenceKeySet(AbstractHashedMap<K, ?> parent) {
         super(parent);
      }

      @Override
      public Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      @Override
      public <T> T[] toArray(T[] arr) {
         List<K> list = new ArrayList<>(this.size());

         for (K key : this) {
            list.add(key);
         }

         return (T[])list.toArray(arr);
      }
   }

   static class ReferenceKeySetIterator<K> extends AbstractReferenceMap.ReferenceBaseIterator<K, Object> implements Iterator<K> {
      ReferenceKeySetIterator(AbstractReferenceMap<K, ?> parent) {
         super((AbstractReferenceMap<K, Object>)parent);
      }

      @Override
      public K next() {
         return this.nextEntry().getKey();
      }
   }

   static class ReferenceMapIterator<K, V> extends AbstractReferenceMap.ReferenceBaseIterator<K, V> implements MapIterator<K, V> {
      protected ReferenceMapIterator(AbstractReferenceMap<K, V> parent) {
         super(parent);
      }

      @Override
      public K next() {
         return this.nextEntry().getKey();
      }

      @Override
      public K getKey() {
         AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return current.getKey();
         }
      }

      @Override
      public V getValue() {
         AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return current.getValue();
         }
      }

      @Override
      public V setValue(V value) {
         AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return current.setValue(value);
         }
      }
   }

   public static enum ReferenceStrength {
      HARD(0),
      SOFT(1),
      WEAK(2);

      public final int value;

      public static AbstractReferenceMap.ReferenceStrength resolve(int value) {
         switch (value) {
            case 0:
               return HARD;
            case 1:
               return SOFT;
            case 2:
               return WEAK;
            default:
               throw new IllegalArgumentException();
         }
      }

      private ReferenceStrength(int value) {
         this.value = value;
      }
   }

   static class ReferenceValues<V> extends AbstractHashedMap.Values<V> {
      protected ReferenceValues(AbstractHashedMap<?, V> parent) {
         super(parent);
      }

      @Override
      public Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      @Override
      public <T> T[] toArray(T[] arr) {
         List<V> list = new ArrayList<>(this.size());

         for (V value : this) {
            list.add(value);
         }

         return (T[])list.toArray(arr);
      }
   }

   static class ReferenceValuesIterator<V> extends AbstractReferenceMap.ReferenceBaseIterator<Object, V> implements Iterator<V> {
      ReferenceValuesIterator(AbstractReferenceMap<?, V> parent) {
         super((AbstractReferenceMap<Object, V>)parent);
      }

      @Override
      public V next() {
         return this.nextEntry().getValue();
      }
   }

   static class SoftRef<T> extends SoftReference<T> {
      private final int hash;

      public SoftRef(int hash, T r, ReferenceQueue<? super T> q) {
         super(r, q);
         this.hash = hash;
      }

      @Override
      public int hashCode() {
         return this.hash;
      }
   }

   static class WeakRef<T> extends WeakReference<T> {
      private final int hash;

      public WeakRef(int hash, T r, ReferenceQueue<? super T> q) {
         super(r, q);
         this.hash = hash;
      }

      @Override
      public int hashCode() {
         return this.hash;
      }
   }
}
