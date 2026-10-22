package javafx.beans.property;

import com.sun.javafx.binding.ListExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class ListPropertyBase<E> extends ListProperty<E> {
   private final ListChangeListener<E> listChangeListener = var1 -> {
      this.invalidateProperties();
      this.invalidated();
      this.fireValueChangedEvent(var1);
   };
   private ObservableList<E> value;
   private ObservableValue<? extends ObservableList<E>> observable = null;
   private InvalidationListener listener = null;
   private boolean valid = true;
   private ListExpressionHelper<E> helper = null;
   private ListPropertyBase<E>.SizeProperty size0;
   private ListPropertyBase<E>.EmptyProperty empty0;

   public ListPropertyBase() {
   }

   public ListPropertyBase(ObservableList<E> var1) {
      this.value = var1;
      if (var1 != null) {
         var1.addListener(this.listChangeListener);
      }
   }

   @Override
   public ReadOnlyIntegerProperty sizeProperty() {
      if (this.size0 == null) {
         this.size0 = new ListPropertyBase.SizeProperty();
      }

      return this.size0;
   }

   @Override
   public ReadOnlyBooleanProperty emptyProperty() {
      if (this.empty0 == null) {
         this.empty0 = new ListPropertyBase.EmptyProperty();
      }

      return this.empty0;
   }

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = ListExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = ListExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ChangeListener<? super ObservableList<E>> var1) {
      this.helper = ListExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super ObservableList<E>> var1) {
      this.helper = ListExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ListChangeListener<? super E> var1) {
      this.helper = ListExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ListChangeListener<? super E> var1) {
      this.helper = ListExpressionHelper.removeListener(this.helper, var1);
   }

   protected void fireValueChangedEvent() {
      ListExpressionHelper.fireValueChangedEvent(this.helper);
   }

   protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
      ListExpressionHelper.fireValueChangedEvent(this.helper, var1);
   }

   private void invalidateProperties() {
      if (this.size0 != null) {
         this.size0.fireValueChangedEvent();
      }

      if (this.empty0 != null) {
         this.empty0.fireValueChangedEvent();
      }
   }

   private void markInvalid(ObservableList<E> var1) {
      if (this.valid) {
         if (var1 != null) {
            var1.removeListener(this.listChangeListener);
         }

         this.valid = false;
         this.invalidateProperties();
         this.invalidated();
         this.fireValueChangedEvent();
      }
   }

   protected void invalidated() {
   }

   public ObservableList<E> get() {
      if (!this.valid) {
         this.value = this.observable == null ? this.value : this.observable.getValue();
         this.valid = true;
         if (this.value != null) {
            this.value.addListener(this.listChangeListener);
         }
      }

      return this.value;
   }

   public void set(ObservableList<E> var1) {
      if (!this.isBound()) {
         if (this.value != var1) {
            ObservableList var2 = this.value;
            this.value = var1;
            this.markInvalid(var2);
         }
      } else {
         throw new RuntimeException(
            (this.getBean() != null && this.getName() != null ? this.getBean().getClass().getSimpleName() + "." + this.getName() + " : " : "")
               + "A bound value cannot be set."
         );
      }
   }

   @Override
   public boolean isBound() {
      return this.observable != null;
   }

   @Override
   public void bind(ObservableValue<? extends ObservableList<E>> var1) {
      if (var1 == null) {
         throw new NullPointerException("Cannot bind to null");
      } else {
         if (var1 != this.observable) {
            this.unbind();
            this.observable = var1;
            if (this.listener == null) {
               this.listener = new ListPropertyBase.Listener<>(this);
            }

            this.observable.addListener(this.listener);
            this.markInvalid(this.value);
         }
      }
   }

   @Override
   public void unbind() {
      if (this.observable != null) {
         this.value = (ObservableList<E>)this.observable.getValue();
         this.observable.removeListener(this.listener);
         this.observable = null;
      }
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("ListProperty [");
      if (var1 != null) {
         var3.append("bean: ").append(var1).append(", ");
      }

      if (var2 != null && !var2.equals("")) {
         var3.append("name: ").append(var2).append(", ");
      }

      if (this.isBound()) {
         var3.append("bound, ");
         if (this.valid) {
            var3.append("value: ").append(this.get());
         } else {
            var3.append("invalid");
         }
      } else {
         var3.append("value: ").append(this.get());
      }

      var3.append("]");
      return var3.toString();
   }

   private class EmptyProperty extends ReadOnlyBooleanPropertyBase {
      @Override
      public boolean get() {
         return ListPropertyBase.this.isEmpty();
      }

      @Override
      public Object getBean() {
         return ListPropertyBase.this;
      }

      @Override
      public String getName() {
         return "empty";
      }

      @Override
      protected void fireValueChangedEvent() {
         super.fireValueChangedEvent();
      }
   }

   private static class Listener<E> implements InvalidationListener, WeakListener {
      private final WeakReference<ListPropertyBase<E>> wref;

      public Listener(ListPropertyBase<E> var1) {
         this.wref = new WeakReference<>(var1);
      }

      @Override
      public void invalidated(Observable var1) {
         ListPropertyBase var2 = this.wref.get();
         if (var2 == null) {
            var1.removeListener(this);
         } else {
            var2.markInvalid(var2.value);
         }
      }

      @Override
      public boolean wasGarbageCollected() {
         return this.wref.get() == null;
      }
   }

   private class SizeProperty extends ReadOnlyIntegerPropertyBase {
      @Override
      public int get() {
         return ListPropertyBase.this.size();
      }

      @Override
      public Object getBean() {
         return ListPropertyBase.this;
      }

      @Override
      public String getName() {
         return "size";
      }

      @Override
      protected void fireValueChangedEvent() {
         super.fireValueChangedEvent();
      }
   }
}
