package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class SantokerSourceFactory extends SerialSourceFactory {
   @Override
   public Unit getUnit(String measureName) {
      return !"beanTemperature".equals(measureName) && !"exhaustTemperature".equals(measureName) ? null : Unit.CELSIUS;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new SantokerSource(this, this._serialPortName)};
   }
}
