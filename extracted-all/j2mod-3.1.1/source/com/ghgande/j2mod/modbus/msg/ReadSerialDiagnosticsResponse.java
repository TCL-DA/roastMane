package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadSerialDiagnosticsResponse extends ModbusResponse {
   private int function;
   private short data;

   public ReadSerialDiagnosticsResponse() {
      this.setFunctionCode(8);
      this.setDataLength(4);
   }

   public int getFunction() {
      return this.function;
   }

   public void setFunction(int function) {
      this.function = function;
      this.data = 0;
   }

   public int getWordCount() {
      return 1;
   }

   public int getData() {
      return this.data;
   }

   public void setData(int value) {
      this.data = (short)value;
   }

   @Deprecated
   public int getData(int index) {
      if (index != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.data;
      }
   }

   @Deprecated
   public void setData(int index, int value) {
      if (index != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         this.data = (short)value;
      }
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.function = din.readUnsignedShort();
      this.data = (short)(din.readShort() & '\uffff');
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)(this.function >> 8), (byte)(this.function & 0xFF), (byte)(this.data >> 8), (byte)(this.data & 255)};
   }
}
