package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.io.ModbusUDPTransport;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusUDPListener extends AbstractModbusListener {
   private static final Logger logger = LoggerFactory.getLogger(ModbusUDPListener.class);
   private UDPSlaveTerminal terminal;

   public ModbusUDPListener(InetAddress ifc) {
      this.address = ifc;
      this.listening = true;
   }

   public ModbusUDPListener() {
      try {
         this.address = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
      } catch (UnknownHostException var2) {
      }
   }

   @Override
   public void setTimeout(int timeout) {
      super.setTimeout(timeout);
      if (this.terminal != null && this.listening) {
         this.terminal.setTimeout(timeout);
      }
   }

   @Override
   public void run() {
      if (this.threadName == null || this.threadName.isEmpty()) {
         this.threadName = String.format("Modbus UDP Listener [port:%d]", this.port);
      }

      Thread.currentThread().setName(this.threadName);

      ModbusUDPTransport transport;
      try {
         if (this.address == null) {
            this.terminal = new UDPSlaveTerminal(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}));
         } else {
            this.terminal = new UDPSlaveTerminal(this.address);
         }

         this.terminal.setTimeout(this.timeout);
         this.terminal.setPort(this.port);
         this.terminal.activate();
         transport = new ModbusUDPTransport(this.terminal);
      } catch (Exception var12) {
         this.error = String.format("Cannot start UDP listener on port %d - %s", this.port, var12.getMessage());
         this.listening = false;
         return;
      }

      this.listening = true;

      try {
         while (this.listening) {
            this.handleRequest(transport, this);
         }
      } catch (ModbusIOException var13) {
         if (!var13.isEOF()) {
            logger.error("Exception occurred before EOF while handling request", var13);
         }
      } finally {
         try {
            this.terminal.deactivate();
            transport.close();
         } catch (Exception var11) {
         }
      }
   }

   @Override
   public void stop() {
      this.terminal.deactivate();
      this.listening = false;
   }
}
