package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class RceSourceFactory extends NetworkSourceFactory {
   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName) || "airflow".equals(measureName) || "gasControl".equals(measureName) || "airflowControl".equals(measureName)) {
         return Unit.PERCENT;
      } else {
         return !"drumSpeed".equals(measureName) && !"drumSpeedControl".equals(measureName) ? null : Unit.RPM;
      }
   }

   @Override
   protected String getDefaultIpAddress() {
      return "";
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         return new MeasurementSource[]{new RceMitsubishiSource(this, this._inetAddress)};
      } catch (Exception var3) {
         throw new IOException("Error initializing measurement source");
      }
   }
}
