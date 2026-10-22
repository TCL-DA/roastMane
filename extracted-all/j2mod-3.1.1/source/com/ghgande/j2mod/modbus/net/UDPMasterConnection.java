package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPMasterConnection {
   private static final Logger logger = LoggerFactory.getLogger(UDPMasterConnection.class);
   private UDPMasterTerminal terminal;
   private int timeout = 3000;
   private boolean connected;
   private InetAddress address;
   private int port = 502;

   public UDPMasterConnection(InetAddress adr) {
      this.address = adr;
   }

   public void connect() throws Exception {
      if (!this.connected) {
         this.terminal = new UDPMasterTerminal(this.address);
         this.terminal.setPort(this.port);
         this.terminal.setTimeout(this.timeout);
         this.terminal.activate();
         this.connected = true;
      }
   }

   public void close() {
      if (this.connected) {
         try {
            this.terminal.deactivate();
         } catch (Exception var2) {
            logger.debug("Exception occurred while closing UDPMasterConnection", var2);
         }

         this.connected = false;
      }
   }

   public AbstractModbusTransport getModbusTransport() {
      return this.terminal == null ? null : this.terminal.getTransport();
   }

   public AbstractUDPTerminal getTerminal() {
      return this.terminal;
   }

   public synchronized int getTimeout() {
      return this.timeout;
   }

   public synchronized void setTimeout(int timeout) {
      this.timeout = timeout;
      if (this.terminal != null) {
         this.terminal.setTimeout(timeout);
      }
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public InetAddress getAddress() {
      return this.address;
   }

   public void setAddress(InetAddress adr) {
      this.address = adr;
   }

   public boolean isConnected() {
      return this.connected;
   }
}
