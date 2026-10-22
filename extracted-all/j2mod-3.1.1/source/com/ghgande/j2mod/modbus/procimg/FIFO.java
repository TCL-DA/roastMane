package com.ghgande.j2mod.modbus.procimg;

import java.util.Vector;

public class FIFO {
   private final int address;
   private int registerCount;
   private final Vector<Register> registers;

   public FIFO(int address) {
      this.address = address;
      this.registerCount = 0;
      this.registers = new Vector<>();
   }

   public synchronized int getRegisterCount() {
      return this.registerCount;
   }

   public synchronized Register[] getRegisters() {
      Register[] result = new Register[this.registerCount + 1];
      result[0] = new SimpleRegister(this.registerCount);

      for (int i = 0; i < this.registerCount; i++) {
         result[i + 1] = this.registers.get(i);
      }

      return result;
   }

   public synchronized void pushRegister(Register register) {
      if (this.registerCount == 31) {
         this.registers.remove(0);
      } else {
         this.registerCount++;
      }

      this.registers.add(new SimpleRegister(register.getValue()));
   }

   public synchronized void resetRegisters() {
      this.registers.removeAllElements();
      this.registerCount = 0;
   }

   public int getAddress() {
      return this.address;
   }
}
