package com.fazecast.jSerialComm;

import java.util.EventListener;

public interface SerialPortDataListener extends EventListener {
   int getListeningEvents();

   void serialEvent(SerialPortEvent var1);
}
