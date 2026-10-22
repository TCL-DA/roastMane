package com.sun.javafx.collections;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class MapAdapterChange<K, V> extends MapChangeListener.Change<K, V> {
   private final MapChangeListener.Change<? extends K, ? extends V> change;

   public MapAdapterChange(ObservableMap<K, V> var1, MapChangeListener.Change<? extends K, ? extends V> var2) {
      super(var1);
      this.change = var2;
   }

   @Override
   public boolean wasAdded() {
      return this.change.wasAdded();
   }

   @Override
   public boolean wasRemoved() {
      return this.change.wasRemoved();
   }

   @Override
   public K getKey() {
      return (K)this.change.getKey();
   }

   @Override
   public V getValueAdded() {
      return (V)this.change.getValueAdded();
   }

   @Override
   public V getValueRemoved() {
      return (V)this.change.getValueRemoved();
   }

   @Override
   public String toString() {
      return this.change.toString();
   }
}
