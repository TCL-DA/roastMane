package org.cropster.roastingintelligence.measurement.impl;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sourceforge.snap7.moka7.S7;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CogenV2Source extends AbstractSiemensS7Source implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(CogenV2Source.class);
   private static final int DB = 6;
   private static final int BEAN_TEMPERATURE_OFFSET = 400;
   private static final int DRUM_TEMPERATURE_OFFSET = 404;
   private static final int DRUM_PRESSURE_OFFSET = 412;
   private static final int DRUM_SPEED_CONTROL_OFFSET = 464;
   private static final int GAS_CONTROL_OFFSET = 544;
   private static final int DRUM_PRESSURE_CONTROL_OFFSET = 558;
   private static final int OFFSET_REQUEST1 = 400;
   private static final int LENGTH_REQUEST1 = 68;
   private static final int OFFSET_REQUEST2 = 544;
   private static final int LENGTH_REQUEST2 = 18;
   private final HashSet<String> configuredValues = new HashSet<>();

   public CogenV2Source(MeasurementSourceFactory factory, InetAddress ipAddress) {
      super(factory, ipAddress);
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
            return 1.0;
         case "drumPressureControl":
         case "drumSpeedControl":
            return 0.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
            return 10.0;
         case "drumPressureControl":
            return 300.0;
         case "drumSpeedControl":
            return 69.0;
         default:
            return null;
      }
   }

   @Override
   public void setValue(String field, Double newValue) {
      int offset;
      int nrOfBytes;
      switch (field) {
         case "gasControl":
            offset = 544;
            nrOfBytes = 2;
            break;
         case "drumPressureControl":
            offset = 558;
            nrOfBytes = 4;
            break;
         case "drumSpeedControl":
            offset = 464;
            nrOfBytes = 4;
            break;
         default:
            throw new IllegalArgumentException("Field not supported: " + field);
      }

      if (!this.isReady()) {
         this.open();
         if (!this.isReady()) {
            this.logger.error("Failed to write {} value because connecting failed", field);
            return;
         }
      }

      try {
         byte[] buffer = new byte[nrOfBytes];
         switch (nrOfBytes) {
            case 2:
               S7.SetShortAt(buffer, 0, newValue.intValue());
               break;
            case 4:
               S7.SetFloatAt(buffer, 0, newValue.floatValue());
               break;
            default:
               throw new IllegalStateException("Unexpected number of bytes: " + nrOfBytes);
         }

         boolean writeSuccessful = false;

         for (int i = 0; !writeSuccessful && i < 3; i++) {
            int errorCode;
            synchronized (this) {
               errorCode = this._client.WriteArea(132, 6, offset, buffer.length, buffer);
            }

            this.handleError(errorCode, buffer);
            byte[] readBuffer = new byte[nrOfBytes];
            synchronized (this) {
               errorCode = this._client.ReadArea(132, 6, offset, readBuffer.length, readBuffer);
            }

            this.handleError(errorCode, readBuffer);
            double readValue = nrOfBytes == 4 ? (double)S7.GetFloatAt(readBuffer, 0) : (double)S7.GetShortAt(readBuffer, 0);
            writeSuccessful = newValue == readValue;
            if (!writeSuccessful) {
               this.logger.warn("Failed attempts to write gas control value: {}", i + 1);
               if (i == 2) {
                  this.logger.error("Could not write {} control value: {}", field, newValue);
               }
            }
         }
      } catch (Exception var14) {
         this.logger.error("Error writing to PLC", var14);
      }
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this.configuredValues.clear();

      for (Channel channel : channels) {
         this.configuredValues.add(channel.getName());
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this.configuredValues;
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new HashMap<>();

         try {
            byte[] buffer = new byte[68];
            int errorCode;
            synchronized (this) {
               errorCode = this._client.ReadArea(132, 6, 400, 68, buffer);
            }

            this.handleError(errorCode, buffer);
            if (errorCode != 0) {
               return values;
            }

            values.put("beanTemperature", (double)S7.GetFloatAt(buffer, 0));
            values.put("drumTemperature", (double)S7.GetFloatAt(buffer, 4));
            values.put("drumPressure", (double)S7.GetFloatAt(buffer, 12));
            double drumSpeed = (double)S7.GetFloatAt(buffer, 64);
            values.put("drumSpeed", drumSpeed);
            values.put("drumSpeedControl", drumSpeed);
         } catch (Exception var10) {
            this.logger.error("Error reading from PLC DB {}", 6, var10);
         }

         try {
            byte[] buffer = new byte[18];
            int errorCode;
            synchronized (this) {
               errorCode = this._client.ReadArea(132, 6, 544, 18, buffer);
            }

            this.handleError(errorCode, buffer);
            if (errorCode != 0) {
               return values;
            }

            int gas = S7.GetShortAt(buffer, 0);
            values.put("gas", (double)gas);
            values.put("gasControl", (double)gas);
            values.put("drumPressureControl", (double)S7.GetFloatAt(buffer, 14));
         } catch (Exception var8) {
            this.logger.error("Error reading from PLC DB {}", 6, var8);
         }

         return values;
      }
   }
}
