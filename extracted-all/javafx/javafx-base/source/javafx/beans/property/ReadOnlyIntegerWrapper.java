package javafx.beans.property;

public class ReadOnlyIntegerWrapper extends SimpleIntegerProperty {
   private ReadOnlyIntegerWrapper.ReadOnlyPropertyImpl readOnlyProperty;

   public ReadOnlyIntegerWrapper() {
   }

   public ReadOnlyIntegerWrapper(int var1) {
      super(var1);
   }

   public ReadOnlyIntegerWrapper(Object var1, String var2) {
      super(var1, var2);
   }

   public ReadOnlyIntegerWrapper(Object var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public ReadOnlyIntegerProperty getReadOnlyProperty() {
      if (this.readOnlyProperty == null) {
         this.readOnlyProperty = new ReadOnlyIntegerWrapper.ReadOnlyPropertyImpl();
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

   private class ReadOnlyPropertyImpl extends ReadOnlyIntegerPropertyBase {
      @Override
      public int get() {
         return ReadOnlyIntegerWrapper.this.get();
      }

      @Override
      public Object getBean() {
         return ReadOnlyIntegerWrapper.this.getBean();
      }

      @Override
      public String getName() {
         return ReadOnlyIntegerWrapper.this.getName();
      }
   }
}
