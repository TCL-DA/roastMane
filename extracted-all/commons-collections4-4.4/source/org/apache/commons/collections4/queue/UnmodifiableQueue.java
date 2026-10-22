package org.apache.commons.collections4.queue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.Predicate;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

public final class UnmodifiableQueue<E> extends AbstractQueueDecorator<E> implements Unmodifiable {
   private static final long serialVersionUID = 1832948656215393357L;

   public static <E> Queue<E> unmodifiableQueue(Queue<? extends E> queue) {
      return (Queue<E>)(queue instanceof Unmodifiable ? queue : new UnmodifiableQueue<>(queue));
   }

   private UnmodifiableQueue(Queue<? extends E> queue) {
      super((Queue<E>)queue);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.decorated());
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.setCollection((Collection<E>)in.readObject());
   }

   @Override
   public Iterator<E> iterator() {
      return UnmodifiableIterator.unmodifiableIterator(this.decorated().iterator());
   }

   @Override
   public boolean add(Object object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(Object object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeIf(Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean offer(E obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public E poll() {
      throw new UnsupportedOperationException();
   }

   @Override
   public E remove() {
      throw new UnsupportedOperationException();
   }
}
