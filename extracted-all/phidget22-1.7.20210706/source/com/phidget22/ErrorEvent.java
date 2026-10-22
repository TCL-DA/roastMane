package com.phidget22;

public class ErrorEvent {
   Phidget Source;
   ErrorEventCode Code;
   String Description;

   public ErrorEvent(Phidget var1, ErrorEventCode var2, String var3) {
      this.Source = var1;
      this.Code = var2;
      this.Description = var3;
   }

   public Phidget getSource() {
      return this.Source;
   }

   public ErrorEventCode getCode() {
      return this.Code;
   }

   public String getDescription() {
      return this.Description;
   }

   @Override
   public String toString() {
      return "[ErrorEvent](Source: " + this.Source + ", Code: " + this.Code + ", Description: " + this.Description + ")";
   }
}
