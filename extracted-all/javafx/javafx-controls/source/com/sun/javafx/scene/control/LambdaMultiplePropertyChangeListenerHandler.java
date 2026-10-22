package com.sun.javafx.scene.control;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;

public final class LambdaMultiplePropertyChangeListenerHandler {
   private static final Consumer EMPTY_CONSUMER = var0 -> {
   };
   private final Map<ObservableValue<?>, Consumer<ObservableValue<?>>> propertyReferenceMap = new HashMap<>();
   private final ChangeListener<Object> propertyChangedListener = (var1, var2, var3) -> this.propertyReferenceMap
         .getOrDefault(var1, EMPTY_CONSUMER)
         .accept(var1);
   private final WeakChangeListener<Object> weakPropertyChangedListener = new WeakChangeListener(this.propertyChangedListener);
   private final Map<Observable, Consumer<Observable>> observableReferenceMap = new HashMap<>();
   private final InvalidationListener invalidationListener = var1 -> this.observableReferenceMap.getOrDefault(var1, EMPTY_CONSUMER).accept(var1);
   private final WeakInvalidationListener weakInvalidationListener = new WeakInvalidationListener(this.invalidationListener);
   private final Map<ObservableList<?>, Consumer<Change<?>>> observableListReferenceMap = new IdentityHashMap<>();
   private final ListChangeListener<Object> listChangeListener = var1 -> this.observableListReferenceMap
         .getOrDefault(var1.getList(), EMPTY_CONSUMER)
         .accept(var1);
   private final WeakListChangeListener<Object> weakListChangeListener = new WeakListChangeListener(this.listChangeListener);

   public final void registerChangeListener(ObservableValue<?> var1, Consumer<ObservableValue<?>> var2) {
      if (var1 != null && var2 != null) {
         if (!this.propertyReferenceMap.containsKey(var1)) {
            var1.addListener(this.weakPropertyChangedListener);
         }

         this.propertyReferenceMap.merge(var1, var2, Consumer::andThen);
      }
   }

   public final Consumer<ObservableValue<?>> unregisterChangeListeners(ObservableValue<?> var1) {
      if (var1 == null) {
         return null;
      } else {
         var1.removeListener(this.weakPropertyChangedListener);
         return this.propertyReferenceMap.remove(var1);
      }
   }

   public final void registerInvalidationListener(Observable var1, Consumer<Observable> var2) {
      if (var1 != null && var2 != null) {
         if (!this.observableReferenceMap.containsKey(var1)) {
            var1.addListener(this.weakInvalidationListener);
         }

         this.observableReferenceMap.merge(var1, var2, Consumer::andThen);
      }
   }

   public final Consumer<Observable> unregisterInvalidationListeners(Observable var1) {
      if (var1 == null) {
         return null;
      } else {
         var1.removeListener(this.weakInvalidationListener);
         return this.observableReferenceMap.remove(var1);
      }
   }

   public final void registerListChangeListener(ObservableList<?> var1, Consumer<Change<?>> var2) {
      if (var1 != null && var2 != null) {
         if (!this.observableListReferenceMap.containsKey(var1)) {
            var1.addListener(this.weakListChangeListener);
         }

         this.observableListReferenceMap.merge(var1, var2, Consumer::andThen);
      }
   }

   public final Consumer<Change<?>> unregisterListChangeListeners(ObservableList<?> var1) {
      if (var1 == null) {
         return null;
      } else {
         var1.removeListener(this.weakListChangeListener);
         return this.observableListReferenceMap.remove(var1);
      }
   }

   public void dispose() {
      for (ObservableValue var2 : this.propertyReferenceMap.keySet()) {
         var2.removeListener(this.weakPropertyChangedListener);
      }

      this.propertyReferenceMap.clear();

      for (Observable var5 : this.observableReferenceMap.keySet()) {
         var5.removeListener(this.weakInvalidationListener);
      }

      this.observableReferenceMap.clear();

      for (ObservableList var6 : this.observableListReferenceMap.keySet()) {
         var6.removeListener(this.weakListChangeListener);
      }

      this.observableListReferenceMap.clear();
   }
}
