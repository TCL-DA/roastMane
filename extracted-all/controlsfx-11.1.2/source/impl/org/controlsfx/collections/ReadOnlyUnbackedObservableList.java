package impl.org.controlsfx.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javafx.collections.ObservableListBase;
import javafx.collections.ListChangeListener.Change;

public abstract class ReadOnlyUnbackedObservableList<E> extends ObservableListBase<E> {
   public void callObservers(Change<E> c) {
      this.fireChange(c);
   }

   public abstract E get(int var1);

   public abstract int size();

   public int indexOf(Object o) {
      if (o == null) {
         return -1;
      } else {
         for (int i = 0; i < this.size(); i++) {
            Object obj = this.get(i);
            if (o.equals(obj)) {
               return i;
            }
         }

         return -1;
      }
   }

   public int lastIndexOf(Object o) {
      if (o == null) {
         return -1;
      } else {
         for (int i = this.size() - 1; i >= 0; i--) {
            Object obj = this.get(i);
            if (o.equals(obj)) {
               return i;
            }
         }

         return -1;
      }
   }

   public boolean contains(Object o) {
      return this.indexOf(o) != -1;
   }

   public boolean containsAll(Collection<?> c) {
      for (Object o : c) {
         if (!this.contains(o)) {
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

   public ListIterator<E> listIterator(int index) {
      return new ReadOnlyUnbackedObservableList.SelectionListIterator<>(this, index);
   }

   public Iterator<E> iterator() {
      return new ReadOnlyUnbackedObservableList.SelectionListIterator<>(this);
   }

   public List<E> subList(final int fromIndex, final int toIndex) {
      if (fromIndex >= 0 && toIndex <= this.size() && fromIndex <= toIndex) {
         final List<E> outer = this;
         return new ReadOnlyUnbackedObservableList<E>() {
            @Override
            public E get(int i) {
               return outer.get(i + fromIndex);
            }

            @Override
            public int size() {
               return toIndex - fromIndex;
            }
         };
      } else {
         throw new IndexOutOfBoundsException("[ fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", size: " + this.size() + " ]");
      }
   }

   public Object[] toArray() {
      Object[] arr = new Object[this.size()];

      for (int i = 0; i < this.size(); i++) {
         arr[i] = this.get(i);
      }

      return arr;
   }

   public <T> T[] toArray(T[] a) {
      Object[] elementData = this.toArray();
      int size = elementData.length;
      if (a.length < size) {
         return (T[])Arrays.copyOf(elementData, size, (Class<? extends T[]>)a.getClass());
      } else {
         System.arraycopy(elementData, 0, a, 0, size);
         if (a.length > size) {
            a[size] = null;
         }

         return a;
      }
   }

   public String toString() {
      Iterator<E> i = this.iterator();
      if (!i.hasNext()) {
         return "[]";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append('[');

         while (true) {
            E e = i.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (!i.hasNext()) {
               return sb.append(']').toString();
            }

            sb.append(", ");
         }
      }
   }

   public boolean add(E e) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public void add(int index, E element) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean addAll(Collection<? extends E> c) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean addAll(int index, Collection<? extends E> c) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean addAll(E... elements) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public E set(int index, E element) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean setAll(Collection<? extends E> col) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean setAll(E... elements) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public void clear() {
      throw new UnsupportedOperationException("Not supported.");
   }

   public E remove(int index) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean remove(Object o) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public void remove(int from, int to) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean removeAll(E... elements) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public boolean retainAll(E... elements) {
      throw new UnsupportedOperationException("Not supported.");
   }

   private static class SelectionListIterator<E> implements ListIterator<E> {
      private int index;
      private final ReadOnlyUnbackedObservableList<E> list;

      public SelectionListIterator(ReadOnlyUnbackedObservableList<E> list) {
         this(list, 0);
      }

      public SelectionListIterator(ReadOnlyUnbackedObservableList<E> list, int index) {
         this.list = list;
         this.index = index;
      }

      @Override
      public boolean hasNext() {
         return this.index < this.list.size();
      }

      @Override
      public E next() {
         return this.list.get(this.index++);
      }

      @Override
      public boolean hasPrevious() {
         return this.index > 0;
      }

      @Override
      public E previous() {
         return this.list.get(this.index--);
      }

      @Override
      public int nextIndex() {
         return this.index + 1;
      }

      @Override
      public int previousIndex() {
         return this.index - 1;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public void set(E e) {
         throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public void add(E e) {
         throw new UnsupportedOperationException("Not supported.");
      }
   }
}
