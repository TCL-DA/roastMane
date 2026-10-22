package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class PressureSensorSourceFactory extends HubSensorSourceFactory {
   public static final String PHIDGET_1136 = "Phidget 1136";
   public static final String PHIDGET_1137 = "Phidget 1137";
   public static final String PHIDGET_1138 = "Phidget 1138";

   @Override
   public String[] getTypes() {
      return new String[]{"Phidget 1136", "Phidget 1137", "Phidget 1138"};
   }

   @Override
   public Unit getUnit(String measureName) {
      return !"drumPressure".equals(measureName) && !"gasPressure".equals(measureName) ? null : Unit.PASCAL;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new VoltageRatioSource(this, this._phidgetHub.equals("VINT Hub"), this._host, this._port, this.getDeviceDetector())};
   }
}
