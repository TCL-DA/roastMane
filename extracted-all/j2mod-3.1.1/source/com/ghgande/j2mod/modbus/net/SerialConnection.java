package com.ghgande.j2mod.modbus.net;

import com.fazecast.jSerialComm.SerialPort;
import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusASCIITransport;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialConnection extends AbstractSerialConnection {
   private static final Logger logger = LoggerFactory.getLogger(SerialConnection.class);
   public static final int CONNECT_RETRY_DELAY = 100;
   public static final int CONNECT_RETRIES = 3;
   private SerialParameters parameters;
   private ModbusSerialTransport transport;
   private SerialPort serialPort;
   private InputStream inputStream;
   private int timeout = 3000;

   public SerialConnection() {
   }

   public SerialConnection(SerialParameters parameters) {
      this.parameters = parameters;
   }

   public static AbstractSerialConnection getCommPort(String commPort) {
      SerialConnection jSerialCommPort = new SerialConnection();
      jSerialCommPort.serialPort = SerialPort.getCommPort(commPort);
      return jSerialCommPort;
   }

   @Override
   public AbstractModbusTransport getModbusTransport() {
      return this.transport;
   }

   @Override
   public synchronized void open() throws IOException {
      if (this.serialPort == null) {
         this.serialPort = SerialPort.getCommPort(this.parameters.getPortName());
         if (this.serialPort.getDescriptivePortName().contains("Bad Port")) {
            this.close();
            throw new IOException(String.format("Port %s is not a valid name for a port on this platform", this.parameters.getPortName()));
         }
      }

      this.serialPort.closePort();
      this.applyConnectionParameters();
      if ("ascii".equals(this.parameters.getEncoding())) {
         this.transport = new ModbusASCIITransport();
      } else if ("rtu".equals(this.parameters.getEncoding())) {
         this.transport = new ModbusRTUTransport();
      } else {
         this.transport = new ModbusRTUTransport();
         logger.warn("Unknown transport encoding [{}] - reverting to RTU", this.parameters.getEncoding());
      }

      this.transport.setEcho(this.parameters.isEcho());
      this.transport.setTimeout(this.timeout);
      this.transport.setCommPort(this);
      int attempts = 0;

      while (!this.serialPort.openPort(this.parameters.getOpenDelay()) && attempts < 3) {
         this.serialPort.closePort();
         ModbusUtil.sleep(100L);
         logger.debug("Retrying to open port [{}] - attempt [{}}", this.parameters.getPortName(), ++attempts);
      }

      if (this.serialPort.isOpen()) {
         this.inputStream = this.serialPort.getInputStream();
      } else {
         Set<String> ports = this.getCommPorts();
         StringBuilder portList = new StringBuilder("<NONE>");
         if (!ports.isEmpty()) {
            portList = new StringBuilder();

            for (String port : ports) {
               portList.append(portList.length() == 0 ? "" : ",").append(port);
            }
         }

         throw new IOException(
            String.format("Port [%s] cannot be opened after [%d] attempts - valid ports are: [%s]", this.parameters.getPortName(), attempts, portList)
         );
      }
   }

   private void applyConnectionParameters() {
      if (this.serialPort != null) {
         this.serialPort
            .setComPortParameters(this.parameters.getBaudRate(), this.parameters.getDatabits(), this.parameters.getStopbits(), this.parameters.getParity());
         this.serialPort.setFlowControl(this.parameters.getFlowControlIn() | this.parameters.getFlowControlOut());
         this.serialPort
            .setRs485ModeParameters(
               this.parameters.getRs485Mode(),
               this.parameters.getRs485TxEnableActiveHigh(),
               this.parameters.getRs485EnableTermination(),
               this.parameters.getRs485RxDuringTx(),
               this.parameters.getRs485DelayBeforeTxMicroseconds(),
               this.parameters.getRs485DelayAfterTxMicroseconds()
            );
      }
   }

   @Override
   public synchronized void close() {
      if (this.serialPort != null) {
         try {
            if (this.inputStream != null) {
               this.inputStream.close();
            }
         } catch (IOException var5) {
            logger.debug(var5.getMessage());
         } finally {
            this.serialPort.closePort();
         }
      }

      this.serialPort = null;
   }

   @Override
   public synchronized boolean isOpen() {
      return this.serialPort != null && this.serialPort.isOpen();
   }

   @Override
   public synchronized int getTimeout() {
      return this.timeout;
   }

   @Override
   public synchronized void setTimeout(int timeout) {
      this.timeout = timeout;
      if (this.transport != null) {
         this.transport.setTimeout(timeout);
      }
   }

   @Override
   public int readBytes(byte[] buffer, long bytesToRead) {
      return this.serialPort == null ? 0 : this.serialPort.readBytes(buffer, bytesToRead);
   }

   @Override
   public int writeBytes(byte[] buffer, long bytesToWrite) {
      return this.serialPort == null ? 0 : this.serialPort.writeBytes(buffer, bytesToWrite);
   }

   @Override
   public int bytesAvailable() {
      return this.serialPort == null ? 0 : this.serialPort.bytesAvailable();
   }

   @Override
   public int getBaudRate() {
      return this.parameters.getBaudRate();
   }

   @Override
   public int getNumDataBits() {
      return this.parameters.getDatabits();
   }

   @Override
   public int getNumStopBits() {
      return this.parameters.getStopbits();
   }

   @Override
   public int getParity() {
      return this.parameters.getParity();
   }

   @Override
   public String getPortName() {
      return this.parameters.getPortName();
   }

   @Override
   public String getDescriptivePortName() {
      return this.serialPort == null ? this.parameters.getPortName() : this.serialPort.getDescriptivePortName();
   }

   @Override
   public void setComPortTimeouts(int newTimeoutMode, int newReadTimeout, int newWriteTimeout) {
      if (this.serialPort != null) {
         this.serialPort.setComPortTimeouts(newTimeoutMode, newReadTimeout, newWriteTimeout);
      }
   }

   @Override
   public Set<String> getCommPorts() {
      Set<String> returnValue = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
      SerialPort[] ports = SerialPort.getCommPorts();
      if (ports != null && ports.length > 0) {
         for (SerialPort port : ports) {
            returnValue.add(port.getSystemPortName());
         }
      }

      return returnValue;
   }
}
