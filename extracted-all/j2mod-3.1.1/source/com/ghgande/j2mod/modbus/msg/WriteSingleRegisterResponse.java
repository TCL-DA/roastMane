package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteSingleRegisterResponse extends ModbusResponse {
   private int reference;
   private int registerValue;

   public WriteSingleRegisterResponse() {
      this.setDataLength(4);
      this.setFunctionCode(6);
   }

   public WriteSingleRegisterResponse(int reference, int value) {
      this.setReference(reference);
      this.setRegisterValue(value);
      this.setDataLength(4);
      this.setFunctionCode(6);
   }

   public int getRegisterValue() {
      return this.registerValue;
   }

   private void setRegisterValue(int value) {
      this.registerValue = value;
   }

   public int getReference() {
      return this.reference;
   }

   private void setReference(int ref) {
      this.reference = ref;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.setReference(din.readUnsignedShort());
      this.setRegisterValue(din.readUnsignedShort());
      this.setDataLength(4);
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{
         (byte)(this.reference >> 8 & 0xFF), (byte)(this.reference & 0xFF), (byte)(this.registerValue >> 8 & 0xFF), (byte)(this.registerValue & 0xFF)
      };
   }
}
