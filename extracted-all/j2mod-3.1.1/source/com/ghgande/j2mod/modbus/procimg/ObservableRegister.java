package com.ghgande.j2mod.modbus.procimg;

import com.ghgande.j2mod.modbus.util.Observable;

public class ObservableRegister extends Observable implements Register {
   private static final String VALUE = "value";
   protected short register;

   @Override
   public synchronized int getValue() {
      return this.register & 65535;
   }

   @Override
   public int toUnsignedShort() {
      return this.register & 65535;
   }

   @Override
   public short toShort() {
      return this.register;
   }

   @Override
   public synchronized byte[] toBytes() {
      return new byte[]{(byte)(this.register >> 8), (byte)(this.register & 255)};
   }

   @Override
   public synchronized void setValue(short s) {
      this.register = s;
      this.notifyObservers("value");
   }

   @Override
   public synchronized void setValue(byte[] bytes) {
      if (bytes.length < 2) {
         throw new IllegalArgumentException();
      } else {
         this.register = (short)((short)(bytes[0] << 8) | (short)bytes[1] & 255);
         this.notifyObservers("value");
      }
   }

   @Override
   public synchronized void setValue(int v) {
      this.register = (short)v;
      this.notifyObservers("value");
   }
}
