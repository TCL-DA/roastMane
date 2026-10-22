package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.msg.ExceptionResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusSerialTransaction extends ModbusTransaction {
   private static final Logger logger = LoggerFactory.getLogger(ModbusSerialTransaction.class);
   private int transDelayMS = 0;
   private long lastTransactionTimestamp = 0L;

   public ModbusSerialTransaction() {
   }

   public ModbusSerialTransaction(ModbusRequest request) {
      this.setRequest(request);
   }

   public ModbusSerialTransaction(AbstractSerialConnection con) {
      this.setSerialConnection(con);
   }

   public synchronized void setSerialConnection(AbstractSerialConnection con) {
      this.transport = con.getModbusTransport();
   }

   public synchronized void setTransport(ModbusSerialTransport transport) {
      this.transport = transport;
   }

   public int getTransDelayMS() {
      return this.transDelayMS;
   }

   public void setTransDelayMS(int newTransDelayMS) {
      this.transDelayMS = newTransDelayMS;
   }

   private void assertExecutable() throws ModbusException {
      if (this.request == null || this.transport == null) {
         throw new ModbusException("Assertion failed, transaction not executable");
      }
   }

   @Override
   public void execute() throws ModbusException {
      this.assertExecutable();
      int tries = 0;
      boolean finished = false;

      do {
         try {
            ((ModbusSerialTransport)this.transport).waitBetweenFrames(this.transDelayMS, this.lastTransactionTimestamp);
            synchronized (this) {
               this.transport.writeRequest(this.request);
               this.response = this.transport.readResponse();
               finished = true;
            }
         } catch (ModbusIOException var6) {
            if (++tries >= this.retries) {
               throw var6;
            }

            ModbusUtil.sleep(this.getRandomSleepTime(tries));
            logger.debug("Execute try {} error: {}", tries, var6.getMessage());
         }
      } while (!finished);

      if (this.response instanceof ExceptionResponse) {
         throw new ModbusSlaveException(((ExceptionResponse)this.response).getExceptionCode());
      } else {
         if (this.isCheckingValidity()) {
            this.checkValidity();
         }

         this.lastTransactionTimestamp = System.nanoTime();
      }
   }
}
