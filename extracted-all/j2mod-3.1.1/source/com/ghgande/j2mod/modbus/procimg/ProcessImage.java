package com.ghgande.j2mod.modbus.procimg;

public interface ProcessImage {
   DigitalOut[] getDigitalOutRange(int var1, int var2) throws IllegalAddressException;

   DigitalOut getDigitalOut(int var1) throws IllegalAddressException;

   int getDigitalOutCount();

   DigitalIn[] getDigitalInRange(int var1, int var2) throws IllegalAddressException;

   DigitalIn getDigitalIn(int var1) throws IllegalAddressException;

   int getDigitalInCount();

   InputRegister[] getInputRegisterRange(int var1, int var2) throws IllegalAddressException;

   InputRegister getInputRegister(int var1) throws IllegalAddressException;

   int getInputRegisterCount();

   Register[] getRegisterRange(int var1, int var2) throws IllegalAddressException;

   Register getRegister(int var1) throws IllegalAddressException;

   int getRegisterCount();

   File getFile(int var1) throws IllegalAddressException;

   File getFileByNumber(int var1) throws IllegalAddressException;

   int getFileCount();

   FIFO getFIFO(int var1) throws IllegalAddressException;

   FIFO getFIFOByAddress(int var1) throws IllegalAddressException;

   int getFIFOCount();
}
