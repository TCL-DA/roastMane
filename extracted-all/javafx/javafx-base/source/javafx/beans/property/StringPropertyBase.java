package javafx.beans.property;

import com.sun.javafx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public abstract class StringPropertyBase extends StringProperty {
   private String value;
   private ObservableValue<? extends String> observable = null;
   private InvalidationListener listener = null;
   private boolean valid = true;
   private ExpressionHelper<String> helper = null;

   public StringPropertyBase() {
   }

   public StringPropertyBase(String var1) {
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
   public void addListener(ChangeListener<? super String> var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super String> var1) {
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

   public String get() {
      this.valid = true;
      return this.observable == null ? this.value : this.observable.getValue();
   }

   public void set(String var1) {
      if (this.isBound()) {
         throw new RuntimeException(
            (this.getBean() != null && this.getName() != null ? this.getBean().getClass().getSimpleName() + "." + this.getName() + " : " : "")
               + "A bound value cannot be set."
         );
      } else {
         if (this.value == null ? var1 != null : !this.value.equals(var1)) {
            this.value = var1;
            this.markInvalid();
         }
      }
   }

   @Override
   public boolean isBound() {
      return this.observable != null;
   }

   @Override
   public void bind(ObservableValue<? extends String> var1) {
      if (var1 == null) {
         throw new NullPointerException("Cannot bind to null");
      } else {
         if (!var1.equals(this.observable)) {
            this.unbind();
            this.observable = var1;
            if (this.listener == null) {
               this.listener = new StringPropertyBase.Listener(this);
            }

            this.observable.addListener(this.listener);
            this.markInvalid();
         }
      }
   }

   @Override
   public void unbind() {
      if (this.observable != null) {
         this.value = this.observable.getValue();
         this.observable.removeListener(this.listener);
         this.observable = null;
      }
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("StringProperty [");
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
      private final WeakReference<StringPropertyBase> wref;

      public Listener(StringPropertyBase var1) {
         this.wref = new WeakReference<>(var1);
      }

      @Override
      public void invalidated(Observable var1) {
         StringPropertyBase var2 = this.wref.get();
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
}
