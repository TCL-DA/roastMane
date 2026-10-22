package javafx.beans.binding;

import com.sun.javafx.binding.BindingHelperObserver;
import com.sun.javafx.binding.ListExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class ListBinding<E> extends ListExpression<E> implements Binding<ObservableList<E>> {
   private final ListChangeListener<E> listChangeListener = new ListChangeListener<E>() {
      @Override
      public void onChanged(ListChangeListener.Change<? extends E> var1) {
         ListBinding.this.invalidateProperties();
         ListBinding.this.onInvalidating();
         ListExpressionHelper.fireValueChangedEvent(ListBinding.this.helper, var1);
      }
   };
   private ObservableList<E> value;
   private boolean valid = false;
   private BindingHelperObserver observer;
   private ListExpressionHelper<E> helper = null;
   private ListBinding<E>.SizeProperty size0;
   private ListBinding<E>.EmptyProperty empty0;

   @Override
   public ReadOnlyIntegerProperty sizeProperty() {
      if (this.size0 == null) {
         this.size0 = new ListBinding.SizeProperty();
      }

      return this.size0;
   }

   @Override
   public ReadOnlyBooleanProperty emptyProperty() {
      if (this.empty0 == null) {
         this.empty0 = new ListBinding.EmptyProperty();
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

   protected final void bind(Observable... var1) {
      if (var1 != null && var1.length > 0) {
         if (this.observer == null) {
            this.observer = new BindingHelperObserver(this);
         }

         for (Observable var5 : var1) {
            if (var5 != null) {
               var5.addListener(this.observer);
            }
         }
      }
   }

   protected final void unbind(Observable... var1) {
      if (this.observer != null) {
         for (Observable var5 : var1) {
            if (var5 != null) {
               var5.removeListener(this.observer);
            }
         }

         this.observer = null;
      }
   }

   @Override
   public void dispose() {
   }

   @Override
   public ObservableList<?> getDependencies() {
      return FXCollections.emptyObservableList();
   }

   public final ObservableList<E> get() {
      if (!this.valid) {
         this.value = this.computeValue();
         this.valid = true;
         if (this.value != null) {
            this.value.addListener(this.listChangeListener);
         }
      }

      return this.value;
   }

   protected void onInvalidating() {
   }

   private void invalidateProperties() {
      if (this.size0 != null) {
         this.size0.fireValueChangedEvent();
      }

      if (this.empty0 != null) {
         this.empty0.fireValueChangedEvent();
      }
   }

   @Override
   public final void invalidate() {
      if (this.valid) {
         if (this.value != null) {
            this.value.removeListener(this.listChangeListener);
         }

         this.valid = false;
         this.invalidateProperties();
         this.onInvalidating();
         ListExpressionHelper.fireValueChangedEvent(this.helper);
      }
   }

   @Override
   public final boolean isValid() {
      return this.valid;
   }

   protected abstract ObservableList<E> computeValue();

   @Override
   public String toString() {
      return this.valid ? "ListBinding [value: " + this.get() + "]" : "ListBinding [invalid]";
   }

   private class EmptyProperty extends ReadOnlyBooleanPropertyBase {
      @Override
      public boolean get() {
         return ListBinding.this.isEmpty();
      }

      @Override
      public Object getBean() {
         return ListBinding.this;
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

   private class SizeProperty extends ReadOnlyIntegerPropertyBase {
      @Override
      public int get() {
         return ListBinding.this.size();
      }

      @Override
      public Object getBean() {
         return ListBinding.this;
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
