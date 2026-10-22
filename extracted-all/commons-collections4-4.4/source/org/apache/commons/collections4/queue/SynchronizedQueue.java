package org.apache.commons.collections4.queue;

import java.util.Queue;
import org.apache.commons.collections4.collection.SynchronizedCollection;

public class SynchronizedQueue<E> extends SynchronizedCollection<E> implements Queue<E> {
   private static final long serialVersionUID = 1L;

   public static <E> SynchronizedQueue<E> synchronizedQueue(Queue<E> queue) {
      return new SynchronizedQueue<>(queue);
   }

   protected SynchronizedQueue(Queue<E> queue) {
      super(queue);
   }

   protected SynchronizedQueue(Queue<E> queue, Object lock) {
      super(queue, lock);
   }

   protected Queue<E> decorated() {
      return (Queue<E>)super.decorated();
   }

   @Override
   public E element() {
      synchronized (this.lock) {
         return this.decorated().element();
      }
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else {
         synchronized (this.lock) {
            return this.decorated().equals(object);
         }
      }
   }

   @Override
   public int hashCode() {
      synchronized (this.lock) {
         return this.decorated().hashCode();
      }
   }

   @Override
   public boolean offer(E e) {
      synchronized (this.lock) {
         return this.decorated().offer(e);
      }
   }

   @Override
   public E peek() {
      synchronized (this.lock) {
         return this.decorated().peek();
      }
   }

   @Override
   public E poll() {
      synchronized (this.lock) {
         return this.decorated().poll();
      }
   }

   @Override
   public E remove() {
      synchronized (this.lock) {
         return this.decorated().remove();
      }
   }
}
