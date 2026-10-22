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

public class GiesenPlcSourceFactory extends NetworkSourceFactory {
   private static final String[] DRUM_SPEED_UNIT_OPTIONS = new String[]{Unit.HZ.name(), Unit.RPM.name()};
   private static final String PARAM_DRUM_SPEED_UNIT = "drumSpeedUnit";
   private final Set<String> _supportedTemperatures = new HashSet<>(Arrays.asList("beanTemperature", "exhaustTemperature", "otherTemperature"));
   private Unit _drumSpeedUnit = Unit.HZ;

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.2.180";
   }

   @Override
   public Unit getUnit(String measureName) {
      if (measureName.equals("gas") || measureName.equals("gasControl")) {
         return Unit.PERCENT;
      } else if (measureName.equals("drumPressure") || measureName.equals("drumPressureControl")) {
         return Unit.PASCAL;
      } else if (measureName.equals("drumSpeed") || measureName.equals("drumSpeedControl")) {
         return this._drumSpeedUnit;
      } else {
         return this._supportedTemperatures.contains(measureName) ? Unit.CELSIUS : null;
      }
   }

   @Override
   public String getAlias(String measureName) {
      return "drumSpeedControl".equals(measureName) ? "drumSpeed" : measureName;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter drumSpeedUnit = new SourceParameter("drumSpeedUnit", String.class, this._drumSpeedUnit.name());
      drumSpeedUnit.setOptions(DRUM_SPEED_UNIT_OPTIONS);
      parameters.add(drumSpeedUnit);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("drumSpeedUnit".equals(parameter.getName())) {
            this._drumSpeedUnit = Unit.RPM.name().equalsIgnoreCase(parameter.getValue()) ? Unit.RPM : Unit.HZ;
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new GiesenPlcSource(this, this._inetAddress, this._drumSpeedUnit)};
   }
}
