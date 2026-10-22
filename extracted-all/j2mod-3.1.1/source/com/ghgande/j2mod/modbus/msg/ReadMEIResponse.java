package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadMEIResponse extends ModbusResponse {
   private static final Logger logger = LoggerFactory.getLogger(ReadMEIResponse.class);
   private int fieldLevel = 0;
   private int conformity = 1;
   private int fieldCount = 0;
   private String[] fields = new String[64];
   private int[] fieldIds = new int[64];
   private boolean moreFollows = false;
   private int nextFieldId;

   public ReadMEIResponse() {
      this.setFunctionCode(43);
   }

   public int getFieldCount() {
      return this.fields == null ? 0 : this.fields.length;
   }

   public synchronized String[] getFields() {
      String[] dest = new String[this.fields.length];
      System.arraycopy(this.fields, 0, dest, 0, dest.length);
      return dest;
   }

   public String getField(int index) throws IndexOutOfBoundsException {
      return this.fields[index];
   }

   public int getFieldId(int index) throws IndexOutOfBoundsException {
      return this.fieldIds[index];
   }

   public void setFieldLevel(int level) {
      this.fieldLevel = level;
   }

   public void addField(int id, String text) {
      this.fieldIds[this.fieldCount] = id;
      this.fields[this.fieldCount] = text;
      this.fieldCount++;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      int subCode = din.readUnsignedByte();
      if (subCode != 14) {
         throw new IOException("Invalid sub code");
      } else {
         this.fieldLevel = din.readUnsignedByte();
         this.conformity = din.readUnsignedByte();
         this.moreFollows = din.readUnsignedByte() == 255;
         this.nextFieldId = din.readUnsignedByte();
         this.fieldCount = din.readUnsignedByte();
         int byteCount = 6;
         if (this.fieldCount > 0) {
            this.fields = new String[this.fieldCount];
            this.fieldIds = new int[this.fieldCount];

            for (int i = 0; i < this.fieldCount; i++) {
               this.fieldIds[i] = din.readUnsignedByte();
               int len = din.readUnsignedByte();
               byte[] data = new byte[len];
               din.readFully(data);
               this.fields[i] = new String(data, "UTF-8");
               byteCount += 2 + len;
            }

            this.setDataLength(byteCount);
         } else {
            this.setDataLength(byteCount);
         }
      }
   }

   @Override
   public byte[] getMessage() {
      int size = 6;

      for (int i = 0; i < this.fieldCount; i++) {
         size++;
         size = ++size + this.fields[i].length();
      }

      byte[] result = new byte[size];
      int offset = 0;
      result[offset++] = 14;
      result[offset++] = (byte)this.fieldLevel;
      result[offset++] = (byte)this.conformity;
      result[offset++] = (byte)(this.moreFollows ? 255 : 0);
      result[offset++] = (byte)this.nextFieldId;
      result[offset++] = (byte)this.fieldCount;

      for (int i = 0; i < this.fieldCount; i++) {
         result[offset++] = (byte)this.fieldIds[i];
         result[offset++] = (byte)this.fields[i].length();

         try {
            System.arraycopy(this.fields[i].getBytes("US-ASCII"), 0, result, offset, this.fields[i].length());
         } catch (Exception var6) {
            logger.debug("Problem converting bytes to string - {}", var6.getMessage());
         }

         offset += this.fields[i].length();
      }

      return result;
   }
}
