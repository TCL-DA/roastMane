package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import java.net.Socket;

public class ModbusRTUTCPTransport extends ModbusTCPTransport {
   public ModbusRTUTCPTransport() {
      this.setHeadless();
   }

   public ModbusRTUTCPTransport(Socket socket) {
      super(socket);
      this.setHeadless();
   }

   @Override
   public void writeResponse(ModbusResponse msg) throws ModbusIOException {
      this.writeMessage(msg, true);
   }

   @Override
   public void writeRequest(ModbusRequest msg) throws ModbusIOException {
      this.writeMessage(msg, true);
   }
}
