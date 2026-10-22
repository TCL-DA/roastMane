package org.cropster.roastingintelligence.sensor;

import com.phidget22.PhidgetException;

public interface SensorValueConverterPhidget22 {
   double convertValue(double var1) throws PhidgetException;

   default double getChangeTrigger() {
      return 0.0;
   }
}
