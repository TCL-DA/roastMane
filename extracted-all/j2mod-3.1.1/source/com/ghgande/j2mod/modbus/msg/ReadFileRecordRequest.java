package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.File;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Record;
import com.ghgande.j2mod.modbus.procimg.Register;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadFileRecordRequest extends ModbusRequest {
   private ReadFileRecordRequest.RecordRequest[] records;

   public ReadFileRecordRequest() {
      this.setFunctionCode(20);
      this.setDataLength(1);
   }

   public int getRequestSize() {
      if (this.records == null) {
         return 1;
      } else {
         int size = 1;

         for (ReadFileRecordRequest.RecordRequest record : this.records) {
            size += record.getRequestSize();
         }

         return size;
      }
   }

   public int getRequestCount() {
      return this.records == null ? 0 : this.records.length;
   }

   public ReadFileRecordRequest.RecordRequest getRecord(int index) {
      return this.records[index];
   }

   public void addRequest(ReadFileRecordRequest.RecordRequest request) {
      if (request.getRequestSize() + this.getRequestSize() > 248) {
         throw new IllegalArgumentException();
      } else {
         if (this.records == null) {
            this.records = new ReadFileRecordRequest.RecordRequest[1];
         } else {
            ReadFileRecordRequest.RecordRequest[] old = this.records;
            this.records = new ReadFileRecordRequest.RecordRequest[old.length + 1];
            System.arraycopy(old, 0, this.records, 0, old.length);
         }

         this.records[this.records.length - 1] = request;
         this.setDataLength(this.getRequestSize());
      }
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new ReadFileRecordResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ReadFileRecordResponse response = (ReadFileRecordResponse)this.getResponse();
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      try {
         int i = 0;

         while (i < this.getRequestCount()) {
            ReadFileRecordRequest.RecordRequest recordRequest = this.getRecord(i);
            if (recordRequest.getFileNumber() >= 0 && recordRequest.getFileNumber() < procimg.getFileCount()) {
               File file = procimg.getFileByNumber(recordRequest.getFileNumber());
               if (recordRequest.getRecordNumber() >= 0 && recordRequest.getRecordNumber() < file.getRecordCount()) {
                  Record record = file.getRecord(recordRequest.getRecordNumber());
                  int registers = recordRequest.getWordCount();
                  if (record == null && registers != 0) {
                     return this.createExceptionResponse(2);
                  }

                  short[] data = new short[registers];

                  for (int j = 0; j < registers; j++) {
                     Register register = record.getRegister(j);
                     if (register == null) {
                        return this.createExceptionResponse(2);
                     }

                     data[j] = register.toShort();
                  }

                  ReadFileRecordResponse.RecordResponse recordResponse = new ReadFileRecordResponse.RecordResponse(data);
                  response.addResponse(recordResponse);
                  i++;
                  continue;
               }

               return this.createExceptionResponse(2);
            }

            return this.createExceptionResponse(2);
         }

         return response;
      } catch (IllegalAddressException var12) {
         return this.createExceptionResponse(2);
      }
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      int byteCount = din.readUnsignedByte();
      int recordCount = byteCount / 7;
      this.records = new ReadFileRecordRequest.RecordRequest[recordCount];

      for (int i = 0; i < recordCount; i++) {
         if (din.readByte() != 6) {
            throw new IOException();
         }

         int file = din.readUnsignedShort();
         int record = din.readUnsignedShort();
         if (record < 0 || record >= 10000) {
            throw new IOException();
         }

         int count = din.readUnsignedShort();
         this.records[i] = new ReadFileRecordRequest.RecordRequest(file, record, count);
      }
   }

   @Override
   public byte[] getMessage() {
      byte[] request = new byte[1 + 7 * this.records.length];
      int offset = 0;
      request[offset++] = (byte)(request.length - 1);

      for (ReadFileRecordRequest.RecordRequest record : this.records) {
         record.getRequest(request, offset);
         offset += 7;
      }

      return request;
   }

   public static class RecordRequest {
      private final int fileNumber;
      private final int recordNumber;
      private final int wordCount;

      public RecordRequest(int file, int record, int count) {
         this.fileNumber = file;
         this.recordNumber = record;
         this.wordCount = count;
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

      public int getRequestSize() {
         return 7 + this.wordCount * 2;
      }

      public void getRequest(byte[] request, int offset) {
         request[offset] = 6;
         request[offset + 1] = (byte)(this.fileNumber >> 8);
         request[offset + 2] = (byte)(this.fileNumber & 0xFF);
         request[offset + 3] = (byte)(this.recordNumber >> 8);
         request[offset + 4] = (byte)(this.recordNumber & 0xFF);
         request[offset + 5] = (byte)(this.wordCount >> 8);
         request[offset + 6] = (byte)(this.wordCount & 0xFF);
      }

      public byte[] getRequest() {
         byte[] request = new byte[7];
         this.getRequest(request, 0);
         return request;
      }
   }
}
