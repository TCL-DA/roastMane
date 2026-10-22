package com.ghgande.j2mod.modbus.facade;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ModbusTCPMaster extends AbstractModbusMaster {
   private final TCPMasterConnection connection;
   private boolean reconnecting = false;
   private final boolean useRtuOverTcp;

   public ModbusTCPMaster(String addr) {
      this(addr, 502, 3000, false, false);
   }

   public ModbusTCPMaster(String addr, boolean useRtuOverTcp) {
      this(addr, 502, 3000, false, useRtuOverTcp);
   }

   public ModbusTCPMaster(String addr, int port) {
      this(addr, port, 3000, false, false);
   }

   public ModbusTCPMaster(String addr, int port, boolean useRtuOverTcp) {
      this(addr, port, 3000, false, useRtuOverTcp);
   }

   public ModbusTCPMaster(String addr, int port, int timeout, boolean reconnect) {
      this(addr, port, timeout, reconnect, false);
   }

   public ModbusTCPMaster(String addr, int port, int timeout, boolean reconnect, boolean useRtuOverTcp) {
      this.useRtuOverTcp = useRtuOverTcp;

      try {
         InetAddress slaveAddress = InetAddress.getByName(addr);
         this.connection = new TCPMasterConnection(slaveAddress);
         this.connection.setPort(port);
         this.connection.setTimeout(timeout);
         this.timeout = timeout;
         this.setReconnecting(reconnect);
      } catch (UnknownHostException var7) {
         throw new RuntimeException("Failed to contruct ModbusTCPMaster instance.", var7);
      }
   }

   @Override
   public synchronized void connect() throws Exception {
      if (this.connection != null && !this.connection.isConnected()) {
         this.connection.connect(this.useRtuOverTcp);
         this.transaction = this.connection.getModbusTransport().createTransaction();
         ((ModbusTCPTransaction)this.transaction).setReconnecting(this.reconnecting);
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

   public synchronized boolean isReconnecting() {
      return this.reconnecting;
   }

   public synchronized void setReconnecting(boolean b) {
      this.reconnecting = b;
      if (this.transaction != null) {
         ((ModbusTCPTransaction)this.transaction).setReconnecting(b);
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
