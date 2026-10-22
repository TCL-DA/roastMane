package org.cropster.roastingintelligence.measurement.impl.simulation;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.measurement.impl.DemoSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;

@NotMutuallyExclusiveSource
public class DeviceSimulatorSourceFactory extends DemoSourceFactory {
   private final ControlState[] supportedStates = new ControlState[]{
      new ControlState("burnerState", DeviceSimulatorSourceFactory.class),
      new ControlState("coolerState", DeviceSimulatorSourceFactory.class),
      new ControlState("stirrerState", DeviceSimulatorSourceFactory.class),
      new ControlState("chargeState", DeviceSimulatorSourceFactory.class),
      new ControlState("dischargeState", DeviceSimulatorSourceFactory.class)
   };

   @Override
   public String[] getTypes() {
      return new String[]{"Default"};
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("gasControl".equals(measureName) || "airflowControl".equals(measureName)) {
         return Unit.PERCENT;
      } else if ("drumSpeedControl".equals(measureName)) {
         return Unit.RPM;
      } else if ("drumPressureControl".equals(measureName)) {
         return Unit.PASCAL;
      } else {
         return "setTemperatureControl".equals(measureName) ? Unit.FAHRENHEIT : null;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new DeviceSimulatorSource(this)};
   }

   @Override
   public boolean supportsAutoMode(String measureName) {
      return "gasControl".equals(measureName) || "setTemperatureControl".equals(measureName);
   }

   @Override
   public String getAutoModeMeasurement(String measureName) {
      return this.supportsAutoMode(measureName) ? "setTemperatureControl" : null;
   }

   @Override
   public String getManualModeMeasurement(String measureName) {
      return this.supportsAutoMode(measureName) ? "gasControl" : null;
   }

   @Override
   public ControlState[] getSupportedStates(Set<SourceParameter> parameters) {
      return this.supportedStates;
   }
}
