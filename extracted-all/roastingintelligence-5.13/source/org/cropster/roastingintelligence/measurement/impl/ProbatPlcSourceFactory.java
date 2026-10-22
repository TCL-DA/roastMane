package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class ProbatPlcSourceFactory extends NetworkSourceFactory {
   @Override
   protected String getDefaultIpAddress() {
      return null;
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName)
         || "gasControl".equals(measureName)
         || "airflow".equals(measureName)
         || "airflowControl".equals(measureName)
         || "drumSpeed".equals(measureName)
         || "drumSpeedControl".equals(measureName)) {
         return Unit.PERCENT;
      } else {
         return "drumPressure".equals(measureName) ? Unit.MILLIBAR : null;
      }
   }

   @Override
   public String getAlias(String measureName) {
      return "drumSpeedControl".equals(measureName) ? "drumSpeed" : measureName;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new ProbatPlcSource(this, this._inetAddress)};
   }
}
