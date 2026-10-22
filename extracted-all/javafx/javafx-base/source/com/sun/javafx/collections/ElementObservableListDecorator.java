package com.sun.javafx.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;
import javafx.util.Callback;

public final class ElementObservableListDecorator<E> extends ObservableListBase<E> implements ObservableList<E> {
   private final ObservableList<E> decoratedList;
   private final ListChangeListener<E> listener;
   private ElementObserver<E> observer;

   public ElementObservableListDecorator(ObservableList<E> var1, Callback<E, Observable[]> var2) {
      this.observer = new ElementObserver<>(var2, new Callback<E, InvalidationListener>() {
         public InvalidationListener call(final E var1) {
            return new InvalidationListener() {
               @Override
               public void invalidated(Observable var1x) {
                  ElementObservableListDecorator.this.beginChange();
                  int var2 = 0;
                  if (ElementObservableListDecorator.this.decoratedList instanceof RandomAccess) {
                     for (int var3 = ElementObservableListDecorator.this.size(); var2 < var3; var2++) {
                        if (ElementObservableListDecorator.this.get(var2) == var1) {
                           ElementObservableListDecorator.this.nextUpdate(var2);
                        }
                     }
                  } else {
                     for (Iterator var4 = ElementObservableListDecorator.this.iterator(); var4.hasNext(); var2++) {
                        if (var4.next() == var1) {
                           ElementObservableListDecorator.this.nextUpdate(var2);
                        }
                     }
                  }

                  ElementObservableListDecorator.this.endChange();
               }
            };
         }
      }, this);
      this.decoratedList = var1;
      int var3 = this.decoratedList.size();

      for (int var4 = 0; var4 < var3; var4++) {
         this.observer.attachListener(this.decoratedList.get(var4));
      }

      this.listener = new ListChangeListener<E>() {
         @Override
         public void onChanged(ListChangeListener.Change<? extends E> var1) {
            while (var1.next()) {
               if (var1.wasAdded() || var1.wasRemoved()) {
                  int var2 = var1.getRemovedSize();
                  List var3 = var1.getRemoved();

                  for (int var4 = 0; var4 < var2; var4++) {
                     ElementObservableListDecorator.this.observer.detachListener((E)var3.get(var4));
                  }

                  if (ElementObservableListDecorator.this.decoratedList instanceof RandomAccess) {
                     int var7 = var1.getTo();

                     for (int var8 = var1.getFrom(); var8 < var7; var8++) {
                        ElementObservableListDecorator.this.observer.attachListener(ElementObservableListDecorator.this.decoratedList.get(var8));
                     }
                  } else {
                     for (Object var5 : var1.getAddedSubList()) {
                        ElementObservableListDecorator.this.observer.attachListener((E)var5);
                     }
                  }
               }
            }

            var1.reset();
            ElementObservableListDecorator.this.fireChange(var1);
         }
      };
      this.decoratedList.addListener(new WeakListChangeListener<>(this.listener));
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      return (T[])this.decoratedList.toArray(var1);
   }

   @Override
   public Object[] toArray() {
      return this.decoratedList.toArray();
   }

   @Override
   public List<E> subList(int var1, int var2) {
      return this.decoratedList.subList(var1, var2);
   }

   @Override
   public int size() {
      return this.decoratedList.size();
   }

   @Override
   public E set(int var1, E var2) {
      return this.decoratedList.set(var1, (E)var2);
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      return this.decoratedList.retainAll(var1);
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      return this.decoratedList.removeAll(var1);
   }

   @Override
   public E remove(int var1) {
      return this.decoratedList.remove(var1);
   }

   @Override
   public boolean remove(Object var1) {
      return this.decoratedList.remove(var1);
   }

   @Override
   public ListIterator<E> listIterator(int var1) {
      return this.decoratedList.listIterator(var1);
   }

   @Override
   public ListIterator<E> listIterator() {
      return this.decoratedList.listIterator();
   }

   @Override
   public int lastIndexOf(Object var1) {
      return this.decoratedList.lastIndexOf(var1);
   }

   @Override
   public Iterator<E> iterator() {
      return this.decoratedList.iterator();
   }

   @Override
   public boolean isEmpty() {
      return this.decoratedList.isEmpty();
   }

   @Override
   public int indexOf(Object var1) {
      return this.decoratedList.indexOf(var1);
   }

   @Override
   public E get(int var1) {
      return this.decoratedList.get(var1);
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.decoratedList.containsAll(var1);
   }

   @Override
   public boolean contains(Object var1) {
      return this.decoratedList.contains(var1);
   }

   @Override
   public void clear() {
      this.decoratedList.clear();
   }

   @Override
   public boolean addAll(int var1, Collection<? extends E> var2) {
      return this.decoratedList.addAll(var1, var2);
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      return this.decoratedList.addAll(var1);
   }

   @Override
   public void add(int var1, E var2) {
      this.decoratedList.add(var1, (E)var2);
   }

   @Override
   public boolean add(E var1) {
      return this.decoratedList.add((E)var1);
   }

   @Override
   public boolean setAll(Collection<? extends E> var1) {
      return this.decoratedList.setAll(var1);
   }

   @Override
   public boolean setAll(E... var1) {
      return this.decoratedList.setAll((E[])var1);
   }

   @Override
   public boolean retainAll(E... var1) {
      return this.decoratedList.retainAll((E[])var1);
   }

   @Override
   public boolean removeAll(E... var1) {
      return this.decoratedList.removeAll((E[])var1);
   }

   @Override
   public void remove(int var1, int var2) {
      this.decoratedList.remove(var1, var2);
   }

   @Override
   public boolean addAll(E... var1) {
      return this.decoratedList.addAll((E[])var1);
   }
}
