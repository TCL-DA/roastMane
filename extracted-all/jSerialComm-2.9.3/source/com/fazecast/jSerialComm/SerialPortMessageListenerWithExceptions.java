package com.fazecast.jSerialComm;

public interface SerialPortMessageListenerWithExceptions extends SerialPortMessageListener {
   void catchException(Exception var1);
}
