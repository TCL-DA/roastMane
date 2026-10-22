package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MaskWriteRegisterRequest extends ModbusRequest {
   private int reference;
   private int andMask;
   private int orMask;

   public MaskWriteRegisterRequest(int ref, int andMask, int orMask) {
      this.setFunctionCode(22);
      this.setReference(ref);
      this.setAndMask(andMask);
      this.setOrMask(orMask);
      this.setDataLength(6);
   }

   public MaskWriteRegisterRequest() {
      this.setFunctionCode(22);
      this.setDataLength(6);
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public int getAndMask() {
      return this.andMask;
   }

   public void setAndMask(int mask) {
      this.andMask = mask;
   }

   public int getOrMask() {
      return this.orMask;
   }

   public void setOrMask(int mask) {
      this.orMask = mask;
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new MaskWriteRegisterResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      try {
         Register register = procimg.getRegister(this.reference);
         int value = register.getValue();
         value = value & this.andMask | this.orMask & ~this.andMask;
         register.setValue(value);
      } catch (IllegalAddressException var6) {
         return this.createExceptionResponse(2);
      }

      MaskWriteRegisterResponse response = (MaskWriteRegisterResponse)this.getResponse();
      response.setReference(this.reference);
      response.setAndMask(this.andMask);
      response.setOrMask(this.orMask);
      return response;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.reference = din.readUnsignedShort();
      this.andMask = din.readUnsignedShort();
      this.orMask = din.readUnsignedShort();
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{
         (byte)(this.reference >> 8),
         (byte)(this.reference & 0xFF),
         (byte)(this.andMask >> 8),
         (byte)(this.andMask & 0xFF),
         (byte)(this.orMask >> 8),
         (byte)(this.orMask & 0xFF)
      };
   }
}
