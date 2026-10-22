package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractModbusListener implements Runnable {
   private static final Logger logger = LoggerFactory.getLogger(AbstractModbusListener.class);
   protected int port = 502;
   protected boolean listening;
   protected InetAddress address;
   protected String error;
   protected int timeout = 3000;
   protected String threadName;

   public abstract void stop();

   public void setPort(int port) {
      this.port = port > 0 ? port : 502;
   }

   public int getPort() {
      return this.port;
   }

   public void setAddress(InetAddress addr) {
      this.address = addr;
   }

   public InetAddress getAddress() {
      return this.address;
   }

   public boolean isListening() {
      return this.listening;
   }

   public void setListening(boolean b) {
      this.listening = b;
   }

   public String getError() {
      return this.error;
   }

   public int getTimeout() {
      return this.timeout;
   }

   public void setTimeout(int timeout) {
      this.timeout = timeout;
   }

   void handleRequest(AbstractModbusTransport transport, AbstractModbusListener listener) throws ModbusIOException {
      if (transport == null) {
         throw new ModbusIOException("No transport specified");
      } else {
         ModbusRequest request = transport.readRequest(listener);
         if (request == null) {
            throw new ModbusIOException("Request for transport %s is invalid (null)", transport.getClass().getSimpleName());
         } else {
            ProcessImage spi = this.getProcessImage(request.getUnitID());
            ModbusResponse response;
            if (spi == null) {
               response = request.createExceptionResponse(2);
               response.setAuxiliaryType(ModbusResponse.AuxiliaryMessageTypes.UNIT_ID_MISSMATCH);
            } else {
               response = request.createResponse(this);
            }

            if (logger.isDebugEnabled()) {
               logger.debug("Request:{}", request.getHexMessage());
               if (transport instanceof ModbusRTUTransport && response.getAuxiliaryType() == ModbusResponse.AuxiliaryMessageTypes.UNIT_ID_MISSMATCH) {
                  logger.debug("Not sending response because it was not meant for us.");
               } else {
                  logger.debug("Response:{}", response.getHexMessage());
               }
            }

            transport.writeResponse(response);
         }
      }
   }

   public ProcessImage getProcessImage(int unitId) {
      ModbusSlave slave = ModbusSlaveFactory.getSlave(this);
      return slave != null ? slave.getProcessImage(unitId) : null;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public void setThreadName(String threadName) {
      this.threadName = threadName;
   }
}
