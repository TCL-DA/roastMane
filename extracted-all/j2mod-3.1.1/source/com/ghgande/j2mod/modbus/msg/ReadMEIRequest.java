package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

public class ReadMEIRequest extends ModbusRequest {
   private int subCode;
   private int fieldLevel;
   private int fieldId;

   public ReadMEIRequest() {
      this.setFunctionCode(43);
      this.subCode = 14;
      this.setDataLength(3);
   }

   public ReadMEIRequest(int level, int id) {
      this.setFunctionCode(43);
      this.subCode = 14;
      this.setDataLength(3);
      this.setLevel(level);
      this.setFieldId(id);
   }

   @Override
   public ModbusResponse getResponse() {
      if (this.getSubCode() != 14) {
         IllegalFunctionExceptionResponse error = new IllegalFunctionExceptionResponse();
         return this.updateResponseWithHeader(error);
      } else {
         return this.updateResponseWithHeader(new ReadMEIResponse());
      }
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      return this.createExceptionResponse(1);
   }

   public int getSubCode() {
      return this.subCode;
   }

   public int getLevel() {
      return this.fieldLevel;
   }

   public void setLevel(int level) {
      this.fieldLevel = level;
   }

   public int getFieldId() {
      return this.fieldId;
   }

   public void setFieldId(int id) {
      this.fieldId = id;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      byte[] results = new byte[]{(byte)this.subCode, (byte)this.fieldLevel, (byte)this.fieldId};
      dout.write(results);
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.subCode = din.readUnsignedByte();
      if (this.subCode == 14) {
         this.fieldLevel = din.readUnsignedByte();
         this.fieldId = din.readUnsignedByte();
      } else {
         try {
            while (din.readByte() >= 0) {
            }
         } catch (EOFException var3) {
         }
      }
   }

   @Override
   public byte[] getMessage() {
      return new byte[]{(byte)this.subCode, (byte)this.fieldLevel, (byte)this.fieldId};
   }
}
