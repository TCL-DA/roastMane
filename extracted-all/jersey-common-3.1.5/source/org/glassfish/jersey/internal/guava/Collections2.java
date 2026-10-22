package org.glassfish.jersey.internal.guava;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

final class Collections2 {
   static final Joiner STANDARD_JOINER = Joiner.on();

   private Collections2() {
   }

   static boolean safeContains(Collection<?> collection, Object object) {
      Preconditions.checkNotNull(collection);

      try {
         return collection.contains(object);
      } catch (ClassCastException var3) {
         return false;
      } catch (NullPointerException var4) {
         return false;
      }
   }

   static boolean safeRemove(Collection<?> collection, Object object) {
      Preconditions.checkNotNull(collection);

      try {
         return collection.remove(object);
      } catch (ClassCastException var3) {
         return false;
      } catch (NullPointerException var4) {
         return false;
      }
   }

   public static <F, T> Collection<T> transform(Collection<F> fromCollection, Function<? super F, T> function) {
      return new Collections2.TransformedCollection<>(fromCollection, function);
   }

   static StringBuilder newStringBuilderForCollection(int size) {
      CollectPreconditions.checkNonnegative(size, "size");
      return new StringBuilder((int)Math.min((long)size * 8L, 1073741824L));
   }

   static <T> Collection<T> cast(Iterable<T> iterable) {
      return (Collection<T>)iterable;
   }

   static class TransformedCollection<F, T> extends AbstractCollection<T> {
      final Collection<F> fromCollection;
      final Function<? super F, ? extends T> function;

      TransformedCollection(Collection<F> fromCollection, Function<? super F, ? extends T> function) {
         this.fromCollection = Preconditions.checkNotNull(fromCollection);
         this.function = Preconditions.checkNotNull(function);
      }

      @Override
      public void clear() {
         this.fromCollection.clear();
      }

      @Override
      public boolean isEmpty() {
         return this.fromCollection.isEmpty();
      }

      @Override
      public Iterator<T> iterator() {
         return Iterators.transform(this.fromCollection.iterator(), this.function);
      }

      @Override
      public int size() {
         return this.fromCollection.size();
      }
   }
}
