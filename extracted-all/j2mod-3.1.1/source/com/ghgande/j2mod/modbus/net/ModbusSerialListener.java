package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusSerialListener extends AbstractModbusListener {
   private static final Logger logger = LoggerFactory.getLogger(ModbusSerialListener.class);
   private final AbstractSerialConnection serialCon;

   public ModbusSerialListener(SerialParameters params) {
      this.serialCon = new SerialConnection(params);
   }

   public ModbusSerialListener(AbstractSerialConnection serialCon) {
      this.serialCon = serialCon;
   }

   @Override
   public void setTimeout(int timeout) {
      super.setTimeout(timeout);
      if (this.serialCon != null && this.listening) {
         ModbusSerialTransport transport = (ModbusSerialTransport)this.serialCon.getModbusTransport();
         if (transport != null) {
            transport.setTimeout(timeout);
         }
      }
   }

   @Override
   public void run() {
      if (this.threadName == null || this.threadName.isEmpty()) {
         this.threadName = String.format("Modbus Serial Listener [port:%s]", this.serialCon.getDescriptivePortName());
      }

      Thread.currentThread().setName(this.threadName);

      try {
         this.serialCon.open();
      } catch (Exception var6) {
         this.error = String.format("Cannot start Serial listener on port %s - %s", this.serialCon.getPortName(), var6.getMessage());
         this.listening = false;
         return;
      }

      this.listening = true;

      try {
         AbstractModbusTransport transport = this.serialCon.getModbusTransport();

         while (this.listening) {
            this.safeHandleRequest(transport);
         }
      } catch (Exception var7) {
         logger.error("Exception occurred while handling request.", var7);
      } finally {
         this.listening = false;
         this.serialCon.close();
      }
   }

   private void safeHandleRequest(AbstractModbusTransport transport) {
      try {
         this.handleRequest(transport, this);
      } catch (ModbusIOException var3) {
         logger.debug(var3.getMessage());
      }
   }

   @Override
   public void stop() {
      if (this.serialCon != null) {
         this.serialCon.close();
      }

      this.listening = false;
   }
}
