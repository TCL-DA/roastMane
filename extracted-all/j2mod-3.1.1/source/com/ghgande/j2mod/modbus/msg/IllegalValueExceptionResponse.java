package com.ghgande.j2mod.modbus.msg;

public class IllegalValueExceptionResponse extends ExceptionResponse {
   public IllegalValueExceptionResponse() {
      super(0, 3);
   }

   public IllegalValueExceptionResponse(int function) {
      super(function, 3);
   }

   @Override
   public void setFunctionCode(int fc) {
      super.setFunctionCode(fc | 128);
   }
}
