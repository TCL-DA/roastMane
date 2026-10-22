package com.sun.javafx.collections;

import com.sun.javafx.binding.ExpressionHelperBase;
import java.util.Arrays;
import javafx.beans.InvalidationListener;
import javafx.collections.ArrayChangeListener;
import javafx.collections.ObservableArray;

public abstract class ArrayListenerHelper<T extends ObservableArray<T>> extends ExpressionHelperBase {
   protected final T observable;

   public static <T extends ObservableArray<T>> ArrayListenerHelper addListener(ArrayListenerHelper var0, T var1, InvalidationListener var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         return (ArrayListenerHelper)(var0 == null ? new ArrayListenerHelper.SingleInvalidation((T)var1, var2) : var0.addListener(var2));
      }
   }

   public static ArrayListenerHelper removeListener(ArrayListenerHelper var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <T extends ObservableArray<T>> ArrayListenerHelper addListener(ArrayListenerHelper var0, T var1, ArrayChangeListener var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         return (ArrayListenerHelper)(var0 == null ? new ArrayListenerHelper.SingleChange((T)var1, var2) : var0.addListener(var2));
      }
   }

   public static ArrayListenerHelper removeListener(ArrayListenerHelper var0, ArrayChangeListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static void fireValueChangedEvent(ArrayListenerHelper var0, boolean var1, int var2, int var3) {
      if (var0 != null && (var2 < var3 || var1)) {
         var0.fireValueChangedEvent(var1, var2, var3);
      }
   }

   public static boolean hasListeners(ArrayListenerHelper var0) {
      return var0 != null;
   }

   public ArrayListenerHelper(T var1) {
      this.observable = (T)var1;
   }

   protected abstract ArrayListenerHelper addListener(InvalidationListener var1);

   protected abstract ArrayListenerHelper removeListener(InvalidationListener var1);

   protected abstract ArrayListenerHelper addListener(ArrayChangeListener<T> var1);

   protected abstract ArrayListenerHelper removeListener(ArrayChangeListener<T> var1);

   protected abstract void fireValueChangedEvent(boolean var1, int var2, int var3);

   private static class Generic<T extends ObservableArray<T>> extends ArrayListenerHelper<T> {
      private InvalidationListener[] invalidationListeners;
      private ArrayChangeListener[] changeListeners;
      private int invalidationSize;
      private int changeSize;
      private boolean locked;

      private Generic(T var1, InvalidationListener var2, InvalidationListener var3) {
         super((T)var1);
         this.invalidationListeners = new InvalidationListener[]{var2, var3};
         this.invalidationSize = 2;
      }

      private Generic(T var1, ArrayChangeListener var2, ArrayChangeListener var3) {
         super((T)var1);
         this.changeListeners = new ArrayChangeListener[]{var2, var3};
         this.changeSize = 2;
      }

      private Generic(T var1, InvalidationListener var2, ArrayChangeListener var3) {
         super((T)var1);
         this.invalidationListeners = new InvalidationListener[]{var2};
         this.invalidationSize = 1;
         this.changeListeners = new ArrayChangeListener[]{var3};
         this.changeSize = 1;
      }

      protected ArrayListenerHelper.Generic addListener(InvalidationListener var1) {
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
      protected ArrayListenerHelper removeListener(InvalidationListener var1) {
         if (this.invalidationListeners != null) {
            for (int var2 = 0; var2 < this.invalidationSize; var2++) {
               if (var1.equals(this.invalidationListeners[var2])) {
                  if (this.invalidationSize == 1) {
                     if (this.changeSize == 1) {
                        return new ArrayListenerHelper.SingleChange(this.observable, this.changeListeners[0]);
                     }

                     this.invalidationListeners = null;
                     this.invalidationSize = 0;
                  } else {
                     if (this.invalidationSize == 2 && this.changeSize == 0) {
                        return new ArrayListenerHelper.SingleInvalidation(this.observable, this.invalidationListeners[1 - var2]);
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
      protected ArrayListenerHelper addListener(ArrayChangeListener<T> var1) {
         if (this.changeListeners == null) {
            this.changeListeners = new ArrayChangeListener[]{var1};
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
      protected ArrayListenerHelper removeListener(ArrayChangeListener<T> var1) {
         if (this.changeListeners != null) {
            for (int var2 = 0; var2 < this.changeSize; var2++) {
               if (var1.equals(this.changeListeners[var2])) {
                  if (this.changeSize == 1) {
                     if (this.invalidationSize == 1) {
                        return new ArrayListenerHelper.SingleInvalidation(this.observable, this.invalidationListeners[0]);
                     }

                     this.changeListeners = null;
                     this.changeSize = 0;
                  } else {
                     if (this.changeSize == 2 && this.invalidationSize == 0) {
                        return new ArrayListenerHelper.SingleChange(this.observable, this.changeListeners[1 - var2]);
                     }

                     int var3 = this.changeSize - var2 - 1;
                     ArrayChangeListener[] var4 = this.changeListeners;
                     if (this.locked) {
                        this.changeListeners = new ArrayChangeListener[this.changeListeners.length];
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
      protected void fireValueChangedEvent(boolean var1, int var2, int var3) {
         InvalidationListener[] var4 = this.invalidationListeners;
         int var5 = this.invalidationSize;
         ArrayChangeListener[] var6 = this.changeListeners;
         int var7 = this.changeSize;

         try {
            this.locked = true;

            for (int var8 = 0; var8 < var5; var8++) {
               try {
                  var4[var8].invalidated(this.observable);
               } catch (Exception var15) {
                  Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var15);
               }
            }

            for (int var17 = 0; var17 < var7; var17++) {
               try {
                  var6[var17].onChanged(this.observable, var1, var2, var3);
               } catch (Exception var14) {
                  Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var14);
               }
            }
         } finally {
            this.locked = false;
         }
      }
   }

   private static class SingleChange<T extends ObservableArray<T>> extends ArrayListenerHelper<T> {
      private final ArrayChangeListener listener;

      private SingleChange(T var1, ArrayChangeListener var2) {
         super((T)var1);
         this.listener = var2;
      }

      @Override
      protected ArrayListenerHelper addListener(InvalidationListener var1) {
         return new ArrayListenerHelper.Generic(this.observable, var1, this.listener);
      }

      @Override
      protected ArrayListenerHelper removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected ArrayListenerHelper addListener(ArrayChangeListener var1) {
         return new ArrayListenerHelper.Generic(this.observable, this.listener, var1);
      }

      @Override
      protected ArrayListenerHelper removeListener(ArrayChangeListener var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected void fireValueChangedEvent(boolean var1, int var2, int var3) {
         try {
            this.listener.onChanged(this.observable, var1, var2, var3);
         } catch (Exception var5) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var5);
         }
      }
   }

   private static class SingleInvalidation<T extends ObservableArray<T>> extends ArrayListenerHelper<T> {
      private final InvalidationListener listener;

      private SingleInvalidation(T var1, InvalidationListener var2) {
         super((T)var1);
         this.listener = var2;
      }

      @Override
      protected ArrayListenerHelper addListener(InvalidationListener var1) {
         return new ArrayListenerHelper.Generic(this.observable, this.listener, var1);
      }

      @Override
      protected ArrayListenerHelper removeListener(InvalidationListener var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected ArrayListenerHelper addListener(ArrayChangeListener var1) {
         return new ArrayListenerHelper.Generic(this.observable, this.listener, var1);
      }

      @Override
      protected ArrayListenerHelper removeListener(ArrayChangeListener var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent(boolean var1, int var2, int var3) {
         try {
            this.listener.invalidated(this.observable);
         } catch (Exception var5) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var5);
         }
      }
   }
}
