package com.sun.javafx.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ObservableSetWrapper<E> implements ObservableSet<E> {
   private final Set<E> backingSet;
   private SetListenerHelper<E> listenerHelper;

   public ObservableSetWrapper(Set<E> var1) {
      this.backingSet = var1;
   }

   private void callObservers(SetChangeListener.Change<E> var1) {
      SetListenerHelper.fireValueChangedEvent(this.listenerHelper, var1);
   }

   @Override
   public void addListener(InvalidationListener var1) {
      this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, var1);
   }

   @Override
   public void addListener(SetChangeListener<? super E> var1) {
      this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
   }

   @Override
   public void removeListener(SetChangeListener<? super E> var1) {
      this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, var1);
   }

   @Override
   public int size() {
      return this.backingSet.size();
   }

   @Override
   public boolean isEmpty() {
      return this.backingSet.isEmpty();
   }

   @Override
   public boolean contains(Object var1) {
      return this.backingSet.contains(var1);
   }

   @Override
   public Iterator iterator() {
      return new Iterator<E>() {
         private final Iterator<E> backingIt = ObservableSetWrapper.this.backingSet.iterator();
         private E lastElement;

         @Override
         public boolean hasNext() {
            return this.backingIt.hasNext();
         }

         @Override
         public E next() {
            this.lastElement = this.backingIt.next();
            return this.lastElement;
         }

         @Override
         public void remove() {
            this.backingIt.remove();
            ObservableSetWrapper.this.callObservers(ObservableSetWrapper.this.new SimpleRemoveChange(this.lastElement));
         }
      };
   }

   @Override
   public Object[] toArray() {
      return this.backingSet.toArray();
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      return (T[])this.backingSet.toArray(var1);
   }

   @Override
   public boolean add(E var1) {
      boolean var2 = this.backingSet.add((E)var1);
      if (var2) {
         this.callObservers(new ObservableSetWrapper.SimpleAddChange(var1));
      }

      return var2;
   }

   @Override
   public boolean remove(Object var1) {
      boolean var2 = this.backingSet.remove(var1);
      if (var2) {
         this.callObservers(new ObservableSetWrapper.SimpleRemoveChange(var1));
      }

      return var2;
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.backingSet.containsAll(var1);
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      boolean var2 = false;

      for (Object var4 : var1) {
         var2 |= this.add((E)var4);
      }

      return var2;
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      return this.removeRetain(var1, false);
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      return this.removeRetain(var1, true);
   }

   private boolean removeRetain(Collection<?> var1, boolean var2) {
      boolean var3 = false;
      Iterator var4 = this.backingSet.iterator();

      while (var4.hasNext()) {
         Object var5 = var4.next();
         if (var2 == var1.contains(var5)) {
            var3 = true;
            var4.remove();
            this.callObservers(new ObservableSetWrapper.SimpleRemoveChange(var5));
         }
      }

      return var3;
   }

   @Override
   public void clear() {
      Iterator var1 = this.backingSet.iterator();

      while (var1.hasNext()) {
         Object var2 = var1.next();
         var1.remove();
         this.callObservers(new ObservableSetWrapper.SimpleRemoveChange(var2));
      }
   }

   @Override
   public String toString() {
      return this.backingSet.toString();
   }

   @Override
   public boolean equals(Object var1) {
      return this.backingSet.equals(var1);
   }

   @Override
   public int hashCode() {
      return this.backingSet.hashCode();
   }

   private class SimpleAddChange extends SetChangeListener.Change<E> {
      private final E added;

      public SimpleAddChange(E var2) {
         super(ObservableSetWrapper.this);
         this.added = (E)var2;
      }

      @Override
      public boolean wasAdded() {
         return true;
      }

      @Override
      public boolean wasRemoved() {
         return false;
      }

      @Override
      public E getElementAdded() {
         return this.added;
      }

      @Override
      public E getElementRemoved() {
         return null;
      }

      @Override
      public String toString() {
         return "added " + this.added;
      }
   }

   private class SimpleRemoveChange extends SetChangeListener.Change<E> {
      private final E removed;

      public SimpleRemoveChange(E var2) {
         super(ObservableSetWrapper.this);
         this.removed = (E)var2;
      }

      @Override
      public boolean wasAdded() {
         return false;
      }

      @Override
      public boolean wasRemoved() {
         return true;
      }

      @Override
      public E getElementAdded() {
         return null;
      }

      @Override
      public E getElementRemoved() {
         return this.removed;
      }

      @Override
      public String toString() {
         return "removed " + this.removed;
      }
   }
}
