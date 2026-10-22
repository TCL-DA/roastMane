package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadFileRecordResponse extends ModbusResponse {
   private ReadFileRecordResponse.RecordResponse[] records = null;

   public ReadFileRecordResponse() {
      this.setFunctionCode(20);
   }

   public int getByteCount() {
      if (this.records == null) {
         return 1;
      } else {
         int size = 1;

         for (ReadFileRecordResponse.RecordResponse record : this.records) {
            size += record.getResponseSize();
         }

         return size;
      }
   }

   public int getRecordCount() {
      return this.records == null ? 0 : this.records.length;
   }

   public ReadFileRecordResponse.RecordResponse getRecord(int index) {
      return this.records[index];
   }

   public void addResponse(ReadFileRecordResponse.RecordResponse response) {
      if (this.records == null) {
         this.records = new ReadFileRecordResponse.RecordResponse[1];
      } else {
         ReadFileRecordResponse.RecordResponse[] old = this.records;
         this.records = new ReadFileRecordResponse.RecordResponse[old.length + 1];
         System.arraycopy(old, 0, this.records, 0, old.length);
      }

      this.records[this.records.length - 1] = response;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeByte(this.getByteCount() - 1);
      if (this.records != null) {
         for (ReadFileRecordResponse.RecordResponse record : this.records) {
            dout.write(record.getResponse());
         }
      }
   }

   @Override
   public void readData(DataInput din) throws IOException {
      int byteCount = din.readUnsignedByte() & 0xFF;
      int remainder = byteCount;

      while (remainder > 0) {
         int length = din.readUnsignedByte();
         int function = din.readByte();
         remainder--;
         if (function != 6 || length - 1 > --remainder) {
            throw new IOException("Invalid response format");
         }

         short[] data = new short[(length - 1) / 2];

         for (int i = 0; i < data.length; i++) {
            data[i] = din.readShort();
            remainder -= 2;
         }

         ReadFileRecordResponse.RecordResponse response = new ReadFileRecordResponse.RecordResponse(data);
         this.addResponse(response);
      }

      this.setDataLength(byteCount + 1);
   }

   @Override
   public byte[] getMessage() {
      byte[] result = new byte[this.getByteCount()];
      int offset = 0;
      result[offset++] = (byte)(result.length - 1);

      for (ReadFileRecordResponse.RecordResponse record : this.records) {
         record.getResponse(result, offset);
         offset += record.getWordCount() * 2;
      }

      return result;
   }

   public static class RecordResponse {
      private final int wordCount;
      private final byte[] data;

      public RecordResponse(short[] data) {
         this.wordCount = data.length;
         this.data = new byte[this.wordCount * 2];
         int offset = 0;

         for (int i = 0; i < this.wordCount; i++) {
            this.data[offset++] = (byte)(data[i] >> 8);
            this.data[offset++] = (byte)(data[i] & 255);
         }
      }

      public int getWordCount() {
         return this.wordCount;
      }

      public SimpleRegister getRegister(int register) {
         if (register >= 0 && register < this.wordCount) {
            byte b1 = this.data[register * 2];
            byte b2 = this.data[register * 2 + 1];
            return new SimpleRegister(b1, b2);
         } else {
            throw new IndexOutOfBoundsException("0 <= " + register + " < " + this.wordCount);
         }
      }

      public int getResponseSize() {
         return 2 + this.wordCount * 2;
      }

      public void getResponse(byte[] request, int offset) {
         request[offset] = (byte)(1 + this.wordCount * 2);
         request[offset + 1] = 6;
         System.arraycopy(this.data, 0, request, offset + 2, this.data.length);
      }

      public byte[] getResponse() {
         byte[] request = new byte[this.getResponseSize()];
         this.getResponse(request, 0);
         return request;
      }
   }
}
