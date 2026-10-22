package com.ghgande.j2mod.modbus.procimg;

public interface InputRegister {
   int getValue();

   int toUnsignedShort();

   short toShort();

   byte[] toBytes();
}
