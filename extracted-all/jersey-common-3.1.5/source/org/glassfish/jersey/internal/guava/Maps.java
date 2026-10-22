package org.glassfish.jersey.internal.guava;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Maps {
   private static final Joiner.MapJoiner STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator();

   private Maps() {
   }

   private static <K> Function<Entry<K, ?>, K> keyFunction() {
      return Maps.EntryFunction.KEY;
   }

   private static <V> Function<Entry<?, V>, V> valueFunction() {
      return Maps.EntryFunction.VALUE;
   }

   private static <K, V> Iterator<K> keyIterator(Iterator<Entry<K, V>> entryIterator) {
      return Iterators.transform(entryIterator, keyFunction());
   }

   static <K, V> Iterator<V> valueIterator(Iterator<Entry<K, V>> entryIterator) {
      return Iterators.transform(entryIterator, valueFunction());
   }

   public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
      return new HashMap<>(capacity(expectedSize));
   }

   static int capacity(int expectedSize) {
      if (expectedSize < 3) {
         CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
         return expectedSize + 1;
      } else {
         return expectedSize < 1073741824 ? expectedSize + expectedSize / 3 : Integer.MAX_VALUE;
      }
   }

   static <K, V> Iterator<Entry<K, V>> asMapEntryIterator(Set<K> set, final Function<? super K, V> function) {
      return new TransformedIterator<K, Entry<K, V>>(set.iterator()) {
         Entry<K, V> transform(K key) {
            return Maps.immutableEntry(key, function.apply(key));
         }
      };
   }

   private static <E> Set<E> removeOnlySet(final Set<E> set) {
      return new ForwardingSet<E>() {
         @Override
         protected Set<E> delegate() {
            return set;
         }

         @Override
         public boolean add(E element) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean addAll(Collection<? extends E> es) {
            throw new UnsupportedOperationException();
         }
      };
   }

   private static <E> SortedSet<E> removeOnlySortedSet(final SortedSet<E> set) {
      return new ForwardingSortedSet<E>() {
         @Override
         protected SortedSet<E> delegate() {
            return set;
         }

         @Override
         public boolean add(E element) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean addAll(Collection<? extends E> es) {
            throw new UnsupportedOperationException();
         }

         @Override
         public SortedSet<E> headSet(E toElement) {
            return Maps.removeOnlySortedSet(super.headSet(toElement));
         }

         @Override
         public SortedSet<E> subSet(E fromElement, E toElement) {
            return Maps.removeOnlySortedSet(super.subSet(fromElement, toElement));
         }

         @Override
         public SortedSet<E> tailSet(E fromElement) {
            return Maps.removeOnlySortedSet(super.tailSet(fromElement));
         }
      };
   }

   public static <K, V> Entry<K, V> immutableEntry(K key, V value) {
      return new ImmutableEntry<>(key, value);
   }

   static <K> Predicate<Entry<K, ?>> keyPredicateOnEntries(Predicate<? super K> keyPredicate) {
      return Predicates.compose(keyPredicate, keyFunction());
   }

   static <V> Predicate<Entry<?, V>> valuePredicateOnEntries(Predicate<? super V> valuePredicate) {
      return Predicates.compose(valuePredicate, valueFunction());
   }

   static <V> V safeGet(Map<?, V> map, Object key) {
      Preconditions.checkNotNull(map);

      try {
         return map.get(key);
      } catch (ClassCastException var3) {
         return null;
      } catch (NullPointerException var4) {
         return null;
      }
   }

   static boolean safeContainsKey(Map<?, ?> map, Object key) {
      Preconditions.checkNotNull(map);

      try {
         return map.containsKey(key);
      } catch (ClassCastException var3) {
         return false;
      } catch (NullPointerException var4) {
         return false;
      }
   }

   static <V> V safeRemove(Map<?, V> map, Object key) {
      Preconditions.checkNotNull(map);

      try {
         return map.remove(key);
      } catch (ClassCastException var3) {
         return null;
      } catch (NullPointerException var4) {
         return null;
      }
   }

   private static class AsMapView<K, V> extends Maps.ImprovedAbstractMap<K, V> {
      final Function<? super K, V> function;
      private final Set<K> set;

      AsMapView(Set<K> set, Function<? super K, V> function) {
         this.set = Preconditions.checkNotNull(set);
         this.function = Preconditions.checkNotNull(function);
      }

      Set<K> backingSet() {
         return this.set;
      }

      @Override
      public Set<K> createKeySet() {
         return Maps.removeOnlySet(this.backingSet());
      }

      @Override
      Collection<V> createValues() {
         return Collections2.transform(this.set, this.function);
      }

      @Override
      public int size() {
         return this.backingSet().size();
      }

      @Override
      public boolean containsKey(Object key) {
         return this.backingSet().contains(key);
      }

      @Override
      public V get(Object key) {
         return Collections2.safeContains(this.backingSet(), key) ? this.function.apply((K)key) : null;
      }

      @Override
      public V remove(Object key) {
         return this.backingSet().remove(key) ? this.function.apply((K)key) : null;
      }

      @Override
      public void clear() {
         this.backingSet().clear();
      }

      @Override
      protected Set<Entry<K, V>> createEntrySet() {
         return new Maps.EntrySet<K, V>() {
            @Override
            Map<K, V> map() {
               return AsMapView.this;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
               return Maps.asMapEntryIterator(AsMapView.this.backingSet(), AsMapView.this.function);
            }
         };
      }
   }

   private static enum EntryFunction implements Function<Entry<?, ?>, Object> {
      KEY {
         public Object apply(Entry<?, ?> entry) {
            return entry.getKey();
         }
      },
      VALUE {
         public Object apply(Entry<?, ?> entry) {
            return entry.getValue();
         }
      };

      private EntryFunction() {
      }
   }

   abstract static class EntrySet<K, V> extends Sets.ImprovedAbstractSet<Entry<K, V>> {
      abstract Map<K, V> map();

      @Override
      public int size() {
         return this.map().size();
      }

      @Override
      public void clear() {
         this.map().clear();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> entry = (Entry<?, ?>)o;
            Object key = entry.getKey();
            V value = Maps.safeGet(this.map(), key);
            return Objects.equals(value, entry.getValue()) && (value != null || this.map().containsKey(key));
         }
      }

      @Override
      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      @Override
      public boolean remove(Object o) {
         if (this.contains(o)) {
            Entry<?, ?> entry = (Entry<?, ?>)o;
            return this.map().keySet().remove(entry.getKey());
         } else {
            return false;
         }
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         try {
            return super.removeAll(Preconditions.checkNotNull(c));
         } catch (UnsupportedOperationException var3) {
            return Sets.removeAllImpl(this, c.iterator());
         }
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         try {
            return super.retainAll(Preconditions.checkNotNull(c));
         } catch (UnsupportedOperationException var7) {
            Set<Object> keys = Sets.newHashSetWithExpectedSize(c.size());

            for (Object o : c) {
               if (this.contains(o)) {
                  Entry<?, ?> entry = (Entry<?, ?>)o;
                  keys.add(entry.getKey());
               }
            }

            return this.map().keySet().retainAll(keys);
         }
      }
   }

   abstract static class ImprovedAbstractMap<K, V> extends AbstractMap<K, V> {
      private transient Set<Entry<K, V>> entrySet;
      private transient Set<K> keySet;
      private transient Collection<V> values;

      abstract Set<Entry<K, V>> createEntrySet();

      @Override
      public Set<Entry<K, V>> entrySet() {
         Set<Entry<K, V>> result = this.entrySet;
         return result == null ? (this.entrySet = this.createEntrySet()) : result;
      }

      @Override
      public Set<K> keySet() {
         Set<K> result = this.keySet;
         return result == null ? (this.keySet = this.createKeySet()) : result;
      }

      Set<K> createKeySet() {
         return new Maps.KeySet<>(this);
      }

      @Override
      public Collection<V> values() {
         Collection<V> result = this.values;
         return result == null ? (this.values = this.createValues()) : result;
      }

      Collection<V> createValues() {
         return new Maps.Values<>(this);
      }
   }

   static class KeySet<K, V> extends Sets.ImprovedAbstractSet<K> {
      final Map<K, V> map;

      KeySet(Map<K, V> map) {
         this.map = Preconditions.checkNotNull(map);
      }

      Map<K, V> map() {
         return this.map;
      }

      @Override
      public Iterator<K> iterator() {
         return Maps.keyIterator(this.map().entrySet().iterator());
      }

      @Override
      public int size() {
         return this.map().size();
      }

      @Override
      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      @Override
      public boolean contains(Object o) {
         return this.map().containsKey(o);
      }

      @Override
      public boolean remove(Object o) {
         if (this.contains(o)) {
            this.map().remove(o);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void clear() {
         this.map().clear();
      }
   }

   static class Values<K, V> extends AbstractCollection<V> {
      final Map<K, V> map;

      Values(Map<K, V> map) {
         this.map = Preconditions.checkNotNull(map);
      }

      final Map<K, V> map() {
         return this.map;
      }

      @Override
      public Iterator<V> iterator() {
         return Maps.valueIterator(this.map().entrySet().iterator());
      }

      @Override
      public boolean remove(Object o) {
         try {
            return super.remove(o);
         } catch (UnsupportedOperationException var5) {
            for (Entry<K, V> entry : this.map().entrySet()) {
               if (Objects.equals(o, entry.getValue())) {
                  this.map().remove(entry.getKey());
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         try {
            return super.removeAll(Preconditions.checkNotNull(c));
         } catch (UnsupportedOperationException var6) {
            Set<K> toRemove = Sets.newHashSet();

            for (Entry<K, V> entry : this.map().entrySet()) {
               if (c.contains(entry.getValue())) {
                  toRemove.add(entry.getKey());
               }
            }

            return this.map().keySet().removeAll(toRemove);
         }
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         try {
            return super.retainAll(Preconditions.checkNotNull(c));
         } catch (UnsupportedOperationException var6) {
            Set<K> toRetain = Sets.newHashSet();

            for (Entry<K, V> entry : this.map().entrySet()) {
               if (c.contains(entry.getValue())) {
                  toRetain.add(entry.getKey());
               }
            }

            return this.map().keySet().retainAll(toRetain);
         }
      }

      @Override
      public int size() {
         return this.map().size();
      }

      @Override
      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      @Override
      public boolean contains(Object o) {
         return this.map().containsValue(o);
      }

      @Override
      public void clear() {
         this.map().clear();
      }
   }
}
