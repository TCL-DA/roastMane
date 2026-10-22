package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class CoffeeTechNetworkSourceFactory extends NetworkSourceFactory {
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private Unit _temperatureUnit = Unit.CELSIUS;

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.1.27";
   }

   @Override
   public Unit getUnit(String measureName) {
      switch (measureName) {
         case "beanTemperature":
         case "drumTemperature":
            return this._temperatureUnit;
         case "gas":
         case "gasControl":
            return Unit.PERCENT;
         case "drumSpeed":
         case "drumSpeedControl":
         case "fanSpeed":
         case "fanSpeedControl":
            return Unit.HZ;
         default:
            return null;
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
      return new MeasurementSource[]{new CoffeeTechNetworkSource(this, this._inetAddress)};
   }
}
