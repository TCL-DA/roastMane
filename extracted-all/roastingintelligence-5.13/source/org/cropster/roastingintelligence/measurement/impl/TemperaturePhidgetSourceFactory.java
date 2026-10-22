package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class TemperaturePhidgetSourceFactory extends Phidget22SourceFactory {
   public static final String TYPE_K = "K";
   public static final String TYPE_J = "J";
   public static final String TYPE_E = "E";
   public static final String TYPE_T = "T";
   private static final Set<Class<?>> HUB_TEMPERATURE_SENSOR_FACTORIES = new HashSet<>(
      Arrays.asList(PhidgetTmp1200SourceFactory.class, PhidgetTmp1100SourceFactory.class, PhidgetTmp1101SourceFactory.class)
   );

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
         boolean hubTemperatureSensorFactoriesConfigured = channels.stream()
            .anyMatch(channel -> HUB_TEMPERATURE_SENSOR_FACTORIES.contains(channel.getSourceFactory()));
         return hubTemperatureSensorFactoriesConfigured
            ? new MeasurementSource[]{new Phidget1048ManagedSource(this, this.getDeviceDetector())}
            : new MeasurementSource[]{new Phidget1048Source(this)};
      } catch (Exception var3) {
         throw new IOException("Error instantiating MeasurementSources", var3);
      }
   }
}
