package org.cropster.roastingintelligence.measurement.impl;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorTrackLaserSource extends AbstractMeasurementSource implements SerialPortDataListener {
   private final Logger logger = LoggerFactory.getLogger(ColorTrackLaserSource.class);
   private static final int BAUD_RATE = 9600;
   private final String _serialPortName;
   private volatile SerialPort _serialPort;
   private volatile BufferedReader _reader;
   private volatile long _lastEvent;
   private volatile Double _value;
   private volatile double _valueTotal;
   private volatile double _valueCount;

   public ColorTrackLaserSource(MeasurementSourceFactory factory, String serialPortName) {
      super(factory);
      this._serialPortName = serialPortName;
   }

   @Override
   public synchronized void open() {
      try {
         this._lastEvent = NanoTime.currentTimeMillis();
         this._serialPort = SerialPort.getCommPort(this._serialPortName);
         this._serialPort.setComPortParameters(9600, 8, 1, 0);
         this._reader = new BufferedReader(new InputStreamReader(this._serialPort.getInputStream()));
         this._serialPort.addDataListener(this);
      } catch (Exception var2) {
         this.logger.error("Error opening serial port", var2);
         IOUtils.closeQuietly(this);
      }
   }

   @Override
   public synchronized boolean isReady() {
      if (this._serialPort != null && this._lastEvent > NanoTime.currentTimeMillis() - 1000L) {
         return true;
      } else {
         try {
            this.close();
         } catch (IOException var2) {
            this.logger.warn("Error closing serial port", var2);
         }

         return false;
      }
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
   }

   private synchronized void calcValue() {
      this._value = this._valueTotal / this._valueCount;
      this._valueCount = 0.0;
      this._valueTotal = 0.0;
   }

   @Override
   public Double getValue(String channelName) {
      if (this.isReady() && "roastColor".equals(channelName)) {
         this.calcValue();
         return this._value;
      } else {
         return null;
      }
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         this.calcValue();
         Map<String, Double> values = new HashMap<>(1);
         values.put("roastColor", this._value);
         return values;
      }
   }

   @Override
   public synchronized void close() throws IOException {
      if (this._serialPort != null) {
         this._lastEvent = -1L;
         this._value = null;
         this._reader.close();
         this._serialPort.closePort();
         this._serialPort = null;
      }
   }

   public int getListeningEvents() {
      return 1;
   }

   public void serialEvent(SerialPortEvent event) {
      this._lastEvent = NanoTime.currentTimeMillis();
      int eventType = event.getEventType();
      if (eventType == 1) {
         try {
            String line = this._reader.readLine();
            if (line != null) {
               if (line.matches("\\d+\\.\\d+")) {
                  double value = Double.parseDouble(line);
                  this._valueTotal += value;
                  this._valueCount++;
               } else {
                  this.logger.info("Serial port debug message: {}", line);
               }
            } else {
               this.logger.warn("Serial port EOF");
               IOUtils.closeQuietly(this);
            }
         } catch (IOException var6) {
            this.logger.error("Error reading from serial port", var6);
            IOUtils.closeQuietly(this);
         }
      }
   }
}
