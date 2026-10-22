package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteFileRecordResponse extends ModbusResponse {
   private WriteFileRecordResponse.RecordResponse[] records;

   public WriteFileRecordResponse() {
      this.setFunctionCode(21);
      this.setDataLength(7);
   }

   public int getResponseSize() {
      if (this.records == null) {
         return 1;
      } else {
         int size = 1;

         for (WriteFileRecordResponse.RecordResponse record : this.records) {
            size += record.getResponseSize();
         }

         return size;
      }
   }

   public int getRequestCount() {
      return this.records == null ? 0 : this.records.length;
   }

   public WriteFileRecordResponse.RecordResponse getRecord(int index) {
      return this.records[index];
   }

   public void addResponse(WriteFileRecordResponse.RecordResponse response) {
      if (response.getResponseSize() + this.getResponseSize() > 248) {
         throw new IllegalArgumentException();
      } else {
         if (this.records == null) {
            this.records = new WriteFileRecordResponse.RecordResponse[1];
         } else {
            WriteFileRecordResponse.RecordResponse[] old = this.records;
            this.records = new WriteFileRecordResponse.RecordResponse[old.length + 1];
            System.arraycopy(old, 0, this.records, 0, old.length);
         }

         this.records[this.records.length - 1] = response;
         this.setDataLength(this.getResponseSize());
      }
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      int byteCount = din.readUnsignedByte();
      this.records = new WriteFileRecordResponse.RecordResponse[0];
      int offset = 1;

      while (offset + 7 < byteCount) {
         int function = din.readUnsignedByte();
         int file = din.readUnsignedShort();
         int record = din.readUnsignedShort();
         int count = din.readUnsignedShort();
         offset += 7;
         if (function != 6) {
            throw new IOException();
         }

         if (record >= 0 && record < 10000) {
            if (count >= 0 && count < 126) {
               short[] registers = new short[count];

               for (int j = 0; j < count; j++) {
                  registers[j] = din.readShort();
                  offset += 2;
               }

               WriteFileRecordResponse.RecordResponse[] dummy = new WriteFileRecordResponse.RecordResponse[this.records.length + 1];
               if (this.records.length > 0) {
                  System.arraycopy(this.records, 0, dummy, 0, this.records.length);
               }

               this.records = dummy;
               this.records[this.records.length - 1] = new WriteFileRecordResponse.RecordResponse(file, record, registers);
               continue;
            }

            throw new IOException();
         }

         throw new IOException();
      }
   }

   @Override
   public byte[] getMessage() {
      byte[] results = new byte[this.getResponseSize()];
      results[0] = (byte)(this.getResponseSize() - 1);
      int offset = 1;

      for (WriteFileRecordResponse.RecordResponse record : this.records) {
         record.getResponse(results, offset);
         offset += record.getResponseSize();
      }

      return results;
   }

   public static class RecordResponse {
      private final int fileNumber;
      private final int recordNumber;
      private final int wordCount;
      private final byte[] data;

      public RecordResponse(int file, int record, short[] values) {
         this.fileNumber = file;
         this.recordNumber = record;
         this.wordCount = values.length;
         this.data = new byte[this.wordCount * 2];
         int offset = 0;

         for (int i = 0; i < this.wordCount; i++) {
            this.data[offset++] = (byte)(values[i] >> 8);
            this.data[offset++] = (byte)(values[i] & 255);
         }
      }

      public int getFileNumber() {
         return this.fileNumber;
      }

      public int getRecordNumber() {
         return this.recordNumber;
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
         return 7 + this.wordCount * 2;
      }

      public void getResponse(byte[] response, int offset) {
         response[offset++] = 6;
         response[offset++] = (byte)(this.fileNumber >> 8);
         response[offset++] = (byte)(this.fileNumber & 0xFF);
         response[offset++] = (byte)(this.recordNumber >> 8);
         response[offset++] = (byte)(this.recordNumber & 0xFF);
         response[offset++] = (byte)(this.wordCount >> 8);
         response[offset++] = (byte)(this.wordCount & 0xFF);
         System.arraycopy(this.data, 0, response, offset, this.data.length);
      }

      public byte[] getResponse() {
         byte[] response = new byte[7 + 2 * this.wordCount];
         this.getResponse(response, 0);
         return response;
      }
   }
}
