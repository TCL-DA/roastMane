package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sourceforge.snap7.moka7.S7;
import org.apache.commons.lang3.ArrayUtils;
import org.cropster.roastingintelligence.measurement.CheckableSource;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.remote.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GiesenPlcSource extends AbstractSiemensS7Source implements MeasurementReaderWriter, CheckableSource {
   private final Logger logger = LoggerFactory.getLogger(GiesenPlcSource.class);
   public static final Version NON_LEGACY_VERSION = new Version("5.40");
   public static final String[] SUPPORTED_CHANNELS = new String[]{
      "exhaustTemperatureControl",
      "gasControl",
      "drumPressureControl",
      "drumSpeedControl",
      "exhaustTemperature",
      "beanTemperature",
      "gas",
      "drumPressure",
      "drumSpeed",
      "otherTemperature"
   };
   public static final String BUZZER_REMOTE = "buzzer-remote";
   public static final Unit[] UNITS = new Unit[]{Unit.CELSIUS, Unit.PERCENT, Unit.PASCAL, Unit.HZ};
   private static final int DB2 = 2;
   private static final int DB4 = 4;
   private static final int LENGTH = 38;
   private static final double AUTO_MODE_VALUE = -1.0;
   private final HashSet<String> configuredValues = new HashSet<>();
   private final Unit _drumSpeedUnit;

   public GiesenPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, Unit drumSpeedUnit) {
      super(factory, ipAddress);
      this._drumSpeedUnit = drumSpeedUnit;
   }

   public int getVersion() throws IOException {
      byte[] buffer = new byte[2];
      int errorCode;
      synchronized (this) {
         errorCode = this._client.ReadArea(132, 4, 2, buffer.length, buffer);
      }

      this.handleError(errorCode, buffer);
      return errorCode != 0 ? Integer.MIN_VALUE : S7.GetShortAt(buffer, 0);
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
            return 0.0;
         case "drumPressureControl":
            return 80.0;
         case "drumSpeedControl":
            return Unit.RPM.equals(this._drumSpeedUnit) ? 50.0 : 35.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
            return 100.0;
         case "drumPressureControl":
            return 180.0;
         case "drumSpeedControl":
            return Unit.RPM.equals(this._drumSpeedUnit) ? 87.0 : 60.0;
         default:
            return null;
      }
   }

   @Override
   public void setValue(String field, Double newValue) {
      if (!this.isReady()) {
         this.open();
         if (!this.isReady()) {
            this.logger.error("Failed to write gas value because connecting failed");
            return;
         }
      }

      try {
         int offset = 10;
         if ("exhaustTemperatureControl".equals(field)) {
            offset += 0;
         } else if ("gasControl".equals(field)) {
            this.logger.info("Will be writing gasControl value: {}", newValue);
            offset += 4;
            newValue = newValue + 1.0;
         } else if ("drumPressureControl".equals(field)) {
            offset += 6;
         } else {
            if (!"drumSpeedControl".equals(field)) {
               throw new IllegalArgumentException("Field not supported!");
            }

            offset += 8;
         }

         byte[] buffer = new byte[2];
         S7.SetShortAt(buffer, 0, newValue.intValue());
         boolean writeSuccessful = false;

         for (int i = 0; !writeSuccessful && i < 3; i++) {
            int errorCode;
            synchronized (this) {
               errorCode = this._client.WriteArea(132, 2, offset, buffer.length, buffer);
            }

            this.handleError(errorCode, buffer);
            byte[] readBuffer = new byte[2];
            synchronized (this) {
               errorCode = this._client.ReadArea(132, 2, offset, buffer.length, readBuffer);
            }

            this.handleError(errorCode, buffer);
            double readValue = (double)S7.GetShortAt(readBuffer, 0);
            writeSuccessful = newValue == readValue;
            if (!writeSuccessful) {
               this.logger.warn("Failed attempts to write gas control value: {}", i + 1);
               if (i == 2) {
                  this.logger.error("Could not write gas control value: {}", newValue - 1.0);
               }
            }
         }
      } catch (Exception var13) {
         this.logger.error("Error writing to PLC", var13);
      }
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this.configuredValues.clear();

      for (Channel channel : channels) {
         if (ArrayUtils.contains(SUPPORTED_CHANNELS, channel.getName())) {
            this.configuredValues.add(channel.getName());
         } else {
            this.logger.warn("Measure not supported by Giesen source: {}", channel.getName());
         }
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
            byte[] buffer = new byte[38];
            int errorCode;
            synchronized (this) {
               errorCode = this._client.ReadArea(132, 4, 6, 38, buffer);
            }

            this.handleError(errorCode, buffer);
            if (errorCode != 0) {
               return values;
            }

            float airTemperature = S7.GetFloatAt(buffer, 0);
            values.put("exhaustTemperature", (double)airTemperature);
            float beanTemperature = S7.GetFloatAt(buffer, 4);
            values.put("beanTemperature", (double)beanTemperature);
            int gas = S7.GetShortAt(buffer, 12);
            values.put("gas", (double)gas);
            double drumPressure = (double)S7.GetShortAt(buffer, 14);
            values.put("drumPressure", drumPressure);
            int drumSpeed = S7.GetShortAt(buffer, 16);
            values.put("drumSpeed", (double)drumSpeed);
            float beanIrTemperature = S7.GetFloatAt(buffer, 30);
            if (beanIrTemperature != 0.0F) {
               values.put("otherTemperature", (double)beanIrTemperature);
            }
         } catch (Exception var15) {
            this.logger.error("Error reading from PLC DB 4", var15);
         }

         try {
            byte[] buffer = new byte[10];
            int errorCode;
            synchronized (this) {
               errorCode = this._client.ReadArea(132, 2, 10, buffer.length, buffer);
            }

            this.handleError(errorCode, buffer);
            if (errorCode != 0) {
               return values;
            }

            double exhaustTemperatureControl = (double)S7.GetShortAt(buffer, 0);
            values.put("exhaustTemperatureControl", exhaustTemperatureControl);
            double gasControl = (double)(S7.GetShortAt(buffer, 4) - 1);
            values.put("gasControl", gasControl);
            double drumPressureControl = (double)S7.GetShortAt(buffer, 6);
            values.put("drumPressureControl", drumPressureControl);
            double drumSpeedControl = (double)S7.GetShortAt(buffer, 8);
            values.put("drumSpeedControl", drumSpeedControl);
         } catch (Exception var13) {
            this.logger.error("Error reading from PLC DB 2", var13);
         }

         return values;
      }
   }

   @Override
   public String checkVersion(Collection<Channel> channels) {
      int plcVersionNr = 0;
      Version version = null;

      try {
         plcVersionNr = this.getVersion();
         version = new Version(String.format("%d.%d", plcVersionNr / 100, plcVersionNr % 100));
      } catch (Exception var5) {
         this.logger.error("Error checking Giesen PLC version", var5);
      }

      if (version != null && version.compareTo(NON_LEGACY_VERSION) < 0) {
         this.logger.warn("{}: Version {} (< {}) not supported!", new Object[]{this.getClass().getSimpleName(), version, NON_LEGACY_VERSION});
         return org.cropster.roastingintelligence.util.Messages.m_(
            "{0}: Version {1} (< {2}) not supported!",
            org.cropster.roastingintelligence.util.Messages.m_(this.getFactory().getClass().getSimpleName()),
            version,
            NON_LEGACY_VERSION
         );
      } else {
         if (version != null) {
            this.logger.info("Connected to Giesen PLC version {}", version);
         } else {
            this.logger.info("Connected to Giesen PLC {}", plcVersionNr);
         }

         return null;
      }
   }
}
