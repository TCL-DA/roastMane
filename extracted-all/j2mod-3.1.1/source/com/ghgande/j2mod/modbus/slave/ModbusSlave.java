package com.ghgande.j2mod.modbus.slave;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.net.ModbusSerialListener;
import com.ghgande.j2mod.modbus.net.ModbusTCPListener;
import com.ghgande.j2mod.modbus.net.ModbusUDPListener;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusSlave {
   private static final Logger logger = LoggerFactory.getLogger(ModbusSlave.class);
   private final ModbusSlaveType type;
   private final int port;
   private final SerialParameters serialParams;
   private final AbstractModbusListener listener;
   private boolean isRunning;
   private Thread listenerThread;
   private final Map<Integer, ProcessImage> processImages = new HashMap<>();

   protected ModbusSlave(int port, int poolSize, boolean useRtuOverTcp) throws ModbusException {
      this(ModbusSlaveType.TCP, null, port, poolSize, null, useRtuOverTcp, 0);
   }

   protected ModbusSlave(InetAddress address, int port, int poolSize, boolean useRtuOverTcp, int maxIdleSeconds) throws ModbusException {
      this(ModbusSlaveType.TCP, address, port, poolSize, null, useRtuOverTcp, maxIdleSeconds);
   }

   protected ModbusSlave(int port, boolean useRtuOverTcp) throws ModbusException {
      this(ModbusSlaveType.UDP, null, port, 0, null, useRtuOverTcp, 0);
   }

   protected ModbusSlave(InetAddress address, int port, boolean useRtuOverTcp) throws ModbusException {
      this(ModbusSlaveType.UDP, address, port, 0, null, useRtuOverTcp, 0);
   }

   protected ModbusSlave(SerialParameters serialParams) throws ModbusException {
      this(ModbusSlaveType.SERIAL, null, 0, 0, serialParams, false, 0);
   }

   private ModbusSlave(
      ModbusSlaveType type, InetAddress address, int port, int poolSize, SerialParameters serialParams, boolean useRtuOverTcp, int maxIdleSeconds
   ) {
      this.type = type == null ? ModbusSlaveType.TCP : type;
      this.port = port;
      this.serialParams = serialParams;
      logger.debug("Creating {} listener", this.type);
      if (this.type.is(ModbusSlaveType.UDP)) {
         this.listener = new ModbusUDPListener();
      } else if (this.type.is(ModbusSlaveType.TCP)) {
         ModbusTCPListener tcpListener = new ModbusTCPListener(poolSize, useRtuOverTcp);
         tcpListener.setMaxIdleSeconds(maxIdleSeconds);
         this.listener = tcpListener;
      } else {
         this.listener = new ModbusSerialListener(serialParams);
      }

      this.listener.setAddress(address);
      this.listener.setPort(port);
      this.listener.setTimeout(0);
   }

   public ModbusSlaveType getType() {
      return this.type;
   }

   public int getPort() {
      return this.port;
   }

   public ProcessImage getProcessImage(int unitId) {
      return this.processImages.get(unitId);
   }

   public ProcessImage removeProcessImage(int unitId) {
      return this.processImages.remove(unitId);
   }

   public ProcessImage addProcessImage(int unitId, ProcessImage processImage) {
      return this.processImages.put(unitId, processImage);
   }

   public SerialParameters getSerialParams() {
      return this.serialParams;
   }

   public void open() throws ModbusException {
      if (!this.isRunning) {
         try {
            this.listenerThread = new Thread(this.listener);
            this.listenerThread.start();

            while (!this.listener.isListening() && this.listener.getError() == null) {
               ModbusUtil.sleep(50L);
            }

            if (!this.listener.isListening()) {
               throw new ModbusException(this.listener.getError());
            }

            this.isRunning = true;
         } catch (Exception var2) {
            this.closeListener();
            throw new ModbusException(var2.getMessage());
         }
      }
   }

   public void close() {
      ModbusSlaveFactory.close(this);
   }

   public String getError() {
      return this.listener != null ? this.listener.getError() : null;
   }

   protected AbstractModbusListener getListener() {
      return this.listener;
   }

   void closeListener() {
      if (this.listener != null && this.listener.isListening()) {
         this.listener.stop();

         for (int count = 0; this.listenerThread != null && this.listenerThread.isAlive() && count < 50; count++) {
            ModbusUtil.sleep(100L);
         }

         if (this.listenerThread != null && this.listenerThread.isAlive()) {
            this.listenerThread.stop();
         }

         this.listenerThread = null;
      }

      this.isRunning = false;
   }

   public String getThreadName() {
      return this.listener == null ? null : this.listener.getThreadName();
   }

   public void setThreadName(String threadName) {
      if (this.listener != null) {
         this.listener.setThreadName(threadName);
      }
   }
}
