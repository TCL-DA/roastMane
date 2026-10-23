package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.ChannelClass;
import com.phidget22.DeviceID;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;

public class PhidgetTmp1101Source extends Phidget1048ManagedSource {
   public PhidgetTmp1101Source(MeasurementSourceFactory factory, String host, Integer port, PhidgetDeviceDetector deviceDetector) {
      super(factory, host, port, deviceDetector);
   }

   public PhidgetTmp1101Source(MeasurementSourceFactory factory, PhidgetDeviceDetector deviceDetector) {
      this(factory, null, null, deviceDetector);
   }

   @Override
   public boolean filter(Phidget phidget) throws PhidgetException {
      return DeviceID.PN_TMP1101.equals(phidget.getDeviceID())
         && ChannelClass.TEMPERATURE_SENSOR.equals(phidget.getChannelClass())
         && phidget.getIsRemote() == (this._host != null && this._port != null);
   }
}
