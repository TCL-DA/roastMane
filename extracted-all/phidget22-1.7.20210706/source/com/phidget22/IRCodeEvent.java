package com.phidget22;

public class IRCodeEvent {
   IR Source;
   String Code;
   int BitCount;
   boolean IsRepeat;

   public IRCodeEvent(IR var1, String var2, int var3, boolean var4) {
      this.Source = var1;
      this.Code = var2;
      this.BitCount = var3;
      this.IsRepeat = var4;
   }

   public IR getSource() {
      return this.Source;
   }

   public String getCode() {
      return this.Code;
   }

   public int getBitCount() {
      return this.BitCount;
   }

   public boolean getIsRepeat() {
      return this.IsRepeat;
   }

   @Override
   public String toString() {
      return "[IRCodeEvent](Source: " + this.Source + ", Code: " + this.Code + ", BitCount: " + this.BitCount + ", IsRepeat: " + this.IsRepeat + ")";
   }
}
