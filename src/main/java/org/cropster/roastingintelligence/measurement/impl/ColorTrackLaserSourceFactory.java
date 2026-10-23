package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class ColorTrackLaserSourceFactory extends SerialSourceFactory {
   @Override
   public Unit getUnit(String measureName) {
      return "roastColor".equals(measureName) ? Unit.COLORTRACK : null;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new ColorTrackLaserSource(this, this._serialPort)};
   }
}
