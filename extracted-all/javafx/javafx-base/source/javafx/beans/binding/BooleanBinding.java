package javafx.beans.binding;

import com.sun.javafx.binding.BindingHelperObserver;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class BooleanBinding extends BooleanExpression implements Binding<Boolean> {
   private boolean value;
   private boolean valid = false;
   private BindingHelperObserver observer;
   private ExpressionHelper<Boolean> helper = null;

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ChangeListener<? super Boolean> var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super Boolean> var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   protected final void bind(Observable... var1) {
      if (var1 != null && var1.length > 0) {
         if (this.observer == null) {
            this.observer = new BindingHelperObserver(this);
         }

         for (Observable var5 : var1) {
            var5.addListener(this.observer);
         }
      }
   }

   protected final void unbind(Observable... var1) {
      if (this.observer != null) {
         for (Observable var5 : var1) {
            var5.removeListener(this.observer);
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

   @Override
   public final boolean get() {
      if (!this.valid) {
         this.value = this.computeValue();
         this.valid = true;
      }

      return this.value;
   }

   protected void onInvalidating() {
   }

   @Override
   public final void invalidate() {
      if (this.valid) {
         this.valid = false;
         this.onInvalidating();
         ExpressionHelper.fireValueChangedEvent(this.helper);
      }
   }

   @Override
   public final boolean isValid() {
      return this.valid;
   }

   protected abstract boolean computeValue();

   @Override
   public String toString() {
      return this.valid ? "BooleanBinding [value: " + this.get() + "]" : "BooleanBinding [invalid]";
   }
}
