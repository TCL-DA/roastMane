package org.glassfish.jersey.internal.util.collection;

import java.util.AbstractMap;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.guava.Preconditions;

public class Views {
   private Views() {
   }

   public static <T, R> List<T> listView(final List<R> originalList, final Function<R, T> transformer) {
      return new AbstractSequentialList<T>() {
         @Override
         public ListIterator<T> listIterator(final int index) {
            return new ListIterator<T>() {
               final ListIterator<R> iterator = originalList.listIterator(index);

               @Override
               public boolean hasNext() {
                  return this.iterator.hasNext();
               }

               @Override
               public T next() {
                  return transformer.apply(this.iterator.next());
               }

               @Override
               public boolean hasPrevious() {
                  return this.iterator.hasPrevious();
               }

               @Override
               public T previous() {
                  return transformer.apply(this.iterator.previous());
               }

               @Override
               public int nextIndex() {
                  return this.iterator.nextIndex();
               }

               @Override
               public int previousIndex() {
                  return this.iterator.previousIndex();
               }

               @Override
               public void remove() {
                  this.iterator.remove();
               }

               @Override
               public void set(T t) {
                  throw new UnsupportedOperationException("Not supported.");
               }

               @Override
               public void add(T t) {
                  throw new UnsupportedOperationException("Not supported.");
               }
            };
         }

         @Override
         public int size() {
            return originalList.size();
         }
      };
   }

   public static <K, V, O> Map<K, V> mapView(final Map<K, O> originalMap, final Function<O, V> valuesTransformer) {
      return new AbstractMap<K, V>() {
         @Override
         public Set<Entry<K, V>> entrySet() {
            return new AbstractSet<Entry<K, V>>() {
               Set<Entry<K, O>> originalSet = originalMap.entrySet();
               Iterator<Entry<K, O>> original = <unrepresentable>.super.originalSet.iterator();

               @Override
               public Iterator<Entry<K, V>> iterator() {
                  return new Iterator<Entry<K, V>>() {
                     @Override
                     public boolean hasNext() {
                        return original.hasNext();
                     }

                     public Entry<K, V> next() {
                        final Entry<K, O> next = original.next();
                        return new Entry<K, V>() {
                           @Override
                           public K getKey() {
                              return next.getKey();
                           }

                           @Override
                           public V getValue() {
                              return valuesTransformer.apply(next.getValue());
                           }

                           @Override
                           public V setValue(V value) {
                              throw new UnsupportedOperationException("Not supported.");
                           }
                        };
                     }

                     @Override
                     public void remove() {
                        original.remove();
                     }
                  };
               }

               @Override
               public int size() {
                  return this.originalSet.size();
               }
            };
         }
      };
   }

   public static <E> Set<E> setUnionView(final Set<? extends E> set1, final Set<? extends E> set2) {
      Preconditions.checkNotNull(set1, "set1");
      Preconditions.checkNotNull(set2, "set2");
      return new AbstractSet<E>() {
         @Override
         public Iterator<E> iterator() {
            return this.getUnion(set1, set2).iterator();
         }

         @Override
         public int size() {
            return this.getUnion(set1, set2).size();
         }

         private Set<E> getUnion(Set<? extends E> set1x, Set<? extends E> set2x) {
            HashSet<E> hashSet = new HashSet<>(set1);
            hashSet.addAll(set2);
            return hashSet;
         }
      };
   }

   public static <E> Set<E> setDiffView(final Set<? extends E> set1, final Set<? extends E> set2) {
      Preconditions.checkNotNull(set1, "set1");
      Preconditions.checkNotNull(set2, "set2");
      return new AbstractSet<E>() {
         @Override
         public Iterator<E> iterator() {
            return this.getDiff(set1, set2).iterator();
         }

         @Override
         public int size() {
            return this.getDiff(set1, set2).size();
         }

         private Set<E> getDiff(final Set<? extends E> set1x, final Set<? extends E> set2x) {
            HashSet<E> hashSet = new HashSet<>();
            hashSet.addAll(set1);
            hashSet.addAll(set2);
            return hashSet.stream().filter(new Predicate<E>() {
               @Override
               public boolean test(E e) {
                  return set1.contains(e) && !set2.contains(e);
               }
            }).collect(Collectors.toSet());
         }
      };
   }
}
