package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SantokerSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(SantokerSource.class);
   private static final int BAUD_RATE = 9600;
   private static final int DATA_BITS = 8;
   private static final int STOP_BITS = 1;
   private static final int PARITY = 0;
   private final String _serialPortName;
   private volatile SerialPort _serialPort;
   private volatile DataInputStream _inStream;
   private volatile OutputStream _outStream;
   private volatile boolean _ready = false;

   public SantokerSource(MeasurementSourceFactory factory, String serialPortName) {
      super(factory);
      this._serialPortName = serialPortName;
   }

   @Override
   public synchronized void open() {
      try {
         this._serialPort = SerialPort.getCommPort(this._serialPortName);
         this.logger.info("Open port: {}", this._serialPortName);
         this._serialPort.openPort();
         this._serialPort.setComPortTimeouts(1, 300, 0);
         this._serialPort.setComPortParameters(9600, 8, 1, 0);
         if (this._serialPort.isOpen()) {
            this.logger.info("Port opened: {}", this._serialPortName);
            this._inStream = new DataInputStream(this._serialPort.getInputStream());
            this._outStream = this._serialPort.getOutputStream();
            this._ready = true;
            this.fireStatusChanged();
         }
      } catch (Exception var2) {
         this.logger.error("Error opening serial port", var2);
         IOUtils.closeQuietly(this);
      }
   }

   @Override
   public synchronized boolean isReady() {
      return this._ready;
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
   }

   private int decodeBcd(byte[] bcdBytes) {
      int factor = 1;
      int decimal = 0;

      for (int i = bcdBytes.length - 1; i >= 0; i--) {
         for (int shift = 0; shift <= 4; shift += 4) {
            int bcdDigit = bcdBytes[i] >>> shift & 15;
            if (bcdDigit > 9) {
               this.logger.debug("Invalid BCD digit {}: 0x{}", i * 2 + shift % 4, Integer.toHexString(bcdDigit));
            } else {
               decimal += bcdDigit * factor;
               factor *= 10;
            }
         }
      }

      return decimal;
   }

   double getTemperature(byte[] bytes, boolean signBit, boolean dividerBit, boolean unitBit) {
      double sign = signBit ? -1.0 : 1.0;
      double divider = dividerBit ? 1.0 : 10.0;
      double temperatureValue = (double)this.decodeBcd(bytes) / divider * sign;
      if (!unitBit) {
         temperatureValue = Unit.CELSIUS.from(BigDecimal.valueOf(temperatureValue), Unit.FAHRENHEIT).doubleValue();
      }

      return MathUtil.roundHalfToEven(temperatureValue, 1);
   }

   @Override
   public synchronized Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private double[] readCenter301() throws IOException, SantokerSource.InvalidResponseException {
      byte request = 65;
      this.logger.debug("Sending request: {}", request);
      this._outStream.write(request);
      this._outStream.flush();
      byte[] response = new byte[8];
      this.logger.debug("Read response");
      this._inStream.readFully(response, 0, 8);
      this.logger.debug("Response read: 0x{}", Hex.encodeHexString(response));
      if (response[0] == 2 && response[7] == 3) {
         BitSet byte1bits = BitSet.valueOf(new byte[]{response[1]});
         this.logger.debug("Byte 1: {}", byte1bits);
         BitSet byte2bits = BitSet.valueOf(new byte[]{response[2]});
         this.logger.debug("Byte 2: {}", byte2bits);
         byte[] temp1bytes = new byte[]{response[3], response[4]};
         double tempValue1 = this.getTemperature(temp1bytes, byte2bits.get(1), byte2bits.get(2), byte1bits.get(7));
         this.logger.debug("Temp 1: Bcd: {} / °C: {}", Hex.encodeHexString(temp1bytes), tempValue1);
         byte[] temp2bytes = new byte[]{response[5], response[6]};
         double tempValue2 = this.getTemperature(temp2bytes, byte2bits.get(4), byte2bits.get(5), byte1bits.get(7));
         this.logger.debug("Temp 2: Bcd: {} / °C: {}", Hex.encodeHexString(temp2bytes), tempValue2);
         return new double[]{tempValue1, tempValue2};
      } else {
         throw new SantokerSource.InvalidResponseException();
      }
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this._ready) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            double[] temperatures = this.readCenter301();
            values.put("exhaustTemperature", temperatures[0]);
            values.put("beanTemperature", temperatures[1]);
            return values;
         } catch (SerialPortTimeoutException var3) {
            this.logger.error("Timeout reading values", var3);
            return Collections.emptyMap();
         } catch (SantokerSource.InvalidResponseException var4) {
            this.logger.error("Invalid response", var4);
            return Collections.emptyMap();
         } catch (Exception var5) {
            this.logger.error("Error reading values", var5);
            IOUtils.closeQuietly(this);
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public synchronized void close() throws IOException {
      this.logger.info("Close serial port: {}", this._serialPortName);
      this._ready = false;
      if (this._serialPort != null) {
         this._outStream.close();
         this._inStream.close();
         this._serialPort.closePort();
         this.fireStatusChanged();
      }
   }

   private final class InvalidResponseException extends Exception {
   }
}
