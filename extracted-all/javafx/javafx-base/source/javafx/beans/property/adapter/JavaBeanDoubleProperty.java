package javafx.beans.property.adapter;

import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.property.MethodHelper;
import com.sun.javafx.property.adapter.Disposer;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public final class JavaBeanDoubleProperty extends DoubleProperty implements JavaBeanProperty<Number> {
   private final PropertyDescriptor descriptor;
   private final PropertyDescriptor.Listener<Number> listener;
   private ObservableValue<? extends Number> observable = null;
   private ExpressionHelper<Number> helper = null;
   private final AccessControlContext acc = AccessController.getContext();

   JavaBeanDoubleProperty(PropertyDescriptor var1, Object var2) {
      this.descriptor = var1;
      this.listener = var1.new Listener<>(var2, this);
      var1.addListener(this.listener);
      Disposer.addRecord(this, new DescriptorListenerCleaner(var1, this.listener));
   }

   @Override
   public double get() {
      return AccessController.doPrivileged((PrivilegedAction<Double>)(() -> {
         try {
            return ((Number)MethodHelper.invoke(this.descriptor.getGetter(), this.getBean(), (Object[])null)).doubleValue();
         } catch (IllegalAccessException var2) {
            throw new UndeclaredThrowableException(var2);
         } catch (InvocationTargetException var3) {
            throw new UndeclaredThrowableException(var3);
         }
      }), this.acc);
   }

   @Override
   public void set(double var1) {
      if (this.isBound()) {
         throw new RuntimeException("A bound value cannot be set.");
      } else {
         AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
            try {
               MethodHelper.invoke(this.descriptor.getSetter(), this.getBean(), new Object[]{var1});
               ExpressionHelper.fireValueChangedEvent(this.helper);
               return null;
            } catch (IllegalAccessException var4) {
               throw new UndeclaredThrowableException(var4);
            } catch (InvocationTargetException var5) {
               throw new UndeclaredThrowableException(var5);
            }
         }), this.acc);
      }
   }

   @Override
   public void bind(ObservableValue<? extends Number> var1) {
      if (var1 == null) {
         throw new NullPointerException("Cannot bind to null");
      } else {
         if (!var1.equals(this.observable)) {
            this.unbind();
            this.set(((Number)var1.getValue()).doubleValue());
            this.observable = var1;
            this.observable.addListener(this.listener);
         }
      }
   }

   @Override
   public void unbind() {
      if (this.observable != null) {
         this.observable.removeListener(this.listener);
         this.observable = null;
      }
   }

   @Override
   public boolean isBound() {
      return this.observable != null;
   }

   @Override
   public Object getBean() {
      return this.listener.getBean();
   }

   @Override
   public String getName() {
      return this.descriptor.getName();
   }

   @Override
   public void addListener(ChangeListener<? super Number> var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super Number> var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
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
   public void fireValueChangedEvent() {
      ExpressionHelper.fireValueChangedEvent(this.helper);
   }

   @Override
   public void dispose() {
      this.descriptor.removeListener(this.listener);
   }

   @Override
   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("DoubleProperty [");
      if (var1 != null) {
         var3.append("bean: ").append(var1).append(", ");
      }

      if (var2 != null && !var2.equals("")) {
         var3.append("name: ").append(var2).append(", ");
      }

      if (this.isBound()) {
         var3.append("bound, ");
      }

      var3.append("value: ").append(this.get());
      var3.append("]");
      return var3.toString();
   }
}
