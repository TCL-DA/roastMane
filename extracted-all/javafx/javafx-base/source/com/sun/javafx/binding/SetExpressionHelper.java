package com.sun.javafx.binding;

import java.util.Arrays;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableSetValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public abstract class SetExpressionHelper<E> extends ExpressionHelperBase {
   protected final ObservableSetValue<E> observable;

   public static <E> SetExpressionHelper<E> addListener(SetExpressionHelper<E> var0, ObservableSetValue<E> var1, InvalidationListener var2) {
      if (var1 != null && var2 != null) {
         var1.getValue();
         return (SetExpressionHelper<E>)(var0 == null ? new SetExpressionHelper.SingleInvalidation<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <E> SetExpressionHelper<E> removeListener(SetExpressionHelper<E> var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <E> SetExpressionHelper<E> addListener(SetExpressionHelper<E> var0, ObservableSetValue<E> var1, ChangeListener<? super ObservableSet<E>> var2) {
      if (var1 != null && var2 != null) {
         return (SetExpressionHelper<E>)(var0 == null ? new SetExpressionHelper.SingleChange<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <E> SetExpressionHelper<E> removeListener(SetExpressionHelper<E> var0, ChangeListener<? super ObservableSet<E>> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <E> SetExpressionHelper<E> addListener(SetExpressionHelper<E> var0, ObservableSetValue<E> var1, SetChangeListener<? super E> var2) {
      if (var1 != null && var2 != null) {
         return (SetExpressionHelper<E>)(var0 == null ? new SetExpressionHelper.SingleSetChange<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <E> SetExpressionHelper<E> removeListener(SetExpressionHelper<E> var0, SetChangeListener<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <E> void fireValueChangedEvent(SetExpressionHelper<E> var0) {
      if (var0 != null) {
         var0.fireValueChangedEvent();
      }
   }

   public static <E> void fireValueChangedEvent(SetExpressionHelper<E> var0, SetChangeListener.Change<? extends E> var1) {
      if (var0 != null) {
         var0.fireValueChangedEvent(var1);
      }
   }

   protected SetExpressionHelper(ObservableSetValue<E> var1) {
      this.observable = var1;
   }

   protected abstract SetExpressionHelper<E> addListener(InvalidationListener var1);

   protected abstract SetExpressionHelper<E> removeListener(InvalidationListener var1);

   protected abstract SetExpressionHelper<E> addListener(ChangeListener<? super ObservableSet<E>> var1);

   protected abstract SetExpressionHelper<E> removeListener(ChangeListener<? super ObservableSet<E>> var1);

   protected abstract SetExpressionHelper<E> addListener(SetChangeListener<? super E> var1);

   protected abstract SetExpressionHelper<E> removeListener(SetChangeListener<? super E> var1);

   protected abstract void fireValueChangedEvent();

   protected abstract void fireValueChangedEvent(SetChangeListener.Change<? extends E> var1);

   private static class Generic<E> extends SetExpressionHelper<E> {
      private InvalidationListener[] invalidationListeners;
      private ChangeListener<? super ObservableSet<E>>[] changeListeners;
      private SetChangeListener<? super E>[] setChangeListeners;
      private int invalidationSize;
      private int changeSize;
      private int setChangeSize;
      private boolean locked;
      private ObservableSet<E> currentValue;

      private Generic(ObservableSetValue<E> var1, InvalidationListener var2, InvalidationListener var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2, var3};
         this.invalidationSize = 2;
      }

      private Generic(ObservableSetValue<E> var1, ChangeListener<? super ObservableSet<E>> var2, ChangeListener<? super ObservableSet<E>> var3) {
         super(var1);
         this.changeListeners = new ChangeListener[]{var2, var3};
         this.changeSize = 2;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableSetValue<E> var1, SetChangeListener<? super E> var2, SetChangeListener<? super E> var3) {
         super(var1);
         this.setChangeListeners = new SetChangeListener[]{var2, var3};
         this.setChangeSize = 2;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableSetValue<E> var1, InvalidationListener var2, ChangeListener<? super ObservableSet<E>> var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2};
         this.invalidationSize = 1;
         this.changeListeners = new ChangeListener[]{var3};
         this.changeSize = 1;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableSetValue<E> var1, InvalidationListener var2, SetChangeListener<? super E> var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2};
         this.invalidationSize = 1;
         this.setChangeListeners = new SetChangeListener[]{var3};
         this.setChangeSize = 1;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableSetValue<E> var1, ChangeListener<? super ObservableSet<E>> var2, SetChangeListener<? super E> var3) {
         super(var1);
         this.changeListeners = new ChangeListener[]{var2};
         this.changeSize = 1;
         this.setChangeListeners = new SetChangeListener[]{var3};
         this.setChangeSize = 1;
         this.currentValue = var1.getValue();
      }

      @Override
      protected SetExpressionHelper<E> addListener(InvalidationListener var1) {
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
      protected SetExpressionHelper<E> removeListener(InvalidationListener var1) {
         if (this.invalidationListeners != null) {
            for (int var2 = 0; var2 < this.invalidationSize; var2++) {
               if (var1.equals(this.invalidationListeners[var2])) {
                  if (this.invalidationSize == 1) {
                     if (this.changeSize == 1 && this.setChangeSize == 0) {
                        return new SetExpressionHelper.SingleChange<>(this.observable, this.changeListeners[0]);
                     }

                     if (this.changeSize == 0 && this.setChangeSize == 1) {
                        return new SetExpressionHelper.SingleSetChange<>(this.observable, this.setChangeListeners[0]);
                     }

                     this.invalidationListeners = null;
                     this.invalidationSize = 0;
                  } else {
                     if (this.invalidationSize == 2 && this.changeSize == 0 && this.setChangeSize == 0) {
                        return new SetExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[1 - var2]);
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
      protected SetExpressionHelper<E> addListener(ChangeListener<? super ObservableSet<E>> var1) {
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
      protected SetExpressionHelper<E> removeListener(ChangeListener<? super ObservableSet<E>> var1) {
         if (this.changeListeners != null) {
            for (int var2 = 0; var2 < this.changeSize; var2++) {
               if (var1.equals(this.changeListeners[var2])) {
                  if (this.changeSize == 1) {
                     if (this.invalidationSize == 1 && this.setChangeSize == 0) {
                        return new SetExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[0]);
                     }

                     if (this.invalidationSize == 0 && this.setChangeSize == 1) {
                        return new SetExpressionHelper.SingleSetChange<>(this.observable, this.setChangeListeners[0]);
                     }

                     this.changeListeners = null;
                     this.changeSize = 0;
                  } else {
                     if (this.changeSize == 2 && this.invalidationSize == 0 && this.setChangeSize == 0) {
                        return new SetExpressionHelper.SingleChange<>(this.observable, this.changeListeners[1 - var2]);
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
      protected SetExpressionHelper<E> addListener(SetChangeListener<? super E> var1) {
         if (this.setChangeListeners == null) {
            this.setChangeListeners = new SetChangeListener[]{var1};
            this.setChangeSize = 1;
         } else {
            int var2 = this.setChangeListeners.length;
            if (this.locked) {
               int var3 = this.setChangeSize < var2 ? var2 : var2 * 3 / 2 + 1;
               this.setChangeListeners = Arrays.copyOf(this.setChangeListeners, var3);
            } else if (this.setChangeSize == var2) {
               this.setChangeSize = trim(this.setChangeSize, this.setChangeListeners);
               if (this.setChangeSize == var2) {
                  int var4 = var2 * 3 / 2 + 1;
                  this.setChangeListeners = Arrays.copyOf(this.setChangeListeners, var4);
               }
            }

            this.setChangeListeners[this.setChangeSize++] = var1;
         }

         if (this.setChangeSize == 1) {
            this.currentValue = this.observable.getValue();
         }

         return this;
      }

      @Override
      protected SetExpressionHelper<E> removeListener(SetChangeListener<? super E> var1) {
         if (this.setChangeListeners != null) {
            for (int var2 = 0; var2 < this.setChangeSize; var2++) {
               if (var1.equals(this.setChangeListeners[var2])) {
                  if (this.setChangeSize == 1) {
                     if (this.invalidationSize == 1 && this.changeSize == 0) {
                        return new SetExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[0]);
                     }

                     if (this.invalidationSize == 0 && this.changeSize == 1) {
                        return new SetExpressionHelper.SingleChange<>(this.observable, this.changeListeners[0]);
                     }

                     this.setChangeListeners = null;
                     this.setChangeSize = 0;
                  } else {
                     if (this.setChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                        return new SetExpressionHelper.SingleSetChange<>(this.observable, this.setChangeListeners[1 - var2]);
                     }

                     int var3 = this.setChangeSize - var2 - 1;
                     SetChangeListener[] var4 = this.setChangeListeners;
                     if (this.locked) {
                        this.setChangeListeners = new SetChangeListener[this.setChangeListeners.length];
                        System.arraycopy(var4, 0, this.setChangeListeners, 0, var2 + 1);
                     }

                     if (var3 > 0) {
                        System.arraycopy(var4, var2 + 1, this.setChangeListeners, var2, var3);
                     }

                     this.setChangeSize--;
                     if (!this.locked) {
                        this.setChangeListeners[this.setChangeSize] = null;
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
         if (this.changeSize == 0 && this.setChangeSize == 0) {
            this.notifyListeners(this.currentValue, null);
         } else {
            ObservableSet var1 = this.currentValue;
            this.currentValue = this.observable.getValue();
            this.notifyListeners(var1, null);
         }
      }

      @Override
      protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> var1) {
         SetExpressionHelper.SimpleChange var2 = this.setChangeSize == 0 ? null : new SetExpressionHelper.SimpleChange<>(this.observable, var1);
         this.notifyListeners(this.currentValue, var2);
      }

      private void notifyListeners(ObservableSet<E> var1, SetExpressionHelper.SimpleChange<E> var2) {
         InvalidationListener[] var3 = this.invalidationListeners;
         int var4 = this.invalidationSize;
         ChangeListener[] var5 = this.changeListeners;
         int var6 = this.changeSize;
         SetChangeListener[] var7 = this.setChangeListeners;
         int var8 = this.setChangeSize;

         try {
            this.locked = true;

            for (int var9 = 0; var9 < var4; var9++) {
               var3[var9].invalidated(this.observable);
            }

            if (this.currentValue != var1 || var2 != null) {
               for (int var16 = 0; var16 < var6; var16++) {
                  var5[var16].changed(this.observable, var1, this.currentValue);
               }

               if (var8 > 0) {
                  if (var2 != null) {
                     for (int var17 = 0; var17 < var8; var17++) {
                        var7[var17].onChanged(var2);
                     }
                  } else {
                     var2 = new SetExpressionHelper.SimpleChange<>(this.observable);
                     if (this.currentValue == null) {
                        for (Object var10 : var1) {
                           var2.setRemoved(var10);

                           for (int var11 = 0; var11 < var8; var11++) {
                              var7[var11].onChanged(var2);
                           }
                        }
                     } else if (var1 == null) {
                        for (Object var22 : this.currentValue) {
                           var2.setAdded(var22);

                           for (int var25 = 0; var25 < var8; var25++) {
                              var7[var25].onChanged(var2);
                           }
                        }
                     } else {
                        for (Object var23 : var1) {
                           if (!this.currentValue.contains(var23)) {
                              var2.setRemoved(var23);

                              for (int var26 = 0; var26 < var8; var26++) {
                                 var7[var26].onChanged(var2);
                              }
                           }
                        }

                        for (Object var24 : this.currentValue) {
                           if (!var1.contains(var24)) {
                              var2.setAdded(var24);

                              for (int var27 = 0; var27 < var8; var27++) {
                                 var7[var27].onChanged(var2);
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

   public static class SimpleChange<E> extends SetChangeListener.Change<E> {
      private E old;
      private E added;
      private boolean addOp;

      public SimpleChange(ObservableSet<E> var1) {
         super(var1);
      }

      public SimpleChange(ObservableSet<E> var1, SetChangeListener.Change<? extends E> var2) {
         super(var1);
         this.old = (E)var2.getElementRemoved();
         this.added = (E)var2.getElementAdded();
         this.addOp = var2.wasAdded();
      }

      public SetExpressionHelper.SimpleChange<E> setRemoved(E var1) {
         this.old = (E)var1;
         this.added = null;
         this.addOp = false;
         return this;
      }

      public SetExpressionHelper.SimpleChange<E> setAdded(E var1) {
         this.old = null;
         this.added = (E)var1;
         this.addOp = true;
         return this;
      }

      @Override
      public boolean wasAdded() {
         return this.addOp;
      }

      @Override
      public boolean wasRemoved() {
         return !this.addOp;
      }

      @Override
      public E getElementAdded() {
         return this.added;
      }

      @Override
      public E getElementRemoved() {
         return this.old;
      }

      @Override
      public String toString() {
         return this.addOp ? "added " + this.added : "removed " + this.old;
      }
   }

   private static class SingleChange<E> extends SetExpressionHelper<E> {
      private final ChangeListener<? super ObservableSet<E>> listener;
      private ObservableSet<E> currentValue;

      private SingleChange(ObservableSetValue<E> var1, ChangeListener<? super ObservableSet<E>> var2) {
         super(var1);
         this.listener = var2;
         this.currentValue = var1.getValue();
      }

      @Override
      protected SetExpressionHelper<E> addListener(InvalidationListener var1) {
         return new SetExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected SetExpressionHelper<E> addListener(ChangeListener<? super ObservableSet<E>> var1) {
         return new SetExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(ChangeListener<? super ObservableSet<E>> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected SetExpressionHelper<E> addListener(SetChangeListener<? super E> var1) {
         return new SetExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(SetChangeListener<? super E> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent() {
         ObservableSet var1 = this.currentValue;
         this.currentValue = this.observable.getValue();
         if (this.currentValue != var1) {
            this.listener.changed(this.observable, var1, this.currentValue);
         }
      }

      @Override
      protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> var1) {
         this.listener.changed(this.observable, this.currentValue, this.currentValue);
      }
   }

   private static class SingleInvalidation<E> extends SetExpressionHelper<E> {
      private final InvalidationListener listener;

      private SingleInvalidation(ObservableSetValue<E> var1, InvalidationListener var2) {
         super(var1);
         this.listener = var2;
      }

      @Override
      protected SetExpressionHelper<E> addListener(InvalidationListener var1) {
         return new SetExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(InvalidationListener var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected SetExpressionHelper<E> addListener(ChangeListener<? super ObservableSet<E>> var1) {
         return new SetExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(ChangeListener<? super ObservableSet<E>> var1) {
         return this;
      }

      @Override
      protected SetExpressionHelper<E> addListener(SetChangeListener<? super E> var1) {
         return new SetExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(SetChangeListener<? super E> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent() {
         this.listener.invalidated(this.observable);
      }

      @Override
      protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> var1) {
         this.listener.invalidated(this.observable);
      }
   }

   private static class SingleSetChange<E> extends SetExpressionHelper<E> {
      private final SetChangeListener<? super E> listener;
      private ObservableSet<E> currentValue;

      private SingleSetChange(ObservableSetValue<E> var1, SetChangeListener<? super E> var2) {
         super(var1);
         this.listener = var2;
         this.currentValue = var1.getValue();
      }

      @Override
      protected SetExpressionHelper<E> addListener(InvalidationListener var1) {
         return new SetExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected SetExpressionHelper<E> addListener(ChangeListener<? super ObservableSet<E>> var1) {
         return new SetExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(ChangeListener<? super ObservableSet<E>> var1) {
         return this;
      }

      @Override
      protected SetExpressionHelper<E> addListener(SetChangeListener<? super E> var1) {
         return new SetExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected SetExpressionHelper<E> removeListener(SetChangeListener<? super E> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected void fireValueChangedEvent() {
         ObservableSet var1 = this.currentValue;
         this.currentValue = this.observable.getValue();
         if (this.currentValue != var1) {
            SetExpressionHelper.SimpleChange var2 = new SetExpressionHelper.SimpleChange<>(this.observable);
            if (this.currentValue == null) {
               for (Object var4 : var1) {
                  this.listener.onChanged(var2.setRemoved(var4));
               }
            } else if (var1 == null) {
               for (Object var8 : this.currentValue) {
                  this.listener.onChanged(var2.setAdded(var8));
               }
            } else {
               for (Object var9 : var1) {
                  if (!this.currentValue.contains(var9)) {
                     this.listener.onChanged(var2.setRemoved(var9));
                  }
               }

               for (Object var10 : this.currentValue) {
                  if (!var1.contains(var10)) {
                     this.listener.onChanged(var2.setAdded(var10));
                  }
               }
            }
         }
      }

      @Override
      protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> var1) {
         this.listener.onChanged(new SetExpressionHelper.SimpleChange<>(this.observable, var1));
      }
   }
}
