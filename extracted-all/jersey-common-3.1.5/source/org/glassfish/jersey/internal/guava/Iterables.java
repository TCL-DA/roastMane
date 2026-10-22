package org.glassfish.jersey.internal.guava;

final class Iterables {
   private Iterables() {
   }

   public static <T> T getFirst(Iterable<? extends T> iterable, T defaultValue) {
      return Iterators.getNext(iterable.iterator(), defaultValue);
   }
}
