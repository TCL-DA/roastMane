package com.ghgande.j2mod.modbus.msg;

public class IllegalFunctionExceptionResponse extends ExceptionResponse {
   public IllegalFunctionExceptionResponse() {
      super(0, 1);
   }

   public IllegalFunctionExceptionResponse(int function) {
      super(function | 128, 1);
   }

   @Override
   public void setFunctionCode(int fc) {
      super.setFunctionCode(fc | 128);
   }
}
