package com.ghgande.j2mod.modbus.facade;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class ModbusSerialMaster extends AbstractModbusMaster {
   private final AbstractSerialConnection connection;
   private final int transDelay;

   public ModbusSerialMaster(SerialParameters param) {
      this(param, 3000, 0);
   }

   public ModbusSerialMaster(SerialParameters param, int timeout) {
      this(param, timeout, 0);
   }

   public ModbusSerialMaster(SerialParameters param, int timeout, int transDelay) {
      try {
         this.transDelay = transDelay > -1 ? transDelay : 0;
         this.connection = new SerialConnection(param);
         this.connection.setTimeout(timeout);
         this.timeout = timeout;
      } catch (Exception var5) {
         throw new RuntimeException(var5.getMessage());
      }
   }

   public AbstractSerialConnection getConnection() {
      return this.connection;
   }

   @Override
   public synchronized void connect() throws Exception {
      if (this.connection != null && !this.connection.isOpen()) {
         this.connection.open();
         this.transaction = this.connection.getModbusTransport().createTransaction();
         ((ModbusSerialTransaction)this.transaction).setTransDelayMS(this.transDelay);
         this.setTransaction(this.transaction);
      }
   }

   @Override
   public synchronized void disconnect() {
      if (this.connection != null && this.connection.isOpen()) {
         this.connection.close();
         this.transaction = null;
         this.setTransaction(null);
      }
   }

   @Override
   public synchronized void setTimeout(int timeout) {
      super.setTimeout(timeout);
      if (this.connection != null) {
         this.connection.setTimeout(timeout);
      }
   }

   @Override
   public AbstractModbusTransport getTransport() {
      return this.connection == null ? null : this.connection.getModbusTransport();
   }

   @Override
   public boolean isConnected() {
      return this.connection != null && this.connection.isOpen();
   }
}
