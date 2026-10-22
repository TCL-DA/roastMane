package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class CogenPlcV2SourceFactory extends NetworkSourceFactory {
   @Override
   protected String getDefaultIpAddress() {
      return "";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "drumTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName) || "gasControl".equals(measureName)) {
         return Unit.NUMBER;
      } else if ("drumPressure".equals(measureName) || "drumPressureControl".equals(measureName)) {
         return Unit.PASCAL;
      } else {
         return !"drumSpeed".equals(measureName) && !"drumSpeedControl".equals(measureName) ? null : Unit.RPM;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new CogenV2Source(this, this._inetAddress)};
   }
}
