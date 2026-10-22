package com.fasterxml.jackson.databind.util.internal;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class LinkedDeque<E extends Linked<E>> extends AbstractCollection<E> implements Deque<E> {
   E first;
   E last;

   void linkFirst(E e) {
      E f = this.first;
      this.first = e;
      if (f == null) {
         this.last = e;
      } else {
         f.setPrevious(e);
         e.setNext(f);
      }
   }

   void linkLast(E e) {
      E l = this.last;
      this.last = e;
      if (l == null) {
         this.first = e;
      } else {
         l.setNext(e);
         e.setPrevious(l);
      }
   }

   E unlinkFirst() {
      E f = this.first;
      E next = f.getNext();
      f.setNext(null);
      this.first = next;
      if (next == null) {
         this.last = null;
      } else {
         next.setPrevious(null);
      }

      return f;
   }

   E unlinkLast() {
      E l = this.last;
      E prev = l.getPrevious();
      l.setPrevious(null);
      this.last = prev;
      if (prev == null) {
         this.first = null;
      } else {
         prev.setNext(null);
      }

      return l;
   }

   void unlink(E e) {
      E prev = e.getPrevious();
      E next = e.getNext();
      if (prev == null) {
         this.first = next;
      } else {
         prev.setNext(next);
         e.setPrevious(null);
      }

      if (next == null) {
         this.last = prev;
      } else {
         next.setPrevious(prev);
         e.setNext(null);
      }
   }

   @Override
   public boolean isEmpty() {
      return this.first == null;
   }

   void checkNotEmpty() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      }
   }

   @Override
   public int size() {
      int size = 0;

      for (E e = this.first; e != null; e = e.getNext()) {
         size++;
      }

      return size;
   }

   @Override
   public void clear() {
      E e = this.first;

      while (e != null) {
         E next = e.getNext();
         e.setPrevious(null);
         e.setNext(null);
         e = next;
      }

      this.first = this.last = null;
   }

   @Override
   public boolean contains(Object o) {
      return o instanceof Linked && this.contains((Linked<?>)o);
   }

   boolean contains(Linked<?> e) {
      return e.getPrevious() != null || e.getNext() != null || e == this.first;
   }

   public void moveToFront(E e) {
      if (e != this.first) {
         this.unlink(e);
         this.linkFirst(e);
      }
   }

   public void moveToBack(E e) {
      if (e != this.last) {
         this.unlink(e);
         this.linkLast(e);
      }
   }

   public E peek() {
      return this.peekFirst();
   }

   public E peekFirst() {
      return this.first;
   }

   public E peekLast() {
      return this.last;
   }

   public E getFirst() {
      this.checkNotEmpty();
      return this.peekFirst();
   }

   public E getLast() {
      this.checkNotEmpty();
      return this.peekLast();
   }

   public E element() {
      return this.getFirst();
   }

   public boolean offer(E e) {
      return this.offerLast(e);
   }

   public boolean offerFirst(E e) {
      if (this.contains(e)) {
         return false;
      } else {
         this.linkFirst(e);
         return true;
      }
   }

   public boolean offerLast(E e) {
      if (this.contains(e)) {
         return false;
      } else {
         this.linkLast(e);
         return true;
      }
   }

   public boolean add(E e) {
      return this.offerLast(e);
   }

   public void addFirst(E e) {
      if (!this.offerFirst(e)) {
         throw new IllegalArgumentException();
      }
   }

   public void addLast(E e) {
      if (!this.offerLast(e)) {
         throw new IllegalArgumentException();
      }
   }

   public E poll() {
      return this.pollFirst();
   }

   public E pollFirst() {
      return this.isEmpty() ? null : this.unlinkFirst();
   }

   public E pollLast() {
      return this.isEmpty() ? null : this.unlinkLast();
   }

   public E remove() {
      return this.removeFirst();
   }

   @Override
   public boolean remove(Object o) {
      return o instanceof Linked && this.remove((E)o);
   }

   boolean remove(E e) {
      if (this.contains(e)) {
         this.unlink(e);
         return true;
      } else {
         return false;
      }
   }

   public E removeFirst() {
      this.checkNotEmpty();
      return this.pollFirst();
   }

   @Override
   public boolean removeFirstOccurrence(Object o) {
      return this.remove(o);
   }

   public E removeLast() {
      this.checkNotEmpty();
      return this.pollLast();
   }

   @Override
   public boolean removeLastOccurrence(Object o) {
      return this.remove(o);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      boolean modified = false;

      for (Object o : c) {
         modified |= this.remove(o);
      }

      return modified;
   }

   public void push(E e) {
      this.addFirst(e);
   }

   public E pop() {
      return this.removeFirst();
   }

   @Override
   public Iterator<E> iterator() {
      return new LinkedDeque<E>.AbstractLinkedIterator(this.first) {
         @Override
         E computeNext() {
            return this.cursor.getNext();
         }
      };
   }

   @Override
   public Iterator<E> descendingIterator() {
      return new LinkedDeque<E>.AbstractLinkedIterator(this.last) {
         @Override
         E computeNext() {
            return this.cursor.getPrevious();
         }
      };
   }

   abstract class AbstractLinkedIterator implements Iterator<E> {
      E cursor;

      AbstractLinkedIterator(E start) {
         this.cursor = start;
      }

      @Override
      public boolean hasNext() {
         return this.cursor != null;
      }

      public E next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            E e = this.cursor;
            this.cursor = (E)this.computeNext();
            return e;
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      abstract E computeNext();
   }
}
