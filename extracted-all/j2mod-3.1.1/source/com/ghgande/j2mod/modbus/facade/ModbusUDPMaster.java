package com.ghgande.j2mod.modbus.facade;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.net.UDPMasterConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ModbusUDPMaster extends AbstractModbusMaster {
   private final UDPMasterConnection connection;

   public ModbusUDPMaster(String addr) {
      this(addr, 502);
   }

   public ModbusUDPMaster(String addr, int port) {
      this(addr, port, 3000);
   }

   public ModbusUDPMaster(String addr, int port, int timeout) {
      try {
         InetAddress slaveAddress = InetAddress.getByName(addr);
         this.connection = new UDPMasterConnection(slaveAddress);
         this.connection.setPort(port);
         this.connection.setTimeout(timeout);
      } catch (UnknownHostException var5) {
         throw new RuntimeException("Failed to construct ModbusUDPMaster instance.", var5);
      }
   }

   @Override
   public synchronized void connect() throws Exception {
      if (this.connection != null && !this.connection.isConnected()) {
         this.connection.connect();
         this.transaction = this.connection.getModbusTransport().createTransaction();
         this.setTransaction(this.transaction);
      }
   }

   @Override
   public synchronized void disconnect() {
      if (this.connection != null && this.connection.isConnected()) {
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
      return this.connection != null && this.connection.isConnected();
   }
}
