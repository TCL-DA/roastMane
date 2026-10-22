package com.ghgande.j2mod.modbus.procimg;

public abstract class SynchronizedAbstractRegister implements Register {
   protected byte[] register = new byte[2];

   @Override
   public synchronized int getValue() {
      if (this.register == null) {
         throw new IllegalAddressException();
      } else {
         return (this.register[0] & 0xFF) << 8 | this.register[1] & 0xFF;
      }
   }

   @Override
   public int toUnsignedShort() {
      return this.getValue();
   }

   @Override
   public short toShort() {
      if (this.register == null) {
         throw new IllegalAddressException();
      } else {
         return (short)(this.register[0] << 8 | this.register[1] & 255);
      }
   }

   @Override
   public synchronized byte[] toBytes() {
      byte[] dest = new byte[this.register.length];
      System.arraycopy(this.register, 0, dest, 0, dest.length);
      return dest;
   }

   @Override
   public synchronized void setValue(short s) {
      if (this.register == null) {
         throw new IllegalAddressException();
      } else {
         this.register[0] = (byte)(0xFF & s >> 8);
         this.register[1] = (byte)(255 & s);
      }
   }

   @Override
   public synchronized void setValue(byte[] bytes) {
      if (bytes.length < 2) {
         throw new IllegalArgumentException();
      } else if (this.register == null) {
         throw new IllegalAddressException();
      } else {
         this.register[0] = bytes[0];
         this.register[1] = bytes[1];
      }
   }

   @Override
   public synchronized void setValue(int v) {
      if (this.register == null) {
         throw new IllegalAddressException();
      } else {
         this.register[0] = (byte)(0xFF & v >> 8);
         this.register[1] = (byte)(0xFF & v);
      }
   }
}
