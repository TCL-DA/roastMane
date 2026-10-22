package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.DigitalIn;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadInputDiscretesRequest extends ModbusRequest {
   private int reference;
   private int bitCount;

   public ReadInputDiscretesRequest() {
      this.setFunctionCode(2);
      this.setDataLength(4);
   }

   public ReadInputDiscretesRequest(int ref, int count) {
      this.setFunctionCode(2);
      this.setDataLength(4);
      this.setReference(ref);
      this.setBitCount(count);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new ReadInputDiscretesResponse(this.getBitCount()));
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      DigitalIn[] dins;
      try {
         dins = procimg.getDigitalInRange(this.getReference(), this.getBitCount());
      } catch (IllegalAddressException var6) {
         return this.createExceptionResponse(2);
      }

      ReadInputDiscretesResponse response = (ReadInputDiscretesResponse)this.getResponse();

      for (int i = 0; i < dins.length; i++) {
         response.setDiscreteStatus(i, dins[i].isSet());
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
