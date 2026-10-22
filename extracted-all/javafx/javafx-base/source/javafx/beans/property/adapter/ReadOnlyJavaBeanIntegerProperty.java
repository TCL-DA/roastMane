package javafx.beans.property.adapter;

import com.sun.javafx.property.MethodHelper;
import com.sun.javafx.property.adapter.Disposer;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;

public final class ReadOnlyJavaBeanIntegerProperty extends ReadOnlyIntegerPropertyBase implements ReadOnlyJavaBeanProperty<Number> {
   private final ReadOnlyPropertyDescriptor descriptor;
   private final ReadOnlyPropertyDescriptor.ReadOnlyListener<Number> listener;
   private final AccessControlContext acc = AccessController.getContext();

   ReadOnlyJavaBeanIntegerProperty(ReadOnlyPropertyDescriptor var1, Object var2) {
      this.descriptor = var1;
      this.listener = var1.new ReadOnlyListener<>(var2, this);
      var1.addListener(this.listener);
      Disposer.addRecord(this, new DescriptorListenerCleaner(var1, this.listener));
   }

   @Override
   public int get() {
      return AccessController.doPrivileged((PrivilegedAction<Integer>)(() -> {
         try {
            return ((Number)MethodHelper.invoke(this.descriptor.getGetter(), this.getBean(), (Object[])null)).intValue();
         } catch (IllegalAccessException var2) {
            throw new UndeclaredThrowableException(var2);
         } catch (InvocationTargetException var3) {
            throw new UndeclaredThrowableException(var3);
         }
      }), this.acc);
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
   public void fireValueChangedEvent() {
      super.fireValueChangedEvent();
   }

   @Override
   public void dispose() {
      this.descriptor.removeListener(this.listener);
   }
}
