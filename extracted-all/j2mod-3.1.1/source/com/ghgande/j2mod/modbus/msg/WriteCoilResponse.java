package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.Modbus;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteCoilResponse extends ModbusResponse {
   private boolean coil = false;
   private int reference;

   public WriteCoilResponse() {
      this.setFunctionCode(5);
      this.setDataLength(4);
   }

   public WriteCoilResponse(int reference, boolean b) {
      this.setFunctionCode(5);
      this.setDataLength(4);
      this.setReference(reference);
      this.setCoil(b);
   }

   public boolean getCoil() {
      return this.coil;
   }

   public void setCoil(boolean b) {
      this.coil = b;
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      byte[] data = new byte[4];
      din.readFully(data);
      this.setReference(data[0] << 8 | data[1] & 255);
      this.setCoil(data[2] == -1);
      this.setDataLength(4);
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
