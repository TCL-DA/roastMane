package com.ghgande.j2mod.modbus.procimg;

public class File {
   private final int fileNumber;
   private final int recordCount;
   private final Record[] records;

   public File(int fileNumber, int records) {
      this.fileNumber = fileNumber;
      this.recordCount = records;
      this.records = new Record[records];
   }

   public int getFileNumber() {
      return this.fileNumber;
   }

   public int getRecordCount() {
      return this.recordCount;
   }

   public Record getRecord(int i) {
      if (i >= 0 && i < this.recordCount) {
         return this.records[i];
      } else {
         throw new IllegalAddressException();
      }
   }

   public File setRecord(int i, Record record) {
      if (i >= 0 && i < this.recordCount) {
         this.records[i] = record;
         return this;
      } else {
         throw new IllegalAddressException();
      }
   }
}
