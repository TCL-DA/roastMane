package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.io.ModbusUDPTransport;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UDPMasterTerminal extends AbstractUDPTerminal {
   private static final Logger logger = LoggerFactory.getLogger(UDPMasterTerminal.class);

   UDPMasterTerminal(InetAddress addr) {
      this.address = addr;
   }

   public UDPMasterTerminal() {
   }

   @Override
   public synchronized void activate() throws Exception {
      if (!this.isActive()) {
         if (this.socket == null) {
            this.socket = new DatagramSocket();
         }

         logger.debug("UDPMasterTerminal::haveSocket():{}", this.socket);
         logger.debug("UDPMasterTerminal::raddr=:{}:rport:{}", this.address, this.port);
         this.socket.setReceiveBufferSize(1024);
         this.socket.setSendBufferSize(1024);
         this.socket.setSoTimeout(this.timeout);
         this.transport = new ModbusUDPTransport(this);
         this.active = true;
      }

      logger.debug("UDPMasterTerminal::activated");
   }

   @Override
   public synchronized void deactivate() {
      try {
         logger.debug("UDPMasterTerminal::deactivate()");
         if (this.socket != null) {
            this.socket.close();
         }

         this.transport = null;
         this.active = false;
      } catch (Exception var2) {
         logger.error("Error closing socket", var2);
      }
   }

   @Override
   public void sendMessage(byte[] msg) throws Exception {
      DatagramPacket req = new DatagramPacket(msg, msg.length, this.address, this.port);
      this.socket.send(req);
   }

   @Override
   public byte[] receiveMessage() throws Exception {
      byte[] buffer = new byte[262];
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      this.socket.setSoTimeout(this.timeout);
      this.socket.receive(packet);
      return buffer;
   }
}
