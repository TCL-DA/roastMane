package com.ghgande.j2mod.modbus.procimg;

public class SimpleDigitalIn implements DigitalIn {
   protected boolean set = false;

   public SimpleDigitalIn() {
   }

   public SimpleDigitalIn(boolean b) {
      this.set(b);
   }

   @Override
   public boolean isSet() {
      return this.set;
   }

   public void set(boolean b) {
      this.set = b;
   }
}
