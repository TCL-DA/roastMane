package com.ghgande.j2mod.modbus.procimg;

public interface ProcessImageImplementation extends ProcessImage {
   byte DIG_TRUE = 1;
   byte DIG_FALSE = 0;
   byte DIG_INVALID = -1;

   void setDigitalOut(int var1, DigitalOut var2) throws IllegalAddressException;

   void addDigitalOut(DigitalOut var1);

   void addDigitalOut(int var1, DigitalOut var2);

   void removeDigitalOut(DigitalOut var1);

   void setDigitalIn(int var1, DigitalIn var2) throws IllegalAddressException;

   void addDigitalIn(DigitalIn var1);

   void addDigitalIn(int var1, DigitalIn var2);

   void removeDigitalIn(DigitalIn var1);

   void setInputRegister(int var1, InputRegister var2) throws IllegalAddressException;

   void addInputRegister(InputRegister var1);

   void addInputRegister(int var1, InputRegister var2);

   void removeInputRegister(InputRegister var1);

   void setRegister(int var1, Register var2) throws IllegalAddressException;

   void addRegister(Register var1);

   void addRegister(int var1, Register var2);

   void removeRegister(Register var1);

   void setFile(int var1, File var2) throws IllegalAddressException;

   void addFile(File var1);

   void addFile(int var1, File var2);

   void removeFile(File var1);

   void setFIFO(int var1, FIFO var2) throws IllegalAddressException;

   void addFIFO(FIFO var1);

   void addFIFO(int var1, FIFO var2);

   void removeFIFO(FIFO var1);
}
