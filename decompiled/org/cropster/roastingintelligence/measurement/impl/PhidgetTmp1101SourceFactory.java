package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class PhidgetTmp1101SourceFactory extends AbstractPhidgetNetworkingSourceFactory {
   @Override
   public int getChannelCount() {
      return 4;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"K", "J", "E", "T"};
   }

   @Override
   public Unit getUnit(String measureName) {
      return measureName.matches(".+Temperature") ? Unit.CELSIUS : null;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         return new MeasurementSource[]{new PhidgetTmp1101Source(this, this._host, this._port, this.getDeviceDetector())};
      } catch (Exception var3) {
         throw new IOException("Error instantiating " + PhidgetTmp1101Source.class.getName(), var3);
      }
   }
}
