package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Iterators {
   private static final UnmodifiableListIterator<Object> EMPTY_LIST_ITERATOR = new UnmodifiableListIterator<Object>() {
      @Override
      public boolean hasNext() {
         return false;
      }

      @Override
      public Object next() {
         throw new NoSuchElementException();
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
   };
   private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>() {
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
         CollectPreconditions.checkRemove(false);
      }
   };

   private Iterators() {
   }

   @Deprecated
   public static <T> UnmodifiableIterator<T> emptyIterator() {
      return emptyListIterator();
   }

   private static <T> UnmodifiableListIterator<T> emptyListIterator() {
      return (UnmodifiableListIterator<T>)EMPTY_LIST_ITERATOR;
   }

   static <T> Iterator<T> emptyModifiableIterator() {
      return (Iterator<T>)EMPTY_MODIFIABLE_ITERATOR;
   }

   public static <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<T> iterator) {
      Preconditions.checkNotNull(iterator);
      return iterator instanceof UnmodifiableIterator ? (UnmodifiableIterator)iterator : new UnmodifiableIterator<T>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public T next() {
            return iterator.next();
         }
      };
   }

   public static int size(Iterator<?> iterator) {
      int count;
      for (count = 0; iterator.hasNext(); count++) {
         iterator.next();
      }

      return count;
   }

   public static boolean removeAll(Iterator<?> removeFrom, Collection<?> elementsToRemove) {
      return removeIf(removeFrom, Predicates.in(elementsToRemove));
   }

   public static <T> boolean removeIf(Iterator<T> removeFrom, Predicate<? super T> predicate) {
      Preconditions.checkNotNull(predicate);
      boolean modified = false;

      while (removeFrom.hasNext()) {
         if (predicate.test(removeFrom.next())) {
            removeFrom.remove();
            modified = true;
         }
      }

      return modified;
   }

   public static boolean elementsEqual(Iterator<?> iterator1, Iterator<?> iterator2) {
      while (iterator1.hasNext()) {
         if (!iterator2.hasNext()) {
            return false;
         }

         Object o1 = iterator1.next();
         Object o2 = iterator2.next();
         if (!Objects.equals(o1, o2)) {
            return false;
         }
      }

      return !iterator2.hasNext();
   }

   public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
      Preconditions.checkNotNull(addTo);
      Preconditions.checkNotNull(iterator);
      boolean wasModified = false;

      while (iterator.hasNext()) {
         wasModified |= addTo.add((T)iterator.next());
      }

      return wasModified;
   }

   public static <T> boolean all(Iterator<T> iterator, Predicate<? super T> predicate) {
      Preconditions.checkNotNull(predicate);

      while (iterator.hasNext()) {
         T element = iterator.next();
         if (!predicate.test(element)) {
            return false;
         }
      }

      return true;
   }

   private static <T> int indexOf(Iterator<T> iterator, Predicate<? super T> predicate) {
      Preconditions.checkNotNull(predicate, "predicate");

      for (int i = 0; iterator.hasNext(); i++) {
         T current = iterator.next();
         if (predicate.test(current)) {
            return i;
         }
      }

      return -1;
   }

   public static <F, T> Iterator<T> transform(Iterator<F> fromIterator, final Function<? super F, ? extends T> function) {
      Preconditions.checkNotNull(function);
      return new TransformedIterator<F, T>(fromIterator) {
         @Override
         T transform(F from) {
            return (T)function.apply(from);
         }
      };
   }

   public static <T> T getNext(Iterator<? extends T> iterator, T defaultValue) {
      return (T)(iterator.hasNext() ? iterator.next() : defaultValue);
   }

   static <T> T pollNext(Iterator<T> iterator) {
      if (iterator.hasNext()) {
         T result = iterator.next();
         iterator.remove();
         return result;
      } else {
         return null;
      }
   }

   static void clear(Iterator<?> iterator) {
      Preconditions.checkNotNull(iterator);

      while (iterator.hasNext()) {
         iterator.next();
         iterator.remove();
      }
   }

   public static <T> UnmodifiableIterator<T> forArray(T... array) {
      return forArray(array, 0, array.length, 0);
   }

   static <T> UnmodifiableListIterator<T> forArray(final T[] array, final int offset, int length, int index) {
      Preconditions.checkArgument(length >= 0);
      int end = offset + length;
      Preconditions.checkPositionIndexes(offset, end, array.length);
      Preconditions.checkPositionIndex(index, length);
      return (UnmodifiableListIterator<T>)(length == 0 ? emptyListIterator() : new AbstractIndexedListIterator<T>(length, index) {
         @Override
         protected T get(int index) {
            return array[offset + index];
         }
      });
   }

   public static <T> UnmodifiableIterator<T> singletonIterator(final T value) {
      return new UnmodifiableIterator<T>() {
         boolean done;

         @Override
         public boolean hasNext() {
            return !this.done;
         }

         @Override
         public T next() {
            if (this.done) {
               throw new NoSuchElementException();
            } else {
               this.done = true;
               return value;
            }
         }
      };
   }

   public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> iterator) {
      return iterator instanceof Iterators.PeekingImpl ? (Iterators.PeekingImpl)iterator : new Iterators.PeekingImpl<>(iterator);
   }

   private static class PeekingImpl<E> implements PeekingIterator<E> {
      private final Iterator<? extends E> iterator;
      private boolean hasPeeked;
      private E peekedElement;

      public PeekingImpl(Iterator<? extends E> iterator) {
         this.iterator = Preconditions.checkNotNull(iterator);
      }

      @Override
      public boolean hasNext() {
         return this.hasPeeked || this.iterator.hasNext();
      }

      @Override
      public E next() {
         if (!this.hasPeeked) {
            return (E)this.iterator.next();
         } else {
            E result = this.peekedElement;
            this.hasPeeked = false;
            this.peekedElement = null;
            return result;
         }
      }

      @Override
      public void remove() {
         Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
         this.iterator.remove();
      }

      @Override
      public E peek() {
         if (!this.hasPeeked) {
            this.peekedElement = (E)this.iterator.next();
            this.hasPeeked = true;
         }

         return this.peekedElement;
      }
   }
}
