package org.glassfish.jersey.internal.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

public class ImmutableCollectors {
   public static <T> Collector<T, List<T>, List<T>> toImmutableList() {
      return Collector.of(ArrayList::new, List::add, (left, right) -> {
         left.addAll(right);
         return left;
      }, Collections::unmodifiableList);
   }

   public static <T> Collector<T, Set<T>, Set<T>> toImmutableSet() {
      return Collector.of(HashSet::new, Set::add, (left, right) -> {
         left.addAll(right);
         return left;
      }, Collections::unmodifiableSet);
   }

   public static <T> Collector<T, Set<T>, Set<T>> toImmutableLinkedSet() {
      return Collector.of(LinkedHashSet::new, Set::add, (left, right) -> {
         left.addAll(right);
         return left;
      }, Collections::unmodifiableSet);
   }
}
