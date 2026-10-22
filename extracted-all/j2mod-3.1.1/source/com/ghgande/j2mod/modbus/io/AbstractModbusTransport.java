package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import java.io.IOException;

public abstract class AbstractModbusTransport {
   protected int timeout = 3000;

   public void setTimeout(int time) {
      this.timeout = time;
   }

   public abstract void close() throws IOException;

   public abstract ModbusTransaction createTransaction();

   public abstract void writeRequest(ModbusRequest var1) throws ModbusIOException;

   public abstract void writeResponse(ModbusResponse var1) throws ModbusIOException;

   public abstract ModbusRequest readRequest(AbstractModbusListener var1) throws ModbusIOException;

   public abstract ModbusResponse readResponse() throws ModbusIOException;
}
