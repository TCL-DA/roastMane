package com.sun.javafx.collections;

import com.sun.javafx.binding.ExpressionHelperBase;
import java.util.Arrays;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;

public abstract class ListListenerHelper<E> extends ExpressionHelperBase {
   public static <E> ListListenerHelper<E> addListener(ListListenerHelper<E> var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return (ListListenerHelper<E>)(var0 == null ? new ListListenerHelper.SingleInvalidation<>(var1) : var0.addListener(var1));
      }
   }

   public static <E> ListListenerHelper<E> removeListener(ListListenerHelper<E> var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <E> ListListenerHelper<E> addListener(ListListenerHelper<E> var0, ListChangeListener<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return (ListListenerHelper<E>)(var0 == null ? new ListListenerHelper.SingleChange<>(var1) : var0.addListener(var1));
      }
   }

   public static <E> ListListenerHelper<E> removeListener(ListListenerHelper<E> var0, ListChangeListener<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <E> void fireValueChangedEvent(ListListenerHelper<E> var0, ListChangeListener.Change<? extends E> var1) {
      if (var0 != null) {
         var1.reset();
         var0.fireValueChangedEvent(var1);
      }
   }

   public static <E> boolean hasListeners(ListListenerHelper<E> var0) {
      return var0 != null;
   }

   protected abstract ListListenerHelper<E> addListener(InvalidationListener var1);

   protected abstract ListListenerHelper<E> removeListener(InvalidationListener var1);

   protected abstract ListListenerHelper<E> addListener(ListChangeListener<? super E> var1);

   protected abstract ListListenerHelper<E> removeListener(ListChangeListener<? super E> var1);

   protected abstract void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1);

   private static class Generic<E> extends ListListenerHelper<E> {
      private InvalidationListener[] invalidationListeners;
      private ListChangeListener<? super E>[] changeListeners;
      private int invalidationSize;
      private int changeSize;
      private boolean locked;

      private Generic(InvalidationListener var1, InvalidationListener var2) {
         this.invalidationListeners = new InvalidationListener[]{var1, var2};
         this.invalidationSize = 2;
      }

      private Generic(ListChangeListener<? super E> var1, ListChangeListener<? super E> var2) {
         this.changeListeners = new ListChangeListener[]{var1, var2};
         this.changeSize = 2;
      }

      private Generic(InvalidationListener var1, ListChangeListener<? super E> var2) {
         this.invalidationListeners = new InvalidationListener[]{var1};
         this.invalidationSize = 1;
         this.changeListeners = new ListChangeListener[]{var2};
         this.changeSize = 1;
      }

      protected ListListenerHelper.Generic<E> addListener(InvalidationListener var1) {
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
      protected ListListenerHelper<E> removeListener(InvalidationListener var1) {
         if (this.invalidationListeners != null) {
            for (int var2 = 0; var2 < this.invalidationSize; var2++) {
               if (var1.equals(this.invalidationListeners[var2])) {
                  if (this.invalidationSize == 1) {
                     if (this.changeSize == 1) {
                        return new ListListenerHelper.SingleChange<>(this.changeListeners[0]);
                     }

                     this.invalidationListeners = null;
                     this.invalidationSize = 0;
                  } else {
                     if (this.invalidationSize == 2 && this.changeSize == 0) {
                        return new ListListenerHelper.SingleInvalidation<>(this.invalidationListeners[1 - var2]);
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
      protected ListListenerHelper<E> addListener(ListChangeListener<? super E> var1) {
         if (this.changeListeners == null) {
            this.changeListeners = new ListChangeListener[]{var1};
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
      protected ListListenerHelper<E> removeListener(ListChangeListener<? super E> var1) {
         if (this.changeListeners != null) {
            for (int var2 = 0; var2 < this.changeSize; var2++) {
               if (var1.equals(this.changeListeners[var2])) {
                  if (this.changeSize == 1) {
                     if (this.invalidationSize == 1) {
                        return new ListListenerHelper.SingleInvalidation<>(this.invalidationListeners[0]);
                     }

                     this.changeListeners = null;
                     this.changeSize = 0;
                  } else {
                     if (this.changeSize == 2 && this.invalidationSize == 0) {
                        return new ListListenerHelper.SingleChange<>(this.changeListeners[1 - var2]);
                     }

                     int var3 = this.changeSize - var2 - 1;
                     ListChangeListener[] var4 = this.changeListeners;
                     if (this.locked) {
                        this.changeListeners = new ListChangeListener[this.changeListeners.length];
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
      protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
         InvalidationListener[] var2 = this.invalidationListeners;
         int var3 = this.invalidationSize;
         ListChangeListener[] var4 = this.changeListeners;
         int var5 = this.changeSize;

         try {
            this.locked = true;

            for (int var6 = 0; var6 < var3; var6++) {
               try {
                  var2[var6].invalidated(var1.getList());
               } catch (Exception var13) {
                  Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var13);
               }
            }

            for (int var15 = 0; var15 < var5; var15++) {
               var1.reset();

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

   private static class SingleChange<E> extends ListListenerHelper<E> {
      private final ListChangeListener<? super E> listener;

      private SingleChange(ListChangeListener<? super E> var1) {
         this.listener = var1;
      }

      @Override
      protected ListListenerHelper<E> addListener(InvalidationListener var1) {
         return new ListListenerHelper.Generic<>(var1, this.listener);
      }

      @Override
      protected ListListenerHelper<E> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected ListListenerHelper<E> addListener(ListChangeListener<? super E> var1) {
         return new ListListenerHelper.Generic<>(this.listener, var1);
      }

      @Override
      protected ListListenerHelper<E> removeListener(ListChangeListener<? super E> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
         try {
            this.listener.onChanged(var1);
         } catch (Exception var3) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var3);
         }
      }
   }

   private static class SingleInvalidation<E> extends ListListenerHelper<E> {
      private final InvalidationListener listener;

      private SingleInvalidation(InvalidationListener var1) {
         this.listener = var1;
      }

      @Override
      protected ListListenerHelper<E> addListener(InvalidationListener var1) {
         return new ListListenerHelper.Generic<>(this.listener, var1);
      }

      @Override
      protected ListListenerHelper<E> removeListener(InvalidationListener var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected ListListenerHelper<E> addListener(ListChangeListener<? super E> var1) {
         return new ListListenerHelper.Generic<>(this.listener, var1);
      }

      @Override
      protected ListListenerHelper<E> removeListener(ListChangeListener<? super E> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
         try {
            this.listener.invalidated(var1.getList());
         } catch (Exception var3) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var3);
         }
      }
   }
}
