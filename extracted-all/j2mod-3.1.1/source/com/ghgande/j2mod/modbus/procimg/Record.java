package com.ghgande.j2mod.modbus.procimg;

public class Record {
   private final int recordNumber;
   private final int registerCount;
   private final Register[] registers;

   public Record(int recordNumber, int registers) {
      this.recordNumber = recordNumber;
      this.registerCount = registers;
      this.registers = new Register[registers];

      for (int i = 0; i < this.registerCount; i++) {
         this.registers[i] = new SimpleRegister(0);
      }
   }

   public int getRecordNumber() {
      return this.recordNumber;
   }

   public int getRegisterCount() {
      return this.registerCount;
   }

   public Register getRegister(int register) {
      if (register >= 0 && register < this.registerCount) {
         return this.registers[register];
      } else {
         throw new IllegalAddressException();
      }
   }

   public Record setRegister(int ref, Register register) {
      if (ref >= 0 && ref < this.registerCount) {
         this.registers[ref] = register;
         return this;
      } else {
         throw new IllegalAddressException();
      }
   }
}
