package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusRTUTCPTransport;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransport;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPSlaveConnection {
   private static final Logger logger = LoggerFactory.getLogger(TCPSlaveConnection.class);
   private Socket socket;
   private int timeout = 3000;
   private boolean connected;
   private ModbusTCPTransport transport;

   public TCPSlaveConnection(Socket socket) {
      this(socket, false);
   }

   public TCPSlaveConnection(Socket socket, boolean useRtuOverTcp) {
      try {
         this.setSocket(socket, useRtuOverTcp);
      } catch (IOException var4) {
         logger.debug("TCPSlaveConnection::Socket invalid");
         throw new IllegalStateException("Socket invalid", var4);
      }
   }

   public void close() {
      if (this.connected) {
         try {
            this.transport.close();
            this.socket.close();
         } catch (IOException var2) {
            logger.warn("Could not close socket", var2);
         }

         this.connected = false;
      }
   }

   public AbstractModbusTransport getModbusTransport() {
      return this.transport;
   }

   public long getLastActivityTimestamp() {
      return this.transport.getLastActivityTimestamp();
   }

   private void setSocket(Socket socket, boolean useRtuOverTcp) throws IOException {
      this.socket = socket;
      if (this.transport == null) {
         if (useRtuOverTcp) {
            logger.trace("setSocket() -> using RTU over TCP transport.");
            this.transport = new ModbusRTUTCPTransport(socket);
         } else {
            logger.trace("setSocket() -> using standard TCP transport.");
            this.transport = new ModbusTCPTransport(socket);
         }
      } else {
         this.transport.setSocket(socket);
      }

      this.connected = true;
   }

   public int getTimeout() {
      return this.timeout;
   }

   public void setTimeout(int timeout) {
      this.timeout = timeout;

      try {
         this.socket.setSoTimeout(timeout);
      } catch (IOException var3) {
         logger.warn("Could not set timeout to {}", timeout, var3);
      }
   }

   public int getPort() {
      return this.socket.getLocalPort();
   }

   public InetAddress getAddress() {
      return this.socket.getLocalAddress();
   }

   public boolean isConnected() {
      return this.connected;
   }
}
