package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.msg.ExceptionResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusTCPTransaction extends ModbusTransaction {
   private static final Logger logger = LoggerFactory.getLogger(ModbusTCPTransaction.class);
   private TCPMasterConnection connection;
   protected boolean reconnecting = false;

   public ModbusTCPTransaction() {
   }

   public ModbusTCPTransaction(ModbusRequest request) {
      this.setRequest(request);
   }

   public ModbusTCPTransaction(TCPMasterConnection con) {
      this.setConnection(con);
      this.transport = con.getModbusTransport();
   }

   public void setConnection(TCPMasterConnection con) {
      this.connection = con;
      this.transport = con.getModbusTransport();
   }

   public boolean isReconnecting() {
      return this.reconnecting;
   }

   public void setReconnecting(boolean b) {
      this.reconnecting = b;
   }

   @Override
   public synchronized void execute() throws ModbusException {
      if (this.request != null && this.connection != null) {
         int retryCounter = 0;
         int retryLimit = this.retries > 0 ? this.retries : 5;
         boolean keepTrying = true;

         while (keepTrying) {
            if (!this.connection.isConnected()) {
               try {
                  logger.debug("Connecting to: {}:{}", this.connection.getAddress(), this.connection.getPort());
                  this.connection.connect();
                  this.transport = this.connection.getModbusTransport();
               } catch (Exception var7) {
                  throw new ModbusIOException(
                     "Connection failed for %s:%d %s", this.connection.getAddress().toString(), this.connection.getPort(), var7.getMessage()
                  );
               }
            }

            this.transport.setTimeout(this.connection.getTimeout());

            try {
               if (logger.isDebugEnabled()) {
                  logger.debug(
                     "Writing request: {} (try: {}) request transaction ID = {} to {}:{}",
                     new Object[]{
                        this.request.getHexMessage(), retryCounter, this.request.getTransactionID(), this.connection.getAddress(), this.connection.getPort()
                     }
                  );
               }

               this.transport.writeRequest(this.request);
               this.response = this.transport.readResponse();
               if (logger.isDebugEnabled()) {
                  logger.debug(
                     "Read response: {} (try: {}) response transaction ID = {} from {}:{}",
                     new Object[]{
                        this.response.getHexMessage(), retryCounter, this.response.getTransactionID(), this.connection.getAddress(), this.connection.getPort()
                     }
                  );
               }

               keepTrying = false;
               if (this.response instanceof ExceptionResponse) {
                  throw new ModbusSlaveException(((ExceptionResponse)this.response).getExceptionCode());
               }

               if (this.responseIsInValid()) {
                  if (++retryCounter >= retryLimit) {
                     throw new ModbusIOException("Executing transaction failed (tried %d times)", retryLimit);
                  }

                  keepTrying = true;
                  long sleepTime = this.getRandomSleepTime(retryCounter);
                  if (this.response == null) {
                     logger.debug("Failed to get any response (try: {}) - retrying after {} milliseconds", retryCounter, sleepTime);
                  } else {
                     logger.debug(
                        "Failed to get a valid response, transaction IDs do not match (try: {}) - retrying after {} milliseconds", retryCounter, sleepTime
                     );
                  }

                  ModbusUtil.sleep(sleepTime);
               }
            } catch (ModbusIOException var8) {
               if (++retryCounter >= retryLimit) {
                  throw new ModbusIOException(
                     "Executing transaction %s failed (tried %d times) %s", this.request.getHexMessage(), retryLimit, var8.getMessage()
                  );
               }

               long sleepTime = this.getRandomSleepTime(retryCounter);
               logger.debug(
                  "Failed transaction Request: {} (try: {}) - retrying after {} milliseconds",
                  new Object[]{this.request.getHexMessage(), retryCounter, sleepTime}
               );
               ModbusUtil.sleep(sleepTime);
               logger.debug(
                  "Failed request {} (try: {}) request transaction ID = {} - {} closing and re-opening connection {}:{}",
                  new Object[]{
                     this.request.getHexMessage(),
                     retryCounter,
                     this.request.getTransactionID(),
                     var8.getMessage(),
                     this.connection.getAddress().toString(),
                     this.connection.getPort()
                  }
               );
               this.connection.close();
            }

            if (keepTrying) {
               this.incrementTransactionID();
            }
         }

         if (this.isReconnecting()) {
            this.connection.close();
         }

         this.incrementTransactionID();
      } else {
         throw new ModbusException("Invalid request or connection");
      }
   }

   private boolean responseIsInValid() {
      if (this.response == null) {
         return true;
      } else {
         return !this.response.isHeadless() && this.validityCheck ? this.request.getTransactionID() != this.response.getTransactionID() : false;
      }
   }

   private synchronized void incrementTransactionID() {
      if (this.isCheckingValidity()) {
         if (transactionID >= 32767) {
            transactionID = 0;
         } else {
            transactionID++;
         }
      }

      this.request.setTransactionID(this.getTransactionID());
   }
}
