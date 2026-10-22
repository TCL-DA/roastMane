package com.sun.javafx.binding;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.WeakListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ContentBinding {
   private static void checkParameters(Object var0, Object var1) {
      if (var0 == null || var1 == null) {
         throw new NullPointerException("Both parameters must be specified.");
      } else if (var0 == var1) {
         throw new IllegalArgumentException("Cannot bind object to itself");
      }
   }

   public static <E> Object bind(List<E> var0, ObservableList<? extends E> var1) {
      checkParameters(var0, var1);
      ContentBinding.ListContentBinding var2 = new ContentBinding.ListContentBinding(var0);
      if (var0 instanceof ObservableList) {
         ((ObservableList)var0).setAll(var1);
      } else {
         var0.clear();
         var0.addAll(var1);
      }

      var1.removeListener(var2);
      var1.addListener(var2);
      return var2;
   }

   public static <E> Object bind(Set<E> var0, ObservableSet<? extends E> var1) {
      checkParameters(var0, var1);
      ContentBinding.SetContentBinding var2 = new ContentBinding.SetContentBinding(var0);
      var0.clear();
      var0.addAll(var1);
      var1.removeListener(var2);
      var1.addListener(var2);
      return var2;
   }

   public static <K, V> Object bind(Map<K, V> var0, ObservableMap<? extends K, ? extends V> var1) {
      checkParameters(var0, var1);
      ContentBinding.MapContentBinding var2 = new ContentBinding.MapContentBinding(var0);
      var0.clear();
      var0.putAll(var1);
      var1.removeListener(var2);
      var1.addListener(var2);
      return var2;
   }

   public static void unbind(Object var0, Object var1) {
      checkParameters(var0, var1);
      if (var0 instanceof List && var1 instanceof ObservableList) {
         ((ObservableList)var1).removeListener(new ContentBinding.ListContentBinding((List)var0));
      } else if (var0 instanceof Set && var1 instanceof ObservableSet) {
         ((ObservableSet)var1).removeListener(new ContentBinding.SetContentBinding((Set)var0));
      } else if (var0 instanceof Map && var1 instanceof ObservableMap) {
         ((ObservableMap)var1).removeListener(new ContentBinding.MapContentBinding((Map)var0));
      }
   }

   private static class ListContentBinding<E> implements ListChangeListener<E>, WeakListener {
      private final WeakReference<List<E>> listRef;

      public ListContentBinding(List<E> var1) {
         this.listRef = new WeakReference<>(var1);
      }

      @Override
      public void onChanged(ListChangeListener.Change<? extends E> var1) {
         List var2 = this.listRef.get();
         if (var2 == null) {
            var1.getList().removeListener(this);
         } else {
            while (var1.next()) {
               if (var1.wasPermutated()) {
                  var2.subList(var1.getFrom(), var1.getTo()).clear();
                  var2.addAll(var1.getFrom(), var1.getList().subList(var1.getFrom(), var1.getTo()));
               } else {
                  if (var1.wasRemoved()) {
                     var2.subList(var1.getFrom(), var1.getFrom() + var1.getRemovedSize()).clear();
                  }

                  if (var1.wasAdded()) {
                     var2.addAll(var1.getFrom(), var1.getAddedSubList());
                  }
               }
            }
         }
      }

      @Override
      public boolean wasGarbageCollected() {
         return this.listRef.get() == null;
      }

      @Override
      public int hashCode() {
         List var1 = this.listRef.get();
         return var1 == null ? 0 : var1.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            List var2 = this.listRef.get();
            if (var2 == null) {
               return false;
            } else if (var1 instanceof ContentBinding.ListContentBinding) {
               ContentBinding.ListContentBinding var3 = (ContentBinding.ListContentBinding)var1;
               List var4 = var3.listRef.get();
               return var2 == var4;
            } else {
               return false;
            }
         }
      }
   }

   private static class MapContentBinding<K, V> implements MapChangeListener<K, V>, WeakListener {
      private final WeakReference<Map<K, V>> mapRef;

      public MapContentBinding(Map<K, V> var1) {
         this.mapRef = new WeakReference<>(var1);
      }

      @Override
      public void onChanged(MapChangeListener.Change<? extends K, ? extends V> var1) {
         Map var2 = this.mapRef.get();
         if (var2 == null) {
            var1.getMap().removeListener(this);
         } else {
            if (var1.wasRemoved()) {
               var2.remove(var1.getKey());
            }

            if (var1.wasAdded()) {
               var2.put(var1.getKey(), var1.getValueAdded());
            }
         }
      }

      @Override
      public boolean wasGarbageCollected() {
         return this.mapRef.get() == null;
      }

      @Override
      public int hashCode() {
         Map var1 = this.mapRef.get();
         return var1 == null ? 0 : var1.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            Map var2 = this.mapRef.get();
            if (var2 == null) {
               return false;
            } else if (var1 instanceof ContentBinding.MapContentBinding) {
               ContentBinding.MapContentBinding var3 = (ContentBinding.MapContentBinding)var1;
               Map var4 = var3.mapRef.get();
               return var2 == var4;
            } else {
               return false;
            }
         }
      }
   }

   private static class SetContentBinding<E> implements SetChangeListener<E>, WeakListener {
      private final WeakReference<Set<E>> setRef;

      public SetContentBinding(Set<E> var1) {
         this.setRef = new WeakReference<>(var1);
      }

      @Override
      public void onChanged(SetChangeListener.Change<? extends E> var1) {
         Set var2 = this.setRef.get();
         if (var2 == null) {
            var1.getSet().removeListener(this);
         } else if (var1.wasRemoved()) {
            var2.remove(var1.getElementRemoved());
         } else {
            var2.add(var1.getElementAdded());
         }
      }

      @Override
      public boolean wasGarbageCollected() {
         return this.setRef.get() == null;
      }

      @Override
      public int hashCode() {
         Set var1 = this.setRef.get();
         return var1 == null ? 0 : var1.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            Set var2 = this.setRef.get();
            if (var2 == null) {
               return false;
            } else if (var1 instanceof ContentBinding.SetContentBinding) {
               ContentBinding.SetContentBinding var3 = (ContentBinding.SetContentBinding)var1;
               Set var4 = var3.setRef.get();
               return var2 == var4;
            } else {
               return false;
            }
         }
      }
   }
}
