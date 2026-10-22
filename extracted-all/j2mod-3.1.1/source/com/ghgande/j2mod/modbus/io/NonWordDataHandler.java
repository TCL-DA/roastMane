package com.ghgande.j2mod.modbus.io;

import java.io.DataInput;
import java.io.IOException;

@Deprecated
public interface NonWordDataHandler {
   byte[] getData();

   void readData(DataInput var1, int var2, int var3) throws IOException;

   int getWordCount();

   int commitUpdate();

   void prepareData(int var1, int var2);
}
