package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MaskWriteRegisterResponse extends ModbusResponse {
   private int reference;
   private int andMask;
   private int orMask;

   public MaskWriteRegisterResponse() {
      this.setFunctionCode(22);
      this.setDataLength(6);
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public int getAndMask() {
      return this.andMask;
   }

   public void setAndMask(int mask) {
      this.andMask = mask;
   }

   public int getOrMask() {
      return this.orMask;
   }

   public void setOrMask(int mask) {
      this.orMask = mask;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.reference = din.readUnsignedShort();
      this.andMask = din.readUnsignedShort();
      this.orMask = din.readUnsignedShort();
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{
         (byte)(this.reference >> 8),
         (byte)(this.reference & 0xFF),
         (byte)(this.andMask >> 8),
         (byte)(this.andMask & 0xFF),
         (byte)(this.orMask >> 8),
         (byte)(this.orMask & 0xFF)
      };
   }
}
