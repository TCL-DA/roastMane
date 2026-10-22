package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.util.BitVector;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadCoilsResponse extends ModbusResponse {
   private BitVector coils;

   public ReadCoilsResponse() {
      this.setFunctionCode(1);
      this.setDataLength(1);
      this.coils = null;
   }

   public ReadCoilsResponse(int count) {
      this.setFunctionCode(1);
      this.coils = new BitVector(count);
      this.setDataLength(this.coils.byteSize() + 1);
   }

   public int getBitCount() {
      return this.coils == null ? 0 : this.coils.size();
   }

   public BitVector getCoils() {
      return this.coils;
   }

   public boolean getCoilStatus(int index) throws IndexOutOfBoundsException {
      if (index < 0) {
         throw new IllegalArgumentException(index + " < 0");
      } else if (index > this.coils.size()) {
         throw new IndexOutOfBoundsException(index + " > " + this.coils.size());
      } else {
         return this.coils.getBit(index);
      }
   }

   public void setCoilStatus(int index, boolean b) {
      if (index < 0) {
         throw new IllegalArgumentException(index + " < 0");
      } else if (index > this.coils.size()) {
         throw new IndexOutOfBoundsException(index + " > " + this.coils.size());
      } else {
         this.coils.setBit(index, b);
      }
   }

   @Override
   public void writeData(DataOutput output) throws IOException {
      byte[] result = this.getMessage();
      output.write(result);
   }

   @Override
   public void readData(DataInput input) throws IOException {
      int count = input.readUnsignedByte();
      byte[] data = new byte[count];
      input.readFully(data, 0, count);
      this.coils = BitVector.createBitVector(data);
      this.setDataLength(count + 1);
   }

   @Override
   public byte[] getMessage() {
      int len = 1 + this.coils.byteSize();
      byte[] result = new byte[len];
      result[0] = (byte)this.coils.byteSize();
      System.arraycopy(this.coils.getBytes(), 0, result, 1, this.coils.byteSize());
      return result;
   }
}
