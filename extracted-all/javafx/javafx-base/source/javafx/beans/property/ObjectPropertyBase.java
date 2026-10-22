package javafx.beans.property;

import com.sun.javafx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public abstract class ObjectPropertyBase<T> extends ObjectProperty<T> {
   private T value;
   private ObservableValue<? extends T> observable = null;
   private InvalidationListener listener = null;
   private boolean valid = true;
   private ExpressionHelper<T> helper = null;

   public ObjectPropertyBase() {
   }

   public ObjectPropertyBase(T var1) {
      this.value = (T)var1;
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
   public void addListener(ChangeListener<? super T> var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super T> var1) {
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
   public T get() {
      this.valid = true;
      return this.observable == null ? this.value : this.observable.getValue();
   }

   @Override
   public void set(T var1) {
      if (!this.isBound()) {
         if (this.value != var1) {
            this.value = (T)var1;
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
   public void bind(ObservableValue<? extends T> var1) {
      if (var1 == null) {
         throw new NullPointerException("Cannot bind to null");
      } else {
         if (!var1.equals(this.observable)) {
            this.unbind();
            this.observable = var1;
            if (this.listener == null) {
               this.listener = new ObjectPropertyBase.Listener(this);
            }

            this.observable.addListener(this.listener);
            this.markInvalid();
         }
      }
   }

   @Override
   public void unbind() {
      if (this.observable != null) {
         this.value = (T)this.observable.getValue();
         this.observable.removeListener(this.listener);
         this.observable = null;
      }
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("ObjectProperty [");
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
      private final WeakReference<ObjectPropertyBase<?>> wref;

      public Listener(ObjectPropertyBase<?> var1) {
         this.wref = new WeakReference<>(var1);
      }

      @Override
      public void invalidated(Observable var1) {
         ObjectPropertyBase var2 = this.wref.get();
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
