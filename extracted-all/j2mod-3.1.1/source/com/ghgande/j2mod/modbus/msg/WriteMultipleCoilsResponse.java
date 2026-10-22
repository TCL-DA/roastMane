package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteMultipleCoilsResponse extends ModbusResponse {
   private int reference;
   private int bitCount;

   public WriteMultipleCoilsResponse(int ref, int count) {
      this.reference = ref;
      this.bitCount = count;
      this.setDataLength(4);
   }

   public WriteMultipleCoilsResponse() {
      this.setDataLength(4);
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public int getBitCount() {
      return this.bitCount;
   }

   public void setBitCount(int count) {
      this.bitCount = count;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeShort(this.reference);
      dout.writeShort(this.bitCount);
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.reference = din.readUnsignedShort();
      this.bitCount = din.readUnsignedShort();
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)(this.reference >> 8 & 0xFF), (byte)(this.reference & 0xFF), (byte)(this.bitCount >> 8 & 0xFF), (byte)(this.bitCount & 0xFF)};
   }
}
