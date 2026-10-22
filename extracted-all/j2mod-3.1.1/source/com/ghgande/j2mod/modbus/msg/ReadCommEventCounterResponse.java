package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadCommEventCounterResponse extends ModbusResponse {
   private int status;
   private int events;

   public ReadCommEventCounterResponse() {
      this.setFunctionCode(11);
      this.setDataLength(4);
   }

   public int getStatus() {
      return this.status;
   }

   public void setStatus(int status) {
      if (status != 0 && status != 65535) {
         throw new IllegalArgumentException("Illegal status value: " + status);
      } else {
         this.status = status;
      }
   }

   public int getEventCount() {
      return this.events;
   }

   public void setEventCount(int count) {
      this.events = count;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.status = din.readUnsignedShort();
      this.events = din.readUnsignedShort();
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)(this.status >> 8), (byte)(this.status & 0xFF), (byte)(this.events >> 8), (byte)(this.events & 0xFF)};
   }
}
