package com.phidget22;

public class PhidgetException extends Exception {
   private String description;
   private String detail;
   private ErrorCode errno;

   private PhidgetException(int var1, String var2, String var3) {
      super("PhidgetException 0x" + String.format("%02x", var1) + " (" + var2 + ")\n" + var3);
      this.errno = ErrorCode.getEnum(var1);
      this.description = var2;
      this.detail = var3;
   }

   private PhidgetException(int var1, String var2) {
      super("PhidgetException 0x" + String.format("%02x", var1) + " (" + var2 + ")");
      this.errno = ErrorCode.getEnum(var1);
      this.description = var2;
      this.detail = "";
   }

   public ErrorCode getErrorCode() {
      return this.errno;
   }

   public String getDescription() {
      return this.description;
   }

   public String getDetail() {
      return this.detail;
   }

   @Override
   public String toString() {
      return this.detail.length() > 0
         ? "PhidgetException 0x" + String.format("%02x", this.errno.getCode()) + " (" + this.description + ")\n" + this.detail
         : "PhidgetException 0x" + String.format("%02x", this.errno.getCode()) + " (" + this.description + ")";
   }
}
