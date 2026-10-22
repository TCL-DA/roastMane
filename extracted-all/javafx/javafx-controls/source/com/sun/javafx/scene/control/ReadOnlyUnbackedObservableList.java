package com.sun.javafx.scene.control;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javafx.collections.ObservableListBase;
import javafx.collections.ListChangeListener.Change;

public abstract class ReadOnlyUnbackedObservableList<E> extends ObservableListBase<E> {
   public abstract E get(int var1);

   public abstract int size();

   public void _beginChange() {
      this.beginChange();
   }

   public void _endChange() {
      this.endChange();
   }

   public void _nextUpdate(int var1) {
      this.nextUpdate(var1);
   }

   public void _nextSet(int var1, E var2) {
      this.nextSet(var1, var2);
   }

   public void _nextReplace(int var1, int var2, List<? extends E> var3) {
      this.nextReplace(var1, var2, var3);
   }

   public void _nextRemove(int var1, List<? extends E> var2) {
      this.nextRemove(var1, var2);
   }

   public void _nextRemove(E var1) {
      int var2 = this.indexOf(var1);
      this._nextRemove(var2, (E)var1);
   }

   public void _nextRemove(int var1, E var2) {
      this.nextRemove(var1, var2);
   }

   public void _nextPermutation(int var1, int var2, int[] var3) {
      this.nextPermutation(var1, var2, var3);
   }

   public void _nextAdd(int var1, int var2) {
      this.nextAdd(var1, var2);
   }

   public void fireChange(Runnable var1) {
      this._beginChange();
      var1.run();
      this._endChange();
   }

   public void callObservers(Change<E> var1) {
      this.fireChange(var1);
   }

   public int indexOf(Object var1) {
      if (var1 == null) {
         return -1;
      } else {
         for (int var2 = 0; var2 < this.size(); var2++) {
            Object var3 = this.get(var2);
            if (var1.equals(var3)) {
               return var2;
            }
         }

         return -1;
      }
   }

   public int lastIndexOf(Object var1) {
      if (var1 == null) {
         return -1;
      } else {
         for (int var2 = this.size() - 1; var2 >= 0; var2--) {
            Object var3 = this.get(var2);
            if (var1.equals(var3)) {
               return var2;
            }
         }

         return -1;
      }
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1) != -1;
   }

   public boolean containsAll(Collection<?> var1) {
      for (Object var3 : var1) {
         if (!this.contains(var3)) {
            return false;
         }
      }

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ListIterator<E> listIterator() {
      return new ReadOnlyUnbackedObservableList.SelectionListIterator<>(this);
   }

   public ListIterator<E> listIterator(int var1) {
      return new ReadOnlyUnbackedObservableList.SelectionListIterator<>(this, var1);
   }

   public Iterator<E> iterator() {
      return new ReadOnlyUnbackedObservableList.SelectionListIterator<>(this);
   }

   public List<E> subList(final int var1, final int var2) {
      if (var1 >= 0 && var2 <= this.size() && var1 <= var2) {
         return new ReadOnlyUnbackedObservableList<E>() {
            @Override
            public E get(int var1x) {
               return (E)ReadOnlyUnbackedObservableList.this.get(var1x + var1);
            }

            @Override
            public int size() {
               return var2 - var1;
            }
         };
      } else {
         throw new IndexOutOfBoundsException("[ fromIndex: " + var1 + ", toIndex: " + var2 + ", size: " + this.size() + " ]");
      }
   }

   public Object[] toArray() {
      Object[] var1 = new Object[this.size()];

      for (int var2 = 0; var2 < this.size(); var2++) {
         var1[var2] = this.get(var2);
      }

      return var1;
   }

   public <T> T[] toArray(T[] var1) {
      Object[] var2 = this.toArray();
      int var3 = var2.length;
      if (var1.length < var3) {
         return (T[])Arrays.copyOf(var2, var3, (Class<? extends T[]>)var1.getClass());
      } else {
         System.arraycopy(var2, 0, var1, 0, var3);
         if (var1.length > var3) {
            var1[var3] = null;
         }

         return (T[])var1;
      }
   }

   public String toString() {
      Iterator var1 = this.iterator();
      if (!var1.hasNext()) {
         return "[]";
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append('[');

         while (true) {
            Object var3 = var1.next();
            var2.append(var3 == this ? "(this Collection)" : var3);
            if (!var1.hasNext()) {
               return var2.append(']').toString();
            }

            var2.append(", ");
         }
      }
   }

   public boolean add(E var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public void add(int var1, E var2) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean addAll(Collection<? extends E> var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean addAll(int var1, Collection<? extends E> var2) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean addAll(E... var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public E set(int var1, E var2) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean setAll(Collection<? extends E> var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean setAll(E... var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public void clear() {
      throw new UnsupportedOperationException("Not supported.");
   }

   public E remove(int var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean remove(Object var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public void remove(int var1, int var2) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean removeAll(E... var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean retainAll(E... var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   private static class SelectionListIterator<E> implements ListIterator<E> {
      private int pos;
      private final ReadOnlyUnbackedObservableList<E> list;

      public SelectionListIterator(ReadOnlyUnbackedObservableList<E> var1) {
         this(var1, 0);
      }

      public SelectionListIterator(ReadOnlyUnbackedObservableList<E> var1, int var2) {
         this.list = var1;
         this.pos = var2;
      }

      @Override
      public boolean hasNext() {
         return this.pos < this.list.size();
      }

      @Override
      public E next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.list.get(this.pos++);
         }
      }

      @Override
      public boolean hasPrevious() {
         return this.pos > 0;
      }

      @Override
      public E previous() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.list.get(--this.pos);
         }
      }

      @Override
      public int nextIndex() {
         return this.pos;
      }

      @Override
      public int previousIndex() {
         return this.pos - 1;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public void set(E var1) {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public void add(E var1) {
         throw new UnsupportedOperationException("Not supported.");
      }
   }
}
