package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.util.BitVector;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadInputDiscretesResponse extends ModbusResponse {
   private int bitCount;
   private BitVector discretes;

   public ReadInputDiscretesResponse() {
      this.setFunctionCode(2);
   }

   public ReadInputDiscretesResponse(int count) {
      this.setFunctionCode(2);
      this.setBitCount(count);
   }

   public int getBitCount() {
      return this.bitCount;
   }

   public void setBitCount(int count) {
      this.bitCount = count;
      this.discretes = new BitVector(count);
      this.setDataLength(this.discretes.byteSize() + 1);
   }

   public BitVector getDiscretes() {
      return this.discretes;
   }

   public boolean getDiscreteStatus(int index) throws IndexOutOfBoundsException {
      return this.discretes.getBit(index);
   }

   public void setDiscreteStatus(int index, boolean b) throws IndexOutOfBoundsException {
      this.discretes.setBit(index, b);
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeByte(this.discretes.byteSize());
      dout.write(this.discretes.getBytes(), 0, this.discretes.byteSize());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      int count = din.readUnsignedByte();
      byte[] data = new byte[count];

      for (int k = 0; k < count; k++) {
         data[k] = din.readByte();
      }

      this.discretes = BitVector.createBitVector(data);
      if (this.discretes != null) {
         this.bitCount = this.discretes.size();
      }

      this.setDataLength(count + 1);
   }

   @Override
   public byte[] getMessage() {
      int len = 1 + this.discretes.byteSize();
      byte[] result = new byte[len];
      result[0] = (byte)this.discretes.byteSize();
      System.arraycopy(this.discretes.getBytes(), 0, result, 1, this.discretes.byteSize());
      return result;
   }
}
