package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteSingleRegisterRequest extends ModbusRequest {
   private int reference;
   private Register register;

   public WriteSingleRegisterRequest() {
      this.setFunctionCode(6);
      this.setDataLength(4);
   }

   public WriteSingleRegisterRequest(int ref, Register reg) {
      this.setFunctionCode(6);
      this.setDataLength(4);
      this.reference = ref;
      this.register = reg;
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new WriteSingleRegisterResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      Register reg;
      try {
         reg = procimg.getRegister(this.reference);
         reg.setValue(this.register.toBytes());
      } catch (IllegalAddressException var5) {
         return this.createExceptionResponse(2);
      }

      return this.updateResponseWithHeader(new WriteSingleRegisterResponse(this.getReference(), reg.getValue()));
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public Register getRegister() {
      return this.register;
   }

   public void setRegister(Register reg) {
      this.register = reg;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeShort(this.reference);
      dout.write(this.register.toBytes());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.reference = din.readUnsignedShort();
      this.register = new SimpleRegister(din.readByte(), din.readByte());
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{
         (byte)(this.reference >> 8 & 0xFF),
         (byte)(this.reference & 0xFF),
         (byte)(this.register.getValue() >> 8 & 0xFF),
         (byte)(this.register.getValue() & 0xFF)
      };
   }
}
