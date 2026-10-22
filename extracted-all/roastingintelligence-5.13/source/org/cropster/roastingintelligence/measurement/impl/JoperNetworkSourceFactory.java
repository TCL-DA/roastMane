package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class JoperNetworkSourceFactory extends NetworkSourceFactory {
   @Override
   protected String getDefaultIpAddress() {
      return "192.168.100.20";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName)
         || "exhaustTemperature".equals(measureName)
         || "environmentalTemperature".equals(measureName)
         || "otherTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName)
         || "gasControl".equals(measureName)
         || "airflowControl".equals(measureName)
         || "drumSpeedControl".equals(measureName)) {
         return Unit.PERCENT;
      } else if ("drumPressure".equals(measureName)) {
         return Unit.PASCAL;
      } else {
         return "drumSpeed".equals(measureName) ? Unit.RPM : null;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new JoperNetworkSource(this, this._inetAddress)};
   }
}
