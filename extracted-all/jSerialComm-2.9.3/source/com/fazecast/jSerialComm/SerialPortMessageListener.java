package com.fazecast.jSerialComm;

public interface SerialPortMessageListener extends SerialPortDataListener {
   byte[] getMessageDelimiter();

   boolean delimiterIndicatesEndOfMessage();
}
