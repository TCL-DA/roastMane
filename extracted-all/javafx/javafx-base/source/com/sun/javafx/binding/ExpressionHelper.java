package com.sun.javafx.binding;

import java.util.Arrays;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public abstract class ExpressionHelper<T> extends ExpressionHelperBase {
   protected final ObservableValue<T> observable;

   public static <T> ExpressionHelper<T> addListener(ExpressionHelper<T> var0, ObservableValue<T> var1, InvalidationListener var2) {
      if (var1 != null && var2 != null) {
         var1.getValue();
         return (ExpressionHelper<T>)(var0 == null ? new ExpressionHelper.SingleInvalidation<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <T> ExpressionHelper<T> removeListener(ExpressionHelper<T> var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <T> ExpressionHelper<T> addListener(ExpressionHelper<T> var0, ObservableValue<T> var1, ChangeListener<? super T> var2) {
      if (var1 != null && var2 != null) {
         return (ExpressionHelper<T>)(var0 == null ? new ExpressionHelper.SingleChange<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <T> ExpressionHelper<T> removeListener(ExpressionHelper<T> var0, ChangeListener<? super T> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <T> void fireValueChangedEvent(ExpressionHelper<T> var0) {
      if (var0 != null) {
         var0.fireValueChangedEvent();
      }
   }

   private ExpressionHelper(ObservableValue<T> var1) {
      this.observable = var1;
   }

   protected abstract ExpressionHelper<T> addListener(InvalidationListener var1);

   protected abstract ExpressionHelper<T> removeListener(InvalidationListener var1);

   protected abstract ExpressionHelper<T> addListener(ChangeListener<? super T> var1);

   protected abstract ExpressionHelper<T> removeListener(ChangeListener<? super T> var1);

   protected abstract void fireValueChangedEvent();

   private static class Generic<T> extends ExpressionHelper<T> {
      private InvalidationListener[] invalidationListeners;
      private ChangeListener<? super T>[] changeListeners;
      private int invalidationSize;
      private int changeSize;
      private boolean locked;
      private T currentValue;

      private Generic(ObservableValue<T> var1, InvalidationListener var2, InvalidationListener var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2, var3};
         this.invalidationSize = 2;
      }

      private Generic(ObservableValue<T> var1, ChangeListener<? super T> var2, ChangeListener<? super T> var3) {
         super(var1);
         this.changeListeners = new ChangeListener[]{var2, var3};
         this.changeSize = 2;
         this.currentValue = (T)var1.getValue();
      }

      private Generic(ObservableValue<T> var1, InvalidationListener var2, ChangeListener<? super T> var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2};
         this.invalidationSize = 1;
         this.changeListeners = new ChangeListener[]{var3};
         this.changeSize = 1;
         this.currentValue = (T)var1.getValue();
      }

      protected ExpressionHelper.Generic<T> addListener(InvalidationListener var1) {
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
      protected ExpressionHelper<T> removeListener(InvalidationListener var1) {
         if (this.invalidationListeners != null) {
            for (int var2 = 0; var2 < this.invalidationSize; var2++) {
               if (var1.equals(this.invalidationListeners[var2])) {
                  if (this.invalidationSize == 1) {
                     if (this.changeSize == 1) {
                        return new ExpressionHelper.SingleChange<>(this.observable, this.changeListeners[0]);
                     }

                     this.invalidationListeners = null;
                     this.invalidationSize = 0;
                  } else {
                     if (this.invalidationSize == 2 && this.changeSize == 0) {
                        return new ExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[1 - var2]);
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
      protected ExpressionHelper<T> addListener(ChangeListener<? super T> var1) {
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
      protected ExpressionHelper<T> removeListener(ChangeListener<? super T> var1) {
         if (this.changeListeners != null) {
            for (int var2 = 0; var2 < this.changeSize; var2++) {
               if (var1.equals(this.changeListeners[var2])) {
                  if (this.changeSize == 1) {
                     if (this.invalidationSize == 1) {
                        return new ExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[0]);
                     }

                     this.changeListeners = null;
                     this.changeSize = 0;
                  } else {
                     if (this.changeSize == 2 && this.invalidationSize == 0) {
                        return new ExpressionHelper.SingleChange<>(this.observable, this.changeListeners[1 - var2]);
                     }

                     int var3 = this.changeSize - var2 - 1;
                     ChangeListener[] var4 = this.changeListeners;
                     if (this.locked) {
                        this.changeListeners = new ChangeListener[this.changeListeners.length];
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
      protected void fireValueChangedEvent() {
         InvalidationListener[] var1 = this.invalidationListeners;
         int var2 = this.invalidationSize;
         ChangeListener[] var3 = this.changeListeners;
         int var4 = this.changeSize;

         try {
            this.locked = true;

            for (int var5 = 0; var5 < var2; var5++) {
               try {
                  var1[var5].invalidated(this.observable);
               } catch (Exception var14) {
                  Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var14);
               }
            }

            if (var4 > 0) {
               Object var16 = this.currentValue;
               this.currentValue = this.observable.getValue();
               boolean var6 = this.currentValue == null ? var16 != null : !this.currentValue.equals(var16);
               if (var6) {
                  for (int var7 = 0; var7 < var4; var7++) {
                     try {
                        var3[var7].changed(this.observable, (T)var16, this.currentValue);
                     } catch (Exception var13) {
                        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var13);
                     }
                  }
               }
            }
         } finally {
            this.locked = false;
         }
      }
   }

   private static class SingleChange<T> extends ExpressionHelper<T> {
      private final ChangeListener<? super T> listener;
      private T currentValue;

      private SingleChange(ObservableValue<T> var1, ChangeListener<? super T> var2) {
         super(var1);
         this.listener = var2;
         this.currentValue = (T)var1.getValue();
      }

      @Override
      protected ExpressionHelper<T> addListener(InvalidationListener var1) {
         return new ExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected ExpressionHelper<T> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected ExpressionHelper<T> addListener(ChangeListener<? super T> var1) {
         return new ExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ExpressionHelper<T> removeListener(ChangeListener<? super T> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected void fireValueChangedEvent() {
         Object var1 = this.currentValue;
         this.currentValue = this.observable.getValue();
         boolean var2 = this.currentValue == null ? var1 != null : !this.currentValue.equals(var1);
         if (var2) {
            try {
               this.listener.changed(this.observable, (T)var1, this.currentValue);
            } catch (Exception var4) {
               Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var4);
            }
         }
      }
   }

   private static class SingleInvalidation<T> extends ExpressionHelper<T> {
      private final InvalidationListener listener;

      private SingleInvalidation(ObservableValue<T> var1, InvalidationListener var2) {
         super(var1);
         this.listener = var2;
      }

      @Override
      protected ExpressionHelper<T> addListener(InvalidationListener var1) {
         return new ExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ExpressionHelper<T> removeListener(InvalidationListener var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected ExpressionHelper<T> addListener(ChangeListener<? super T> var1) {
         return new ExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ExpressionHelper<T> removeListener(ChangeListener<? super T> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent() {
         try {
            this.listener.invalidated(this.observable);
         } catch (Exception var2) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var2);
         }
      }
   }
}
