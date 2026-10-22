package com.ghgande.j2mod.modbus.msg;

public class IllegalAddressExceptionResponse extends ExceptionResponse {
   public IllegalAddressExceptionResponse() {
      super(0, 2);
   }

   public IllegalAddressExceptionResponse(int function) {
      super(function, 2);
   }

   @Override
   public void setFunctionCode(int fc) {
      super.setFunctionCode(fc | 128);
   }
}
