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

public class PrimoSourceFactory extends SerialSourceFactory {
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private Unit _temperatureUnit = Unit.FAHRENHEIT;

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "setTemperatureControl".equals(measureName)) {
         return this._temperatureUnit;
      } else {
         return !"gas".equals(measureName) && !"gasControl".equals(measureName) ? null : Unit.PERCENT;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter temperatureUnit = new SourceParameter("temperatureUnit", String.class, this._temperatureUnit.name());
      temperatureUnit.setOptions(TEMP_OPTIONS);
      parameters.add(temperatureUnit);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("temperatureUnit".equals(parameter.getName())) {
            this._temperatureUnit = Unit.FAHRENHEIT.name().equalsIgnoreCase(parameter.getValue()) ? Unit.FAHRENHEIT : Unit.CELSIUS;
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         SerialParameters parameters = new SerialParameters();
         parameters.setPortName(this._serialPort);
         parameters.setBaudRate(9600);
         parameters.setDatabits(8);
         parameters.setParity(2);
         parameters.setStopbits(1);
         parameters.setEncoding("ascii");
         parameters.setEcho(false);
         return new MeasurementSource[]{new PrimoSource(this, parameters, this._temperatureUnit)};
      } catch (Exception var3) {
         throw new IOException(var3);
      }
   }

   @Override
   public boolean supportsAutoMode(String channelName) {
      return "gasControl".equals(channelName) || "setTemperatureControl".equals(channelName);
   }

   @Override
   public String getAutoModeMeasurement(String measurement) {
      return this.supportsAutoMode(measurement) ? "setTemperatureControl" : null;
   }

   @Override
   public String getManualModeMeasurement(String measurement) {
      return this.supportsAutoMode(measurement) ? "gasControl" : null;
   }
}
