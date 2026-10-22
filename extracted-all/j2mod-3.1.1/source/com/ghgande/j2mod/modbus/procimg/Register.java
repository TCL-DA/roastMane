package com.ghgande.j2mod.modbus.procimg;

public interface Register extends InputRegister {
   void setValue(int var1);

   void setValue(short var1);

   void setValue(byte[] var1);
}
