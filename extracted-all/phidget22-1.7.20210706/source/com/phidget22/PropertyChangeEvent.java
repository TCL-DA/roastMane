package com.phidget22;

public class PropertyChangeEvent {
   Phidget Source;
   String PropertyName;

   public PropertyChangeEvent(Phidget var1, String var2) {
      this.Source = var1;
      this.PropertyName = var2;
   }

   public Phidget getSource() {
      return this.Source;
   }

   public String getPropertyName() {
      return this.PropertyName;
   }

   @Override
   public String toString() {
      return "[PropertyChangeEvent](Source: " + this.Source + ", PropertyName: " + this.PropertyName + ")";
   }
}
