package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class YoctopuceSourceFactory implements MeasurementSourceFactory {
   public static final String TYPE_N = "N";

   static int getYoctoSensorType(String type) {
      if ("K".equals(type)) {
         return 1;
      } else if ("J".equals(type)) {
         return 3;
      } else if ("E".equals(type)) {
         return 2;
      } else if ("T".equals(type)) {
         return 7;
      } else if ("N".equals(type)) {
         return 4;
      } else {
         throw new IllegalArgumentException("Probe type not supported: " + type);
      }
   }

   @Override
   public int getChannelCount() {
      return 2;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"K", "J", "E", "T", "N"};
   }

   @Override
   public Unit getUnit(String measureName) {
      return measureName.matches(".+Temperature") ? Unit.CELSIUS : null;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      return Collections.emptySet();
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new YoctopuceSource(this)};
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }
}
