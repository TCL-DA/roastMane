package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteMultipleRegistersResponse extends ModbusResponse {
   private int wordCount;
   private int reference;

   public WriteMultipleRegistersResponse() {
      this.setFunctionCode(16);
      this.setDataLength(4);
   }

   public WriteMultipleRegistersResponse(int reference, int wordCount) {
      this.setFunctionCode(16);
      this.setDataLength(4);
      this.reference = reference;
      this.wordCount = wordCount;
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public int getByteCount() {
      return this.wordCount * 2;
   }

   public int getWordCount() {
      return this.wordCount;
   }

   public void setWordCount(int count) {
      this.wordCount = count;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.setReference(din.readUnsignedShort());
      this.setWordCount(din.readUnsignedShort());
      this.setDataLength(4);
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)(this.reference >> 8 & 0xFF), (byte)(this.reference & 0xFF), (byte)(this.wordCount >> 8 & 0xFF), (byte)(this.wordCount & 0xFF)};
   }
}
