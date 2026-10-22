package javafx.beans.property;

public class ReadOnlyBooleanWrapper extends SimpleBooleanProperty {
   private ReadOnlyBooleanWrapper.ReadOnlyPropertyImpl readOnlyProperty;

   public ReadOnlyBooleanWrapper() {
   }

   public ReadOnlyBooleanWrapper(boolean var1) {
      super(var1);
   }

   public ReadOnlyBooleanWrapper(Object var1, String var2) {
      super(var1, var2);
   }

   public ReadOnlyBooleanWrapper(Object var1, String var2, boolean var3) {
      super(var1, var2, var3);
   }

   public ReadOnlyBooleanProperty getReadOnlyProperty() {
      if (this.readOnlyProperty == null) {
         this.readOnlyProperty = new ReadOnlyBooleanWrapper.ReadOnlyPropertyImpl();
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

   private class ReadOnlyPropertyImpl extends ReadOnlyBooleanPropertyBase {
      @Override
      public boolean get() {
         return ReadOnlyBooleanWrapper.this.get();
      }

      @Override
      public Object getBean() {
         return ReadOnlyBooleanWrapper.this.getBean();
      }

      @Override
      public String getName() {
         return ReadOnlyBooleanWrapper.this.getName();
      }
   }
}
