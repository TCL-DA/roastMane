package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class KirschUndMausserPlcSourceFactory extends NetworkSourceFactory {
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private Unit _temperatureUnit = Unit.CELSIUS;
   private final Set<String> _supportedTemperatures = new HashSet<>(Arrays.asList("beanTemperature", "exhaustTemperature", "inletTemperature"));

   @Override
   protected String getDefaultIpAddress() {
      return "10.32.176.124";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("gas".equals(measureName) || "gasControl".equals(measureName) || "airflow".equals(measureName) || "airflowControl".equals(measureName)) {
         return Unit.PERCENT;
      } else if ("gasPressure".equals(measureName) || "drumPressure".equals(measureName)) {
         return Unit.PASCAL;
      } else if ("drumSpeed".equals(measureName) || "drumSpeedControl".equals(measureName)) {
         return Unit.RPM;
      } else {
         return this._supportedTemperatures.contains(measureName) ? this._temperatureUnit : null;
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
      return new MeasurementSource[]{new KirschUndMausserPlcSource(this, this._inetAddress, this._temperatureUnit)};
   }
}
