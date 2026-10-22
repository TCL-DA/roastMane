package com.phidget22;

public class AsyncResult {
   Phidget Source;
   ErrorCode ReturnCode;

   public AsyncResult(Phidget var1, ErrorCode var2) {
      this.Source = var1;
      this.ReturnCode = var2;
   }

   public Phidget getSource() {
      return this.Source;
   }

   public ErrorCode getReturnCode() {
      return this.ReturnCode;
   }

   @Override
   public String toString() {
      return "[AsyncResult](Source: " + this.Source + ", ReturnCode: " + this.ReturnCode + ")";
   }
}
