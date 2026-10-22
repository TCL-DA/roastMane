package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.DigitalOut;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadCoilsRequest extends ModbusRequest {
   private int reference;
   private int bitCount;

   public ReadCoilsRequest() {
      this.setFunctionCode(1);
      this.setDataLength(4);
   }

   public ReadCoilsRequest(int ref, int count) {
      this.setFunctionCode(1);
      this.setDataLength(4);
      this.setReference(ref);
      this.setBitCount(count);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new ReadCoilsResponse(this.bitCount));
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      DigitalOut[] douts;
      try {
         douts = procimg.getDigitalOutRange(this.getReference(), this.getBitCount());
      } catch (IllegalAddressException var6) {
         return this.createExceptionResponse(2);
      }

      ModbusResponse response = this.getResponse();

      for (int i = 0; i < douts.length; i++) {
         ((ReadCoilsResponse)response).setCoilStatus(i, douts[i].isSet());
      }

      return response;
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
      if (count > 2000) {
         throw new IllegalArgumentException("Maximum bitcount exceeded");
      } else {
         this.bitCount = count;
      }
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
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
