package com.ghgande.j2mod.modbus.procimg;

public class SimpleInputRegister extends SynchronizedAbstractRegister implements InputRegister {
   public SimpleInputRegister() {
   }

   public SimpleInputRegister(byte b1, byte b2) {
      this.register[0] = b1;
      this.register[1] = b2;
   }

   public SimpleInputRegister(int value) {
      this.setValue(value);
   }

   @Override
   public String toString() {
      return this.register == null ? "invalid" : this.getValue() + "";
   }
}
