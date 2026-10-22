package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadFIFOQueueRequest extends ModbusRequest {
   private int reference;

   public ReadFIFOQueueRequest() {
      this.setFunctionCode(24);
      this.setDataLength(2);
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new ReadFIFOQueueResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      InputRegister[] registers;
      try {
         Register queue = procimg.getRegister(this.reference);
         int count = queue.getValue();
         if (count < 0 || count > 31) {
            return this.createExceptionResponse(3);
         }

         registers = procimg.getRegisterRange(this.reference + 1, count);
      } catch (IllegalAddressException var7) {
         return this.createExceptionResponse(2);
      }

      ReadFIFOQueueResponse response = (ReadFIFOQueueResponse)this.getResponse();
      response.setRegisters(registers);
      return response;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.reference = din.readUnsignedShort();
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)(this.reference >> 8), (byte)(this.reference & 0xFF)};
   }
}
