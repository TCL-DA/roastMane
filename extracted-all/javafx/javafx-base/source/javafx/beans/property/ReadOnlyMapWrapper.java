package javafx.beans.property;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class ReadOnlyMapWrapper<K, V> extends SimpleMapProperty<K, V> {
   private ReadOnlyMapWrapper<K, V>.ReadOnlyPropertyImpl readOnlyProperty;

   public ReadOnlyMapWrapper() {
   }

   public ReadOnlyMapWrapper(ObservableMap<K, V> var1) {
      super(var1);
   }

   public ReadOnlyMapWrapper(Object var1, String var2) {
      super(var1, var2);
   }

   public ReadOnlyMapWrapper(Object var1, String var2, ObservableMap<K, V> var3) {
      super(var1, var2, var3);
   }

   public ReadOnlyMapProperty<K, V> getReadOnlyProperty() {
      if (this.readOnlyProperty == null) {
         this.readOnlyProperty = new ReadOnlyMapWrapper.ReadOnlyPropertyImpl();
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
   protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
      super.fireValueChangedEvent(var1);
      if (this.readOnlyProperty != null) {
         this.readOnlyProperty.fireValueChangedEvent(var1);
      }
   }

   private class ReadOnlyPropertyImpl extends ReadOnlyMapPropertyBase<K, V> {
      public ObservableMap<K, V> get() {
         return ReadOnlyMapWrapper.this.get();
      }

      @Override
      public Object getBean() {
         return ReadOnlyMapWrapper.this.getBean();
      }

      @Override
      public String getName() {
         return ReadOnlyMapWrapper.this.getName();
      }

      @Override
      public ReadOnlyIntegerProperty sizeProperty() {
         return ReadOnlyMapWrapper.this.sizeProperty();
      }

      @Override
      public ReadOnlyBooleanProperty emptyProperty() {
         return ReadOnlyMapWrapper.this.emptyProperty();
      }
   }
}
