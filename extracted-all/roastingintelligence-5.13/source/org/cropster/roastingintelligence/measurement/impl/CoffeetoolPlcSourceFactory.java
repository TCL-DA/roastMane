package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class CoffeetoolPlcSourceFactory extends NetworkSourceFactory {
   @Override
   protected String getDefaultIpAddress() {
      return "192.168.1.200";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName) || "burnerTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else {
         return !"gas".equals(measureName)
               && !"gasControl".equals(measureName)
               && !"drumSpeed".equals(measureName)
               && !"drumSpeedControl".equals(measureName)
               && !"airflow".equals(measureName)
               && !"airflowControl".equals(measureName)
            ? null
            : Unit.PERCENT;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new CoffeetoolPlcSource(this, this._inetAddress)};
   }
}
