package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface ModbusMessage {
   boolean isHeadless();

   void setHeadless();

   int getTransactionID();

   int getProtocolID();

   int getDataLength();

   int getUnitID();

   int getFunctionCode();

   byte[] getMessage();

   String getHexMessage();

   int getOutputLength();

   void writeTo(DataOutput var1) throws IOException;

   void readFrom(DataInput var1) throws IOException;
}
