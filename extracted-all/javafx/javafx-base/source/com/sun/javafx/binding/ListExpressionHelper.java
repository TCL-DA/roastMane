package com.sun.javafx.binding;

import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.collections.SourceAdapterChange;
import java.util.Arrays;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class ListExpressionHelper<E> extends ExpressionHelperBase {
   protected final ObservableListValue<E> observable;

   public static <E> ListExpressionHelper<E> addListener(ListExpressionHelper<E> var0, ObservableListValue<E> var1, InvalidationListener var2) {
      if (var1 != null && var2 != null) {
         var1.getValue();
         return (ListExpressionHelper<E>)(var0 == null ? new ListExpressionHelper.SingleInvalidation<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <E> ListExpressionHelper<E> removeListener(ListExpressionHelper<E> var0, InvalidationListener var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <E> ListExpressionHelper<E> addListener(
      ListExpressionHelper<E> var0, ObservableListValue<E> var1, ChangeListener<? super ObservableList<E>> var2
   ) {
      if (var1 != null && var2 != null) {
         return (ListExpressionHelper<E>)(var0 == null ? new ListExpressionHelper.SingleChange<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <E> ListExpressionHelper<E> removeListener(ListExpressionHelper<E> var0, ChangeListener<? super ObservableList<E>> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <E> ListExpressionHelper<E> addListener(ListExpressionHelper<E> var0, ObservableListValue<E> var1, ListChangeListener<? super E> var2) {
      if (var1 != null && var2 != null) {
         return (ListExpressionHelper<E>)(var0 == null ? new ListExpressionHelper.SingleListChange<>(var1, var2) : var0.addListener(var2));
      } else {
         throw new NullPointerException();
      }
   }

   public static <E> ListExpressionHelper<E> removeListener(ListExpressionHelper<E> var0, ListChangeListener<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return var0 == null ? null : var0.removeListener(var1);
      }
   }

   public static <E> void fireValueChangedEvent(ListExpressionHelper<E> var0) {
      if (var0 != null) {
         var0.fireValueChangedEvent();
      }
   }

   public static <E> void fireValueChangedEvent(ListExpressionHelper<E> var0, ListChangeListener.Change<? extends E> var1) {
      if (var0 != null) {
         var0.fireValueChangedEvent(var1);
      }
   }

   protected ListExpressionHelper(ObservableListValue<E> var1) {
      this.observable = var1;
   }

   protected abstract ListExpressionHelper<E> addListener(InvalidationListener var1);

   protected abstract ListExpressionHelper<E> removeListener(InvalidationListener var1);

   protected abstract ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> var1);

   protected abstract ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> var1);

   protected abstract ListExpressionHelper<E> addListener(ListChangeListener<? super E> var1);

   protected abstract ListExpressionHelper<E> removeListener(ListChangeListener<? super E> var1);

   protected abstract void fireValueChangedEvent();

   protected abstract void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1);

   private static class Generic<E> extends ListExpressionHelper<E> {
      private InvalidationListener[] invalidationListeners;
      private ChangeListener<? super ObservableList<E>>[] changeListeners;
      private ListChangeListener<? super E>[] listChangeListeners;
      private int invalidationSize;
      private int changeSize;
      private int listChangeSize;
      private boolean locked;
      private ObservableList<E> currentValue;

      private Generic(ObservableListValue<E> var1, InvalidationListener var2, InvalidationListener var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2, var3};
         this.invalidationSize = 2;
      }

      private Generic(ObservableListValue<E> var1, ChangeListener<? super ObservableList<E>> var2, ChangeListener<? super ObservableList<E>> var3) {
         super(var1);
         this.changeListeners = new ChangeListener[]{var2, var3};
         this.changeSize = 2;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableListValue<E> var1, ListChangeListener<? super E> var2, ListChangeListener<? super E> var3) {
         super(var1);
         this.listChangeListeners = new ListChangeListener[]{var2, var3};
         this.listChangeSize = 2;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableListValue<E> var1, InvalidationListener var2, ChangeListener<? super ObservableList<E>> var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2};
         this.invalidationSize = 1;
         this.changeListeners = new ChangeListener[]{var3};
         this.changeSize = 1;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableListValue<E> var1, InvalidationListener var2, ListChangeListener<? super E> var3) {
         super(var1);
         this.invalidationListeners = new InvalidationListener[]{var2};
         this.invalidationSize = 1;
         this.listChangeListeners = new ListChangeListener[]{var3};
         this.listChangeSize = 1;
         this.currentValue = var1.getValue();
      }

      private Generic(ObservableListValue<E> var1, ChangeListener<? super ObservableList<E>> var2, ListChangeListener<? super E> var3) {
         super(var1);
         this.changeListeners = new ChangeListener[]{var2};
         this.changeSize = 1;
         this.listChangeListeners = new ListChangeListener[]{var3};
         this.listChangeSize = 1;
         this.currentValue = var1.getValue();
      }

      @Override
      protected ListExpressionHelper<E> addListener(InvalidationListener var1) {
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
      protected ListExpressionHelper<E> removeListener(InvalidationListener var1) {
         if (this.invalidationListeners != null) {
            for (int var2 = 0; var2 < this.invalidationSize; var2++) {
               if (var1.equals(this.invalidationListeners[var2])) {
                  if (this.invalidationSize == 1) {
                     if (this.changeSize == 1 && this.listChangeSize == 0) {
                        return new ListExpressionHelper.SingleChange<>(this.observable, this.changeListeners[0]);
                     }

                     if (this.changeSize == 0 && this.listChangeSize == 1) {
                        return new ListExpressionHelper.SingleListChange<>(this.observable, this.listChangeListeners[0]);
                     }

                     this.invalidationListeners = null;
                     this.invalidationSize = 0;
                  } else {
                     if (this.invalidationSize == 2 && this.changeSize == 0 && this.listChangeSize == 0) {
                        return new ListExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[1 - var2]);
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
      protected ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> var1) {
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
      protected ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> var1) {
         if (this.changeListeners != null) {
            for (int var2 = 0; var2 < this.changeSize; var2++) {
               if (var1.equals(this.changeListeners[var2])) {
                  if (this.changeSize == 1) {
                     if (this.invalidationSize == 1 && this.listChangeSize == 0) {
                        return new ListExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[0]);
                     }

                     if (this.invalidationSize == 0 && this.listChangeSize == 1) {
                        return new ListExpressionHelper.SingleListChange<>(this.observable, this.listChangeListeners[0]);
                     }

                     this.changeListeners = null;
                     this.changeSize = 0;
                  } else {
                     if (this.changeSize == 2 && this.invalidationSize == 0 && this.listChangeSize == 0) {
                        return new ListExpressionHelper.SingleChange<>(this.observable, this.changeListeners[1 - var2]);
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
      protected ListExpressionHelper<E> addListener(ListChangeListener<? super E> var1) {
         if (this.listChangeListeners == null) {
            this.listChangeListeners = new ListChangeListener[]{var1};
            this.listChangeSize = 1;
         } else {
            int var2 = this.listChangeListeners.length;
            if (this.locked) {
               int var3 = this.listChangeSize < var2 ? var2 : var2 * 3 / 2 + 1;
               this.listChangeListeners = Arrays.copyOf(this.listChangeListeners, var3);
            } else if (this.listChangeSize == var2) {
               this.listChangeSize = trim(this.listChangeSize, this.listChangeListeners);
               if (this.listChangeSize == var2) {
                  int var4 = var2 * 3 / 2 + 1;
                  this.listChangeListeners = Arrays.copyOf(this.listChangeListeners, var4);
               }
            }

            this.listChangeListeners[this.listChangeSize++] = var1;
         }

         if (this.listChangeSize == 1) {
            this.currentValue = this.observable.getValue();
         }

         return this;
      }

      @Override
      protected ListExpressionHelper<E> removeListener(ListChangeListener<? super E> var1) {
         if (this.listChangeListeners != null) {
            for (int var2 = 0; var2 < this.listChangeSize; var2++) {
               if (var1.equals(this.listChangeListeners[var2])) {
                  if (this.listChangeSize == 1) {
                     if (this.invalidationSize == 1 && this.changeSize == 0) {
                        return new ListExpressionHelper.SingleInvalidation<>(this.observable, this.invalidationListeners[0]);
                     }

                     if (this.invalidationSize == 0 && this.changeSize == 1) {
                        return new ListExpressionHelper.SingleChange<>(this.observable, this.changeListeners[0]);
                     }

                     this.listChangeListeners = null;
                     this.listChangeSize = 0;
                  } else {
                     if (this.listChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                        return new ListExpressionHelper.SingleListChange<>(this.observable, this.listChangeListeners[1 - var2]);
                     }

                     int var3 = this.listChangeSize - var2 - 1;
                     ListChangeListener[] var4 = this.listChangeListeners;
                     if (this.locked) {
                        this.listChangeListeners = new ListChangeListener[this.listChangeListeners.length];
                        System.arraycopy(var4, 0, this.listChangeListeners, 0, var2 + 1);
                     }

                     if (var3 > 0) {
                        System.arraycopy(var4, var2 + 1, this.listChangeListeners, var2, var3);
                     }

                     this.listChangeSize--;
                     if (!this.locked) {
                        this.listChangeListeners[this.listChangeSize] = null;
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
         if (this.changeSize == 0 && this.listChangeSize == 0) {
            this.notifyListeners(this.currentValue, null, false);
         } else {
            ObservableList var1 = this.currentValue;
            this.currentValue = this.observable.getValue();
            if (this.currentValue != var1) {
               NonIterableChange.GenericAddRemoveChange var2 = null;
               if (this.listChangeSize > 0) {
                  int var3 = this.currentValue == null ? 0 : this.currentValue.size();
                  ObservableList var4 = var1 == null ? FXCollections.emptyObservableList() : FXCollections.unmodifiableObservableList(var1);
                  var2 = new NonIterableChange.GenericAddRemoveChange<>(0, var3, var4, this.observable);
               }

               this.notifyListeners(var1, var2, false);
            } else {
               this.notifyListeners(this.currentValue, null, true);
            }
         }
      }

      @Override
      protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
         SourceAdapterChange var2 = this.listChangeSize == 0 ? null : new SourceAdapterChange<>(this.observable, var1);
         this.notifyListeners(this.currentValue, var2, false);
      }

      private void notifyListeners(ObservableList<E> var1, ListChangeListener.Change<E> var2, boolean var3) {
         InvalidationListener[] var4 = this.invalidationListeners;
         int var5 = this.invalidationSize;
         ChangeListener[] var6 = this.changeListeners;
         int var7 = this.changeSize;
         ListChangeListener[] var8 = this.listChangeListeners;
         int var9 = this.listChangeSize;

         try {
            this.locked = true;

            for (int var10 = 0; var10 < var5; var10++) {
               var4[var10].invalidated(this.observable);
            }

            if (!var3) {
               for (int var14 = 0; var14 < var7; var14++) {
                  var6[var14].changed(this.observable, var1, this.currentValue);
               }

               if (var2 != null) {
                  for (int var15 = 0; var15 < var9; var15++) {
                     var2.reset();
                     var8[var15].onChanged(var2);
                  }
               }
            }
         } finally {
            this.locked = false;
         }
      }
   }

   private static class SingleChange<E> extends ListExpressionHelper<E> {
      private final ChangeListener<? super ObservableList<E>> listener;
      private ObservableList<E> currentValue;

      private SingleChange(ObservableListValue<E> var1, ChangeListener<? super ObservableList<E>> var2) {
         super(var1);
         this.listener = var2;
         this.currentValue = var1.getValue();
      }

      @Override
      protected ListExpressionHelper<E> addListener(InvalidationListener var1) {
         return new ListExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> var1) {
         return new ListExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected ListExpressionHelper<E> addListener(ListChangeListener<? super E> var1) {
         return new ListExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(ListChangeListener<? super E> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent() {
         ObservableList var1 = this.currentValue;
         this.currentValue = this.observable.getValue();
         if (this.currentValue != var1) {
            this.listener.changed(this.observable, var1, this.currentValue);
         }
      }

      @Override
      protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
         this.listener.changed(this.observable, this.currentValue, this.currentValue);
      }
   }

   private static class SingleInvalidation<E> extends ListExpressionHelper<E> {
      private final InvalidationListener listener;

      private SingleInvalidation(ObservableListValue<E> var1, InvalidationListener var2) {
         super(var1);
         this.listener = var2;
      }

      @Override
      protected ListExpressionHelper<E> addListener(InvalidationListener var1) {
         return new ListExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(InvalidationListener var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> var1) {
         return new ListExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> var1) {
         return this;
      }

      @Override
      protected ListExpressionHelper<E> addListener(ListChangeListener<? super E> var1) {
         return new ListExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(ListChangeListener<? super E> var1) {
         return this;
      }

      @Override
      protected void fireValueChangedEvent() {
         this.listener.invalidated(this.observable);
      }

      @Override
      protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
         this.listener.invalidated(this.observable);
      }
   }

   private static class SingleListChange<E> extends ListExpressionHelper<E> {
      private final ListChangeListener<? super E> listener;
      private ObservableList<E> currentValue;

      private SingleListChange(ObservableListValue<E> var1, ListChangeListener<? super E> var2) {
         super(var1);
         this.listener = var2;
         this.currentValue = var1.getValue();
      }

      @Override
      protected ListExpressionHelper<E> addListener(InvalidationListener var1) {
         return new ListExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(InvalidationListener var1) {
         return this;
      }

      @Override
      protected ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> var1) {
         return new ListExpressionHelper.Generic<>(this.observable, var1, this.listener);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> var1) {
         return this;
      }

      @Override
      protected ListExpressionHelper<E> addListener(ListChangeListener<? super E> var1) {
         return new ListExpressionHelper.Generic<>(this.observable, this.listener, var1);
      }

      @Override
      protected ListExpressionHelper<E> removeListener(ListChangeListener<? super E> var1) {
         return var1.equals(this.listener) ? null : this;
      }

      @Override
      protected void fireValueChangedEvent() {
         ObservableList var1 = this.currentValue;
         this.currentValue = this.observable.getValue();
         if (this.currentValue != var1) {
            int var2 = this.currentValue == null ? 0 : this.currentValue.size();
            ObservableList var3 = var1 == null ? FXCollections.emptyObservableList() : FXCollections.unmodifiableObservableList(var1);
            NonIterableChange.GenericAddRemoveChange var4 = new NonIterableChange.GenericAddRemoveChange<>(0, var2, var3, this.observable);
            this.listener.onChanged(var4);
         }
      }

      @Override
      protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
         this.listener.onChanged(new SourceAdapterChange<>(this.observable, var1));
      }
   }
}
