package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class WatlowEzZoneSourceFactory extends SerialSourceFactory {
   private static final String PARAM_VERSION = "version";
   private static final String[] TEMPERATURES = new String[]{"beanTemperature", "exhaustTemperature", "environmentalTemperature"};
   private WatlowEzZoneSourceFactory.SanFranciscanVersion _version = WatlowEzZoneSourceFactory.SanFranciscanVersion.WATLOW;

   @Override
   public Unit getUnit(String measureName) {
      for (String temperature : TEMPERATURES) {
         if (temperature.equals(measureName)) {
            return Unit.FAHRENHEIT;
         }
      }

      return null;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("version", String.class, this._version != null ? this._version.name() : null);
      version.setOptions(
         new String[]{WatlowEzZoneSourceFactory.SanFranciscanVersion.WATLOW.name(), WatlowEzZoneSourceFactory.SanFranciscanVersion.EUROTHERM.name()}
      );
      version.setOptionDisplayName(WatlowEzZoneSourceFactory.SanFranciscanVersion.WATLOW.name(), "Watlow EZ-ZONE");
      version.setOptionDisplayName(WatlowEzZoneSourceFactory.SanFranciscanVersion.EUROTHERM.name(), "Eurotherm EPC3008");
      parameters.add(version);
      parameters.addAll(super.getParameters());
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("version".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value == null || value.trim().length() == 0) {
               throw new IllegalArgumentException("Version of the San Franciscan machine required!");
            }

            this._version = WatlowEzZoneSourceFactory.SanFranciscanVersion.valueOf(value);
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         if (WatlowEzZoneSourceFactory.SanFranciscanVersion.WATLOW.equals(this._version)) {
            SerialParameters parameters = new SerialParameters();
            parameters.setPortName(this._serialPort);
            parameters.setBaudRate(9600);
            parameters.setDatabits(8);
            parameters.setParity(0);
            parameters.setStopbits(3);
            parameters.setEncoding("rtu");
            parameters.setEcho(false);
            return new MeasurementSource[]{new WatlowEzZoneSource(this, parameters)};
         } else if (WatlowEzZoneSourceFactory.SanFranciscanVersion.EUROTHERM.equals(this._version)) {
            SerialParameters parameters = new SerialParameters();
            parameters.setPortName(this._serialPort);
            parameters.setBaudRate(19200);
            parameters.setDatabits(8);
            parameters.setParity(0);
            parameters.setStopbits(1);
            parameters.setEncoding("rtu");
            parameters.setEcho(false);
            return new MeasurementSource[]{new SanFranciscanEurothermSource(this, parameters)};
         } else {
            return null;
         }
      } catch (Exception var3) {
         throw new IOException(var3);
      }
   }

   public static enum SanFranciscanVersion {
      WATLOW,
      EUROTHERM;
   }
}
