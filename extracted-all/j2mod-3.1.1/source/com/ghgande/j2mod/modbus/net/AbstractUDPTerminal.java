package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.io.ModbusUDPTransport;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class AbstractUDPTerminal {
   protected InetAddress address;
   protected ModbusUDPTransport transport;
   protected boolean active;
   protected int port = 502;
   protected int timeout = 3000;
   protected DatagramSocket socket;

   public InetAddress getAddress() {
      return this.address;
   }

   public synchronized int getPort() {
      return this.port;
   }

   protected synchronized void setPort(int port) {
      this.port = port;
   }

   public boolean isActive() {
      return this.active;
   }

   public synchronized void setTimeout(int timeout) {
      this.timeout = timeout;
   }

   public ModbusUDPTransport getTransport() {
      return this.transport;
   }

   public abstract void activate() throws Exception;

   public abstract void deactivate();

   public abstract void sendMessage(byte[] var1) throws Exception;

   public abstract byte[] receiveMessage() throws Exception;
}
