package org.glassfish.jersey.internal.guava;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class Lists {
   private Lists() {
   }

   private static <E> ArrayList<E> newArrayList() {
      return new ArrayList<>();
   }

   public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
      Preconditions.checkNotNull(elements);
      return elements instanceof Collection ? new ArrayList<>(Collections2.cast(elements)) : newArrayList(elements.iterator());
   }

   public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
      ArrayList<E> list = newArrayList();
      Iterators.addAll(list, elements);
      return list;
   }

   private static <T> List<T> reverse(List<T> list) {
      return (List<T>)(list instanceof Lists.ReverseList ? ((Lists.ReverseList)list).getForwardList() : new Lists.ReverseList<>(list));
   }

   static boolean equalsImpl(List<?> list, Object object) {
      if (object == Preconditions.<List<?>>checkNotNull(list)) {
         return true;
      } else if (!(object instanceof List)) {
         return false;
      } else {
         List<?> o = (List<?>)object;
         return list.size() == o.size() && Iterators.elementsEqual(list.iterator(), o.iterator());
      }
   }

   static int indexOfImpl(List<?> list, Object element) {
      ListIterator<?> listIterator = list.listIterator();

      while (listIterator.hasNext()) {
         if (Objects.equals(element, listIterator.next())) {
            return listIterator.previousIndex();
         }
      }

      return -1;
   }

   static int lastIndexOfImpl(List<?> list, Object element) {
      ListIterator<?> listIterator = list.listIterator(list.size());

      while (listIterator.hasPrevious()) {
         if (Objects.equals(element, listIterator.previous())) {
            return listIterator.nextIndex();
         }
      }

      return -1;
   }

   private static class ReverseList<T> extends AbstractList<T> {
      private final List<T> forwardList;

      ReverseList(List<T> forwardList) {
         this.forwardList = Preconditions.checkNotNull(forwardList);
      }

      List<T> getForwardList() {
         return this.forwardList;
      }

      private int reverseIndex(int index) {
         int size = this.size();
         Preconditions.checkElementIndex(index, size);
         return size - 1 - index;
      }

      private int reversePosition(int index) {
         int size = this.size();
         Preconditions.checkPositionIndex(index, size);
         return size - index;
      }

      @Override
      public void add(int index, T element) {
         this.forwardList.add(this.reversePosition(index), element);
      }

      @Override
      public void clear() {
         this.forwardList.clear();
      }

      @Override
      public T remove(int index) {
         return this.forwardList.remove(this.reverseIndex(index));
      }

      @Override
      protected void removeRange(int fromIndex, int toIndex) {
         this.subList(fromIndex, toIndex).clear();
      }

      @Override
      public T set(int index, T element) {
         return this.forwardList.set(this.reverseIndex(index), element);
      }

      @Override
      public T get(int index) {
         return this.forwardList.get(this.reverseIndex(index));
      }

      @Override
      public int size() {
         return this.forwardList.size();
      }

      @Override
      public List<T> subList(int fromIndex, int toIndex) {
         Preconditions.checkPositionIndexes(fromIndex, toIndex, this.size());
         return Lists.reverse(this.forwardList.subList(this.reversePosition(toIndex), this.reversePosition(fromIndex)));
      }

      @Override
      public Iterator<T> iterator() {
         return this.listIterator();
      }

      @Override
      public ListIterator<T> listIterator(int index) {
         int start = this.reversePosition(index);
         final ListIterator<T> forwardIterator = this.forwardList.listIterator(start);
         return new ListIterator<T>() {
            boolean canRemoveOrSet;

            @Override
            public void add(T e) {
               forwardIterator.add(e);
               forwardIterator.previous();
               this.canRemoveOrSet = false;
            }

            @Override
            public boolean hasNext() {
               return forwardIterator.hasPrevious();
            }

            @Override
            public boolean hasPrevious() {
               return forwardIterator.hasNext();
            }

            @Override
            public T next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.canRemoveOrSet = true;
                  return forwardIterator.previous();
               }
            }

            @Override
            public int nextIndex() {
               return ReverseList.this.reversePosition(forwardIterator.nextIndex());
            }

            @Override
            public T previous() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  this.canRemoveOrSet = true;
                  return forwardIterator.next();
               }
            }

            @Override
            public int previousIndex() {
               return this.nextIndex() - 1;
            }

            @Override
            public void remove() {
               CollectPreconditions.checkRemove(this.canRemoveOrSet);
               forwardIterator.remove();
               this.canRemoveOrSet = false;
            }

            @Override
            public void set(T e) {
               Preconditions.checkState(this.canRemoveOrSet);
               forwardIterator.set(e);
            }
         };
      }
   }
}
