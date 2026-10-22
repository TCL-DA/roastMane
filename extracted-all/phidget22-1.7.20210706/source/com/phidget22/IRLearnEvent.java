package com.phidget22;

public class IRLearnEvent {
   IR Source;
   String Code;
   IRCodeInfo CodeInfo;

   public IRLearnEvent(IR var1, String var2, IRCodeInfo var3) {
      this.Source = var1;
      this.Code = var2;
      this.CodeInfo = var3;
   }

   public IR getSource() {
      return this.Source;
   }

   public String getCode() {
      return this.Code;
   }

   public IRCodeInfo getCodeInfo() {
      return this.CodeInfo;
   }

   @Override
   public String toString() {
      return "[IRLearnEvent](Source: " + this.Source + ", Code: " + this.Code + ", CodeInfo: " + this.CodeInfo + ")";
   }
}
