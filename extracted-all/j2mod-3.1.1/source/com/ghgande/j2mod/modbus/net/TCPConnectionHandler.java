package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPConnectionHandler implements Runnable {
   private static final Logger logger = LoggerFactory.getLogger(TCPConnectionHandler.class);
   private static final long watchDogResolution = 5000L;
   private static final long nanosPerSecond = 1000000000L;
   private final TCPSlaveConnection connection;
   private final AbstractModbusTransport transport;
   private final AbstractModbusListener listener;
   private final ScheduledExecutorService watchDog;

   public TCPConnectionHandler(AbstractModbusListener listener, TCPSlaveConnection connection, final int maxIdleSeconds) {
      this.listener = listener;
      this.connection = connection;
      this.transport = this.connection.getModbusTransport();
      if (maxIdleSeconds > 0) {
         this.watchDog = Executors.newSingleThreadScheduledExecutor();
         long checkRate = Math.min((long)maxIdleSeconds * 1000L, 5000L);
         this.watchDog.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
               long nanosIdle = System.nanoTime() - TCPConnectionHandler.this.connection.getLastActivityTimestamp();
               if (nanosIdle > (long)maxIdleSeconds * 1000000000L) {
                  TCPConnectionHandler.logger.warn("Watchdog expired: {}, limit: {}", nanosIdle / 1000000000L, maxIdleSeconds);
                  TCPConnectionHandler.this.connection.close();
                  TCPConnectionHandler.this.watchDog.shutdown();
               }
            }
         }, checkRate, checkRate, TimeUnit.MILLISECONDS);
      } else {
         this.watchDog = null;
      }
   }

   @Override
   public void run() {
      try {
         while (true) {
            this.listener.handleRequest(this.transport, this.listener);
            if (!Thread.currentThread().isInterrupted()) {
               continue;
            }
         }
      } catch (ModbusIOException var5) {
         if (!var5.isEOF()) {
            logger.debug(var5.getMessage());
         }
      } finally {
         this.connection.close();
      }
   }
}
