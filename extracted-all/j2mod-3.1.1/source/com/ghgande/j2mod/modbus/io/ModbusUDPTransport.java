package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.net.AbstractUDPTerminal;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusUDPTransport extends AbstractModbusTransport {
   private static final Logger logger = LoggerFactory.getLogger(ModbusUDPTransport.class);
   private final AbstractUDPTerminal terminal;
   private final BytesOutputStream byteOutputStream = new BytesOutputStream(256);
   private final BytesInputStream byteInputStream = new BytesInputStream(256);

   public ModbusUDPTransport(AbstractUDPTerminal terminal) {
      this.terminal = terminal;
   }

   @Override
   public void setTimeout(int time) {
      super.setTimeout(time);
      if (this.terminal != null) {
         this.terminal.setTimeout(this.timeout);
      }
   }

   @Override
   public void close() throws IOException {
   }

   @Override
   public ModbusTransaction createTransaction() {
      ModbusUDPTransaction trans = new ModbusUDPTransaction();
      trans.setTerminal(this.terminal);
      return trans;
   }

   @Override
   public void writeResponse(ModbusResponse msg) throws ModbusIOException {
      this.writeMessage(msg);
   }

   @Override
   public void writeRequest(ModbusRequest msg) throws ModbusIOException {
      this.writeMessage(msg);
   }

   @Override
   public ModbusRequest readRequest(AbstractModbusListener listener) throws ModbusIOException {
      try {
         ModbusRequest req;
         synchronized (this.byteInputStream) {
            this.byteInputStream.reset(this.terminal.receiveMessage());
            this.byteInputStream.skip(7);
            int functionCode = this.byteInputStream.readUnsignedByte();
            this.byteInputStream.reset();
            req = ModbusRequest.createModbusRequest(functionCode);
            req.readFrom(this.byteInputStream);
         }

         return req;
      } catch (Exception var7) {
         throw new ModbusIOException("I/O exception - failed to read", var7);
      }
   }

   @Override
   public ModbusResponse readResponse() throws ModbusIOException {
      try {
         ModbusResponse res;
         synchronized (this.byteInputStream) {
            this.byteInputStream.reset(this.terminal.receiveMessage());
            this.byteInputStream.skip(7);
            int functionCode = this.byteInputStream.readUnsignedByte();
            this.byteInputStream.reset();
            res = ModbusResponse.createModbusResponse(functionCode);
            res.readFrom(this.byteInputStream);
         }

         return res;
      } catch (InterruptedIOException var6) {
         throw new ModbusIOException("Socket was interrupted", var6);
      } catch (Exception var7) {
         logger.debug("I/O exception while reading modbus response.", var7);
         throw new ModbusIOException("I/O exception - failed to read - %s", var7.getMessage());
      }
   }

   private void writeMessage(ModbusMessage msg) throws ModbusIOException {
      try {
         synchronized (this.byteOutputStream) {
            int len = msg.getOutputLength();
            this.byteOutputStream.reset();
            msg.writeTo(this.byteOutputStream);
            byte[] data = this.byteOutputStream.getBuffer();
            data = Arrays.copyOf(data, len);
            this.terminal.sendMessage(data);
         }
      } catch (Exception var7) {
         throw new ModbusIOException("I/O exception - failed to write", var7);
      }
   }
}
