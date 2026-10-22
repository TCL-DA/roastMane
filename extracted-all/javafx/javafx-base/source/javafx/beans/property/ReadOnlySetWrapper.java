package javafx.beans.property;

import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ReadOnlySetWrapper<E> extends SimpleSetProperty<E> {
   private ReadOnlySetWrapper<E>.ReadOnlyPropertyImpl readOnlyProperty;

   public ReadOnlySetWrapper() {
   }

   public ReadOnlySetWrapper(ObservableSet<E> var1) {
      super(var1);
   }

   public ReadOnlySetWrapper(Object var1, String var2) {
      super(var1, var2);
   }

   public ReadOnlySetWrapper(Object var1, String var2, ObservableSet<E> var3) {
      super(var1, var2, var3);
   }

   public ReadOnlySetProperty<E> getReadOnlyProperty() {
      if (this.readOnlyProperty == null) {
         this.readOnlyProperty = new ReadOnlySetWrapper.ReadOnlyPropertyImpl();
      }

      return this.readOnlyProperty;
   }

   @Override
   protected void fireValueChangedEvent() {
      super.fireValueChangedEvent();
      if (this.readOnlyProperty != null) {
         this.readOnlyProperty.fireValueChangedEvent();
      }
   }

   @Override
   protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> var1) {
      super.fireValueChangedEvent(var1);
      if (this.readOnlyProperty != null) {
         this.readOnlyProperty.fireValueChangedEvent(var1);
      }
   }

   private class ReadOnlyPropertyImpl extends ReadOnlySetPropertyBase<E> {
      public ObservableSet<E> get() {
         return ReadOnlySetWrapper.this.get();
      }

      @Override
      public Object getBean() {
         return ReadOnlySetWrapper.this.getBean();
      }

      @Override
      public String getName() {
         return ReadOnlySetWrapper.this.getName();
      }

      @Override
      public ReadOnlyIntegerProperty sizeProperty() {
         return ReadOnlySetWrapper.this.sizeProperty();
      }

      @Override
      public ReadOnlyBooleanProperty emptyProperty() {
         return ReadOnlySetWrapper.this.emptyProperty();
      }
   }
}
