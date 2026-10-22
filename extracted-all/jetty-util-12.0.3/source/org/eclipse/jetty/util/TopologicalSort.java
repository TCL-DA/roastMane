package org.eclipse.jetty.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TopologicalSort<T> {
   private final Map<T, Set<T>> _dependencies = new HashMap<>();

   public void addDependency(T dependent, T... dependency) {
      Set<T> set = this._dependencies.get(dependent);
      if (set == null) {
         set = new HashSet<>();
         this._dependencies.put(dependent, set);
      }

      for (T d : dependency) {
         set.add(d);
      }
   }

   public void addBeforeAfter(T before, T after) {
      this.addDependency(after, before);
   }

   public void sort(T[] array) {
      List<T> sorted = new ArrayList<>();
      Set<T> visited = new HashSet<>();
      Comparator<T> comparator = new TopologicalSort.InitialOrderComparator<>(array);

      for (T t : array) {
         this.visit(t, visited, sorted, comparator);
      }

      sorted.toArray(array);
   }

   public void sort(Collection<T> list) {
      List<T> sorted = new ArrayList<>();
      Set<T> visited = new HashSet<>();
      Comparator<T> comparator = new TopologicalSort.InitialOrderComparator<>(list);

      for (T t : list) {
         this.visit(t, visited, sorted, comparator);
      }

      list.clear();
      list.addAll(sorted);
   }

   private void visit(T item, Set<T> visited, List<T> sorted, Comparator<T> comparator) {
      if (!visited.contains(item)) {
         visited.add(item);
         Set<T> dependencies = this._dependencies.get(item);
         if (dependencies != null) {
            SortedSet<T> orderedDeps = new TreeSet<>(comparator);
            orderedDeps.addAll(dependencies);

            try {
               for (T d : orderedDeps) {
                  this.visit(d, visited, sorted, comparator);
               }
            } catch (TopologicalSort.CyclicException var9) {
               throw new TopologicalSort.CyclicException(item, var9);
            }
         }

         sorted.add(item);
      } else if (!sorted.contains(item)) {
         throw new TopologicalSort.CyclicException(item);
      }
   }

   @Override
   public String toString() {
      return "TopologicalSort " + this._dependencies;
   }

   private static class CyclicException extends IllegalStateException {
      CyclicException(Object item) {
         super("cyclic at " + item);
      }

      CyclicException(Object item, TopologicalSort.CyclicException e) {
         super("cyclic at " + item, e);
      }
   }

   private static class InitialOrderComparator<T> implements Comparator<T> {
      private final Map<T, Integer> _indexes = new HashMap<>();

      InitialOrderComparator(T[] initial) {
         int i = 0;

         for (T t : initial) {
            this._indexes.put(t, i++);
         }
      }

      InitialOrderComparator(Collection<T> initial) {
         int i = 0;

         for (T t : initial) {
            this._indexes.put(t, i++);
         }
      }

      @Override
      public int compare(T o1, T o2) {
         Integer i1 = this._indexes.get(o1);
         Integer i2 = this._indexes.get(o2);
         if (i1 != null && i2 != null && !i1.equals(o2)) {
            return i1 < i2 ? -1 : 1;
         } else {
            return 0;
         }
      }
   }
}
