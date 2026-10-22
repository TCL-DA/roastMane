package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ModbusSerialTransport extends AbstractModbusTransport {
   private static final Logger logger = LoggerFactory.getLogger(ModbusSerialTransport.class);
   static final int FRAME_START = 1000;
   static final int FRAME_END = 2000;
   private static final int NS_IN_A_MS = 1000000;
   private static final String CANNOT_READ_FROM_SERIAL_PORT = "Cannot read from serial port";
   private static final String COMM_PORT_IS_NOT_VALID_OR_NOT_OPEN = "Comm port is not valid or not open";
   private AbstractSerialConnection commPort;
   boolean echo = false;
   private final Set<AbstractSerialTransportListener> listeners = Collections.synchronizedSet(new HashSet<>());

   @Override
   public ModbusTransaction createTransaction() {
      ModbusSerialTransaction transaction = new ModbusSerialTransaction();
      transaction.setTransport(this);
      return transaction;
   }

   @Override
   public void writeResponse(ModbusResponse msg) throws ModbusIOException {
      if (msg.getAuxiliaryType().equals(ModbusResponse.AuxiliaryMessageTypes.UNIT_ID_MISSMATCH)) {
         logger.debug("Ignoring response not meant for us");
      } else {
         this.waitBetweenFrames();
         this.writeMessage(msg);
      }
   }

   @Override
   public void writeRequest(ModbusRequest msg) throws ModbusIOException {
      this.writeMessage(msg);
   }

   private void writeMessage(ModbusMessage msg) throws ModbusIOException {
      this.open();
      this.notifyListenersBeforeWrite(msg);

      try {
         this.writeMessageOut(msg);
         long startTime = System.nanoTime();
         double bytesPerSec = (double)this.commPort.getBaudRate()
            / (double)(
               (this.commPort.getNumDataBits() == 0 ? 8 : this.commPort.getNumDataBits())
                  + (this.commPort.getNumStopBits() == 0 ? 1 : this.commPort.getNumStopBits())
                  + (this.commPort.getParity() == 0 ? 0 : 1)
            );
         double delay = 1.0E9 * (double)msg.getOutputLength() / bytesPerSec;
         double delayMilliSeconds = Math.floor(delay / 1000000.0);
         double delayNanoSeconds = delay % 1000000.0;

         try {
            if (delayMilliSeconds == 0.0) {
               int priority = Thread.currentThread().getPriority();
               Thread.currentThread().setPriority(1);
               long end = startTime + (long)((int)(delayNanoSeconds * 1.3));

               while (System.nanoTime() < end) {
               }

               Thread.currentThread().setPriority(priority);
            } else {
               Thread.sleep((long)((int)(delayMilliSeconds * 1.7)), (int)(delayNanoSeconds * 1.5));
            }
         } catch (Exception var18) {
            logger.debug("nothing to do");
         }
      } finally {
         this.notifyListenersAfterWrite(msg);
      }
   }

   @Override
   public ModbusRequest readRequest(AbstractModbusListener listener) throws ModbusIOException {
      this.open();
      this.notifyListenersBeforeRequest();
      ModbusRequest req = this.readRequestIn(listener);
      this.notifyListenersAfterRequest(req);
      return req;
   }

   @Override
   public ModbusResponse readResponse() throws ModbusIOException {
      this.notifyListenersBeforeResponse();
      ModbusResponse res = this.readResponseIn();
      this.notifyListenersAfterResponse(res);
      return res;
   }

   private void open() throws ModbusIOException {
      if (this.commPort != null && !this.commPort.isOpen()) {
         this.setTimeout(this.timeout);

         try {
            this.commPort.open();
         } catch (IOException var2) {
            throw new ModbusIOException(String.format("Cannot open port %s - %s", this.commPort.getDescriptivePortName(), var2.getMessage()));
         }
      }
   }

   @Override
   public void setTimeout(int time) {
      super.setTimeout(time);
      if (this.commPort != null) {
         this.commPort.setComPortTimeouts(16, this.timeout, this.timeout);
      }
   }

   protected abstract void writeMessageOut(ModbusMessage var1) throws ModbusIOException;

   protected abstract ModbusRequest readRequestIn(AbstractModbusListener var1) throws ModbusIOException;

   protected abstract ModbusResponse readResponseIn() throws ModbusIOException;

   public void addListener(AbstractSerialTransportListener listener) {
      if (listener != null) {
         this.listeners.add(listener);
      }
   }

   public void removeListener(AbstractSerialTransportListener listener) {
      if (listener != null) {
         this.listeners.remove(listener);
      }
   }

   public void clearListeners() {
      this.listeners.clear();
   }

   private void notifyListenersBeforeRequest() {
      synchronized (this.listeners) {
         for (AbstractSerialTransportListener listener : this.listeners) {
            listener.beforeRequestRead(this.commPort);
         }
      }
   }

   private void notifyListenersAfterRequest(ModbusRequest req) {
      synchronized (this.listeners) {
         for (AbstractSerialTransportListener listener : this.listeners) {
            listener.afterRequestRead(this.commPort, req);
         }
      }
   }

   private void notifyListenersBeforeResponse() {
      synchronized (this.listeners) {
         for (AbstractSerialTransportListener listener : this.listeners) {
            listener.beforeResponseRead(this.commPort);
         }
      }
   }

   private void notifyListenersAfterResponse(ModbusResponse res) {
      synchronized (this.listeners) {
         for (AbstractSerialTransportListener listener : this.listeners) {
            listener.afterResponseRead(this.commPort, res);
         }
      }
   }

   private void notifyListenersBeforeWrite(ModbusMessage msg) {
      synchronized (this.listeners) {
         for (AbstractSerialTransportListener listener : this.listeners) {
            listener.beforeMessageWrite(this.commPort, msg);
         }
      }
   }

   private void notifyListenersAfterWrite(ModbusMessage msg) {
      synchronized (this.listeners) {
         for (AbstractSerialTransportListener listener : this.listeners) {
            listener.afterMessageWrite(this.commPort, msg);
         }
      }
   }

   public void setCommPort(AbstractSerialConnection cp) throws IOException {
      this.commPort = cp;
      this.setTimeout(this.timeout);
   }

   public AbstractSerialConnection getCommPort() {
      return this.commPort;
   }

   public boolean isEcho() {
      return this.echo;
   }

   public void setEcho(boolean b) {
      this.echo = b;
   }

   protected void readEcho(int len) throws IOException {
      byte[] echoBuf = new byte[len];
      int echoLen = this.commPort.readBytes(echoBuf, (long)len);
      if (logger.isDebugEnabled()) {
         logger.debug("Echo: {}", ModbusUtil.toHex(echoBuf, 0, echoLen));
      }

      if (echoLen != len) {
         logger.debug("Error: Transmit echo not received");
         throw new IOException("Echo not received");
      }
   }

   protected int availableBytes() {
      return this.commPort.bytesAvailable();
   }

   protected int readByte() throws IOException {
      if (this.commPort != null && this.commPort.isOpen()) {
         byte[] buffer = new byte[1];
         int cnt = this.commPort.readBytes(buffer, 1L);
         if (cnt != 1) {
            throw new IOException("Cannot read from serial port");
         } else {
            return buffer[0] & 0xFF;
         }
      } else {
         throw new IOException("Comm port is not valid or not open");
      }
   }

   void readBytes(byte[] buffer, long bytesToRead) throws IOException {
      if (this.commPort != null && this.commPort.isOpen()) {
         int cnt = this.commPort.readBytes(buffer, bytesToRead);
         if ((long)cnt != bytesToRead) {
            throw new IOException("Cannot read from serial port - truncated");
         }
      } else {
         throw new IOException("Comm port is not valid or not open");
      }
   }

   final int writeBytes(byte[] buffer, long bytesToWrite) throws IOException {
      if (this.commPort != null && this.commPort.isOpen()) {
         return this.commPort.writeBytes(buffer, bytesToWrite);
      } else {
         throw new IOException("Comm port is not valid or not open");
      }
   }

   int readAsciiByte() throws IOException {
      if (this.commPort != null && this.commPort.isOpen()) {
         byte[] buffer = new byte[1];
         int cnt = this.commPort.readBytes(buffer, 1L);
         if (cnt != 1) {
            throw new IOException("Cannot read from serial port");
         } else if (buffer[0] == 58) {
            return 1000;
         } else if (buffer[0] != 13 && buffer[0] != 10) {
            if (logger.isDebugEnabled()) {
               logger.debug("Read From buffer: {} ({})", buffer[0], String.format("%02X", buffer[0]));
            }

            byte firstValue = buffer[0];
            cnt = this.commPort.readBytes(buffer, 1L);
            if (cnt != 1) {
               throw new IOException("Cannot read from serial port");
            } else {
               int combinedValue = (Character.digit(firstValue, 16) << 4) + Character.digit(buffer[0], 16);
               if (logger.isDebugEnabled()) {
                  logger.debug("Returning combined value of: {}", String.format("%02X", combinedValue));
               }

               return combinedValue;
            }
         } else {
            return 2000;
         }
      } else {
         throw new IOException("Comm port is not valid or not open");
      }
   }

   final int writeAsciiByte(int value) throws IOException {
      if (this.commPort != null && this.commPort.isOpen()) {
         byte[] buffer;
         if (value == 1000) {
            buffer = new byte[]{58};
            logger.debug("Wrote FRAME_START");
         } else if (value == 2000) {
            buffer = new byte[]{13, 10};
            logger.debug("Wrote FRAME_END");
         } else {
            buffer = ModbusUtil.toHex(value);
            if (logger.isDebugEnabled()) {
               logger.debug("Wrote byte {}={}", value, ModbusUtil.toHex(value));
            }
         }

         if (buffer != null) {
            return this.commPort.writeBytes(buffer, (long)buffer.length);
         } else {
            throw new IOException("Message to send is empty");
         }
      } else {
         throw new IOException("Comm port is not valid or not open");
      }
   }

   int writeAsciiBytes(byte[] buffer, long bytesToWrite) throws IOException {
      if (this.commPort != null && this.commPort.isOpen()) {
         int cnt = 0;

         for (int i = 0; (long)i < bytesToWrite; i++) {
            if (this.writeAsciiByte(buffer[i]) != 2) {
               return cnt;
            }

            cnt++;
         }

         return cnt;
      } else {
         throw new IOException("Comm port is not valid or not open");
      }
   }

   void clearInput() throws IOException {
      if (this.commPort.bytesAvailable() > 0) {
         int len = this.commPort.bytesAvailable();
         byte[] buf = new byte[len];
         this.readBytes(buf, (long)len);
         if (logger.isDebugEnabled()) {
            logger.debug("Clear input: {}", ModbusUtil.toHex(buf, 0, len));
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.commPort.close();
   }

   private void waitBetweenFrames() {
      this.waitBetweenFrames(0, 0L);
   }

   void waitBetweenFrames(int transDelayMS, long lastTransactionTimestamp) {
      if (transDelayMS > 0) {
         ModbusUtil.sleep((long)transDelayMS);
      } else {
         int delay = this.getInterFrameDelay() / 1000;
         long gapSinceLastMessage = (System.nanoTime() - lastTransactionTimestamp) / 1000000L;
         if ((long)delay > gapSinceLastMessage) {
            long sleepTime = (long)delay - gapSinceLastMessage;
            ModbusUtil.sleep(sleepTime);
            if (logger.isDebugEnabled()) {
               logger.debug("Waited between frames for {} ms", sleepTime);
            }
         }
      }
   }

   int getInterFrameDelay() {
      return this.commPort.getBaudRate() > 19200 ? 1750 : Math.max(this.getCharInterval(4.0), 2);
   }

   long getMaxCharDelay() {
      return this.commPort.getBaudRate() > 19200 ? 1750L : this.getCharIntervalMicro(1.5);
   }

   int getCharInterval(double chars) {
      return (int)(this.getCharIntervalMicro(chars) / 1000L);
   }

   long getCharIntervalMicro(double chars) {
      return (long)chars
         * 1000000L
         * (long)(1 + this.commPort.getNumDataBits() + this.commPort.getNumStopBits() + (this.commPort.getParity() == 0 ? 0 : 1))
         / (long)this.commPort.getBaudRate();
   }

   boolean spinUntilBytesAvailable(long waitTimeMicroSec) {
      long start = System.nanoTime();

      while (this.availableBytes() < 1) {
         long delta = System.nanoTime() - start;
         if (delta > waitTimeMicroSec * 1000L) {
            return false;
         }
      }

      return true;
   }
}
