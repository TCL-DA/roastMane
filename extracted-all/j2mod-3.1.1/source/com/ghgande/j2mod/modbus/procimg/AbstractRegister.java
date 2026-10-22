package com.ghgande.j2mod.modbus.procimg;

public abstract class AbstractRegister implements Register {
   protected byte[] register = new byte[2];

   @Override
   public int getValue() {
      return (this.register[0] & 0xFF) << 8 | this.register[1] & 0xFF;
   }

   @Override
   public int toUnsignedShort() {
      return (this.register[0] & 0xFF) << 8 | this.register[1] & 0xFF;
   }

   @Override
   public short toShort() {
      return (short)(this.register[0] << 8 | this.register[1] & 255);
   }

   @Override
   public synchronized byte[] toBytes() {
      byte[] dest = new byte[this.register.length];
      System.arraycopy(this.register, 0, dest, 0, dest.length);
      return dest;
   }

   @Override
   public void setValue(short s) {
      this.register[0] = (byte)(0xFF & s >> 8);
      this.register[1] = (byte)(255 & s);
   }

   @Override
   public void setValue(byte[] bytes) {
      if (bytes.length < 2) {
         throw new IllegalArgumentException();
      } else {
         this.register[0] = bytes[0];
         this.register[1] = bytes[1];
      }
   }

   @Override
   public void setValue(int v) {
      this.register[0] = (byte)(0xFF & v >> 8);
      this.register[1] = (byte)(0xFF & v);
   }
}
