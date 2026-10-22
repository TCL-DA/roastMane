package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class LoringPlcSourceFactory extends NetworkSourceFactory {
   private final Set<String> _supportedTemperatures = new HashSet<>(
      Arrays.asList("beanTemperature", "inletTemperature", "stackTemperature", "returnTemperature")
   );

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.1.199";
   }

   @Override
   public Unit getUnit(String measureName) {
      if (this._supportedTemperatures.contains(measureName)) {
         return Unit.FAHRENHEIT;
      } else {
         return "gas".equals(measureName) ? Unit.PERCENT : null;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new LoringPlcSource(this, this._inetAddress)};
   }

   @Override
   public boolean supportsAutoStart(Set<SourceParameter> parameters) {
      return true;
   }
}
