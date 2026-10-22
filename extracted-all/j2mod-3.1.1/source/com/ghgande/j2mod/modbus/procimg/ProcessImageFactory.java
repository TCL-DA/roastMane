package com.ghgande.j2mod.modbus.procimg;

public interface ProcessImageFactory {
   ProcessImageImplementation createProcessImageImplementation();

   DigitalIn createDigitalIn();

   DigitalIn createDigitalIn(boolean var1);

   DigitalOut createDigitalOut();

   DigitalOut createDigitalOut(boolean var1);

   InputRegister createInputRegister();

   InputRegister createInputRegister(byte var1, byte var2);

   Register createRegister();

   Register createRegister(byte var1, byte var2);
}
