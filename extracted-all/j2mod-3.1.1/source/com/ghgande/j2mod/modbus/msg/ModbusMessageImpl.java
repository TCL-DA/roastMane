package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.util.ModbusUtil;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class ModbusMessageImpl implements ModbusMessage {
   private int transactionID = 0;
   private int protocolID = 0;
   private int dataLength;
   private int unitID = 0;
   private int functionCode;
   private boolean headless = false;

   @Override
   public boolean isHeadless() {
      return this.headless;
   }

   @Override
   public void setHeadless() {
      this.headless = true;
   }

   @Override
   public int getTransactionID() {
      return this.transactionID & 65535;
   }

   public void setTransactionID(int tid) {
      this.transactionID = tid & 65535;
   }

   @Override
   public int getProtocolID() {
      return this.protocolID;
   }

   public void setProtocolID(int pid) {
      this.protocolID = pid;
   }

   @Override
   public int getDataLength() {
      return this.dataLength;
   }

   public void setDataLength(int length) {
      if (length >= 0 && length + 2 <= 255) {
         this.dataLength = length + 2;
      } else {
         throw new IllegalArgumentException("Invalid length: " + length);
      }
   }

   @Override
   public int getUnitID() {
      return this.unitID;
   }

   public void setUnitID(int num) {
      this.unitID = num;
   }

   @Override
   public int getFunctionCode() {
      return this.functionCode;
   }

   protected void setFunctionCode(int code) {
      this.functionCode = code;
   }

   @Override
   public String getHexMessage() {
      return ModbusUtil.toHex(this);
   }

   public void setHeadless(boolean b) {
      this.headless = b;
   }

   @Override
   public int getOutputLength() {
      int l = 2 + this.getDataLength();
      if (!this.isHeadless()) {
         l += 4;
      }

      return l;
   }

   @Override
   public void writeTo(DataOutput dout) throws IOException {
      if (!this.isHeadless()) {
         dout.writeShort(this.getTransactionID());
         dout.writeShort(this.getProtocolID());
         dout.writeShort(this.getDataLength());
      }

      dout.writeByte(this.getUnitID());
      dout.writeByte(this.getFunctionCode());
      this.writeData(dout);
   }

   @Override
   public void readFrom(DataInput din) throws IOException {
      if (!this.isHeadless()) {
         this.setTransactionID(din.readUnsignedShort());
         this.setProtocolID(din.readUnsignedShort());
         this.dataLength = din.readUnsignedShort();
      }

      this.setUnitID(din.readUnsignedByte());
      this.setFunctionCode(din.readUnsignedByte());
      this.readData(din);
   }

   public abstract void writeData(DataOutput var1) throws IOException;

   public abstract void readData(DataInput var1) throws IOException;
}
