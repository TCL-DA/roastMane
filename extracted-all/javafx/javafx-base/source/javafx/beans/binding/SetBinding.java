package javafx.beans.binding;

import com.sun.javafx.binding.BindingHelperObserver;
import com.sun.javafx.binding.SetExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public abstract class SetBinding<E> extends SetExpression<E> implements Binding<ObservableSet<E>> {
   private final SetChangeListener<E> setChangeListener = new SetChangeListener<E>() {
      @Override
      public void onChanged(SetChangeListener.Change<? extends E> var1) {
         SetBinding.this.invalidateProperties();
         SetBinding.this.onInvalidating();
         SetExpressionHelper.fireValueChangedEvent(SetBinding.this.helper, var1);
      }
   };
   private ObservableSet<E> value;
   private boolean valid = false;
   private BindingHelperObserver observer;
   private SetExpressionHelper<E> helper = null;
   private SetBinding<E>.SizeProperty size0;
   private SetBinding<E>.EmptyProperty empty0;

   @Override
   public ReadOnlyIntegerProperty sizeProperty() {
      if (this.size0 == null) {
         this.size0 = new SetBinding.SizeProperty();
      }

      return this.size0;
   }

   @Override
   public ReadOnlyBooleanProperty emptyProperty() {
      if (this.empty0 == null) {
         this.empty0 = new SetBinding.EmptyProperty();
      }

      return this.empty0;
   }

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = SetExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = SetExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ChangeListener<? super ObservableSet<E>> var1) {
      this.helper = SetExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super ObservableSet<E>> var1) {
      this.helper = SetExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(SetChangeListener<? super E> var1) {
      this.helper = SetExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(SetChangeListener<? super E> var1) {
      this.helper = SetExpressionHelper.removeListener(this.helper, var1);
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

   public final ObservableSet<E> get() {
      if (!this.valid) {
         this.value = this.computeValue();
         this.valid = true;
         if (this.value != null) {
            this.value.addListener(this.setChangeListener);
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
            this.value.removeListener(this.setChangeListener);
         }

         this.valid = false;
         this.invalidateProperties();
         this.onInvalidating();
         SetExpressionHelper.fireValueChangedEvent(this.helper);
      }
   }

   @Override
   public final boolean isValid() {
      return this.valid;
   }

   protected abstract ObservableSet<E> computeValue();

   @Override
   public String toString() {
      return this.valid ? "SetBinding [value: " + this.get() + "]" : "SetBinding [invalid]";
   }

   private class EmptyProperty extends ReadOnlyBooleanPropertyBase {
      @Override
      public boolean get() {
         return SetBinding.this.isEmpty();
      }

      @Override
      public Object getBean() {
         return SetBinding.this;
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
         return SetBinding.this.size();
      }

      @Override
      public Object getBean() {
         return SetBinding.this;
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
