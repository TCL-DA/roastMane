package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class CaparaoSourceFactory extends NetworkSourceFactory {
   @Override
   protected String getDefaultIpAddress() {
      return "192.168.5.11";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName) || "gasControl".equals(measureName) || "airflow".equals(measureName) || "airflowControl".equals(measureName)) {
         return Unit.PERCENT;
      } else {
         return !"drumSpeed".equals(measureName) && !"drumSpeedControl".equals(measureName) ? null : Unit.RPM;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new CaparaoSource(this, this._inetAddress)};
   }
}
