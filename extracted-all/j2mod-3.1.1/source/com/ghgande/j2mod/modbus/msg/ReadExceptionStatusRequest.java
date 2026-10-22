package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadExceptionStatusRequest extends ModbusRequest {
   public ReadExceptionStatusRequest() {
      this.setFunctionCode(7);
      this.setDataLength(0);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new ReadExceptionStatusResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      return this.createExceptionResponse(1);
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
   }

   @Override
   public byte[] getMessage() {
      return new byte[0];
   }
}
