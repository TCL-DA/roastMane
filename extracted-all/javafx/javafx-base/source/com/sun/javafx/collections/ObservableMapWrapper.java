package com.sun.javafx.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class ObservableMapWrapper<K, V> implements ObservableMap<K, V> {
   private ObservableMapWrapper<K, V>.ObservableEntrySet entrySet;
   private ObservableMapWrapper<K, V>.ObservableKeySet keySet;
   private ObservableMapWrapper<K, V>.ObservableValues values;
   private MapListenerHelper<K, V> listenerHelper;
   private final Map<K, V> backingMap;

   public ObservableMapWrapper(Map<K, V> var1) {
      this.backingMap = var1;
   }

   protected void callObservers(MapChangeListener.Change<K, V> var1) {
      MapListenerHelper.fireValueChangedEvent(this.listenerHelper, var1);
   }

   @Override
   public void addListener(InvalidationListener var1) {
      this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, var1);
   }

   @Override
   public void addListener(MapChangeListener<? super K, ? super V> var1) {
      this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, var1);
   }

   @Override
   public void removeListener(MapChangeListener<? super K, ? super V> var1) {
      this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, var1);
   }

   @Override
   public int size() {
      return this.backingMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this.backingMap.isEmpty();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.backingMap.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.backingMap.containsValue(var1);
   }

   @Override
   public V get(Object var1) {
      return this.backingMap.get(var1);
   }

   @Override
   public V put(K var1, V var2) {
      Object var3;
      if (this.backingMap.containsKey(var1)) {
         var3 = this.backingMap.put((K)var1, (V)var2);
         if (var3 == null && var2 != null || var3 != null && !var3.equals(var2)) {
            this.callObservers(new ObservableMapWrapper.SimpleChange(var1, var3, var2, true, true));
         }
      } else {
         var3 = this.backingMap.put((K)var1, (V)var2);
         this.callObservers(new ObservableMapWrapper.SimpleChange(var1, var3, var2, true, false));
      }

      return (V)var3;
   }

   @Override
   public V remove(Object var1) {
      if (!this.backingMap.containsKey(var1)) {
         return null;
      } else {
         Object var2 = this.backingMap.remove(var1);
         this.callObservers(new ObservableMapWrapper.SimpleChange(var1, var2, null, false, true));
         return (V)var2;
      }
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      for (Entry var3 : var1.entrySet()) {
         this.put((K)var3.getKey(), (V)var3.getValue());
      }
   }

   @Override
   public void clear() {
      Iterator var1 = this.backingMap.entrySet().iterator();

      while (var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         Object var3 = var2.getKey();
         Object var4 = var2.getValue();
         var1.remove();
         this.callObservers(new ObservableMapWrapper.SimpleChange(var3, var4, null, false, true));
      }
   }

   @Override
   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = new ObservableMapWrapper.ObservableKeySet();
      }

      return this.keySet;
   }

   @Override
   public Collection<V> values() {
      if (this.values == null) {
         this.values = new ObservableMapWrapper.ObservableValues();
      }

      return this.values;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new ObservableMapWrapper.ObservableEntrySet();
      }

      return this.entrySet;
   }

   @Override
   public String toString() {
      return this.backingMap.toString();
   }

   @Override
   public boolean equals(Object var1) {
      return this.backingMap.equals(var1);
   }

   @Override
   public int hashCode() {
      return this.backingMap.hashCode();
   }

   private class ObservableEntry implements Entry<K, V> {
      private final Entry<K, V> backingEntry;

      public ObservableEntry(Entry<K, V> var2) {
         this.backingEntry = var2;
      }

      @Override
      public K getKey() {
         return this.backingEntry.getKey();
      }

      @Override
      public V getValue() {
         return this.backingEntry.getValue();
      }

      @Override
      public V setValue(V var1) {
         Object var2 = this.backingEntry.setValue((V)var1);
         ObservableMapWrapper.this.callObservers(ObservableMapWrapper.this.new SimpleChange(this.getKey(), var2, var1, true, true));
         return (V)var2;
      }

      @Override
      public final boolean equals(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            Object var3 = this.getKey();
            Object var4 = var2.getKey();
            if (var3 == var4 || var3 != null && var3.equals(var4)) {
               Object var5 = this.getValue();
               Object var6 = var2.getValue();
               if (var5 == var6 || var5 != null && var5.equals(var6)) {
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public final int hashCode() {
         return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
      }

      @Override
      public final String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }

   private class ObservableEntrySet implements Set<Entry<K, V>> {
      @Override
      public int size() {
         return ObservableMapWrapper.this.backingMap.size();
      }

      @Override
      public boolean isEmpty() {
         return ObservableMapWrapper.this.backingMap.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return ObservableMapWrapper.this.backingMap.entrySet().contains(var1);
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return new Iterator<Entry<K, V>>() {
            private Iterator<Entry<K, V>> backingIt = ObservableMapWrapper.this.backingMap.entrySet().iterator();
            private K lastKey;
            private V lastValue;

            @Override
            public boolean hasNext() {
               return this.backingIt.hasNext();
            }

            public Entry<K, V> next() {
               Entry var1 = this.backingIt.next();
               this.lastKey = (K)var1.getKey();
               this.lastValue = (V)var1.getValue();
               return ObservableMapWrapper.this.new ObservableEntry(var1);
            }

            @Override
            public void remove() {
               this.backingIt.remove();
               ObservableMapWrapper.this.callObservers(ObservableMapWrapper.this.new SimpleChange(this.lastKey, this.lastValue, null, false, true));
            }
         };
      }

      @Override
      public Object[] toArray() {
         Object[] var1 = ObservableMapWrapper.this.backingMap.entrySet().toArray();

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = ObservableMapWrapper.this.new ObservableEntry((Entry<K, V>)var1[var2]);
         }

         return var1;
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         Object[] var2 = ObservableMapWrapper.this.backingMap.entrySet().toArray(var1);

         for (int var3 = 0; var3 < var2.length; var3++) {
            var2[var3] = ObservableMapWrapper.this.new ObservableEntry((Entry<K, V>)var2[var3]);
         }

         return (T[])var2;
      }

      public boolean add(Entry<K, V> var1) {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public boolean remove(Object var1) {
         boolean var2 = ObservableMapWrapper.this.backingMap.entrySet().remove(var1);
         if (var2) {
            Entry var3 = (Entry)var1;
            ObservableMapWrapper.this.callObservers(ObservableMapWrapper.this.new SimpleChange(var3.getKey(), var3.getValue(), null, false, true));
         }

         return var2;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return ObservableMapWrapper.this.backingMap.entrySet().containsAll(var1);
      }

      @Override
      public boolean addAll(Collection<? extends Entry<K, V>> var1) {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return this.removeRetain(var1, false);
      }

      private boolean removeRetain(Collection<?> var1, boolean var2) {
         boolean var3 = false;
         Iterator var4 = ObservableMapWrapper.this.backingMap.entrySet().iterator();

         while (var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            if (var2 == var1.contains(var5)) {
               var3 = true;
               Object var6 = var5.getKey();
               Object var7 = var5.getValue();
               var4.remove();
               ObservableMapWrapper.this.callObservers(ObservableMapWrapper.this.new SimpleChange(var6, var7, null, false, true));
            }
         }

         return var3;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         return this.removeRetain(var1, true);
      }

      @Override
      public void clear() {
         ObservableMapWrapper.this.clear();
      }

      @Override
      public String toString() {
         return ObservableMapWrapper.this.backingMap.entrySet().toString();
      }

      @Override
      public boolean equals(Object var1) {
         return ObservableMapWrapper.this.backingMap.entrySet().equals(var1);
      }

      @Override
      public int hashCode() {
         return ObservableMapWrapper.this.backingMap.entrySet().hashCode();
      }
   }

   private class ObservableKeySet implements Set<K> {
      @Override
      public int size() {
         return ObservableMapWrapper.this.backingMap.size();
      }

      @Override
      public boolean isEmpty() {
         return ObservableMapWrapper.this.backingMap.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return ObservableMapWrapper.this.backingMap.keySet().contains(var1);
      }

      @Override
      public Iterator<K> iterator() {
         return new Iterator<K>() {
            private Iterator<Entry<K, V>> entryIt = ObservableMapWrapper.this.backingMap.entrySet().iterator();
            private K lastKey;
            private V lastValue;

            @Override
            public boolean hasNext() {
               return this.entryIt.hasNext();
            }

            @Override
            public K next() {
               Entry var1 = this.entryIt.next();
               this.lastKey = (K)var1.getKey();
               this.lastValue = (V)var1.getValue();
               return (K)var1.getKey();
            }

            @Override
            public void remove() {
               this.entryIt.remove();
               ObservableMapWrapper.this.callObservers(ObservableMapWrapper.this.new SimpleChange(this.lastKey, this.lastValue, null, false, true));
            }
         };
      }

      @Override
      public Object[] toArray() {
         return ObservableMapWrapper.this.backingMap.keySet().toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])ObservableMapWrapper.this.backingMap.keySet().toArray(var1);
      }

      @Override
      public boolean add(K var1) {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public boolean remove(Object var1) {
         return ObservableMapWrapper.this.remove(var1) != null;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return ObservableMapWrapper.this.backingMap.keySet().containsAll(var1);
      }

      @Override
      public boolean addAll(Collection<? extends K> var1) {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return this.removeRetain(var1, false);
      }

      private boolean removeRetain(Collection<?> var1, boolean var2) {
         boolean var3 = false;
         Iterator var4 = ObservableMapWrapper.this.backingMap.entrySet().iterator();

         while (var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            if (var2 == var1.contains(var5.getKey())) {
               var3 = true;
               Object var6 = var5.getKey();
               Object var7 = var5.getValue();
               var4.remove();
               ObservableMapWrapper.this.callObservers(ObservableMapWrapper.this.new SimpleChange(var6, var7, null, false, true));
            }
         }

         return var3;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         return this.removeRetain(var1, true);
      }

      @Override
      public void clear() {
         ObservableMapWrapper.this.clear();
      }

      @Override
      public String toString() {
         return ObservableMapWrapper.this.backingMap.keySet().toString();
      }

      @Override
      public boolean equals(Object var1) {
         return ObservableMapWrapper.this.backingMap.keySet().equals(var1);
      }

      @Override
      public int hashCode() {
         return ObservableMapWrapper.this.backingMap.keySet().hashCode();
      }
   }

   private class ObservableValues implements Collection<V> {
      @Override
      public int size() {
         return ObservableMapWrapper.this.backingMap.size();
      }

      @Override
      public boolean isEmpty() {
         return ObservableMapWrapper.this.backingMap.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return ObservableMapWrapper.this.backingMap.values().contains(var1);
      }

      @Override
      public Iterator<V> iterator() {
         return new Iterator<V>() {
            private Iterator<Entry<K, V>> entryIt = ObservableMapWrapper.this.backingMap.entrySet().iterator();
            private K lastKey;
            private V lastValue;

            @Override
            public boolean hasNext() {
               return this.entryIt.hasNext();
            }

            @Override
            public V next() {
               Entry var1 = this.entryIt.next();
               this.lastKey = (K)var1.getKey();
               this.lastValue = (V)var1.getValue();
               return this.lastValue;
            }

            @Override
            public void remove() {
               this.entryIt.remove();
               ObservableMapWrapper.this.callObservers(ObservableMapWrapper.this.new SimpleChange(this.lastKey, this.lastValue, null, false, true));
            }
         };
      }

      @Override
      public Object[] toArray() {
         return ObservableMapWrapper.this.backingMap.values().toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])ObservableMapWrapper.this.backingMap.values().toArray(var1);
      }

      @Override
      public boolean add(V var1) {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public boolean remove(Object var1) {
         Iterator var2 = this.iterator();

         while (var2.hasNext()) {
            if (var2.next().equals(var1)) {
               var2.remove();
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return ObservableMapWrapper.this.backingMap.values().containsAll(var1);
      }

      @Override
      public boolean addAll(Collection<? extends V> var1) {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         return this.removeRetain(var1, true);
      }

      private boolean removeRetain(Collection<?> var1, boolean var2) {
         boolean var3 = false;
         Iterator var4 = ObservableMapWrapper.this.backingMap.entrySet().iterator();

         while (var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            if (var2 == var1.contains(var5.getValue())) {
               var3 = true;
               Object var6 = var5.getKey();
               Object var7 = var5.getValue();
               var4.remove();
               ObservableMapWrapper.this.callObservers(ObservableMapWrapper.this.new SimpleChange(var6, var7, null, false, true));
            }
         }

         return var3;
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return this.removeRetain(var1, false);
      }

      @Override
      public void clear() {
         ObservableMapWrapper.this.clear();
      }

      @Override
      public String toString() {
         return ObservableMapWrapper.this.backingMap.values().toString();
      }

      @Override
      public boolean equals(Object var1) {
         return ObservableMapWrapper.this.backingMap.values().equals(var1);
      }

      @Override
      public int hashCode() {
         return ObservableMapWrapper.this.backingMap.values().hashCode();
      }
   }

   private class SimpleChange extends MapChangeListener.Change<K, V> {
      private final K key;
      private final V old;
      private final V added;
      private final boolean wasAdded;
      private final boolean wasRemoved;

      public SimpleChange(K var2, V var3, V var4, boolean var5, boolean var6) {
         super(ObservableMapWrapper.this);

         assert var5 || var6;

         this.key = (K)var2;
         this.old = (V)var3;
         this.added = (V)var4;
         this.wasAdded = var5;
         this.wasRemoved = var6;
      }

      @Override
      public boolean wasAdded() {
         return this.wasAdded;
      }

      @Override
      public boolean wasRemoved() {
         return this.wasRemoved;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public V getValueAdded() {
         return this.added;
      }

      @Override
      public V getValueRemoved() {
         return this.old;
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.wasAdded) {
            if (this.wasRemoved) {
               var1.append(this.old).append(" replaced by ").append(this.added);
            } else {
               var1.append(this.added).append(" added");
            }
         } else {
            var1.append(this.old).append(" removed");
         }

         var1.append(" at key ").append(this.key);
         return var1.toString();
      }
   }
}
