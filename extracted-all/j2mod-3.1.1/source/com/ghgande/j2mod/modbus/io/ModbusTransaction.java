package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import java.util.Random;

public abstract class ModbusTransaction {
   protected AbstractModbusTransport transport;
   protected ModbusRequest request;
   protected ModbusResponse response;
   boolean validityCheck = true;
   int retries = 5;
   private final Random random = new Random(System.nanoTime());
   static int transactionID = 0;

   public ModbusRequest getRequest() {
      return this.request;
   }

   public void setRequest(ModbusRequest req) {
      this.request = req;
      if (req != null) {
         this.request.setTransactionID(this.getTransactionID());
      }
   }

   public ModbusResponse getResponse() {
      return this.response;
   }

   int getRetries() {
      return this.retries;
   }

   public void setRetries(int retries) {
      this.retries = retries;
   }

   public boolean isCheckingValidity() {
      return this.validityCheck;
   }

   public void setCheckingValidity(boolean b) {
      this.validityCheck = b;
   }

   public synchronized int getTransactionID() {
      if (transactionID < 0 && this.isCheckingValidity()) {
         transactionID = 0;
      }

      if (transactionID >= 32767) {
         transactionID = 0;
      }

      return transactionID;
   }

   long getRandomSleepTime(int count) {
      return 250L + (long)(this.random.nextDouble() * 500.0 * (double)count);
   }

   void checkValidity() throws ModbusException {
      if (this.request != null && this.response != null) {
         if (this.request.getUnitID() != this.response.getUnitID()) {
            throw new ModbusIOException("Unit ID mismatch - Request [%s] Response [%s]", this.request.getHexMessage(), this.response.getHexMessage());
         }

         if (this.request.getFunctionCode() != this.response.getFunctionCode()) {
            throw new ModbusIOException("Function code mismatch - Request [%s] Response [%s]", this.request.getHexMessage(), this.response.getHexMessage());
         }
      }
   }

   public abstract void execute() throws ModbusException;
}
