package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class LillaPlcSourceFactory extends NetworkSourceFactory {
   @Override
   protected String getDefaultIpAddress() {
      return "192.168.1.100";
   }

   @Override
   public Unit getUnit(String measureName) {
      return !"beanTemperature".equals(measureName)
            && !"exhaustTemperature".equals(measureName)
            && !"burnerTemperature".equals(measureName)
            && !"stackTemperature".equals(measureName)
         ? null
         : Unit.FAHRENHEIT;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new LillaPlcSource(this, this._inetAddress)};
   }
}
