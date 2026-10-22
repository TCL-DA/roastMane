package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IllegalFunctionRequest extends ModbusRequest {
   public IllegalFunctionRequest(int function) {
      this.setFunctionCode(function);
   }

   public IllegalFunctionRequest(int unit, int function) {
      this.setUnitID(unit);
      this.setFunctionCode(function);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new IllegalFunctionExceptionResponse(this.getFunctionCode()), true);
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      return this.createExceptionResponse(1);
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      throw new RuntimeException();
   }

   @Override
   public void readData(DataInput din) throws IOException {
      int length = this.getDataLength();

      for (int i = 0; i < length; i++) {
         din.readByte();
      }
   }

   @Override
   public byte[] getMessage() {
      return null;
   }
}
