package javafx.beans.property;

import com.sun.javafx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

public abstract class BooleanPropertyBase extends BooleanProperty {
   private boolean value;
   private ObservableBooleanValue observable = null;
   private InvalidationListener listener = null;
   private boolean valid = true;
   private ExpressionHelper<Boolean> helper = null;

   public BooleanPropertyBase() {
   }

   public BooleanPropertyBase(boolean var1) {
      this.value = var1;
   }

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

   protected void fireValueChangedEvent() {
      ExpressionHelper.fireValueChangedEvent(this.helper);
   }

   private void markInvalid() {
      if (this.valid) {
         this.valid = false;
         this.invalidated();
         this.fireValueChangedEvent();
      }
   }

   protected void invalidated() {
   }

   @Override
   public boolean get() {
      this.valid = true;
      return this.observable == null ? this.value : this.observable.get();
   }

   @Override
   public void set(boolean var1) {
      if (!this.isBound()) {
         if (this.value != var1) {
            this.value = var1;
            this.markInvalid();
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
   public void bind(ObservableValue<? extends Boolean> var1) {
      if (var1 == null) {
         throw new NullPointerException("Cannot bind to null");
      } else {
         Object var2 = var1 instanceof ObservableBooleanValue ? (ObservableBooleanValue)var1 : new BooleanPropertyBase.ValueWrapper(var1);
         if (!var2.equals(this.observable)) {
            this.unbind();
            this.observable = (ObservableBooleanValue)var2;
            if (this.listener == null) {
               this.listener = new BooleanPropertyBase.Listener(this);
            }

            this.observable.addListener(this.listener);
            this.markInvalid();
         }
      }
   }

   @Override
   public void unbind() {
      if (this.observable != null) {
         this.value = this.observable.get();
         this.observable.removeListener(this.listener);
         if (this.observable instanceof BooleanPropertyBase.ValueWrapper) {
            ((BooleanPropertyBase.ValueWrapper)this.observable).dispose();
         }

         this.observable = null;
      }
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("BooleanProperty [");
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

   private static class Listener implements InvalidationListener, WeakListener {
      private final WeakReference<BooleanPropertyBase> wref;

      public Listener(BooleanPropertyBase var1) {
         this.wref = new WeakReference<>(var1);
      }

      @Override
      public void invalidated(Observable var1) {
         BooleanPropertyBase var2 = this.wref.get();
         if (var2 == null) {
            var1.removeListener(this);
         } else {
            var2.markInvalid();
         }
      }

      @Override
      public boolean wasGarbageCollected() {
         return this.wref.get() == null;
      }
   }

   private class ValueWrapper extends BooleanBinding {
      private ObservableValue<? extends Boolean> observable;

      public ValueWrapper(ObservableValue<? extends Boolean> var2) {
         this.observable = var2;
         this.bind(new Observable[]{var2});
      }

      @Override
      protected boolean computeValue() {
         Boolean var1 = this.observable.getValue();
         return var1 == null ? false : var1;
      }

      @Override
      public void dispose() {
         this.unbind(new Observable[]{this.observable});
      }
   }
}
