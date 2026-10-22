package com.sun.javafx.binding;

import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.binding.Binding;

public class BindingHelperObserver implements InvalidationListener, WeakListener {
   private final WeakReference<Binding<?>> ref;

   public BindingHelperObserver(Binding<?> var1) {
      if (var1 == null) {
         throw new NullPointerException("Binding has to be specified.");
      } else {
         this.ref = new WeakReference<>(var1);
      }
   }

   @Override
   public void invalidated(Observable var1) {
      Binding var2 = this.ref.get();
      if (var2 == null) {
         var1.removeListener(this);
      } else {
         var2.invalidate();
      }
   }

   @Override
   public boolean wasGarbageCollected() {
      return this.ref.get() == null;
   }
}
