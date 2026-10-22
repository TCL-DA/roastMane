package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.msg.ExceptionResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.AbstractUDPTerminal;
import com.ghgande.j2mod.modbus.net.UDPMasterConnection;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusUDPTransaction extends ModbusTransaction {
   private static final Logger logger = LoggerFactory.getLogger(ModbusUDPTransaction.class);
   private AbstractUDPTerminal terminal;

   public ModbusUDPTransaction() {
   }

   public ModbusUDPTransaction(ModbusRequest request) {
      this.setRequest(request);
   }

   public ModbusUDPTransaction(AbstractUDPTerminal terminal) {
      this.setTerminal(terminal);
   }

   public ModbusUDPTransaction(UDPMasterConnection con) {
      this.setTerminal(con.getTerminal());
   }

   public void setTerminal(AbstractUDPTerminal terminal) {
      this.terminal = terminal;
      if (terminal.isActive()) {
         this.transport = terminal.getTransport();
      }
   }

   @Override
   public void execute() throws ModbusIOException, ModbusSlaveException, ModbusException {
      this.assertExecutable();
      if (!this.terminal.isActive()) {
         try {
            this.terminal.activate();
            this.transport = this.terminal.getTransport();
         } catch (Exception var5) {
            logger.debug("Terminal activation failed.", var5);
            throw new ModbusIOException("Activation failed");
         }
      }

      int retryCount = 0;

      while (retryCount <= this.retries) {
         try {
            synchronized (this) {
               this.transport.writeRequest(this.request);
               this.response = this.transport.readResponse();
               break;
            }
         } catch (ModbusIOException var6) {
            if (++retryCount > this.retries) {
               logger.error("Cannot send UDP message", var6);
            } else {
               ModbusUtil.sleep(this.getRandomSleepTime(retryCount));
            }
         }
      }

      if (this.response instanceof ExceptionResponse) {
         throw new ModbusSlaveException(((ExceptionResponse)this.response).getExceptionCode());
      } else {
         if (this.isCheckingValidity()) {
            this.checkValidity();
         }

         this.incrementTransactionID();
      }
   }

   private void assertExecutable() throws ModbusException {
      if (this.request == null || this.terminal == null) {
         throw new ModbusException("Assertion failed, transaction not executable");
      }
   }

   private void incrementTransactionID() {
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
