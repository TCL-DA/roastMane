package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sourceforge.snap7.moka7.S7;
import net.sourceforge.snap7.moka7.S7Client;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.CheckableSource;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KirschUndMausserPlcSource extends AbstractSiemensS7Source implements MeasurementReaderWriter, CheckableSource {
   private final Logger logger = LoggerFactory.getLogger(KirschUndMausserPlcSource.class);
   private static final int DB_V1 = 100;
   private static final int DB_V2 = 200;
   private static final int OFFSET = 14;
   private static final int AIRFLOW_CONTROL_OFFSET = 64;
   private static final int GAS_CONTROL_OFFSET = 80;
   private static final int DRUM_SPEED_CONTROL_OFFSET = 72;
   private static final int RETRIES_WRITE = 2;
   private static final int KEEP_ALIVE_INTERVAL = 11;
   private final HashSet<String> _channelNames = new HashSet<>();
   private volatile long _lastKeepAlive = 0L;
   private boolean _controlsEnabled = false;
   private boolean _remoteControlActivated = false;
   private KirschUndMausserPlcSource.KirschUndMausserVersion _version;
   private Unit _temperatureUnit;

   public KirschUndMausserPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, Unit temperatureUnit) {
      super(factory, ipAddress);
      this._temperatureUnit = temperatureUnit;
   }

   public KirschUndMausserPlcSource.KirschUndMausserVersion getVersion() {
      if (!this.isReady()) {
         return null;
      } else {
         byte[] buffer = new byte[16];
         int errorCode = this._client.ReadArea(132, 200, 0, buffer.length, buffer);

         try {
            if (errorCode == 0) {
               String serialNr = S7.GetStringAt(buffer, 0, 10);
               int softwareVersion = S7.GetShortAt(buffer, 10);
               int interfaceVersion = S7.GetShortAt(buffer, 12);
               this.logger.info("Connection established: {}, {}, {}", new Object[]{serialNr, softwareVersion, interfaceVersion});
               return interfaceVersion >= 100 && softwareVersion >= 330
                  ? KirschUndMausserPlcSource.KirschUndMausserVersion.VERSION2
                  : KirschUndMausserPlcSource.KirschUndMausserVersion.VERSION1;
            }

            this.logger.error("Error reading from Siemens PLC: {} - {}.", errorCode, S7Client.ErrorText(errorCode));
            if (errorCode == 10) {
               return KirschUndMausserPlcSource.KirschUndMausserVersion.VERSION1;
            }
         } catch (Exception var6) {
            this.logger.error("Error reading version from PLC DB {}", 200, var6);
         }

         return null;
      }
   }

   public Map<String, Double> getValuesV1() {
      Map<String, Double> values = new HashMap<>();

      try {
         byte[] buffer = new byte[32];
         int errorCode = this._client.ReadArea(132, 100, 0, buffer.length, buffer);
         this.handleError(errorCode, buffer);
         float inletTemperature = S7.GetFloatAt(buffer, 0);
         values.put("inletTemperature", (double)inletTemperature);
         float beanTemperature = S7.GetFloatAt(buffer, 4);
         values.put("beanTemperature", (double)beanTemperature);
         float exhaustTemperature = S7.GetFloatAt(buffer, 8);
         values.put("exhaustTemperature", (double)exhaustTemperature);
         int gasPressure = S7.GetShortAt(buffer, 16);
         values.put("gasPressure", (double)gasPressure * 100.0);
         int drumPressure = S7.GetShortAt(buffer, 18);
         values.put("drumPressure", (double)drumPressure * 100.0);
         int drumSpeed = S7.GetShortAt(buffer, 22);
         values.put("drumSpeed", (double)drumSpeed);
         int gas = S7.GetShortAt(buffer, 28);
         values.put("gas", (double)gas);
         int airflow = S7.GetShortAt(buffer, 30);
         values.put("airflow", (double)airflow);
      } catch (Exception var12) {
         this.logger.error("Error reading from PLC DB {}", 100, var12);
      }

      return values;
   }

   private double getDoubleValue(float value) {
      return !Unit.FAHRENHEIT.equals(this._temperatureUnit)
         ? (double)value
         : Unit.FAHRENHEIT.from(BigDecimal.valueOf((double)value), Unit.CELSIUS).doubleValue();
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else if (KirschUndMausserPlcSource.KirschUndMausserVersion.VERSION1.equals(this._version)) {
         return this.getValuesV1();
      } else {
         byte[] buffer = new byte[103];
         Map<String, Double> values = new HashMap<>();

         try {
            int errorCode = this._client.ReadArea(132, 200, 14, buffer.length, buffer);
            this.handleError(errorCode, buffer);
            float inletTemperature = S7.GetFloatAt(buffer, 0);
            values.put("inletTemperature", this.getDoubleValue(inletTemperature));
            float beanTemperature = S7.GetFloatAt(buffer, 4);
            values.put("beanTemperature", this.getDoubleValue(beanTemperature));
            float exhaustTemperature = S7.GetFloatAt(buffer, 8);
            values.put("exhaustTemperature", this.getDoubleValue(exhaustTemperature));
            int drumPressure = S7.GetShortAt(buffer, 28);
            values.put("drumPressure", (double)drumPressure);
            int gasPressure = S7.GetShortAt(buffer, 30);
            values.put("gasPressure", (double)gasPressure);
            int airflowControl = S7.GetShortAt(buffer, 50);
            values.put("airflowControl", (double)airflowControl);
            int airflow = S7.GetShortAt(buffer, 52);
            values.put("airflow", (double)airflow);
            int drumSpeedControl = S7.GetShortAt(buffer, 58);
            values.put("drumSpeedControl", (double)drumSpeedControl);
            values.put("drumSpeed", (double)drumSpeedControl);
            int gasControl = S7.GetShortAt(buffer, 66);
            values.put("gasControl", (double)gasControl);
            int gas = S7.GetShortAt(buffer, 68);
            values.put("gas", (double)gas);
         } catch (Exception var16) {
            this.logger.error("Error reading from PLC DB {}", 200, var16);
            return values;
         }

         if (!this._controlsEnabled) {
            return values;
         } else {
            try {
               byte bitRegister = buffer[102];
               boolean isRemoteControlRequested = (bitRegister & 1) == 1;
               this._remoteControlActivated = (bitRegister >> 1 & 1) == 1;
               long now = NanoTime.currentTimeMillis();
               if (!isRemoteControlRequested || now - this._lastKeepAlive >= 11000L) {
                  byte[] writeBuffer = new byte[]{bitRegister};
                  S7.SetBitAt(writeBuffer, 0, 0, true);
                  synchronized (this) {
                     int errorCode = this._client.WriteArea(132, 200, 116, writeBuffer.length, writeBuffer);
                     this.handleError(errorCode, writeBuffer);
                  }

                  this._lastKeepAlive = NanoTime.currentTimeMillis();
               }
            } catch (IOException var17) {
               this.logger.error("Error activating remote control", var17);
            }

            return values;
         }
      }
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
            return 0.0;
         case "airflowControl":
         case "drumSpeedControl":
            return 25.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
         case "airflowControl":
            return 100.0;
         case "drumSpeedControl":
            return 60.0;
         default:
            return null;
      }
   }

   @Override
   public void setValue(String field, Double newValue) {
      if (!"gasControl".equals(field) && !"airflowControl".equals(field) && !"drumSpeedControl".equals(field)) {
         throw new IllegalArgumentException("Field not supported: " + field);
      } else if (!this._remoteControlActivated) {
         this.logger.info("Cannot write {} value {}. Remote control not activated", field, newValue);
      } else {
         if (!this.isReady()) {
            this.open();
            if (!this.isReady()) {
               this.logger.error("Failed to write {} value because connecting failed", field);
               return;
            }
         }

         try {
            int offset = "gasControl".equals(field) ? 80 : 64;

            byte var14 = switch (field) {
               case "airflowControl" -> 64;
               case "drumSpeedControl" -> 72;
               default -> 80;
            };
            boolean writeSuccessful = false;

            for (int i = 0; !writeSuccessful && i <= 2; i++) {
               byte[] buffer = new byte[2];
               S7.SetShortAt(buffer, 0, newValue.intValue());
               int errorCode;
               synchronized (this) {
                  errorCode = this._client.WriteArea(132, 200, var14, buffer.length, buffer);
               }

               this.handleError(errorCode, buffer);
               byte[] readBuffer = new byte[2];
               synchronized (this) {
                  errorCode = this._client.ReadArea(132, 200, var14, buffer.length, readBuffer);
               }

               this.handleError(errorCode, buffer);
               double readValue = (double)S7.GetShortAt(readBuffer, 0);
               writeSuccessful = newValue == readValue;
               if (!writeSuccessful) {
                  this.logger.warn("Failed attempts to write {} value: {}", field, i + 1);
                  if (i == 2) {
                     this.logger.error("Could not write {} value: {}", field, newValue);
                  }
               }
            }
         } catch (Exception var13) {
            this.logger.error("Error writing to PLC", var13);
         }
      }
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      this._channelNames.clear();
      this._controlsEnabled = false;

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
         if ("gasControl".equals(channel.getName()) || "airflowControl".equals(channel.getName()) || "drumSpeedControl".equals(channel.getName())) {
            this._controlsEnabled = true;
         }
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   @Override
   public String checkVersion(Collection<Channel> channels) {
      KirschUndMausserPlcSource.KirschUndMausserVersion version = this.getVersion();
      this._version = version;
      if (version == null) {
         this.logger.error("Error checking Kirsch+Mausser PLC version");
         IOUtils.closeQuietly(this);
         return null;
      } else {
         this.logger.error("Detected Kirsch+Mausser PLC {}", this._version);
         if (KirschUndMausserPlcSource.KirschUndMausserVersion.VERSION1.equals(version)) {
            return channels.stream().filter(c -> c.getName().matches(".+[cC]ontrol")).findAny().isPresent()
               ? "PLC version does not support controls. Disable the control measurements in the machine preferences."
               : null;
         } else {
            return null;
         }
      }
   }

   @Override
   public boolean isValid(String measureName, Double value) {
      return !"drumPressure".equals(measureName) && !"gasPressure".equals(measureName) ? super.isValid(measureName, value) : true;
   }

   public static enum KirschUndMausserVersion {
      VERSION1,
      VERSION2;
   }
}
