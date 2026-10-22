package com.sun.javafx.collections;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map.Entry;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.WeakMapChangeListener;

public class UnmodifiableObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
   private MapListenerHelper<K, V> listenerHelper;
   private final ObservableMap<K, V> backingMap;
   private final MapChangeListener<K, V> listener;
   private Set<K> keyset;
   private Collection<V> values;
   private Set<Entry<K, V>> entryset;

   public UnmodifiableObservableMap(ObservableMap<K, V> var1) {
      this.backingMap = var1;
      this.listener = var1x -> this.callObservers(new MapAdapterChange<>(this, var1x));
      this.backingMap.addListener(new WeakMapChangeListener<>(this.listener));
   }

   private void callObservers(MapChangeListener.Change<? extends K, ? extends V> var1) {
      MapListenerHelper.fireValueChangedEvent(this.listenerHelper, var1);
   }

   @Override
   public void addListener(InvalidationListener var1) {
      this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, var1);
   }

   @Override
   public void addListener(MapChangeListener<? super K, ? super V> var1) {
      this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, var1);
   }

   @Override
   public void removeListener(MapChangeListener<? super K, ? super V> var1) {
      this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, var1);
   }

   @Override
   public int size() {
      return this.backingMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this.backingMap.isEmpty();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.backingMap.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.backingMap.containsValue(var1);
   }

   @Override
   public V get(Object var1) {
      return this.backingMap.get(var1);
   }

   @Override
   public Set<K> keySet() {
      if (this.keyset == null) {
         this.keyset = Collections.unmodifiableSet(this.backingMap.keySet());
      }

      return this.keyset;
   }

   @Override
   public Collection<V> values() {
      if (this.values == null) {
         this.values = Collections.unmodifiableCollection(this.backingMap.values());
      }

      return this.values;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      if (this.entryset == null) {
         this.entryset = Collections.unmodifiableMap(this.backingMap).entrySet();
      }

      return this.entryset;
   }
}
