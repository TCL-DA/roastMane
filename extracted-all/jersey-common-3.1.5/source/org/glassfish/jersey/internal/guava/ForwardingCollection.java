package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Iterator;

public abstract class ForwardingCollection<E> extends ForwardingObject implements Collection<E> {
   ForwardingCollection() {
   }

   protected abstract Collection<E> delegate();

   @Override
   public Iterator<E> iterator() {
      return this.delegate().iterator();
   }

   @Override
   public int size() {
      return this.delegate().size();
   }

   @Override
   public boolean removeAll(Collection<?> collection) {
      return this.delegate().removeAll(collection);
   }

   @Override
   public boolean isEmpty() {
      return this.delegate().isEmpty();
   }

   @Override
   public boolean contains(Object object) {
      return this.delegate().contains(object);
   }

   @Override
   public boolean add(E element) {
      return this.delegate().add(element);
   }

   @Override
   public boolean remove(Object object) {
      return this.delegate().remove(object);
   }

   @Override
   public boolean containsAll(Collection<?> collection) {
      return this.delegate().containsAll(collection);
   }

   @Override
   public boolean addAll(Collection<? extends E> collection) {
      return this.delegate().addAll(collection);
   }

   @Override
   public boolean retainAll(Collection<?> collection) {
      return this.delegate().retainAll(collection);
   }

   @Override
   public void clear() {
      this.delegate().clear();
   }

   @Override
   public Object[] toArray() {
      return this.delegate().toArray();
   }

   @Override
   public <T> T[] toArray(T[] array) {
      return (T[])this.delegate().toArray(array);
   }
}
