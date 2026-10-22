package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadExceptionStatusResponse extends ModbusResponse {
   private int status;

   public ReadExceptionStatusResponse() {
      this.setFunctionCode(7);
      this.setDataLength(1);
   }

   public int getStatus() {
      return this.status;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.status = din.readByte() & 255;
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)(this.status & 0xFF)};
   }
}
