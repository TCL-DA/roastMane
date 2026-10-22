package javafx.collections;

import com.sun.javafx.collections.ListListenerHelper;
import com.sun.javafx.collections.MapAdapterChange;
import com.sun.javafx.collections.MapListenerHelper;
import com.sun.javafx.collections.ObservableFloatArrayImpl;
import com.sun.javafx.collections.ObservableIntegerArrayImpl;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.collections.ObservableMapWrapper;
import com.sun.javafx.collections.ObservableSequentialListWrapper;
import com.sun.javafx.collections.ObservableSetWrapper;
import com.sun.javafx.collections.SetAdapterChange;
import com.sun.javafx.collections.SetListenerHelper;
import com.sun.javafx.collections.SortableList;
import com.sun.javafx.collections.SourceAdapterChange;
import com.sun.javafx.collections.UnmodifiableObservableMap;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.util.Callback;

public class FXCollections {
   private static ObservableMap EMPTY_OBSERVABLE_MAP = new FXCollections.EmptyObservableMap();
   private static ObservableList EMPTY_OBSERVABLE_LIST = new FXCollections.EmptyObservableList();
   private static ObservableSet EMPTY_OBSERVABLE_SET = new FXCollections.EmptyObservableSet();
   private static Random r;

   private FXCollections() {
   }

