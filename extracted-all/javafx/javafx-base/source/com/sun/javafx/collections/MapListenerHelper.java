package com.sun.javafx.collections;

import com.sun.javafx.binding.ExpressionHelperBase;
import java.util.Arrays;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;

public abstract class MapListenerHelper<K, V> extends ExpressionHelperBase {
   public static <K, V> MapListenerHelper<K, V> addListener(MapListenerHelper<K, V> var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return (MapListenerHelper<K, V>)(var0 == null ? new MapListenerHelper.SingleInvalidation<>(var1) : var0.addListener(var1));
      }
   }

   public static <K, V> MapListenerHelper<K, V> removeListener(MapListenerHelper<K, V> var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <K, V> MapListenerHelper<K, V> addListener(MapListenerHelper<K, V> var0, MapChangeListener<? super K, ? super V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return (MapListenerHelper<K, V>)(var0 == null ? new MapListenerHelper.SingleChange<>(var1) : var0.addListener(var1));
      }
   }

   public static <K, V> MapListenerHelper<K, V> removeListener(MapListenerHelper<K, V> var0, MapChangeListener<? super K, ? super V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <K, V> void fireValueChangedEvent(MapListenerHelper<K, V> var0, MapChangeListener.Change<? extends K, ? extends V> var1) {
      if (var0 != null) {
         var0.fireValueChangedEvent(var1);
      }
   }

   public static <K, V> boolean hasListeners(MapListenerHelper<K, V> var0) {
      return var0 != null;
   }

   protected abstract MapListenerHelper<K, V> addListener(InvalidationListener var1);

   protected abstract MapListenerHelper<K, V> removeListener(InvalidationListener var1);

   protected abstract MapListenerHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1);

   protected abstract MapListenerHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1);

   protected abstract void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1);

   private static class Generic<K, V> extends MapListenerHelper<K, V> {
      private InvalidationListener[] invalidationListeners;
      private MapChangeListener<? super K, ? super V>[] changeListeners;
      private int invalidationSize;
      private int changeSize;
      private boolean locked;

      private Generic(InvalidationListener var1, InvalidationListener var2) {
         this.invalidationListeners = new InvalidationListener[]{var1, var2};
         this.invalidationSize = 2;
      }

      private Generic(MapChangeListener<? super K, ? super V> var1, MapChangeListener<? super K, ? super V> var2) {
         this.changeListeners = new MapChangeListener[]{var1, var2};
         this.changeSize = 2;
      }

      private Generic(InvalidationListener var1, MapChangeListener<? super K, ? super V> var2) {
         this.invalidationListeners = new InvalidationListener[]{var1};
         this.invalidationSize = 1;
         this.changeListeners = new MapChangeListener[]{var2};
         this.changeSize = 1;
      }

      protected MapListenerHelper.Generic<K, V> addListener(InvalidationListener var1) {
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
      protected MapListenerHelper<K, V> removeListener(InvalidationListener var1) {
         if (this.invalidationListeners != null) {
            for (int var2 = 0; var2 < this.invalidationSize; var2++) {
               if (var1.equals(this.invalidationListeners[var2])) {
                  if (this.invalidationSize == 1) {
                     if (this.changeSize == 1) {
                        return new MapListenerHelper.SingleChange<>(this.changeListeners[0]);
                     }

                     this.invalidationListeners = null;
                     this.invalidationSize = 0;
                  } else {
                     if (this.invalidationSize == 2 && this.changeSize == 0) {
                        return new MapListenerHelper.SingleInvalidation<>(this.invalidationListeners[1 - var2]);
                     }

                     int var3 = this.invalidationSize - var2 - 1;
                     InvalidationListener[] var4 = this.invalidationListeners;
                     if (this.locked) {
                        this.invalidationListeners = new InvalidationListener[this.invalidationListeners.length];
                        System.arraycopy(var4, 0, this.invalidationListeners, 0, var2);
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
      protected MapListenerHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1) {
         if (this.changeListeners == null) {
            this.changeListeners = new MapChangeListener[]{var1};
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

         return this;
      }

      @Override
      protected MapListenerHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1) {
         if (this.changeListeners != null) {
            for (int var2 = 0; var2 < this.changeSize; var2++) {
               if (var1.equals(this.changeListeners[var2])) {
                  if (this.changeSize == 1) {
                     if (this.invalidationSize == 1) {
                        return new MapListenerHelper.SingleInvalidation<>(this.invalidationListeners[0]);
                     }

                     this.changeListeners = null;
                     this.changeSize = 0;
                  } else {
                     if (this.changeSize == 2 && this.invalidationSize == 0) {
                        return new MapListenerHelper.SingleChange<>(this.changeListeners[1 - var2]);
                     }

                     int var3 = this.changeSize - var2 - 1;
                     MapChangeListener[] var4 = this.changeListeners;
                     if (this.locked) {
                        this.changeListeners = new MapChangeListener[this.changeListeners.length];
                        System.arraycopy(var4, 0, this.changeListeners, 0, var2);
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
      protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
         InvalidationListener[] var2 = this.invalidationListeners;
         int var3 = this.invalidationSize;
         MapChangeListener[] var4 = this.changeListeners;
         int var5 = this.changeSize;

         try {
            this.locked = true;

            for (int var6 = 0; var6 < var3; var6++) {
               try {
                  var2[var6].invalidated(var1.getMap());
               } catch (Exception var13) {
                  Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var13);
               }
            }

            for (int var15 = 0; var15 < var5; var15++) {
               try {
                  var4[var15].onChanged(var1);
               } catch (Exception var12) {
                  Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var12);
               }
            }
         } finally {
            this.locked = false;
         }
      }
   }

   private static class SingleChange<K, V> extends MapListenerHelper<K, V> {
      private final MapChangeListener<? super K, ? super V> listener;

      private SingleChange(MapChangeListener<? super K, ? super V> var1) {
         this.listener = var1;
      }

      @Override
      protected MapListenerHelper<K, V> addListener(InvalidationListener var1) {
         return new MapListenerHelper.Generic<>(var1, this.listener);
      }

      @Override
      protected MapListenerHelper<K, V> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected MapListenerHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1) {
         return new MapListenerHelper.Generic<>(this.listener, var1);
      }

      @Override
      protected MapListenerHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
         try {
            this.listener.onChanged(var1);
         } catch (Exception var3) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var3);
         }
      }
   }

   private static class SingleInvalidation<K, V> extends MapListenerHelper<K, V> {
      private final InvalidationListener listener;

      private SingleInvalidation(InvalidationListener var1) {
         this.listener = var1;
      }

      @Override
      protected MapListenerHelper<K, V> addListener(InvalidationListener var1) {
         return new MapListenerHelper.Generic<>(this.listener, var1);
      }

      @Override
      protected MapListenerHelper<K, V> removeListener(InvalidationListener var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected MapListenerHelper<K, V> addListener(MapChangeListener<? super K, ? super V> var1) {
         return new MapListenerHelper.Generic<>(this.listener, var1);
      }

      @Override
      protected MapListenerHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
         try {
            this.listener.invalidated(var1.getMap());
         } catch (Exception var3) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var3);
         }
      }
   }
}
