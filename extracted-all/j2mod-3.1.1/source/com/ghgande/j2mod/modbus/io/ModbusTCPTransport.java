package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusTCPTransport extends AbstractModbusTransport {
   private static final Logger logger = LoggerFactory.getLogger(ModbusTCPTransport.class);
   private DataInputStream dataInputStream;
   private DataOutputStream dataOutputStream;
   private final BytesInputStream byteInputStream = new BytesInputStream(262);
   private final BytesOutputStream byteOutputStream = new BytesOutputStream(262);
   protected Socket socket = null;
   protected TCPMasterConnection master = null;
   private boolean headless = false;
   private long lastActivityTimestamp = System.nanoTime();

   public ModbusTCPTransport() {
   }

   public ModbusTCPTransport(Socket socket) {
      try {
         this.setSocket(socket);
         socket.setSoTimeout(this.timeout);
      } catch (IOException var3) {
         logger.debug("ModbusTCPTransport::Socket invalid");
         throw new IllegalStateException("Socket invalid", var3);
      }
   }

   public void setSocket(Socket socket) throws IOException {
      if (this.socket != null) {
         this.socket.close();
         this.socket = null;
      }

      this.socket = socket;
      this.setTimeout(this.timeout);
      this.prepareStreams(socket);
   }

   public void setHeadless() {
      this.headless = true;
   }

   public void setHeadless(boolean headless) {
      this.headless = headless;
   }

   public void setMaster(TCPMasterConnection master) {
      this.master = master;
   }

   @Override
   public void setTimeout(int time) {
      super.setTimeout(time);
      if (this.socket != null) {
         try {
            this.socket.setSoTimeout(time);
         } catch (SocketException var3) {
            logger.warn("Socket exception occurred while setting timeout to {}", time, var3);
         }
      }
   }

   public long getLastActivityTimestamp() {
      return this.lastActivityTimestamp;
   }

   @Override
   public void close() throws IOException {
      this.dataInputStream.close();
      this.dataOutputStream.close();
      this.socket.close();
   }

   @Override
   public ModbusTransaction createTransaction() {
      if (this.master == null) {
         this.master = new TCPMasterConnection(this.socket.getInetAddress());
         this.master.setPort(this.socket.getPort());
         this.master.setModbusTransport(this);
      }

      return new ModbusTCPTransaction(this.master);
   }

   @Override
   public void writeResponse(ModbusResponse msg) throws ModbusIOException {
      this.writeMessage(msg, false);
   }

   @Override
   public void writeRequest(ModbusRequest msg) throws ModbusIOException {
      this.writeMessage(msg, false);
   }

   @Override
   public ModbusRequest readRequest(AbstractModbusListener listener) throws ModbusIOException {
      this.lastActivityTimestamp = System.nanoTime();

      try {
         this.byteInputStream.reset();
         ModbusRequest req;
         synchronized (this.byteInputStream) {
            byte[] buffer = this.byteInputStream.getBuffer();
            if (!this.headless) {
               this.dataInputStream.readFully(buffer, 0, 6);
               int transaction = ModbusUtil.registerToShort(buffer, 0) & '\uffff';
               int protocol = ModbusUtil.registerToShort(buffer, 2);
               int count = ModbusUtil.registerToShort(buffer, 4);
               this.dataInputStream.readFully(buffer, 6, count);
               if (logger.isDebugEnabled()) {
                  logger.debug("Read: {}", ModbusUtil.toHex(buffer, 0, count + 6));
               }

               this.byteInputStream.reset(buffer, 6 + count);
               this.byteInputStream.skip(6);
               int unit = this.byteInputStream.readByte();
               int functionCode = this.byteInputStream.readUnsignedByte();
               this.byteInputStream.reset();
               req = ModbusRequest.createModbusRequest(functionCode);
               req.setUnitID(unit);
               req.setHeadless(false);
               req.setTransactionID(transaction);
               req.setProtocolID(protocol);
               req.setDataLength(count);
               req.readFrom(this.byteInputStream);
            } else {
               int unit = this.dataInputStream.readByte();
               int function = this.dataInputStream.readByte();
               req = ModbusRequest.createModbusRequest(function);
               req.setUnitID(unit);
               req.setHeadless(true);
               req.readData(this.dataInputStream);
               this.dataInputStream.readShort();
               if (logger.isDebugEnabled()) {
                  logger.debug("Read: {}", req.getHexMessage());
               }
            }
         }

         return req;
      } catch (EOFException var12) {
         throw new ModbusIOException("End of File", true);
      } catch (SocketTimeoutException var13) {
         throw new ModbusIOException("Timeout reading request", var13);
      } catch (SocketException var14) {
         throw new ModbusIOException("Socket Exception", var14);
      } catch (IOException var15) {
         throw new ModbusIOException("I/O exception - failed to read", var15);
      }
   }

   @Override
   public ModbusResponse readResponse() throws ModbusIOException {
      this.lastActivityTimestamp = System.nanoTime();

      try {
         ModbusResponse response;
         synchronized (this.byteInputStream) {
            byte[] buffer = this.byteInputStream.getBuffer();
            logger.debug("Reading response...");
            if (!this.headless) {
               this.dataInputStream.readFully(buffer, 0, 6);
               int transaction = ModbusUtil.registerToShort(buffer, 0) & '\uffff';
               int protocol = ModbusUtil.registerToShort(buffer, 2);
               int count = ModbusUtil.registerToShort(buffer, 4);
               this.dataInputStream.readFully(buffer, 6, count);
               this.byteInputStream.reset(buffer, 6 + count);
               this.byteInputStream.reset();
               this.byteInputStream.skip(7);
               int function = this.byteInputStream.readUnsignedByte();
               response = ModbusResponse.createModbusResponse(function);
               this.byteInputStream.reset();
               response.readFrom(this.byteInputStream);
               response.setTransactionID(transaction);
               response.setProtocolID(protocol);
            } else {
               int unit = this.dataInputStream.readByte();
               int function = this.dataInputStream.readByte();
               response = ModbusResponse.createModbusResponse(function);
               response.setUnitID(unit);
               response.setHeadless();
               response.readData(this.dataInputStream);
               this.dataInputStream.readShort();
            }
         }

         if (logger.isDebugEnabled()) {
            logger.debug("Successfully read: {}", response.getHexMessage());
         }

         return response;
      } catch (EOFException var10) {
         throw new ModbusIOException("Premature end of stream (Message truncated) - %s", var10.getMessage());
      } catch (SocketTimeoutException var11) {
         throw new ModbusIOException("Socket timeout reading response - %s", var11.getMessage());
      } catch (Exception var12) {
         throw new ModbusIOException("General exception - failed to read - %s", var12.getMessage());
      }
   }

   private void prepareStreams(Socket socket) throws IOException {
      try {
         if (this.dataInputStream != null) {
            this.dataInputStream.close();
         }

         if (this.dataOutputStream != null) {
            this.dataOutputStream.close();
         }
      } catch (IOException var3) {
      }

      this.dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
   }

   void writeMessage(ModbusMessage msg, boolean useRtuOverTcp) throws ModbusIOException {
      this.lastActivityTimestamp = System.nanoTime();

      try {
         if (logger.isDebugEnabled()) {
            logger.debug("Sending: {}", msg.getHexMessage());
         }

         byte[] message = msg.getMessage();
         this.byteOutputStream.reset();
         if (!this.headless) {
            this.byteOutputStream.writeShort(msg.getTransactionID());
            this.byteOutputStream.writeShort(msg.getProtocolID());
            this.byteOutputStream.writeShort((message != null ? message.length : 0) + 2);
         }

         this.byteOutputStream.writeByte(msg.getUnitID());
         this.byteOutputStream.writeByte(msg.getFunctionCode());
         if (message != null && message.length > 0) {
            this.byteOutputStream.write(message);
         }

         if (useRtuOverTcp) {
            int len = this.byteOutputStream.size();
            int[] crc = ModbusUtil.calculateCRC(this.byteOutputStream.getBuffer(), 0, len);
            this.byteOutputStream.writeByte(crc[0]);
            this.byteOutputStream.writeByte(crc[1]);
         }

         this.dataOutputStream.write(this.byteOutputStream.toByteArray());
         this.dataOutputStream.flush();
         if (logger.isDebugEnabled()) {
            logger.debug("Successfully sent: {}", ModbusUtil.toHex(this.byteOutputStream.toByteArray()));
         }
      } catch (SocketException var7) {
         if (this.master != null && !this.master.isConnected()) {
            try {
               this.master.connect(useRtuOverTcp);
            } catch (Exception var6) {
            }
         }

         throw new ModbusIOException("I/O socket exception - failed to write - %s", var7.getMessage());
      } catch (Exception var8) {
         throw new ModbusIOException("General exception - failed to write - %s", var8.getMessage());
      }
   }
}
