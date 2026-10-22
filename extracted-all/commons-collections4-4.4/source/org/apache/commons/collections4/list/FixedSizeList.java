package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import org.apache.commons.collections4.BoundedCollection;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

public class FixedSizeList<E> extends AbstractSerializableListDecorator<E> implements BoundedCollection<E> {
   private static final long serialVersionUID = -2218010673611160319L;

   public static <E> FixedSizeList<E> fixedSizeList(List<E> list) {
      return new FixedSizeList<>(list);
   }

   protected FixedSizeList(List<E> list) {
      super(list);
   }

   @Override
   public boolean add(E object) {
      throw unsupportedOperationException();
   }

   @Override
   public void add(int index, E object) {
      throw unsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      throw unsupportedOperationException();
   }

   @Override
   public boolean addAll(int index, Collection<? extends E> coll) {
      throw unsupportedOperationException();
   }

   @Override
   public void clear() {
      throw unsupportedOperationException();
   }

   @Override
   public E get(int index) {
      return this.decorated().get(index);
   }

   @Override
   public int indexOf(Object object) {
      return this.decorated().indexOf(object);
   }

   @Override
   public Iterator<E> iterator() {
      return UnmodifiableIterator.unmodifiableIterator(this.decorated().iterator());
   }

   @Override
   public int lastIndexOf(Object object) {
      return this.decorated().lastIndexOf(object);
   }

   @Override
   public ListIterator<E> listIterator() {
      return new FixedSizeList.FixedSizeListIterator(this.decorated().listIterator(0));
   }

   @Override
   public ListIterator<E> listIterator(int index) {
      return new FixedSizeList.FixedSizeListIterator(this.decorated().listIterator(index));
   }

   @Override
   public E remove(int index) {
      throw unsupportedOperationException();
   }

   @Override
   public boolean remove(Object object) {
      throw unsupportedOperationException();
   }

   @Override
   public boolean removeIf(Predicate<? super E> filter) {
      throw unsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      throw unsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> coll) {
      throw unsupportedOperationException();
   }

   @Override
   public E set(int index, E object) {
      return this.decorated().set(index, object);
   }

   @Override
   public List<E> subList(int fromIndex, int toIndex) {
      List<E> sub = this.decorated().subList(fromIndex, toIndex);
      return new FixedSizeList<>(sub);
   }

   @Override
   public boolean isFull() {
      return true;
   }

   @Override
   public int maxSize() {
      return this.size();
   }

   private static UnsupportedOperationException unsupportedOperationException() {
      return new UnsupportedOperationException("List is fixed size");
   }

   private class FixedSizeListIterator extends AbstractListIteratorDecorator<E> {
      protected FixedSizeListIterator(ListIterator<E> iterator) {
         super(iterator);
      }

      @Override
      public void remove() {
         throw FixedSizeList.unsupportedOperationException();
      }

      @Override
      public void add(Object object) {
         throw FixedSizeList.unsupportedOperationException();
      }
   }
}
