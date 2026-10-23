package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class BridgePhidgetSourceFactory extends Phidget22SourceFactory {
   public static final String TYPE_PT100 = "Pt100";
   public static final String TYPE_PT1000 = "Pt1000";

   @Override
   public int getChannelCount() {
      return 4;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"Pt100", "Pt1000"};
   }

   @Override
   public Unit getUnit(String measureName) {
      return measureName.matches(".+Temperature") ? Unit.CELSIUS : null;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      boolean anyOtherPhidgetConfigured = channels.stream()
         .map(channel -> channel.getSourceFactory())
         .filter(Objects::nonNull)
         .anyMatch(
            sourceFactory -> Phidget22SourceFactory.class.isAssignableFrom((Class<?>)sourceFactory)
                  && !BridgePhidgetSourceFactory.class.isAssignableFrom((Class<?>)sourceFactory)
         );

      try {
         return anyOtherPhidgetConfigured
            ? new MeasurementSource[]{new Phidget1046ManagedSource(this, this.getDeviceDetector())}
            : new MeasurementSource[]{new Phidget1046Source(this)};
      } catch (Exception var4) {
         throw new IOException("Error instantiating MeasurementSources", var4);
      }
   }
}
