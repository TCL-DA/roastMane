package com.ghgande.j2mod.modbus.procimg;

public class SimpleRegister extends SynchronizedAbstractRegister implements Register {
   public SimpleRegister(byte b1, byte b2) {
      this.register[0] = b1;
      this.register[1] = b2;
   }

   public SimpleRegister(int value) {
      this.setValue(value);
   }

   public SimpleRegister() {
      this.register = null;
   }

   @Override
   public String toString() {
      return this.register == null ? "invalid" : this.getValue() + "";
   }
}
