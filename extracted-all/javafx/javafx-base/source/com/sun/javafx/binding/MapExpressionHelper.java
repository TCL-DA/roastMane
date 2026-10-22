package com.sun.javafx.binding;

import java.util.Arrays;
import java.util.Map.Entry;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public abstract class MapExpressionHelper<K, V> extends ExpressionHelperBase {
   protected final ObservableMapValue<K, V> observable;

   public static <K, V> MapExpressionHelper<K, V> addListener(MapExpressionHelper<K, V> var0, ObservableMapValue<K, V> var1, InvalidationListener var2) {
      if (var1 != null && var2 != null) {
         var1.getValue();
         return (MapExpressionHelper<K, V>)(var0 == null ? new MapExpressionHelper.SingleInvalidation<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <K, V> MapExpressionHelper<K, V> removeListener(MapExpressionHelper<K, V> var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <K, V> MapExpressionHelper<K, V> addListener(
      MapExpressionHelper<K, V> var0, ObservableMapValue<K, V> var1, ChangeListener<? super ObservableMap<K, V>> var2
   ) {
      if (var1 != null && var2 != null) {
         return (MapExpressionHelper<K, V>)(var0 == null ? new MapExpressionHelper.SingleChange<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <K, V> MapExpressionHelper<K, V> removeListener(MapExpressionHelper<K, V> var0, ChangeListener<? super ObservableMap<K, V>> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <K, V> MapExpressionHelper<K, V> addListener(
      MapExpressionHelper<K, V> var0, ObservableMapValue<K, V> var1, MapChangeListener<? super K, ? super V> var2
   ) {
      if (var1 != null && var2 != null) {
         return (MapExpressionHelper<K, V>)(var0 == null ? new MapExpressionHelper.SingleMapChange<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <K, V> MapExpressionHelper<K, V> removeListener(MapExpressionHelper<K, V> var0, MapChangeListener<? super K, ? super V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <K, V> void fireValueChangedEvent(MapExpressionHelper<K, V> var0) {
      if (var0 != null) {
         var0.fireValueChangedEvent();
      }
   }

   public static <K, V> void fireValueChangedEvent(MapExpressionHelper<K, V> var0, MapChangeListener.Change<? extends K, ? extends V> var1) {
      if (var0 != null) {
         var0.fireValueChangedEvent(var1);
      }
   }

   protected MapExpressionHelper(ObservableMapValue<K, V> var1) {
      this.observable = var1;
   }

   protected abstract MapExpressionHelper<K, V> addListener(InvalidationListener var1);

   protected abstract MapExpressionHelper<K, V> removeListener(InvalidationListener var1);

   protected abstract MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> var1);

   protected abstract MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> var1);

   protected abstract MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1);

   protected abstract MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1);

   protected abstract void fireValueChangedEvent();

   protected abstract void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1);

   private static class Generic<K, V> extends MapExpressionHelper<K, V> {
      private InvalidationListener[] invalidationListeners;
      private ChangeListener<? super ObservableMap<K, V>>[] changeListeners;
      private MapChangeListener<? super K, ? super V>[] mapChangeListeners;
      private int invalidationSize;
      private int changeSize;
      private int mapChangeSize;
      private boolean locked;
      private ObservableMap<K, V> currentValue;

      private Generic(ObservableMapValue<K, V> var1, InvalidationListener var2, InvalidationListener var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2, var3};
         this.invalidationSize = 2;
      }

      private Generic(ObservableMapValue<K, V> var1, ChangeListener<? super ObservableMap<K, V>> var2, ChangeListener<? super ObservableMap<K, V>> var3) {
         super(var1);
         this.changeListeners = new ChangeListener[]{var2, var3};
         this.changeSize = 2;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableMapValue<K, V> var1, MapChangeListener<? super K, ? super V> var2, MapChangeListener<? super K, ? super V> var3) {
         super(var1);
         this.mapChangeListeners = new MapChangeListener[]{var2, var3};
         this.mapChangeSize = 2;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableMapValue<K, V> var1, InvalidationListener var2, ChangeListener<? super ObservableMap<K, V>> var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2};
         this.invalidationSize = 1;
         this.changeListeners = new ChangeListener[]{var3};
         this.changeSize = 1;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableMapValue<K, V> var1, InvalidationListener var2, MapChangeListener<? super K, ? super V> var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2};
         this.invalidationSize = 1;
         this.mapChangeListeners = new MapChangeListener[]{var3};
         this.mapChangeSize = 1;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableMapValue<K, V> var1, ChangeListener<? super ObservableMap<K, V>> var2, MapChangeListener<? super K, ? super V> var3) {
         super(var1);
         this.changeListeners = new ChangeListener[]{var2};
         this.changeSize = 1;
         this.mapChangeListeners = new MapChangeListener[]{var3};
         this.mapChangeSize = 1;
         this.currentValue = var1.getValue();
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(InvalidationListener var1) {
         if (this.invalidationListeners == null) {
            this.invalidationListeners = new InvalidationListener[]{var1};
            this.invalidationSize = 1;
         } else {
            int var2 = this.invalidationListeners.length;
            if (this.locked) {
               int var3 = this.invalidationSize < var2 ? var2 : var2 * 3 / 2 + 1;
               this.invalidationListeners = Arrays.copyOf(this.invalidationListeners, var3);
            } else if (this.invalidationSize == var2) {
               this.invalidationSize = trim(this.invalidationSize, this.invalidationListeners);
               if (this.invalidationSize == var2) {
                  int var4 = var2 * 3 / 2 + 1;
                  this.invalidationListeners = Arrays.copyOf(this.invalidationListeners, var4);
               }
            }

            this.invalidationListeners[this.invalidationSize++] = var1;
         }

         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(InvalidationListener var1) {
         if (this.invalidationListeners != null) {
            for (int var2 = 0; var2 < this.invalidationSize; var2++) {
               if (var1.equals(this.invalidationListeners[var2])) {
                  if (this.invalidationSize == 1) {
                     if (this.changeSize == 1 && this.mapChangeSize == 0) {
                        return new MapExpressionHelper.SingleChange<>(this.observable, this.changeListeners[0]);
                     }

                     if (this.changeSize == 0 && this.mapChangeSize == 1) {
                        return new MapExpressionHelper.SingleMapChange<>(this.observable, this.mapChangeListeners[0]);
                     }

                     this.invalidationListeners = null;
                     this.invalidationSize = 0;
                  } else {
                     if (this.invalidationSize == 2 && this.changeSize == 0 && this.mapChangeSize == 0) {
                        return new MapExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[1 - var2]);
                     }

                     int var3 = this.invalidationSize - var2 - 1;
                     InvalidationListener[] var4 = this.invalidationListeners;
                     if (this.locked) {
                        this.invalidationListeners = new InvalidationListener[this.invalidationListeners.length];
                        System.arraycopy(var4, 0, this.invalidationListeners, 0, var2 + 1);
                     }

                     if (var3 > 0) {
                        System.arraycopy(var4, var2 + 1, this.invalidationListeners, var2, var3);
                     }

                     this.invalidationSize--;
                     if (!this.locked) {
                        this.invalidationListeners[this.invalidationSize] = null;
                     }
                  }
                  break;
               }
            }
         }

         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> var1) {
         if (this.changeListeners == null) {
            this.changeListeners = new ChangeListener[]{var1};
            this.changeSize = 1;
         } else {
            int var2 = this.changeListeners.length;
            if (this.locked) {
               int var3 = this.changeSize < var2 ? var2 : var2 * 3 / 2 + 1;
               this.changeListeners = Arrays.copyOf(this.changeListeners, var3);
            } else if (this.changeSize == var2) {
               this.changeSize = trim(this.changeSize, this.changeListeners);
               if (this.changeSize == var2) {
                  int var4 = var2 * 3 / 2 + 1;
                  this.changeListeners = Arrays.copyOf(this.changeListeners, var4);
               }
            }

            this.changeListeners[this.changeSize++] = var1;
         }

         if (this.changeSize == 1) {
            this.currentValue = this.observable.getValue();
         }

         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> var1) {
         if (this.changeListeners != null) {
            for (int var2 = 0; var2 < this.changeSize; var2++) {
               if (var1.equals(this.changeListeners[var2])) {
                  if (this.changeSize == 1) {
                     if (this.invalidationSize == 1 && this.mapChangeSize == 0) {
                        return new MapExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[0]);
                     }

                     if (this.invalidationSize == 0 && this.mapChangeSize == 1) {
                        return new MapExpressionHelper.SingleMapChange<>(this.observable, this.mapChangeListeners[0]);
                     }

                     this.changeListeners = null;
                     this.changeSize = 0;
                  } else {
                     if (this.changeSize == 2 && this.invalidationSize == 0 && this.mapChangeSize == 0) {
                        return new MapExpressionHelper.SingleChange<>(this.observable, this.changeListeners[1 - var2]);
                     }

                     int var3 = this.changeSize - var2 - 1;
                     ChangeListener[] var4 = this.changeListeners;
                     if (this.locked) {
                        this.changeListeners = new ChangeListener[this.changeListeners.length];
                        System.arraycopy(var4, 0, this.changeListeners, 0, var2 + 1);
                     }

                     if (var3 > 0) {
                        System.arraycopy(var4, var2 + 1, this.changeListeners, var2, var3);
                     }

                     this.changeSize--;
                     if (!this.locked) {
                        this.changeListeners[this.changeSize] = null;
                     }
                  }
                  break;
               }
            }
         }

         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1) {
         if (this.mapChangeListeners == null) {
            this.mapChangeListeners = new MapChangeListener[]{var1};
            this.mapChangeSize = 1;
         } else {
            int var2 = this.mapChangeListeners.length;
            if (this.locked) {
               int var3 = this.mapChangeSize < var2 ? var2 : var2 * 3 / 2 + 1;
               this.mapChangeListeners = Arrays.copyOf(this.mapChangeListeners, var3);
            } else if (this.mapChangeSize == var2) {
               this.mapChangeSize = trim(this.mapChangeSize, this.mapChangeListeners);
               if (this.mapChangeSize == var2) {
                  int var4 = var2 * 3 / 2 + 1;
                  this.mapChangeListeners = Arrays.copyOf(this.mapChangeListeners, var4);
               }
            }

            this.mapChangeListeners[this.mapChangeSize++] = var1;
         }

         if (this.mapChangeSize == 1) {
            this.currentValue = this.observable.getValue();
         }

         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1) {
         if (this.mapChangeListeners != null) {
            for (int var2 = 0; var2 < this.mapChangeSize; var2++) {
               if (var1.equals(this.mapChangeListeners[var2])) {
                  if (this.mapChangeSize == 1) {
                     if (this.invalidationSize == 1 && this.changeSize == 0) {
                        return new MapExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[0]);
                     }

                     if (this.invalidationSize == 0 && this.changeSize == 1) {
                        return new MapExpressionHelper.SingleChange<>(this.observable, this.changeListeners[0]);
                     }

                     this.mapChangeListeners = null;
                     this.mapChangeSize = 0;
                  } else {
                     if (this.mapChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                        return new MapExpressionHelper.SingleMapChange<>(this.observable, this.mapChangeListeners[1 - var2]);
                     }

                     int var3 = this.mapChangeSize - var2 - 1;
                     MapChangeListener[] var4 = this.mapChangeListeners;
                     if (this.locked) {
                        this.mapChangeListeners = new MapChangeListener[this.mapChangeListeners.length];
                        System.arraycopy(var4, 0, this.mapChangeListeners, 0, var2 + 1);
                     }

                     if (var3 > 0) {
                        System.arraycopy(var4, var2 + 1, this.mapChangeListeners, var2, var3);
                     }

                     this.mapChangeSize--;
                     if (!this.locked) {
                        this.mapChangeListeners[this.mapChangeSize] = null;
                     }
                  }
                  break;
               }
            }
         }

         return this;
      }

      @Override
      protected void fireValueChangedEvent() {
         if (this.changeSize == 0 && this.mapChangeSize == 0) {
            this.notifyListeners(this.currentValue, null);
         } else {
            ObservableMap var1 = this.currentValue;
            this.currentValue = this.observable.getValue();
            this.notifyListeners(var1, null);
         }
      }

      @Override
      protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
         MapExpressionHelper.SimpleChange var2 = this.mapChangeSize == 0 ? null : new MapExpressionHelper.SimpleChange<>(this.observable, var1);
         this.notifyListeners(this.currentValue, var2);
      }

      private void notifyListeners(ObservableMap<K, V> var1, MapExpressionHelper.SimpleChange<K, V> var2) {
         InvalidationListener[] var3 = this.invalidationListeners;
         int var4 = this.invalidationSize;
         ChangeListener[] var5 = this.changeListeners;
         int var6 = this.changeSize;
         MapChangeListener[] var7 = this.mapChangeListeners;
         int var8 = this.mapChangeSize;

         try {
            this.locked = true;

            for (int var9 = 0; var9 < var4; var9++) {
               var3[var9].invalidated(this.observable);
            }

            if (this.currentValue != var1 || var2 != null) {
               for (int var19 = 0; var19 < var6; var19++) {
                  var5[var19].changed(this.observable, var1, this.currentValue);
               }

               if (var8 > 0) {
                  if (var2 != null) {
                     for (int var20 = 0; var20 < var8; var20++) {
                        var7[var20].onChanged(var2);
                     }
                  } else {
                     var2 = new MapExpressionHelper.SimpleChange<>(this.observable);
                     if (this.currentValue == null) {
                        for (Entry var10 : var1.entrySet()) {
                           var2.setRemoved(var10.getKey(), var10.getValue());

                           for (int var11 = 0; var11 < var8; var11++) {
                              var7[var11].onChanged(var2);
                           }
                        }
                     } else if (var1 == null) {
                        for (Entry var25 : this.currentValue.entrySet()) {
                           var2.setAdded(var25.getKey(), var25.getValue());

                           for (int var28 = 0; var28 < var8; var28++) {
                              var7[var28].onChanged(var2);
                           }
                        }
                     } else {
                        for (Entry var26 : var1.entrySet()) {
                           Object var29 = var26.getKey();
                           Object var12 = var26.getValue();
                           if (this.currentValue.containsKey(var29)) {
                              Object var32 = this.currentValue.get(var29);
                              if (var12 == null ? var32 != null : !var32.equals(var12)) {
                                 var2.setPut(var29, var12, var32);

                                 for (int var14 = 0; var14 < var8; var14++) {
                                    var7[var14].onChanged(var2);
                                 }
                              }
                           } else {
                              var2.setRemoved(var29, var12);

                              for (int var13 = 0; var13 < var8; var13++) {
                                 var7[var13].onChanged(var2);
                              }
                           }
                        }

                        for (Entry var27 : this.currentValue.entrySet()) {
                           Object var30 = var27.getKey();
                           if (!var1.containsKey(var30)) {
                              var2.setAdded(var30, var27.getValue());

                              for (int var31 = 0; var31 < var8; var31++) {
                                 var7[var31].onChanged(var2);
                              }
                           }
                        }
                     }
                  }
               }
            }
         } finally {
            this.locked = false;
         }
      }
   }

   public static class SimpleChange<K, V> extends MapChangeListener.Change<K, V> {
      private K key;
      private V old;
      private V added;
      private boolean removeOp;
      private boolean addOp;

      public SimpleChange(ObservableMap<K, V> var1) {
         super(var1);
      }

      public SimpleChange(ObservableMap<K, V> var1, MapChangeListener.Change<? extends K, ? extends V> var2) {
         super(var1);
         this.key = (K)var2.getKey();
         this.old = (V)var2.getValueRemoved();
         this.added = (V)var2.getValueAdded();
         this.addOp = var2.wasAdded();
         this.removeOp = var2.wasRemoved();
      }

      public MapExpressionHelper.SimpleChange<K, V> setRemoved(K var1, V var2) {
         this.key = (K)var1;
         this.old = (V)var2;
         this.added = null;
         this.addOp = false;
         this.removeOp = true;
         return this;
      }

      public MapExpressionHelper.SimpleChange<K, V> setAdded(K var1, V var2) {
         this.key = (K)var1;
         this.old = null;
         this.added = (V)var2;
         this.addOp = true;
         this.removeOp = false;
         return this;
      }

      public MapExpressionHelper.SimpleChange<K, V> setPut(K var1, V var2, V var3) {
         this.key = (K)var1;
         this.old = (V)var2;
         this.added = (V)var3;
         this.addOp = true;
         this.removeOp = true;
         return this;
      }

      @Override
      public boolean wasAdded() {
         return this.addOp;
      }

      @Override
      public boolean wasRemoved() {
         return this.removeOp;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public V getValueAdded() {
         return this.added;
      }

      @Override
      public V getValueRemoved() {
         return this.old;
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.addOp) {
            if (this.removeOp) {
               var1.append("replaced ").append(this.old).append(" by ").append(this.added);
            } else {
               var1.append("added ").append(this.added);
            }
         } else {
            var1.append("removed ").append(this.old);
         }

         var1.append(" at key ").append(this.key);
         return var1.toString();
      }
   }

   private static class SingleChange<K, V> extends MapExpressionHelper<K, V> {
      private final ChangeListener<? super ObservableMap<K, V>> listener;
      private ObservableMap<K, V> currentValue;

      private SingleChange(ObservableMapValue<K, V> var1, ChangeListener<? super ObservableMap<K, V>> var2) {
         super(var1);
         this.listener = var2;
         this.currentValue = var1.getValue();
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(InvalidationListener var1) {
         return new MapExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> var1) {
         return new MapExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1) {
         return new MapExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent() {
         ObservableMap var1 = this.currentValue;
         this.currentValue = this.observable.getValue();
         if (this.currentValue != var1) {
            this.listener.changed(this.observable, var1, this.currentValue);
         }
      }

      @Override
      protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
         this.listener.changed(this.observable, this.currentValue, this.currentValue);
      }
   }

   private static class SingleInvalidation<K, V> extends MapExpressionHelper<K, V> {
      private final InvalidationListener listener;

      private SingleInvalidation(ObservableMapValue<K, V> var1, InvalidationListener var2) {
         super(var1);
         this.listener = var2;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(InvalidationListener var1) {
         return new MapExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(InvalidationListener var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> var1) {
         return new MapExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> var1) {
         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1) {
         return new MapExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent() {
         this.listener.invalidated(this.observable);
      }

      @Override
      protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
         this.listener.invalidated(this.observable);
      }
   }

   private static class SingleMapChange<K, V> extends MapExpressionHelper<K, V> {
      private final MapChangeListener<? super K, ? super V> listener;
      private ObservableMap<K, V> currentValue;

      private SingleMapChange(ObservableMapValue<K, V> var1, MapChangeListener<? super K, ? super V> var2) {
         super(var1);
         this.listener = var2;
         this.currentValue = var1.getValue();
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(InvalidationListener var1) {
         return new MapExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> var1) {
         return new MapExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> var1) {
         return this;
      }

      @Override
      protected MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1) {
         return new MapExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected void fireValueChangedEvent() {
         ObservableMap var1 = this.currentValue;
         this.currentValue = this.observable.getValue();
         if (this.currentValue != var1) {
            MapExpressionHelper.SimpleChange var2 = new MapExpressionHelper.SimpleChange<>(this.observable);
            if (this.currentValue == null) {
               for (Entry var4 : var1.entrySet()) {
                  this.listener.onChanged(var2.setRemoved(var4.getKey(), var4.getValue()));
               }
            } else if (var1 == null) {
               for (Entry var11 : this.currentValue.entrySet()) {
                  this.listener.onChanged(var2.setAdded(var11.getKey(), var11.getValue()));
               }
            } else {
               for (Entry var12 : var1.entrySet()) {
                  Object var5 = var12.getKey();
                  Object var6 = var12.getValue();
                  if (this.currentValue.containsKey(var5)) {
                     Object var7 = this.currentValue.get(var5);
                     if (var6 == null ? var7 != null : !var7.equals(var6)) {
                        this.listener.onChanged(var2.setPut(var5, var6, var7));
                     }
                  } else {
                     this.listener.onChanged(var2.setRemoved(var5, var6));
                  }
               }

               for (Entry var13 : this.currentValue.entrySet()) {
                  Object var14 = var13.getKey();
                  if (!var1.containsKey(var14)) {
                     this.listener.onChanged(var2.setAdded(var14, var13.getValue()));
                  }
               }
            }
         }
      }

      @Override
      protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
         this.listener.onChanged(new MapExpressionHelper.SimpleChange<>(this.observable, var1));
      }
   }
}
