package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.DigitalOut;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.util.BitVector;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteMultipleCoilsRequest extends ModbusRequest {
   private int reference;
   private BitVector coils;

   public WriteMultipleCoilsRequest(int ref, BitVector bv) {
      this.setFunctionCode(15);
      this.setDataLength(bv.byteSize() + 5);
      this.setReference(ref);
      this.coils = bv;
   }

   public WriteMultipleCoilsRequest(int ref, int count) {
      this.setFunctionCode(15);
      this.setDataLength((count + 7) / 8 + 5);
      this.setReference(ref);
      this.coils = new BitVector(count);
   }

   public WriteMultipleCoilsRequest() {
      this.setFunctionCode(15);
      this.setDataLength(5);
      this.coils = new BitVector(1);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new WriteMultipleCoilsResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      try {
         DigitalOut[] douts = procimg.getDigitalOutRange(this.reference, this.coils.size());

         for (int i = 0; i < douts.length; i++) {
            douts[i].set(this.coils.getBit(i));
         }
      } catch (IllegalAddressException var6) {
         return this.createExceptionResponse(2);
      }

      WriteMultipleCoilsResponse response = (WriteMultipleCoilsResponse)this.getResponse();
      response.setBitCount(this.coils.size());
      response.setReference(this.reference);
      return response;
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public int getBitCount() {
      return this.coils == null ? 0 : this.coils.size();
   }

   public int getByteCount() {
      return this.coils.byteSize();
   }

   public boolean getCoilStatus(int index) throws IndexOutOfBoundsException {
      return this.coils.getBit(index);
   }

   public void setCoilStatus(int index, boolean b) throws IndexOutOfBoundsException {
      this.coils.setBit(index, b);
   }

   public BitVector getCoils() {
      return this.coils;
   }

   public void setCoils(BitVector bv) {
      this.coils = bv;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeShort(this.reference);
      dout.writeShort(this.coils.size());
      dout.writeByte(this.coils.byteSize());
      dout.write(this.coils.getBytes());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.reference = din.readUnsignedShort();
      int bitcount = din.readUnsignedShort();
      int coilBytes = din.readUnsignedByte();
      byte[] data = new byte[coilBytes];

      for (int k = 0; k < coilBytes; k++) {
         data[k] = din.readByte();
      }

      this.coils = BitVector.createBitVector(data, bitcount);
      this.setDataLength(coilBytes + 5);
   }

   @Override
   public byte[] getMessage() {
      int len = this.coils.byteSize() + 5;
      byte[] result = new byte[len];
      result[0] = (byte)(this.reference >> 8 & 0xFF);
      result[1] = (byte)(this.reference & 0xFF);
      result[2] = (byte)(this.coils.size() >> 8 & 0xFF);
      result[3] = (byte)(this.coils.size() & 0xFF);
      result[4] = (byte)this.coils.byteSize();
      System.arraycopy(this.coils.getBytes(), 0, result, 5, this.coils.byteSize());
      return result;
   }
}
