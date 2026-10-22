package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadMultipleRegistersRequest extends ModbusRequest {
   private int reference;
   private int wordCount;

   public ReadMultipleRegistersRequest() {
      this.setFunctionCode(3);
      this.setDataLength(4);
   }

   public ReadMultipleRegistersRequest(int ref, int count) {
      this.setFunctionCode(3);
      this.setDataLength(4);
      this.setReference(ref);
      this.setWordCount(count);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new ReadMultipleRegistersResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      Register[] regs;
      try {
         regs = procimg.getRegisterRange(this.getReference(), this.getWordCount());
      } catch (IllegalAddressException var6) {
         return this.createExceptionResponse(2);
      }

      ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)this.getResponse();
      response.setRegisters(regs);
      return response;
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public int getWordCount() {
      return this.wordCount;
   }

   public void setWordCount(int count) {
      this.wordCount = count;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeShort(this.reference);
      dout.writeShort(this.wordCount);
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.reference = din.readUnsignedShort();
      this.wordCount = din.readUnsignedShort();
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)(this.reference >> 8 & 0xFF), (byte)(this.reference & 0xFF), (byte)(this.wordCount >> 8 & 0xFF), (byte)(this.wordCount & 0xFF)};
   }
}