   public static <E> ObservableList<E> observableList(List<E> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return (ObservableList<E>)(var0 instanceof RandomAccess ? new ObservableListWrapper<>(var0) : new ObservableSequentialListWrapper<>(var0));
      }
   }

   public static <E> ObservableList<E> observableList(List<E> var0, Callback<E, Observable[]> var1) {
      if (var0 != null && var1 != null) {
         return (ObservableList<E>)(var0 instanceof RandomAccess ? new ObservableListWrapper<>(var0, var1) : new ObservableSequentialListWrapper<>(var0, var1));
      } else {
         throw new NullPointerException();
      }
   }

   public static <K, V> ObservableMap<K, V> observableMap(Map<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new ObservableMapWrapper<>(var0);
      }
   }

   public static <E> ObservableSet<E> observableSet(Set<E> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new ObservableSetWrapper<>(var0);
      }
   }

   public static <E> ObservableSet<E> observableSet(E... var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         HashSet var1 = new HashSet(var0.length);
         Collections.addAll(var1, (E[])var0);
         return new ObservableSetWrapper<>(var1);
      }
   }

   public static <K, V> ObservableMap<K, V> unmodifiableObservableMap(ObservableMap<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new UnmodifiableObservableMap<>(var0);
      }
   }

   public static <K, V> ObservableMap<K, V> checkedObservableMap(ObservableMap<K, V> var0, Class<K> var1, Class<V> var2) {
      if (var0 != null && var1 != null && var2 != null) {
         return new FXCollections.CheckedObservableMap<>(var0, var1, var2);
      } else {
         throw new NullPointerException();
      }
   }

   public static <K, V> ObservableMap<K, V> synchronizedObservableMap(ObservableMap<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new FXCollections.SynchronizedObservableMap<>(var0);
      }
   }

   public static <K, V> ObservableMap<K, V> emptyObservableMap() {
      return EMPTY_OBSERVABLE_MAP;
   }

   public static ObservableIntegerArray observableIntegerArray() {
      return new ObservableIntegerArrayImpl();
   }

   public static ObservableIntegerArray observableIntegerArray(int... var0) {
      return new ObservableIntegerArrayImpl(var0);
   }

   public static ObservableIntegerArray observableIntegerArray(ObservableIntegerArray var0) {
      return new ObservableIntegerArrayImpl(var0);
   }

   public static ObservableFloatArray observableFloatArray() {
      return new ObservableFloatArrayImpl();
   }

   public static ObservableFloatArray observableFloatArray(float... var0) {
      return new ObservableFloatArrayImpl(var0);
   }

   public static ObservableFloatArray observableFloatArray(ObservableFloatArray var0) {
      return new ObservableFloatArrayImpl(var0);
   }

   public static <E> ObservableList<E> observableArrayList() {
      return observableList(new ArrayList<>());
   }

   public static <E> ObservableList<E> observableArrayList(Callback<E, Observable[]> var0) {
      return observableList(new ArrayList<>(), var0);
   }

   public static <E> ObservableList<E> observableArrayList(E... var0) {
      ObservableList var1 = observableArrayList();
      var1.addAll(var0);
      return var1;
   }

   public static <E> ObservableList<E> observableArrayList(Collection<? extends E> var0) {
      ObservableList var1 = observableArrayList();
      var1.addAll(var0);
      return var1;
   }

   public static <K, V> ObservableMap<K, V> observableHashMap() {
      return observableMap(new HashMap<>());
   }

   public static <E> ObservableList<E> concat(ObservableList<E>... var0) {
      if (var0.length == 0) {
         return observableArrayList();
      } else if (var0.length == 1) {
         return observableArrayList(var0[0]);
      } else {
         ArrayList var1 = new ArrayList();

         for (ObservableList var5 : var0) {
            var1.addAll(var5);
         }

         return observableList(var1);
      }
   }

   public static <E> ObservableList<E> unmodifiableObservableList(ObservableList<E> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new FXCollections.UnmodifiableObservableListImpl<>(var0);
      }
   }

   public static <E> ObservableList<E> checkedObservableList(ObservableList<E> var0, Class<E> var1) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new FXCollections.CheckedObservableList<>(var0, var1);
      }
   }

   public static <E> ObservableList<E> synchronizedObservableList(ObservableList<E> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new FXCollections.SynchronizedObservableList<>(var0);
      }
   }

   public static <E> ObservableList<E> emptyObservableList() {
      return EMPTY_OBSERVABLE_LIST;
   }

   public static <E> ObservableList<E> singletonObservableList(E var0) {
      return new FXCollections.SingletonObservableList<>((E)var0);
   }

   public static <E> ObservableSet<E> unmodifiableObservableSet(ObservableSet<E> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new FXCollections.UnmodifiableObservableSet<>(var0);
      }
   }

   public static <E> ObservableSet<E> checkedObservableSet(ObservableSet<E> var0, Class<E> var1) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new FXCollections.CheckedObservableSet<>(var0, var1);
      }
   }

   public static <E> ObservableSet<E> synchronizedObservableSet(ObservableSet<E> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new FXCollections.SynchronizedObservableSet<>(var0);
      }
   }

   public static <E> ObservableSet<E> emptyObservableSet() {
      return EMPTY_OBSERVABLE_SET;
   }

   public static <T> void copy(ObservableList<? super T> var0, List<? extends T> var1) {
      int var2 = var1.size();
      if (var2 > var0.size()) {
         throw new IndexOutOfBoundsException("Source does not fit in dest");
      } else {
         Object[] var3 = var0.toArray();
         System.arraycopy(var1.toArray(), 0, var3, 0, var2);
         var0.setAll(var3);
      }
   }

   public static <T> void fill(ObservableList<? super T> var0, T var1) {
      Object[] var2 = new Object[var0.size()];
      Arrays.fill(var2, var1);
      var0.setAll(var2);
   }

   public static <T> boolean replaceAll(ObservableList<T> var0, T var1, T var2) {
      Object[] var3 = var0.toArray();
      boolean var4 = false;

      for (int var5 = 0; var5 < var3.length; var5++) {
         if (var3[var5].equals(var1)) {
            var3[var5] = var2;
            var4 = true;
         }
      }

      if (var4) {
         var0.setAll(var3);
      }

      return var4;
   }

   public static void reverse(ObservableList var0) {
      Object[] var1 = var0.toArray();

      for (int var2 = 0; var2 < var1.length / 2; var2++) {
         Object var3 = var1[var2];
         var1[var2] = var1[var1.length - var2 - 1];
         var1[var1.length - var2 - 1] = var3;
      }

      var0.setAll(var1);
   }

   public static void rotate(ObservableList var0, int var1) {
      Object[] var2 = var0.toArray();
      int var3 = var0.size();
      var1 %= var3;
      if (var1 < 0) {
         var1 += var3;
      }

      if (var1 != 0) {
         int var4 = 0;

         for (int var5 = 0; var5 != var3; var4++) {
            Object var6 = var2[var4];
            int var8 = var4;

            do {
               var8 += var1;
               if (var8 >= var3) {
                  var8 -= var3;
               }

               Object var7 = var2[var8];
               var2[var8] = var6;
               var5++;
            } while (var8 == var4);
         }

         var0.setAll(var2);
      }
   }

   public static void shuffle(ObservableList<?> var0) {
      if (r == null) {
         r = new Random();
      }

      shuffle(var0, r);
   }

   public static void shuffle(ObservableList var0, Random var1) {
      Object[] var2 = var0.toArray();

      for (int var3 = var0.size(); var3 > 1; var3--) {
         swap(var2, var3 - 1, var1.nextInt(var3));
      }

      var0.setAll(var2);
   }

   private static void swap(Object[] var0, int var1, int var2) {
      Object var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   public static <T extends Comparable<? super T>> void sort(ObservableList<T> var0) {
      if (var0 instanceof SortableList) {
         ((SortableList)var0).sort();
      } else {
         ArrayList var1 = new ArrayList(var0);
         Collections.sort(var1);
         var0.setAll(var1);
      }
   }

   public static <T> void sort(ObservableList<T> var0, Comparator<? super T> var1) {
      if (var0 instanceof SortableList) {
         ((SortableList)var0).sort(var1);
      } else {
         ArrayList var2 = new ArrayList(var0);
         Collections.sort(var2, var1);
         var0.setAll(var2);
      }
   }

   private static class CheckedObservableList<T> extends ObservableListBase<T> implements ObservableList<T> {
      private final ObservableList<T> list;
      private final Class<T> type;
      private final ListChangeListener<T> listener;

      CheckedObservableList(ObservableList<T> var1, Class<T> var2) {
         if (var1 != null && var2 != null) {
            this.list = var1;
            this.type = var2;
            this.listener = var1x -> this.fireChange(new SourceAdapterChange<>(this, var1x));
            var1.addListener(new WeakListChangeListener<>(this.listener));
         } else {
            throw new NullPointerException();
         }
      }

      void typeCheck(Object var1) {
         if (var1 != null && !this.type.isInstance(var1)) {
            throw new ClassCastException("Attempt to insert " + var1.getClass() + " element into collection with element type " + this.type);
         }
      }

      @Override
      public int size() {
         return this.list.size();
      }

      @Override
      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return this.list.contains(var1);
      }

      @Override
      public Object[] toArray() {
         return this.list.toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])this.list.toArray(var1);
      }

      @Override
      public String toString() {
         return this.list.toString();
      }

      @Override
      public boolean remove(Object var1) {
         return this.list.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return this.list.containsAll(var1);
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         return this.list.removeAll(var1);
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return this.list.retainAll(var1);
      }

      @Override
      public boolean removeAll(T... var1) {
         return this.list.removeAll((T[])var1);
      }

      @Override
      public boolean retainAll(T... var1) {
         return this.list.retainAll((T[])var1);
      }

      @Override
      public void remove(int var1, int var2) {
         this.list.remove(var1, var2);
      }

      @Override
      public void clear() {
         this.list.clear();
      }

      @Override
      public boolean equals(Object var1) {
         return var1 == this || this.list.equals(var1);
      }

      @Override
      public int hashCode() {
         return this.list.hashCode();
      }

      @Override
      public T get(int var1) {
         return this.list.get(var1);
      }

      @Override
      public T remove(int var1) {
         return this.list.remove(var1);
      }

      @Override
      public int indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      @Override
      public int lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }

      @Override
      public T set(int var1, T var2) {
         this.typeCheck(var2);
         return this.list.set(var1, (T)var2);
      }

      @Override
      public void add(int var1, T var2) {
         this.typeCheck(var2);
         this.list.add(var1, (T)var2);
      }

      @Override
      public boolean addAll(int var1, Collection<? extends T> var2) {
         Object var3 = null;

         try {
            var3 = var2.toArray((T[])((Object[])Array.newInstance(this.type, 0)));
         } catch (ArrayStoreException var5) {
            throw new ClassCastException();
         }

         return this.list.addAll(var1, Arrays.asList((T[])var3));
      }

      @Override
      public boolean addAll(Collection<? extends T> var1) {
         Object var2 = null;

         try {
            var2 = var1.toArray((T[])((Object[])Array.newInstance(this.type, 0)));
         } catch (ArrayStoreException var4) {
            throw new ClassCastException();
         }

         return this.list.addAll(Arrays.asList((T[])var2));
      }

      @Override
      public ListIterator<T> listIterator() {
         return this.listIterator(0);
      }

      @Override
      public ListIterator<T> listIterator(final int var1) {
         return new ListIterator<T>() {
            ListIterator<T> i = CheckedObservableList.this.list.listIterator(var1);

            @Override
            public boolean hasNext() {
               return this.i.hasNext();
            }

            @Override
            public T next() {
               return this.i.next();
            }

            @Override
            public boolean hasPrevious() {
               return this.i.hasPrevious();
            }

            @Override
            public T previous() {
               return this.i.previous();
            }

            @Override
            public int nextIndex() {
               return this.i.nextIndex();
            }

            @Override
            public int previousIndex() {
               return this.i.previousIndex();
            }

            @Override
            public void remove() {
               this.i.remove();
            }

            @Override
            public void set(T var1x) {
               CheckedObservableList.this.typeCheck(var1x);
               this.i.set((T)var1x);
            }

            @Override
            public void add(T var1x) {
               CheckedObservableList.this.typeCheck(var1x);
               this.i.add((T)var1x);
            }
         };
      }

      @Override
      public Iterator<T> iterator() {
         return new Iterator<T>() {
            private final Iterator<T> it = CheckedObservableList.this.list.iterator();

            @Override
            public boolean hasNext() {
               return this.it.hasNext();
            }

            @Override
            public T next() {
               return this.it.next();
            }

            @Override
            public void remove() {
               this.it.remove();
            }
         };
      }

      @Override
      public boolean add(T var1) {
         this.typeCheck(var1);
         return this.list.add((T)var1);
      }

      @Override
      public List<T> subList(int var1, int var2) {
         return Collections.checkedList(this.list.subList(var1, var2), this.type);
      }

      @Override
      public boolean addAll(T... var1) {
         try {
            Object[] var2 = (Object[])Array.newInstance(this.type, var1.length);
            System.arraycopy(var1, 0, var2, 0, var1.length);
            return this.list.addAll((T[])var2);
         } catch (ArrayStoreException var3) {
            throw new ClassCastException();
         }
      }

      @Override
      public boolean setAll(T... var1) {
         try {
            Object[] var2 = (Object[])Array.newInstance(this.type, var1.length);
            System.arraycopy(var1, 0, var2, 0, var1.length);
            return this.list.setAll((T[])var2);
         } catch (ArrayStoreException var3) {
            throw new ClassCastException();
         }
      }

      @Override
      public boolean setAll(Collection<? extends T> var1) {
         Object var2 = null;

         try {
            var2 = var1.toArray((T[])((Object[])Array.newInstance(this.type, 0)));
         } catch (ArrayStoreException var4) {
            throw new ClassCastException();
         }

         return this.list.setAll(Arrays.asList((T[])var2));
      }
   }

   private static class CheckedObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
      private final ObservableMap<K, V> backingMap;
      private final Class<K> keyType;
      private final Class<V> valueType;
      private MapListenerHelper listenerHelper;
      private final MapChangeListener<K, V> listener;
      private transient Set<Entry<K, V>> entrySet = null;

      CheckedObservableMap(ObservableMap<K, V> var1, Class<K> var2, Class<V> var3) {
         this.backingMap = var1;
         this.keyType = var2;
         this.valueType = var3;
         this.listener = var1x -> this.callObservers(new MapAdapterChange<>(this, var1x));
         this.backingMap.addListener(new WeakMapChangeListener<>(this.listener));
      }

      private void callObservers(MapChangeListener.Change<? extends K, ? extends V> var1) {
         MapListenerHelper.fireValueChangedEvent(this.listenerHelper, var1);
      }

      void typeCheck(Object var1, Object var2) {
         if (var1 != null && !this.keyType.isInstance(var1)) {
            throw new ClassCastException("Attempt to insert " + var1.getClass() + " key into map with key type " + this.keyType);
         } else if (var2 != null && !this.valueType.isInstance(var2)) {
            throw new ClassCastException("Attempt to insert " + var2.getClass() + " value into map with value type " + this.valueType);
         }
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
         this.typeCheck(var1, var2);
         return this.backingMap.put((K)var1, (V)var2);
      }

      @Override
      public V remove(Object var1) {
         return this.backingMap.remove(var1);
      }

      @Override
      public void putAll(Map var1) {
         Object[] var2 = var1.entrySet().toArray();
         ArrayList var3 = new ArrayList(var2.length);

         for (Object var7 : var2) {
            Entry var8 = (Entry)var7;
            Object var9 = var8.getKey();
            Object var10 = var8.getValue();
            this.typeCheck(var9, var10);
            var3.add(new SimpleImmutableEntry<>(var9, var10));
         }

         for (Entry var12 : var3) {
            this.backingMap.put((K)var12.getKey(), (V)var12.getValue());
         }
      }

      @Override
      public void clear() {
         this.backingMap.clear();
      }

      @Override
      public Set<K> keySet() {
         return this.backingMap.keySet();
      }

      @Override
      public Collection<V> values() {
         return this.backingMap.values();
      }

      @Override
      public Set entrySet() {
         if (this.entrySet == null) {
            this.entrySet = new FXCollections.CheckedObservableMap.CheckedEntrySet<>(this.backingMap.entrySet(), this.valueType);
         }

         return this.entrySet;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 == this || this.backingMap.equals(var1);
      }

      @Override
      public int hashCode() {
         return this.backingMap.hashCode();
      }

      static class CheckedEntrySet<K, V> implements Set<Entry<K, V>> {
         private final Set<Entry<K, V>> s;
         private final Class<V> valueType;

         CheckedEntrySet(Set<Entry<K, V>> var1, Class<V> var2) {
            this.s = var1;
            this.valueType = var2;
         }

         @Override
         public int size() {
            return this.s.size();
         }

         @Override
         public boolean isEmpty() {
            return this.s.isEmpty();
         }

         @Override
         public String toString() {
            return this.s.toString();
         }

         @Override
         public int hashCode() {
            return this.s.hashCode();
         }

         @Override
         public void clear() {
            this.s.clear();
         }

         public boolean add(Entry<K, V> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean addAll(Collection<? extends Entry<K, V>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public Iterator<Entry<K, V>> iterator() {
            final Iterator var1 = this.s.iterator();
            final Class var2 = this.valueType;
            return new Iterator<Entry<K, V>>() {
               @Override
               public boolean hasNext() {
                  return var1.hasNext();
               }

               @Override
               public void remove() {
                  var1.remove();
               }

               public Entry<K, V> next() {
                  return FXCollections.CheckedObservableMap.CheckedEntrySet.checkedEntry((Entry<K, V>)var1.next(), var2);
               }
            };
         }

         @Override
         public Object[] toArray() {
            Object[] var1 = this.s.toArray();
            Object[] var2 = FXCollections.CheckedObservableMap.CheckedEntrySet.CheckedEntry.class.isInstance(var1.getClass().getComponentType())
               ? var1
               : new Object[var1.length];

            for (int var3 = 0; var3 < var1.length; var3++) {
               var2[var3] = checkedEntry((Entry<K, V>)var1[var3], this.valueType);
            }

            return var2;
         }

         @Override
         public <T> T[] toArray(T[] var1) {
            Object[] var2 = this.s.toArray(var1.length == 0 ? var1 : Arrays.copyOf(var1, 0));

            for (int var3 = 0; var3 < var2.length; var3++) {
               var2[var3] = checkedEntry((Entry<K, V>)var2[var3], this.valueType);
            }

            if (var2.length > var1.length) {
               return (T[])var2;
            } else {
               System.arraycopy(var2, 0, var1, 0, var2.length);
               if (var1.length > var2.length) {
                  var1[var2.length] = null;
               }

               return (T[])var1;
            }
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Entry var2 = (Entry)var1;
               return this.s
                  .contains(var2 instanceof FXCollections.CheckedObservableMap.CheckedEntrySet.CheckedEntry ? var2 : checkedEntry(var2, this.valueType));
            }
         }

         @Override
         public boolean containsAll(Collection<?> var1) {
            for (Object var3 : var1) {
               if (!this.contains(var3)) {
                  return false;
               }
            }

            return true;
         }

         @Override
         public boolean remove(Object var1) {
            return !(var1 instanceof Entry) ? false : this.s.remove(new SimpleImmutableEntry((Entry<? extends K, ? extends V>)var1));
         }

         @Override
         public boolean removeAll(Collection<?> var1) {
            return this.batchRemove(var1, false);
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            return this.batchRemove(var1, true);
         }

         private boolean batchRemove(Collection<?> var1, boolean var2) {
            boolean var3 = false;
            Iterator var4 = this.iterator();

            while (var4.hasNext()) {
               if (var1.contains(var4.next()) != var2) {
                  var4.remove();
                  var3 = true;
               }
            }

            return var3;
         }

         @Override
         public boolean equals(Object var1) {
            if (var1 == this) {
               return true;
            } else if (!(var1 instanceof Set)) {
               return false;
            } else {
               Set var2 = (Set)var1;
               return var2.size() == this.s.size() && this.containsAll(var2);
            }
         }

         static <K, V, T> FXCollections.CheckedObservableMap.CheckedEntrySet.CheckedEntry<K, V, T> checkedEntry(Entry<K, V> var0, Class<T> var1) {
            return new FXCollections.CheckedObservableMap.CheckedEntrySet.CheckedEntry<>(var0, var1);
         }

         private static class CheckedEntry<K, V, T> implements Entry<K, V> {
            private final Entry<K, V> e;
            private final Class<T> valueType;

            CheckedEntry(Entry<K, V> var1, Class<T> var2) {
               this.e = var1;
               this.valueType = var2;
            }

            @Override
            public K getKey() {
               return this.e.getKey();
            }

            @Override
            public V getValue() {
               return this.e.getValue();
            }

            @Override
            public int hashCode() {
               return this.e.hashCode();
            }

            @Override
            public String toString() {
               return this.e.toString();
            }

            @Override
            public V setValue(V var1) {
               if (var1 != null && !this.valueType.isInstance(var1)) {
                  throw new ClassCastException(this.badValueMsg(var1));
               } else {
                  return this.e.setValue((V)var1);
               }
            }

            private String badValueMsg(Object var1) {
               return "Attempt to insert " + var1.getClass() + " value into map with value type " + this.valueType;
            }

            @Override
            public boolean equals(Object var1) {
               if (var1 == this) {
                  return true;
               } else {
                  return !(var1 instanceof Entry) ? false : this.e.equals(new SimpleImmutableEntry((Entry<? extends K, ? extends V>)var1));
               }
            }
         }
      }
   }

   private static class CheckedObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
      private final ObservableSet<E> backingSet;
      private final Class<E> type;
      private SetListenerHelper listenerHelper;
      private final SetChangeListener<E> listener;

      CheckedObservableSet(ObservableSet<E> var1, Class<E> var2) {
         if (var1 != null && var2 != null) {
            this.backingSet = var1;
            this.type = var2;
            this.listener = var1x -> this.callObservers(new SetAdapterChange<>(this, var1x));
            this.backingSet.addListener(new WeakSetChangeListener<>(this.listener));
         } else {
            throw new NullPointerException();
         }
      }

      private void callObservers(SetChangeListener.Change<? extends E> var1) {
         SetListenerHelper.fireValueChangedEvent(this.listenerHelper, var1);
      }

      void typeCheck(Object var1) {
         if (var1 != null && !this.type.isInstance(var1)) {
            throw new ClassCastException("Attempt to insert " + var1.getClass() + " element into collection with element type " + this.type);
         }
      }

      @Override
      public void addListener(InvalidationListener var1) {
         this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
      }

      @Override
      public void removeListener(InvalidationListener var1) {
         this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, var1);
      }

      @Override
      public void addListener(SetChangeListener<? super E> var1) {
         this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
      }

      @Override
      public void removeListener(SetChangeListener<? super E> var1) {
         this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, var1);
      }

      @Override
      public int size() {
         return this.backingSet.size();
      }

      @Override
      public boolean isEmpty() {
         return this.backingSet.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return this.backingSet.contains(var1);
      }

      @Override
      public Object[] toArray() {
         return this.backingSet.toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])this.backingSet.toArray(var1);
      }

      @Override
      public boolean add(E var1) {
         this.typeCheck(var1);
         return this.backingSet.add((E)var1);
      }

      @Override
      public boolean remove(Object var1) {
         return this.backingSet.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return this.backingSet.containsAll(var1);
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         Object var2 = null;

         try {
            var2 = var1.toArray((Object[])Array.newInstance(this.type, 0));
         } catch (ArrayStoreException var4) {
            throw new ClassCastException();
         }

         return this.backingSet.addAll(Arrays.asList((E[])var2));
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return this.backingSet.retainAll(var1);
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         return this.backingSet.removeAll(var1);
      }

      @Override
      public void clear() {
         this.backingSet.clear();
      }

      @Override
      public boolean equals(Object var1) {
         return var1 == this || this.backingSet.equals(var1);
      }

      @Override
      public int hashCode() {
         return this.backingSet.hashCode();
      }

      @Override
      public Iterator<E> iterator() {
         final Iterator var1 = this.backingSet.iterator();
         return new Iterator<E>() {
            @Override
            public boolean hasNext() {
               return var1.hasNext();
            }

            @Override
            public E next() {
               return (E)var1.next();
            }

            @Override
            public void remove() {
               var1.remove();
            }
         };
      }
   }

   private static class EmptyObservableList<E> extends AbstractList<E> implements ObservableList<E> {
      private static final ListIterator iterator = new ListIterator() {
         @Override
         public boolean hasNext() {
            return false;
         }

         @Override
         public Object next() {
            throw new NoSuchElementException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean hasPrevious() {
            return false;
         }

         @Override
         public Object previous() {
            throw new NoSuchElementException();
         }

         @Override
         public int nextIndex() {
            return 0;
         }

         @Override
         public int previousIndex() {
            return -1;
         }

         @Override
         public void set(Object var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void add(Object var1) {
            throw new UnsupportedOperationException();
         }
      };

      public EmptyObservableList() {
      }

      @Override
      public final void addListener(InvalidationListener var1) {
      }

      @Override
      public final void removeListener(InvalidationListener var1) {
      }

      @Override
      public void addListener(ListChangeListener<? super E> var1) {
      }

      @Override
      public void removeListener(ListChangeListener<? super E> var1) {
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public boolean contains(Object var1) {
         return false;
      }

      @Override
      public Iterator<E> iterator() {
         return iterator;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return var1.isEmpty();
      }

      @Override
      public E get(int var1) {
         throw new IndexOutOfBoundsException();
      }

      @Override
      public int indexOf(Object var1) {
         return -1;
      }

      @Override
      public int lastIndexOf(Object var1) {
         return -1;
      }

      @Override
      public ListIterator<E> listIterator() {
         return iterator;
      }

      @Override
      public ListIterator<E> listIterator(int var1) {
         if (var1 != 0) {
            throw new IndexOutOfBoundsException();
         } else {
            return iterator;
         }
      }

      @Override
      public List<E> subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      @Override
      public boolean addAll(E... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean setAll(E... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean setAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(E... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(E... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void remove(int var1, int var2) {
         throw new UnsupportedOperationException();
      }
   }

   private static class EmptyObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
      public EmptyObservableMap() {
      }

      @Override
      public void addListener(InvalidationListener var1) {
      }

      @Override
      public void removeListener(InvalidationListener var1) {
      }

      @Override
      public void addListener(MapChangeListener<? super K, ? super V> var1) {
      }

      @Override
      public void removeListener(MapChangeListener<? super K, ? super V> var1) {
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public boolean isEmpty() {
         return true;
      }

      @Override
      public boolean containsKey(Object var1) {
         return false;
      }

      @Override
      public boolean containsValue(Object var1) {
         return false;
      }

      @Override
      public V get(Object var1) {
         return null;
      }

      @Override
      public Set<K> keySet() {
         return FXCollections.emptyObservableSet();
      }

      @Override
      public Collection<V> values() {
         return FXCollections.emptyObservableSet();
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return FXCollections.emptyObservableSet();
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof Map && ((Map)var1).isEmpty();
      }

      @Override
      public int hashCode() {
         return 0;
      }
   }

   private static class EmptyObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
      public EmptyObservableSet() {
      }

      @Override
      public void addListener(InvalidationListener var1) {
      }

      @Override
      public void removeListener(InvalidationListener var1) {
      }

      @Override
      public void addListener(SetChangeListener<? super E> var1) {
      }

      @Override
      public void removeListener(SetChangeListener<? super E> var1) {
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public boolean isEmpty() {
         return true;
      }

      @Override
      public boolean contains(Object var1) {
         return false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return var1.isEmpty();
      }

      @Override
      public Object[] toArray() {
         return new Object[0];
      }

      @Override
      public <E> E[] toArray(E[] var1) {
         if (var1.length > 0) {
            var1[0] = null;
         }

         return (E[])var1;
      }

      @Override
      public Iterator<E> iterator() {
         return new Iterator() {
            @Override
            public boolean hasNext() {
               return false;
            }

            @Override
            public Object next() {
               throw new NoSuchElementException();
            }

            @Override
            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }
   }

   private static class SingletonObservableList<E> extends AbstractList<E> implements ObservableList<E> {
      private final E element;

      public SingletonObservableList(E var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.element = (E)var1;
         }
      }

      @Override
      public boolean addAll(E... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean setAll(E... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean setAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(E... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(E... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void remove(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void addListener(InvalidationListener var1) {
      }

      @Override
      public void removeListener(InvalidationListener var1) {
      }

      @Override
      public void addListener(ListChangeListener<? super E> var1) {
      }

      @Override
      public void removeListener(ListChangeListener<? super E> var1) {
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public boolean contains(Object var1) {
         return this.element.equals(var1);
      }

      @Override
      public E get(int var1) {
         if (var1 != 0) {
            throw new IndexOutOfBoundsException();
         } else {
            return this.element;
         }
      }
   }

   private static class SynchronizedCollection<E> implements Collection<E> {
      private final Collection<E> backingCollection;
      final Object mutex;

      SynchronizedCollection(Collection<E> var1, Object var2) {
         this.backingCollection = var1;
         this.mutex = var2;
      }

      SynchronizedCollection(Collection<E> var1) {
         this(var1, new Object());
      }

      @Override
      public int size() {
         synchronized (this.mutex) {
            return this.backingCollection.size();
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.mutex) {
            return this.backingCollection.isEmpty();
         }
      }

      @Override
      public boolean contains(Object var1) {
         synchronized (this.mutex) {
            return this.backingCollection.contains(var1);
         }
      }

      @Override
      public Iterator<E> iterator() {
         return this.backingCollection.iterator();
      }

      @Override
      public Object[] toArray() {
         synchronized (this.mutex) {
            return this.backingCollection.toArray();
         }
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         synchronized (this.mutex) {
            return (T[])this.backingCollection.toArray(var1);
         }
      }

      @Override
      public boolean add(E var1) {
         synchronized (this.mutex) {
            return this.backingCollection.add((E)var1);
         }
      }

      @Override
      public boolean remove(Object var1) {
         synchronized (this.mutex) {
            return this.backingCollection.remove(var1);
         }
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingCollection.containsAll(var1);
         }
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         synchronized (this.mutex) {
            return this.backingCollection.addAll(var1);
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingCollection.removeAll(var1);
         }
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingCollection.retainAll(var1);
         }
      }

      @Override
      public void clear() {
         synchronized (this.mutex) {
            this.backingCollection.clear();
         }
      }
   }

   private static class SynchronizedList<T> implements List<T> {
      final Object mutex;
      private final List<T> backingList;

      SynchronizedList(List<T> var1, Object var2) {
         this.backingList = var1;
         this.mutex = var2;
      }

      SynchronizedList(List<T> var1) {
         this.backingList = var1;
         this.mutex = this;
      }

      @Override
      public int size() {
         synchronized (this.mutex) {
            return this.backingList.size();
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.mutex) {
            return this.backingList.isEmpty();
         }
      }

      @Override
      public boolean contains(Object var1) {
         synchronized (this.mutex) {
            return this.backingList.contains(var1);
         }
      }

      @Override
      public Iterator<T> iterator() {
         return this.backingList.iterator();
      }

      @Override
      public Object[] toArray() {
         synchronized (this.mutex) {
            return this.backingList.toArray();
         }
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         synchronized (this.mutex) {
            return (T[])this.backingList.toArray(var1);
         }
      }

      @Override
      public boolean add(T var1) {
         synchronized (this.mutex) {
            return this.backingList.add((T)var1);
         }
      }

      @Override
      public boolean remove(Object var1) {
         synchronized (this.mutex) {
            return this.backingList.remove(var1);
         }
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingList.containsAll(var1);
         }
      }

      @Override
      public boolean addAll(Collection<? extends T> var1) {
         synchronized (this.mutex) {
            return this.backingList.addAll(var1);
         }
      }

      @Override
      public boolean addAll(int var1, Collection<? extends T> var2) {
         synchronized (this.mutex) {
            return this.backingList.addAll(var1, var2);
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingList.removeAll(var1);
         }
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingList.retainAll(var1);
         }
      }

      @Override
      public void clear() {
         synchronized (this.mutex) {
            this.backingList.clear();
         }
      }

      @Override
      public T get(int var1) {
         synchronized (this.mutex) {
            return this.backingList.get(var1);
         }
      }

      @Override
      public T set(int var1, T var2) {
         synchronized (this.mutex) {
            return this.backingList.set(var1, (T)var2);
         }
      }

      @Override
      public void add(int var1, T var2) {
         synchronized (this.mutex) {
            this.backingList.add(var1, (T)var2);
         }
      }

      @Override
      public T remove(int var1) {
         synchronized (this.mutex) {
            return this.backingList.remove(var1);
         }
      }

      @Override
      public int indexOf(Object var1) {
         synchronized (this.mutex) {
            return this.backingList.indexOf(var1);
         }
      }

      @Override
      public int lastIndexOf(Object var1) {
         synchronized (this.mutex) {
            return this.backingList.lastIndexOf(var1);
         }
      }

      @Override
      public ListIterator<T> listIterator() {
         return this.backingList.listIterator();
      }

      @Override
      public ListIterator<T> listIterator(int var1) {
         synchronized (this.mutex) {
            return this.backingList.listIterator(var1);
         }
      }

      @Override
      public List<T> subList(int var1, int var2) {
         synchronized (this.mutex) {
            return new FXCollections.SynchronizedList<>(this.backingList.subList(var1, var2), this.mutex);
         }
      }

      @Override
      public String toString() {
         synchronized (this.mutex) {
            return this.backingList.toString();
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.backingList.hashCode();
         }
      }

      @Override
      public boolean equals(Object var1) {
         synchronized (this.mutex) {
            return this.backingList.equals(var1);
         }
      }
   }

   private static class SynchronizedMap<K, V> implements Map<K, V> {
      final Object mutex;
      private final Map<K, V> backingMap;
      private transient Set<K> keySet = null;
      private transient Set<Entry<K, V>> entrySet = null;
      private transient Collection<V> values = null;

      SynchronizedMap(Map<K, V> var1) {
         this.backingMap = var1;
         this.mutex = this;
      }

      @Override
      public int size() {
         synchronized (this.mutex) {
            return this.backingMap.size();
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.mutex) {
            return this.backingMap.isEmpty();
         }
      }

      @Override
      public boolean containsKey(Object var1) {
         synchronized (this.mutex) {
            return this.backingMap.containsKey(var1);
         }
      }

      @Override
      public boolean containsValue(Object var1) {
         synchronized (this.mutex) {
            return this.backingMap.containsValue(var1);
         }
      }

      @Override
      public V get(Object var1) {
         synchronized (this.mutex) {
            return this.backingMap.get(var1);
         }
      }

      @Override
      public V put(K var1, V var2) {
         synchronized (this.mutex) {
            return this.backingMap.put((K)var1, (V)var2);
         }
      }

      @Override
      public V remove(Object var1) {
         synchronized (this.mutex) {
            return this.backingMap.remove(var1);
         }
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> var1) {
         synchronized (this.mutex) {
            this.backingMap.putAll(var1);
         }
      }

      @Override
      public void clear() {
         synchronized (this.mutex) {
            this.backingMap.clear();
         }
      }

      @Override
      public Set<K> keySet() {
         synchronized (this.mutex) {
            if (this.keySet == null) {
               this.keySet = new FXCollections.SynchronizedSet<>(this.backingMap.keySet(), this.mutex);
            }

            return this.keySet;
         }
      }

      @Override
      public Collection<V> values() {
         synchronized (this.mutex) {
            if (this.values == null) {
               this.values = new FXCollections.SynchronizedCollection<>(this.backingMap.values(), this.mutex);
            }

            return this.values;
         }
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         synchronized (this.mutex) {
            if (this.entrySet == null) {
               this.entrySet = new FXCollections.SynchronizedSet<>(this.backingMap.entrySet(), this.mutex);
            }

            return this.entrySet;
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized (this.mutex) {
               return this.backingMap.equals(var1);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.backingMap.hashCode();
         }
      }
   }

   private static class SynchronizedObservableList<T> extends FXCollections.SynchronizedList<T> implements ObservableList<T> {
      private ListListenerHelper helper;
      private final ObservableList<T> backingList;
      private final ListChangeListener<T> listener;

      SynchronizedObservableList(ObservableList<T> var1) {
         super(var1);
         this.backingList = var1;
         this.listener = var1x -> ListListenerHelper.fireValueChangedEvent(this.helper, new SourceAdapterChange<>(this, var1x));
         this.backingList.addListener(new WeakListChangeListener<>(this.listener));
      }

      @Override
      public boolean addAll(T... var1) {
         synchronized (this.mutex) {
            return this.backingList.addAll((T[])var1);
         }
      }

      @Override
      public boolean setAll(T... var1) {
         synchronized (this.mutex) {
            return this.backingList.setAll((T[])var1);
         }
      }

      @Override
      public boolean removeAll(T... var1) {
         synchronized (this.mutex) {
            return this.backingList.removeAll((T[])var1);
         }
      }

      @Override
      public boolean retainAll(T... var1) {
         synchronized (this.mutex) {
            return this.backingList.retainAll((T[])var1);
         }
      }

      @Override
      public void remove(int var1, int var2) {
         synchronized (this.mutex) {
            this.backingList.remove(var1, var2);
         }
      }

      @Override
      public boolean setAll(Collection<? extends T> var1) {
         synchronized (this.mutex) {
            return this.backingList.setAll(var1);
         }
      }

      @Override
      public final void addListener(InvalidationListener var1) {
         synchronized (this.mutex) {
            this.helper = ListListenerHelper.addListener(this.helper, var1);
         }
      }

      @Override
      public final void removeListener(InvalidationListener var1) {
         synchronized (this.mutex) {
            this.helper = ListListenerHelper.removeListener(this.helper, var1);
         }
      }

      @Override
      public void addListener(ListChangeListener<? super T> var1) {
         synchronized (this.mutex) {
            this.helper = ListListenerHelper.addListener(this.helper, var1);
         }
      }

      @Override
      public void removeListener(ListChangeListener<? super T> var1) {
         synchronized (this.mutex) {
            this.helper = ListListenerHelper.removeListener(this.helper, var1);
         }
      }
   }

   private static class SynchronizedObservableMap<K, V> extends FXCollections.SynchronizedMap<K, V> implements ObservableMap<K, V> {
      private final ObservableMap<K, V> backingMap;
      private MapListenerHelper listenerHelper;
      private final MapChangeListener<K, V> listener;

      SynchronizedObservableMap(ObservableMap<K, V> var1) {
         super(var1);
         this.backingMap = var1;
         this.listener = var1x -> MapListenerHelper.fireValueChangedEvent(this.listenerHelper, new MapAdapterChange<>(this, var1x));
         this.backingMap.addListener(new WeakMapChangeListener<>(this.listener));
      }

      @Override
      public void addListener(InvalidationListener var1) {
         synchronized (this.mutex) {
            this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, var1);
         }
      }

      @Override
      public void removeListener(InvalidationListener var1) {
         synchronized (this.mutex) {
            this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, var1);
         }
      }

      @Override
      public void addListener(MapChangeListener<? super K, ? super V> var1) {
         synchronized (this.mutex) {
            this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, var1);
         }
      }

      @Override
      public void removeListener(MapChangeListener<? super K, ? super V> var1) {
         synchronized (this.mutex) {
            this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, var1);
         }
      }
   }

   private static class SynchronizedObservableSet<E> extends FXCollections.SynchronizedSet<E> implements ObservableSet<E> {
      private final ObservableSet<E> backingSet;
      private SetListenerHelper listenerHelper;
      private final SetChangeListener<E> listener;

      SynchronizedObservableSet(ObservableSet<E> var1) {
         super(var1);
         this.backingSet = var1;
         this.listener = var1x -> SetListenerHelper.fireValueChangedEvent(this.listenerHelper, new SetAdapterChange<>(this, var1x));
         this.backingSet.addListener(new WeakSetChangeListener<>(this.listener));
      }

      @Override
      public void addListener(InvalidationListener var1) {
         synchronized (this.mutex) {
            this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
         }
      }

      @Override
      public void removeListener(InvalidationListener var1) {
         synchronized (this.mutex) {
            this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, var1);
         }
      }

      @Override
      public void addListener(SetChangeListener<? super E> var1) {
         synchronized (this.mutex) {
            this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
         }
      }

      @Override
      public void removeListener(SetChangeListener<? super E> var1) {
         synchronized (this.mutex) {
            this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, var1);
         }
      }
   }

   private static class SynchronizedSet<E> implements Set<E> {
      final Object mutex;
      private final Set<E> backingSet;

      SynchronizedSet(Set<E> var1, Object var2) {
         this.backingSet = var1;
         this.mutex = var2;
      }

      SynchronizedSet(Set<E> var1) {
         this.backingSet = var1;
         this.mutex = this;
      }

      @Override
      public int size() {
         synchronized (this.mutex) {
            return this.backingSet.size();
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.mutex) {
            return this.backingSet.isEmpty();
         }
      }

      @Override
      public boolean contains(Object var1) {
         synchronized (this.mutex) {
            return this.backingSet.contains(var1);
         }
      }

      @Override
      public Iterator<E> iterator() {
         return this.backingSet.iterator();
      }

      @Override
      public Object[] toArray() {
         synchronized (this.mutex) {
            return this.backingSet.toArray();
         }
      }

      @Override
      public <E> E[] toArray(E[] var1) {
         synchronized (this.mutex) {
            return (E[])this.backingSet.toArray(var1);
         }
      }

      @Override
      public boolean add(E var1) {
         synchronized (this.mutex) {
            return this.backingSet.add((E)var1);
         }
      }

      @Override
      public boolean remove(Object var1) {
         synchronized (this.mutex) {
            return this.backingSet.remove(var1);
         }
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingSet.containsAll(var1);
         }
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         synchronized (this.mutex) {
            return this.backingSet.addAll(var1);
         }
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingSet.retainAll(var1);
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.backingSet.removeAll(var1);
         }
      }

      @Override
      public void clear() {
         synchronized (this.mutex) {
            this.backingSet.clear();
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized (this.mutex) {
               return this.backingSet.equals(var1);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.backingSet.hashCode();
         }
      }
   }

   private static class UnmodifiableObservableListImpl<T> extends ObservableListBase<T> implements ObservableList<T> {
      private final ObservableList<T> backingList;
      private final ListChangeListener<T> listener;

      public UnmodifiableObservableListImpl(ObservableList<T> var1) {
         this.backingList = var1;
         this.listener = var1x -> this.fireChange(new SourceAdapterChange<>(this, var1x));
         this.backingList.addListener(new WeakListChangeListener<>(this.listener));
      }

      @Override
      public T get(int var1) {
         return this.backingList.get(var1);
      }

      @Override
      public int size() {
         return this.backingList.size();
      }

      @Override
      public boolean addAll(T... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean setAll(T... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean setAll(Collection<? extends T> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(T... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(T... var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void remove(int var1, int var2) {
         throw new UnsupportedOperationException();
      }
   }

   private static class UnmodifiableObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
      private final ObservableSet<E> backingSet;
      private SetListenerHelper<E> listenerHelper;
      private SetChangeListener<E> listener;

      public UnmodifiableObservableSet(ObservableSet<E> var1) {
         this.backingSet = var1;
         this.listener = null;
      }

      private void initListener() {
         if (this.listener == null) {
            this.listener = var1 -> this.callObservers(new SetAdapterChange<>(this, var1));
            this.backingSet.addListener(new WeakSetChangeListener<>(this.listener));
         }
      }

      private void callObservers(SetChangeListener.Change<? extends E> var1) {
         SetListenerHelper.fireValueChangedEvent(this.listenerHelper, var1);
      }

      @Override
      public Iterator<E> iterator() {
         return new Iterator<E>() {
            private final Iterator<? extends E> i = UnmodifiableObservableSet.this.backingSet.iterator();

            @Override
            public boolean hasNext() {
               return this.i.hasNext();
            }

            @Override
            public E next() {
               return (E)this.i.next();
            }
         };
      }

      @Override
      public int size() {
         return this.backingSet.size();
      }

      @Override
      public boolean isEmpty() {
         return this.backingSet.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return this.backingSet.contains(var1);
      }

      @Override
      public void addListener(InvalidationListener var1) {
         this.initListener();
         this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
      }

      @Override
      public void removeListener(InvalidationListener var1) {
         this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, var1);
      }

      @Override
      public void addListener(SetChangeListener<? super E> var1) {
         this.initListener();
         this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
      }

      @Override
      public void removeListener(SetChangeListener<? super E> var1) {
         this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, var1);
      }

      @Override
      public boolean add(E var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }
   }
}
