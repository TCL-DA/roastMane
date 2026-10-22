package javafx.beans.property;

public class ReadOnlyObjectWrapper<T> extends SimpleObjectProperty<T> {
   private ReadOnlyObjectWrapper<T>.ReadOnlyPropertyImpl readOnlyProperty;

   public ReadOnlyObjectWrapper() {
   }

   public ReadOnlyObjectWrapper(T var1) {
      super((T)var1);
   }

   public ReadOnlyObjectWrapper(Object var1, String var2) {
      super(var1, var2);
   }

   public ReadOnlyObjectWrapper(Object var1, String var2, T var3) {
      super(var1, var2, (T)var3);
   }

   public ReadOnlyObjectProperty<T> getReadOnlyProperty() {
      if (this.readOnlyProperty == null) {
         this.readOnlyProperty = new ReadOnlyObjectWrapper.ReadOnlyPropertyImpl();
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

   private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<T> {
      @Override
      public T get() {
         return ReadOnlyObjectWrapper.this.get();
      }

      @Override
      public Object getBean() {
         return ReadOnlyObjectWrapper.this.getBean();
      }

      @Override
      public String getName() {
         return ReadOnlyObjectWrapper.this.getName();
      }
   }
}
