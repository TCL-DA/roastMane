package com.cropster.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

public abstract class Collect {
   public static <T> T first(Collection<T> collection) {
      return !collection.isEmpty() ? collection.iterator().next() : null;
   }

   public static <T> T first(List<T> list) {
      return !list.isEmpty() ? list.get(0) : null;
   }

   public static <T> T unique(Collection<T> collection) {
      if (collection.size() > 1) {
         throw new IllegalArgumentException("Collection size > 1");
      } else {
         return first(collection);
      }
   }

   public static <T> T unique(List<T> list) {
      if (list.size() > 1) {
         throw new IllegalArgumentException("Collection size > 1");
      } else {
         return first(list);
      }
   }

   public static <T> T last(List<T> list) {
      return !list.isEmpty() ? list.get(list.size() - 1) : null;
   }

   public static <T> T last(Iterable<T> iterable) {
      return iterable instanceof List ? last((List<T>)iterable) : last(iterable.iterator());
   }

   public static <T> T last(Iterator<T> iterator) {
      if (!iterator.hasNext()) {
         return null;
      } else {
         T current;
         do {
            current = iterator.next();
         } while (iterator.hasNext());

         return current;
      }
   }

   public static Long proximate(SortedMap<Long, ?> map, Long key, Long maxDeviation) {
      if (map.containsKey(key)) {
         return key;
      } else {
         Long predecessor = null;
         Long successor = null;
         long deviationPredecessor = 0L;
         long deviationSuccessor = 0L;
         SortedMap<Long, ?> headMap = map.headMap(key);
         if (!headMap.isEmpty()) {
            predecessor = headMap.lastKey();
            deviationPredecessor = key - predecessor;
            if (deviationPredecessor > maxDeviation) {
               predecessor = null;
               deviationPredecessor = 0L;
            }
         }

         SortedMap<Long, ?> tailMap = map.tailMap(key);
         if (!tailMap.isEmpty()) {
            successor = tailMap.firstKey();
            deviationSuccessor = successor - key;
            if (deviationSuccessor > maxDeviation) {
               successor = null;
               deviationSuccessor = 0L;
            }
         }

         if (predecessor == null) {
            return successor;
         } else if (successor == null) {
            return predecessor;
         } else {
            return deviationSuccessor <= deviationPredecessor ? successor : predecessor;
         }
      }
   }

   public static Double sum(Collection<Double> values) {
      double sum = 0.0;

      for (Double value : values) {
         sum += value;
      }

      return sum;
   }

   public static Double avg(Collection<Double> values) {
      return sum(values) / (double)values.size();
   }
}
