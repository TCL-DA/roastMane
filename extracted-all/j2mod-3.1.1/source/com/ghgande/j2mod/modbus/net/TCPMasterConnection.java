package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusRTUTCPTransport;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransport;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPMasterConnection {
   private static final Logger logger = LoggerFactory.getLogger(TCPMasterConnection.class);
   private Socket socket;
   private int timeout = 3000;
   private boolean connected;
   private InetAddress address;
   private int port = 502;
   private ModbusTCPTransport transport;
   private boolean useRtuOverTcp = false;
   private boolean useUrgentData = false;

   public TCPMasterConnection(InetAddress adr) {
      this.address = adr;
   }

   private void prepareTransport(boolean useRtuOverTcp) throws IOException {
      if (this.transport != null && this.useRtuOverTcp == useRtuOverTcp) {
         logger.trace("prepareTransport() -> using custom transport: {}", this.transport.getClass().getSimpleName());
         this.transport.setSocket(this.socket);
      } else {
         this.useRtuOverTcp = useRtuOverTcp;
         if (useRtuOverTcp) {
            logger.trace("prepareTransport() -> using RTU over TCP transport.");
            this.transport = new ModbusRTUTCPTransport(this.socket);
            this.transport.setMaster(this);
         } else {
            logger.trace("prepareTransport() -> using standard TCP transport.");
            this.transport = new ModbusTCPTransport(this.socket);
            this.transport.setMaster(this);
         }
      }

      this.transport.setTimeout(this.timeout);
   }

   public void connect() throws Exception {
      this.connect(this.useRtuOverTcp);
   }

   public void connect(boolean useRtuOverTcp) throws Exception {
      if (!this.isConnected()) {
         logger.debug("connect()");
         this.socket = new Socket();
         this.socket.setReuseAddress(true);
         this.socket.setSoLinger(true, 1);
         this.socket.setKeepAlive(true);
         this.setTimeout(this.timeout);
         this.socket.connect(new InetSocketAddress(this.address, this.port), this.timeout);
         this.prepareTransport(useRtuOverTcp);
         this.connected = true;
      }
   }

   public synchronized boolean isConnected() {
      if (!this.connected || this.socket == null) {
         return this.connected;
      } else if (!this.socket.isConnected() || this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
         try {
            try {
               this.socket.close();
            } catch (IOException var9) {
               logger.error("Socket exception", var9);
            }

            return this.connected;
         } finally {
            this.connected = false;
         }
      } else if (this.useUrgentData) {
         try {
            this.socket.sendUrgentData(0);
            ModbusUtil.sleep(5L);
         } catch (IOException var8) {
            this.connected = false;

            try {
               this.socket.close();
            } catch (IOException var7) {
            }

            return this.connected;
         }

         return this.connected;
      } else {
         return this.connected;
      }
   }

   public void close() {
      if (this.connected) {
         try {
            this.transport.close();
         } catch (IOException var5) {
            logger.debug("close()", var5);
         } finally {
            this.connected = false;
         }
      }
   }

   public AbstractModbusTransport getModbusTransport() {
      return this.transport;
   }

   public void setModbusTransport(ModbusTCPTransport trans) {
      this.transport = trans;
   }

   public synchronized int getTimeout() {
      return this.timeout;
   }

   public synchronized void setTimeout(int timeout) {
      try {
         this.timeout = timeout;
         if (this.socket != null) {
            this.socket.setSoTimeout(timeout);
         }
      } catch (IOException var3) {
         logger.warn("Could not set timeout to value {}", timeout, var3);
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

   public boolean getUseUrgentData() {
      return this.useUrgentData;
   }

   public void setUseUrgentData(boolean useUrgentData) {
      this.useUrgentData = useUrgentData;
   }

   public boolean isUseRtuOverTcp() {
      return this.useRtuOverTcp;
   }

   public void setUseRtuOverTcp(boolean useRtuOverTcp) throws Exception {
      this.useRtuOverTcp = useRtuOverTcp;
      if (this.isConnected()) {
         this.prepareTransport(useRtuOverTcp);
      }
   }
}
