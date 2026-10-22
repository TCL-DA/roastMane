package com.fazecast.jSerialComm;

public interface SerialPortDataListenerWithExceptions extends SerialPortDataListener {
   void catchException(Exception var1);
}
