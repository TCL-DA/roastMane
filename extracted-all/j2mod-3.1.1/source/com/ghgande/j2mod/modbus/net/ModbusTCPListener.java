package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.util.ThreadPool;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusTCPListener extends AbstractModbusListener {
   private static final Logger logger = LoggerFactory.getLogger(ModbusTCPListener.class);
   private ServerSocket serverSocket = null;
   private final ThreadPool threadPool;
   private Thread listener;
   private final boolean useRtuOverTcp;
   private int maxIdleSeconds;

   public ModbusTCPListener(int poolsize, InetAddress addr) {
      this(poolsize, addr, false);
   }

   public ModbusTCPListener(int poolsize, InetAddress addr, boolean useRtuOverTcp) {
      this.threadPool = new ThreadPool(poolsize);
      this.address = addr;
      this.useRtuOverTcp = useRtuOverTcp;
      this.maxIdleSeconds = 0;
   }

   public ModbusTCPListener(int poolsize) {
      this(poolsize, false);
   }

   public ModbusTCPListener(int poolsize, boolean useRtuOverTcp) {
      this.threadPool = new ThreadPool(poolsize);

      try {
         this.address = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
      } catch (UnknownHostException var4) {
      }

      this.useRtuOverTcp = useRtuOverTcp;
      this.maxIdleSeconds = 0;
   }

   public void setMaxIdleSeconds(int maxIdleSeconds) {
      if (maxIdleSeconds < 0) {
         throw new IllegalArgumentException("maxIdleSeconds must be >= 0: " + maxIdleSeconds);
      } else {
         this.maxIdleSeconds = maxIdleSeconds;
      }
   }

   @Override
   public void setTimeout(int timeout) {
      super.setTimeout(timeout);
      if (this.serverSocket != null && this.listening) {
         try {
            this.serverSocket.setSoTimeout(timeout);
         } catch (SocketException var3) {
            logger.error("Cannot set socket timeout", var3);
         }
      }
   }

   @Override
   public void run() {
      if (this.threadName == null || this.threadName.isEmpty()) {
         this.threadName = String.format("Modbus TCP Listener [port:%d]", this.port);
      }

      Thread.currentThread().setName(this.threadName);

      try {
         int floodProtection = 100;
         this.serverSocket = new ServerSocket(this.port, floodProtection, this.address);
         this.serverSocket.setSoTimeout(this.timeout);
         logger.debug("Listening to {} (Port {})", this.serverSocket, this.port);
      } catch (Exception var8) {
         this.error = String.format("Cannot start TCP listener on port %d - %s", this.port, var8.getMessage());
         this.listening = false;
         return;
      }

      this.listener = Thread.currentThread();
      this.listening = true;

      try {
         this.threadPool.initPool(this.threadName);

         while (this.listening) {
            Socket incoming;
            try {
               incoming = this.serverSocket.accept();
            } catch (SocketTimeoutException var9) {
               continue;
            }

            logger.debug("Making new connection {}", incoming);
            if (this.listening) {
               TCPSlaveConnection slave = new TCPSlaveConnection(incoming, this.useRtuOverTcp);
               slave.setTimeout(this.timeout);
               this.threadPool.execute(new TCPConnectionHandler(this, slave, this.maxIdleSeconds));
            } else {
               incoming.close();
            }
         }
      } catch (IOException var10) {
         this.error = String.format("Problem starting listener - %s", var10.getMessage());
      } finally {
         this.threadPool.close();
      }
   }

   @Override
   public void stop() {
      this.listening = false;

      try {
         if (this.serverSocket != null) {
            this.serverSocket.close();
         }

         if (this.listener != null) {
            this.listener.join();
         }

         if (this.threadPool != null) {
            this.threadPool.close();
         }
      } catch (Exception var2) {
         logger.error("Error while stopping ModbusTCPListener", var2);
      }
   }
}
