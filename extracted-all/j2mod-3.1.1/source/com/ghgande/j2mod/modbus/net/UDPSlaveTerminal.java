package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.io.ModbusUDPTransport;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UDPSlaveTerminal extends AbstractUDPTerminal {
   private static final Logger logger = LoggerFactory.getLogger(UDPSlaveTerminal.class);
   protected Hashtable<Integer, DatagramPacket> requests = new Hashtable<>(342);
   private final LinkedBlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<>();
   private final LinkedBlockingQueue<byte[]> receiveQueue = new LinkedBlockingQueue<>();
   private UDPSlaveTerminal.PacketSender packetSender;
   private UDPSlaveTerminal.PacketReceiver packetReceiver;

   protected UDPSlaveTerminal(InetAddress localaddress) {
      this.address = localaddress;
   }

   @Override
   public synchronized void activate() throws Exception {
      if (!this.isActive()) {
         logger.debug("UDPSlaveTerminal.activate()");
         if (this.address != null && this.port != -1) {
            this.socket = new DatagramSocket(this.port, this.address);
         } else {
            this.socket = new DatagramSocket();
            this.port = this.socket.getLocalPort();
            this.address = this.socket.getLocalAddress();
         }

         logger.debug("UDPSlaveTerminal::haveSocket():{}", this.socket);
         logger.debug("UDPSlaveTerminal::addr=:{}:port={}", this.address, this.port);
         this.socket.setReceiveBufferSize(1024);
         this.socket.setSendBufferSize(1024);
         this.socket.setSoTimeout(0);
         this.packetSender = new UDPSlaveTerminal.PacketSender(this.socket);
         new Thread(this.packetSender).start();
         logger.debug("UDPSlaveTerminal::sender started()");
         this.packetReceiver = new UDPSlaveTerminal.PacketReceiver(this.socket);
         new Thread(this.packetReceiver).start();
         logger.debug("UDPSlaveTerminal::receiver started()");
         this.transport = new ModbusUDPTransport(this);
         logger.debug("UDPSlaveTerminal::transport created");
         this.active = true;
      }

      logger.debug("UDPSlaveTerminal::activated");
   }

   @Override
   public synchronized void deactivate() {
      try {
         if (this.active) {
            this.packetReceiver.stop();
            this.packetSender.stop();
            this.transport = null;
            this.active = false;
         }
      } catch (Exception var2) {
         logger.error("Error deactivating UDPSlaveTerminal", var2);
      }
   }

   @Override
   public void sendMessage(byte[] msg) throws Exception {
      this.sendQueue.add(msg);
   }

   @Override
   public byte[] receiveMessage() throws Exception {
      return this.receiveQueue.take();
   }

   class PacketReceiver implements Runnable {
      private boolean running = true;
      private boolean closed;
      private final DatagramSocket socket;

      public PacketReceiver(DatagramSocket socket) {
         this.socket = socket;
      }

      public void stop() {
         this.running = false;
         this.socket.close();

         while (!this.closed) {
            ModbusUtil.sleep(100L);
         }
      }

      @Override
      public void run() {
         this.closed = false;

         do {
            try {
               byte[] buffer = new byte[256];
               DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
               this.socket.receive(packet);
               Integer tid = ModbusUtil.registersToInt(buffer);
               UDPSlaveTerminal.this.requests.put(tid, packet);
               UDPSlaveTerminal.this.receiveQueue.put(buffer);
               UDPSlaveTerminal.logger.debug("Received package to queue");
            } catch (Exception var4) {
               if (this.running) {
                  UDPSlaveTerminal.logger.error("Problem reading UDP socket", var4);
               }
            }
         } while (this.running);

         this.closed = true;
      }
   }

   class PacketSender implements Runnable {
      private boolean running = true;
      private boolean closed;
      private Thread thread;
      private final DatagramSocket socket;

      public PacketSender(DatagramSocket socket) {
         this.socket = socket;
      }

      public void stop() {
         this.running = false;
         this.thread.interrupt();

         while (!this.closed) {
            ModbusUtil.sleep(100L);
         }
      }

      @Override
      public void run() {
         this.closed = false;
         this.thread = Thread.currentThread();

         do {
            try {
               byte[] message = UDPSlaveTerminal.this.sendQueue.take();
               DatagramPacket req = UDPSlaveTerminal.this.requests.remove(ModbusUtil.registersToInt(message));
               if (req != null) {
                  DatagramPacket res = new DatagramPacket(message, message.length, req.getAddress(), req.getPort());
                  this.socket.send(res);
                  UDPSlaveTerminal.logger.debug("Sent package from queue");
               }
            } catch (Exception var4) {
               if (this.running) {
                  UDPSlaveTerminal.logger.error("Problem reading UDP socket", var4);
               }
            }
         } while (this.running);

         this.closed = true;
      }
   }
}
