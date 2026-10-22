package javafx.beans.property;

public class ReadOnlyStringWrapper extends SimpleStringProperty {
   private ReadOnlyStringWrapper.ReadOnlyPropertyImpl readOnlyProperty;

   public ReadOnlyStringWrapper() {
   }

   public ReadOnlyStringWrapper(String var1) {
      super(var1);
   }

   public ReadOnlyStringWrapper(Object var1, String var2) {
      super(var1, var2);
   }

   public ReadOnlyStringWrapper(Object var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public ReadOnlyStringProperty getReadOnlyProperty() {
      if (this.readOnlyProperty == null) {
         this.readOnlyProperty = new ReadOnlyStringWrapper.ReadOnlyPropertyImpl();
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

   private class ReadOnlyPropertyImpl extends ReadOnlyStringPropertyBase {
      public String get() {
         return ReadOnlyStringWrapper.this.get();
      }

      @Override
      public Object getBean() {
         return ReadOnlyStringWrapper.this.getBean();
      }

      @Override
      public String getName() {
         return ReadOnlyStringWrapper.this.getName();
      }
   }
}
