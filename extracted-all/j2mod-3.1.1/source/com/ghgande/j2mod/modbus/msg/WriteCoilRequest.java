package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.DigitalOut;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteCoilRequest extends ModbusRequest {
   private int reference;
   private boolean coil;

   public WriteCoilRequest() {
      this.setFunctionCode(5);
      this.setDataLength(4);
   }

   public WriteCoilRequest(int ref, boolean b) {
      this.setFunctionCode(5);
      this.setDataLength(4);
      this.setReference(ref);
      this.setCoil(b);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new WriteCoilResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      try {
         DigitalOut dout = procimg.getDigitalOut(this.getReference());
         dout.set(this.getCoil());
      } catch (IllegalAddressException var6) {
         return this.createExceptionResponse(2);
      }

      WriteCoilResponse response = (WriteCoilResponse)this.getResponse();
      response.setReference(this.getReference());
      response.setCoil(this.getCoil());
      return response;
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public boolean getCoil() {
      return this.coil;
   }

   public void setCoil(boolean b) {
      this.coil = b;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeShort(this.reference);
      if (this.coil) {
         dout.write(Modbus.COIL_ON_BYTES, 0, 2);
      } else {
         dout.write(Modbus.COIL_OFF_BYTES, 0, 2);
      }
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.reference = din.readUnsignedShort();
      if (din.readByte() == -1) {
         this.coil = true;
      } else {
         this.coil = false;
      }

      din.readByte();
   }

   @Override
   public byte[] getMessage() {
      byte[] result = new byte[]{(byte)(this.reference >> 8 & 0xFF), (byte)(this.reference & 0xFF), 0, 0};
      if (this.coil) {
         result[2] = Modbus.COIL_ON_BYTES[0];
         result[3] = Modbus.COIL_ON_BYTES[1];
      } else {
         result[2] = Modbus.COIL_OFF_BYTES[0];
         result[3] = Modbus.COIL_OFF_BYTES[1];
      }

      return result;
   }
}
