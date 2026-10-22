package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ExceptionResponse extends ModbusResponse {
   private int exceptionCode = -1;

   public ExceptionResponse(int fc, int exc) {
      this.setDataLength(1);
      this.setFunctionCode(fc | 128);
      this.exceptionCode = exc;
   }

   public ExceptionResponse(int fc) {
      this.setDataLength(1);
      this.setFunctionCode(fc | 128);
   }

   public ExceptionResponse() {
      this.setDataLength(1);
   }

   public int getExceptionCode() {
      return this.exceptionCode;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeByte(this.getExceptionCode());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.exceptionCode = din.readUnsignedByte();
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)this.getExceptionCode()};
   }
}
