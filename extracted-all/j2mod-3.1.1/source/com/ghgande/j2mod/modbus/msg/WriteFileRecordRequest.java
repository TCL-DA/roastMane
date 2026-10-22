package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.File;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Record;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WriteFileRecordRequest extends ModbusRequest {
   private WriteFileRecordRequest.RecordRequest[] records;

   public WriteFileRecordRequest() {
      this.setFunctionCode(21);
      this.setDataLength(1);
   }

   public int getRequestSize() {
      if (this.records == null) {
         return 1;
      } else {
         int size = 1;

         for (WriteFileRecordRequest.RecordRequest record : this.records) {
            size += record.getRequestSize();
         }

         return size;
      }
   }

   public int getRequestCount() {
      return this.records == null ? 0 : this.records.length;
   }

   public WriteFileRecordRequest.RecordRequest getRecord(int reference) {
      return this.records[reference];
   }

   public void addRequest(WriteFileRecordRequest.RecordRequest request) {
      if (request.getRequestSize() + this.getRequestSize() > 248) {
         throw new IllegalArgumentException();
      } else {
         if (this.records == null) {
            this.records = new WriteFileRecordRequest.RecordRequest[1];
         } else {
            WriteFileRecordRequest.RecordRequest[] old = this.records;
            this.records = new WriteFileRecordRequest.RecordRequest[old.length + 1];
            System.arraycopy(old, 0, this.records, 0, old.length);
         }

         this.records[this.records.length - 1] = request;
         this.setDataLength(this.getRequestSize());
      }
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new WriteFileRecordResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      WriteFileRecordResponse response = (WriteFileRecordResponse)this.getResponse();
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      try {
         int i = 0;

         while (i < this.getRequestCount()) {
            WriteFileRecordRequest.RecordRequest recordRequest = this.getRecord(i);
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

                     register.setValue(recordRequest.getRegister(j).getValue());
                     data[j] = recordRequest.getRegister(j).toShort();
                  }

                  WriteFileRecordResponse.RecordResponse recordResponse = new WriteFileRecordResponse.RecordResponse(
                     file.getFileNumber(), record == null ? 0 : record.getRecordNumber(), data
                  );
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
      this.records = new WriteFileRecordRequest.RecordRequest[0];
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

               WriteFileRecordRequest.RecordRequest[] dummy = new WriteFileRecordRequest.RecordRequest[this.records.length + 1];
               if (this.records.length > 0) {
                  System.arraycopy(this.records, 0, dummy, 0, this.records.length);
               }

               this.records = dummy;
               this.records[this.records.length - 1] = new WriteFileRecordRequest.RecordRequest(file, record, registers);
               continue;
            }

            throw new IOException();
         }

         throw new IOException();
      }
   }

   @Override
   public byte[] getMessage() {
      byte[] results = new byte[this.getRequestSize()];
      results[0] = (byte)(this.getRequestSize() - 1);
      int offset = 1;

      for (WriteFileRecordRequest.RecordRequest record : this.records) {
         record.getRequest(results, offset);
         offset += record.getRequestSize();
      }

      return results;
   }

   public static class RecordRequest {
      private final int fileNumber;
      private final int recordNumber;
      private final int wordCount;
      private final byte[] data;

      public RecordRequest(int file, int record, short[] values) {
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
            throw new IllegalAddressException("0 <= " + register + " < " + this.wordCount);
         }
      }

      public int getRequestSize() {
         return 7 + this.wordCount * 2;
      }

      public void getRequest(byte[] request, int offset) {
         request[offset++] = 6;
         request[offset++] = (byte)(this.fileNumber >> 8);
         request[offset++] = (byte)(this.fileNumber & 0xFF);
         request[offset++] = (byte)(this.recordNumber >> 8);
         request[offset++] = (byte)(this.recordNumber & 0xFF);
         request[offset++] = (byte)(this.wordCount >> 8);
         request[offset++] = (byte)(this.wordCount & 0xFF);
         System.arraycopy(this.data, 0, request, offset, this.data.length);
      }

      public byte[] getRequest() {
         byte[] request = new byte[7 + 2 * this.wordCount];
         this.getRequest(request, 0);
         return request;
      }
   }
}
