package com.fazecast.jSerialComm;

public interface SerialPortPacketListener extends SerialPortDataListener {
   int getPacketSize();
}
