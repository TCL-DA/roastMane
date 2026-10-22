package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;

public class ProbatSampleRoasterSourceFactory extends NetworkSourceFactory {
   private final Set<String> _supportedTemperatures = new HashSet<>(
      Arrays.asList("beanTemperature", "exhaustTemperature", "inletTemperature", "drumTemperature", "otherTemperature")
   );
   private final ControlState[] supportedStates = new ControlState[]{
      new ControlState("coolerState", ProbatSampleRoasterSourceFactory.class),
      new ControlState("stirrerState", ProbatSampleRoasterSourceFactory.class),
      new ControlState("standbyState", ProbatSampleRoasterSourceFactory.class)
   };

   @Override
   public int getChannelCount() {
      return 0;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"Default"};
   }

   @Override
   public Unit getUnit(String measureName) {
      if (this._supportedTemperatures.contains(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName)
         || "gasControl".equals(measureName)
         || "airflow".equals(measureName)
         || "airflowControl".equals(measureName)
         || "drumSpeed".equals(measureName)
         || "drumSpeedControl".equals(measureName)) {
         return Unit.PERCENT;
      } else {
         return "drumPressure".equals(measureName) ? Unit.PASCAL : null;
      }
   }

   @Override
   public String getAlias(String measureName) {
      return "drumSpeedControl".equals(measureName) ? "drumSpeed" : measureName;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new ProbatSampleRoasterSource(this, this._inetAddress)};
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }

   @Override
   protected String getDefaultIpAddress() {
      return null;
   }

   @Override
   public ControlState[] getSupportedStates(Set<SourceParameter> parameters) {
      return this.supportedStates;
   }
}
