package org.apache.commons.collections4.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

public class SynchronizedCollection<E> implements Collection<E>, Serializable {
   private static final long serialVersionUID = 2412805092710877986L;
   private final Collection<E> collection;
   protected final Object lock;

   public static <T> SynchronizedCollection<T> synchronizedCollection(Collection<T> coll) {
      return new SynchronizedCollection<>(coll);
   }

   protected SynchronizedCollection(Collection<E> collection) {
      if (collection == null) {
         throw new NullPointerException("Collection must not be null.");
      } else {
         this.collection = collection;
         this.lock = this;
      }
   }

   protected SynchronizedCollection(Collection<E> collection, Object lock) {
      if (collection == null) {
         throw new NullPointerException("Collection must not be null.");
      } else if (lock == null) {
         throw new NullPointerException("Lock must not be null.");
      } else {
         this.collection = collection;
         this.lock = lock;
      }
   }

   protected Collection<E> decorated() {
      return this.collection;
   }

   @Override
   public boolean add(E object) {
      synchronized (this.lock) {
         return this.decorated().add(object);
      }
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      synchronized (this.lock) {
         return this.decorated().addAll(coll);
      }
   }

   @Override
   public void clear() {
      synchronized (this.lock) {
         this.decorated().clear();
      }
   }

   @Override
   public boolean contains(Object object) {
      synchronized (this.lock) {
         return this.decorated().contains(object);
      }
   }

   @Override
   public boolean containsAll(Collection<?> coll) {
      synchronized (this.lock) {
         return this.decorated().containsAll(coll);
      }
   }

   @Override
   public boolean isEmpty() {
      synchronized (this.lock) {
         return this.decorated().isEmpty();
      }
   }

   @Override
   public Iterator<E> iterator() {
      return this.decorated().iterator();
   }

   @Override
   public Object[] toArray() {
      synchronized (this.lock) {
         return this.decorated().toArray();
      }
   }

   @Override
   public <T> T[] toArray(T[] object) {
      synchronized (this.lock) {
         return (T[])this.decorated().toArray(object);
      }
   }

   @Override
   public boolean remove(Object object) {
      synchronized (this.lock) {
         return this.decorated().remove(object);
      }
   }

   @Override
   public boolean removeIf(Predicate<? super E> filter) {
      synchronized (this.lock) {
         return this.decorated().removeIf(filter);
      }
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      synchronized (this.lock) {
         return this.decorated().removeAll(coll);
      }
   }

   @Override
   public boolean retainAll(Collection<?> coll) {
      synchronized (this.lock) {
         return this.decorated().retainAll(coll);
      }
   }

   @Override
   public int size() {
      synchronized (this.lock) {
         return this.decorated().size();
      }
   }

   @Override
   public boolean equals(Object object) {
      synchronized (this.lock) {
         return object == this ? true : object == this || this.decorated().equals(object);
      }
   }

   @Override
   public int hashCode() {
      synchronized (this.lock) {
         return this.decorated().hashCode();
      }
   }

   @Override
   public String toString() {
      synchronized (this.lock) {
         return this.decorated().toString();
      }
   }
}
