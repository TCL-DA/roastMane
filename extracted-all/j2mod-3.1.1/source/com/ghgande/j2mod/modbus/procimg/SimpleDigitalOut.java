package com.ghgande.j2mod.modbus.procimg;

public class SimpleDigitalOut implements DigitalOut {
   protected boolean set;

   public SimpleDigitalOut() {
   }

   public SimpleDigitalOut(boolean b) {
      this.set(b);
   }

   @Override
   public boolean isSet() {
      return this.set;
   }

   @Override
   public void set(boolean b) {
      this.set = b;
   }
}
