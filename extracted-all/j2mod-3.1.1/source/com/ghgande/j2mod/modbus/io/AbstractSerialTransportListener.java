package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;

public abstract class AbstractSerialTransportListener {
   public void beforeMessageWrite(AbstractSerialConnection port, ModbusMessage msg) {
   }

   public void afterMessageWrite(AbstractSerialConnection port, ModbusMessage msg) {
   }

   public void beforeRequestRead(AbstractSerialConnection port) {
   }

   public void afterRequestRead(AbstractSerialConnection port, ModbusRequest req) {
   }

   public void beforeResponseRead(AbstractSerialConnection port) {
   }

   public void afterResponseRead(AbstractSerialConnection port, ModbusResponse res) {
   }
}
