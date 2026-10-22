package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReportSlaveIDResponse extends ModbusResponse {
   int length;
   byte[] data;
   int status;
   int slaveId;

   public ReportSlaveIDResponse() {
      this.setFunctionCode(17);
   }

   public int getSlaveID() {
      return this.slaveId;
   }

   public void setSlaveID(int unitID) {
      this.slaveId = unitID;
   }

   public boolean getStatus() {
      return this.status != 0;
   }

   public void setStatus(boolean b) {
      this.status = b ? 255 : 0;
   }

   public byte[] getData() {
      byte[] result = new byte[this.length - 2];
      System.arraycopy(this.data, 0, result, 0, this.length - 2);
      return result;
   }

   public void setData(byte[] data) {
      if (data == null) {
         this.length = 2;
         this.data = new byte[0];
      } else if (data.length > 249) {
         throw new IllegalArgumentException("data length limit exceeded");
      } else {
         this.length = data.length + 2;
         this.data = new byte[data.length];
         System.arraycopy(data, 0, this.data, 0, data.length);
      }
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.length = din.readUnsignedByte();
      if (this.length >= 2 && this.length <= 255) {
         this.slaveId = din.readUnsignedByte();
         this.status = din.readUnsignedByte();
         this.data = new byte[this.length - 2];
         if (this.length > 2) {
            din.readFully(this.data, 0, this.length - 2);
         }
      }
   }

   @Override
   public byte[] getMessage() {
      byte[] result = new byte[3 + this.length];
      int offset = 0;
      result[offset++] = (byte)(this.length + 2);
      result[offset++] = (byte)this.slaveId;
      result[offset++] = (byte)this.status;
      if (this.length > 0) {
         System.arraycopy(this.data, 0, result, offset, this.length - 2);
      }

      return result;
   }
}
