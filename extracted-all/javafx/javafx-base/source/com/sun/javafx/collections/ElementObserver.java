package com.sun.javafx.collections;

import java.util.IdentityHashMap;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableListBase;
import javafx.util.Callback;

final class ElementObserver<E> {
   private Callback<E, Observable[]> extractor;
   private final Callback<E, InvalidationListener> listenerGenerator;
   private final ObservableListBase<E> list;
   private IdentityHashMap<E, ElementObserver.ElementsMapElement> elementsMap = new IdentityHashMap<>();

   ElementObserver(Callback<E, Observable[]> var1, Callback<E, InvalidationListener> var2, ObservableListBase<E> var3) {
      this.extractor = var1;
      this.listenerGenerator = var2;
      this.list = var3;
   }

   void attachListener(E var1) {
      if (this.elementsMap != null && var1 != null) {
         if (this.elementsMap.containsKey(var1)) {
            this.elementsMap.get(var1).increment();
         } else {
            InvalidationListener var2 = this.listenerGenerator.call((E)var1);

            for (Observable var6 : this.extractor.call((E)var1)) {
               var6.addListener(var2);
            }

            this.elementsMap.put((E)var1, new ElementObserver.ElementsMapElement(var2));
         }
      }
   }

   void detachListener(E var1) {
      if (this.elementsMap != null && var1 != null) {
         ElementObserver.ElementsMapElement var2 = this.elementsMap.get(var1);

         for (Observable var6 : this.extractor.call((E)var1)) {
            var6.removeListener(var2.getListener());
         }

         if (var2.decrement() == 0) {
            this.elementsMap.remove(var1);
         }
      }
   }

   private static class ElementsMapElement {
      InvalidationListener listener;
      int counter;

      public ElementsMapElement(InvalidationListener var1) {
         this.listener = var1;
         this.counter = 1;
      }

      public void increment() {
         this.counter++;
      }

      public int decrement() {
         return --this.counter;
      }

      private InvalidationListener getListener() {
         return this.listener;
      }
   }
}
