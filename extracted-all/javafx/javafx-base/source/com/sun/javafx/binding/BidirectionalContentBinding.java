package com.sun.javafx.binding;

import java.lang.ref.WeakReference;
import javafx.beans.WeakListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class BidirectionalContentBinding {
   private static void checkParameters(Object var0, Object var1) {
      if (var0 == null || var1 == null) {
         throw new NullPointerException("Both parameters must be specified.");
      } else if (var0 == var1) {
         throw new IllegalArgumentException("Cannot bind object to itself");
      }
   }

   public static <E> Object bind(ObservableList<E> var0, ObservableList<E> var1) {
      checkParameters(var0, var1);
      BidirectionalContentBinding.ListContentBinding var2 = new BidirectionalContentBinding.ListContentBinding(var0, var1);
      var0.setAll(var1);
      var0.addListener(var2);
      var1.addListener(var2);
      return var2;
   }

   public static <E> Object bind(ObservableSet<E> var0, ObservableSet<E> var1) {
      checkParameters(var0, var1);
      BidirectionalContentBinding.SetContentBinding var2 = new BidirectionalContentBinding.SetContentBinding(var0, var1);
      var0.clear();
      var0.addAll(var1);
      var0.addListener(var2);
      var1.addListener(var2);
      return var2;
   }

   public static <K, V> Object bind(ObservableMap<K, V> var0, ObservableMap<K, V> var1) {
      checkParameters(var0, var1);
      BidirectionalContentBinding.MapContentBinding var2 = new BidirectionalContentBinding.MapContentBinding(var0, var1);
      var0.clear();
      var0.putAll(var1);
      var0.addListener(var2);
      var1.addListener(var2);
      return var2;
   }

   public static void unbind(Object var0, Object var1) {
      checkParameters(var0, var1);
      if (var0 instanceof ObservableList && var1 instanceof ObservableList) {
         ObservableList var6 = (ObservableList)var0;
         ObservableList var8 = (ObservableList)var1;
         BidirectionalContentBinding.ListContentBinding var10 = new BidirectionalContentBinding.ListContentBinding(var6, var8);
         var6.removeListener(var10);
         var8.removeListener(var10);
      } else if (var0 instanceof ObservableSet && var1 instanceof ObservableSet) {
         ObservableSet var5 = (ObservableSet)var0;
         ObservableSet var7 = (ObservableSet)var1;
         BidirectionalContentBinding.SetContentBinding var9 = new BidirectionalContentBinding.SetContentBinding(var5, var7);
         var5.removeListener(var9);
         var7.removeListener(var9);
      } else if (var0 instanceof ObservableMap && var1 instanceof ObservableMap) {
         ObservableMap var2 = (ObservableMap)var0;
         ObservableMap var3 = (ObservableMap)var1;
         BidirectionalContentBinding.MapContentBinding var4 = new BidirectionalContentBinding.MapContentBinding(var2, var3);
         var2.removeListener(var4);
         var3.removeListener(var4);
      }
   }

   private static class ListContentBinding<E> implements ListChangeListener<E>, WeakListener {
      private final WeakReference<ObservableList<E>> propertyRef1;
      private final WeakReference<ObservableList<E>> propertyRef2;
      private boolean updating = false;

      public ListContentBinding(ObservableList<E> var1, ObservableList<E> var2) {
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      @Override
      public void onChanged(ListChangeListener.Change<? extends E> var1) {
         if (!this.updating) {
            ObservableList var2 = this.propertyRef1.get();
            ObservableList var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  ObservableList var4 = var2 == var1.getList() ? var3 : var2;

                  while (var1.next()) {
                     if (var1.wasPermutated()) {
                        var4.remove(var1.getFrom(), var1.getTo());
                        var4.addAll(var1.getFrom(), var1.getList().subList(var1.getFrom(), var1.getTo()));
                     } else {
                        if (var1.wasRemoved()) {
                           var4.remove(var1.getFrom(), var1.getFrom() + var1.getRemovedSize());
                        }

                        if (var1.wasAdded()) {
                           var4.addAll(var1.getFrom(), var1.getAddedSubList());
                        }
                     }
                  }
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }

      @Override
      public boolean wasGarbageCollected() {
         return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
      }

      @Override
      public int hashCode() {
         ObservableList var1 = this.propertyRef1.get();
         ObservableList var2 = this.propertyRef2.get();
         int var3 = var1 == null ? 0 : var1.hashCode();
         int var4 = var2 == null ? 0 : var2.hashCode();
         return var3 * var4;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            Object var2 = this.propertyRef1.get();
            Object var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               if (var1 instanceof BidirectionalContentBinding.ListContentBinding) {
                  BidirectionalContentBinding.ListContentBinding var4 = (BidirectionalContentBinding.ListContentBinding)var1;
                  Object var5 = var4.propertyRef1.get();
                  Object var6 = var4.propertyRef2.get();
                  if (var5 == null || var6 == null) {
                     return false;
                  }

                  if (var2 == var5 && var3 == var6) {
                     return true;
                  }

                  if (var2 == var6 && var3 == var5) {
                     return true;
                  }
               }

               return false;
            } else {
               return false;
            }
         }
      }
   }

   private static class MapContentBinding<K, V> implements MapChangeListener<K, V>, WeakListener {
      private final WeakReference<ObservableMap<K, V>> propertyRef1;
      private final WeakReference<ObservableMap<K, V>> propertyRef2;
      private boolean updating = false;

      public MapContentBinding(ObservableMap<K, V> var1, ObservableMap<K, V> var2) {
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      @Override
      public void onChanged(MapChangeListener.Change<? extends K, ? extends V> var1) {
         if (!this.updating) {
            ObservableMap var2 = this.propertyRef1.get();
            ObservableMap var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  ObservableMap var4 = var2 == var1.getMap() ? var3 : var2;
                  if (var1.wasRemoved()) {
                     var4.remove(var1.getKey());
                  }

                  if (var1.wasAdded()) {
                     var4.put(var1.getKey(), var1.getValueAdded());
                  }
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }

      @Override
      public boolean wasGarbageCollected() {
         return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
      }

      @Override
      public int hashCode() {
         ObservableMap var1 = this.propertyRef1.get();
         ObservableMap var2 = this.propertyRef2.get();
         int var3 = var1 == null ? 0 : var1.hashCode();
         int var4 = var2 == null ? 0 : var2.hashCode();
         return var3 * var4;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            Object var2 = this.propertyRef1.get();
            Object var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               if (var1 instanceof BidirectionalContentBinding.MapContentBinding) {
                  BidirectionalContentBinding.MapContentBinding var4 = (BidirectionalContentBinding.MapContentBinding)var1;
                  Object var5 = var4.propertyRef1.get();
                  Object var6 = var4.propertyRef2.get();
                  if (var5 == null || var6 == null) {
                     return false;
                  }

                  if (var2 == var5 && var3 == var6) {
                     return true;
                  }

                  if (var2 == var6 && var3 == var5) {
                     return true;
                  }
               }

               return false;
            } else {
               return false;
            }
         }
      }
   }

   private static class SetContentBinding<E> implements SetChangeListener<E>, WeakListener {
      private final WeakReference<ObservableSet<E>> propertyRef1;
      private final WeakReference<ObservableSet<E>> propertyRef2;
      private boolean updating = false;

      public SetContentBinding(ObservableSet<E> var1, ObservableSet<E> var2) {
         this.propertyRef1 = new WeakReference<>(var1);
         this.propertyRef2 = new WeakReference<>(var2);
      }

      @Override
      public void onChanged(SetChangeListener.Change<? extends E> var1) {
         if (!this.updating) {
            ObservableSet var2 = this.propertyRef1.get();
            ObservableSet var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               try {
                  this.updating = true;
                  ObservableSet var4 = var2 == var1.getSet() ? var3 : var2;
                  if (var1.wasRemoved()) {
                     var4.remove(var1.getElementRemoved());
                  } else {
                     var4.add(var1.getElementAdded());
                  }
               } finally {
                  this.updating = false;
               }
            } else {
               if (var2 != null) {
                  var2.removeListener(this);
               }

               if (var3 != null) {
                  var3.removeListener(this);
               }
            }
         }
      }

      @Override
      public boolean wasGarbageCollected() {
         return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
      }

      @Override
      public int hashCode() {
         ObservableSet var1 = this.propertyRef1.get();
         ObservableSet var2 = this.propertyRef2.get();
         int var3 = var1 == null ? 0 : var1.hashCode();
         int var4 = var2 == null ? 0 : var2.hashCode();
         return var3 * var4;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            Object var2 = this.propertyRef1.get();
            Object var3 = this.propertyRef2.get();
            if (var2 != null && var3 != null) {
               if (var1 instanceof BidirectionalContentBinding.SetContentBinding) {
                  BidirectionalContentBinding.SetContentBinding var4 = (BidirectionalContentBinding.SetContentBinding)var1;
                  Object var5 = var4.propertyRef1.get();
                  Object var6 = var4.propertyRef2.get();
                  if (var5 == null || var6 == null) {
                     return false;
                  }

                  if (var2 == var5 && var3 == var6) {
                     return true;
                  }

                  if (var2 == var6 && var3 == var5) {
                     return true;
                  }
               }

               return false;
            } else {
               return false;
            }
         }
      }
   }
}
