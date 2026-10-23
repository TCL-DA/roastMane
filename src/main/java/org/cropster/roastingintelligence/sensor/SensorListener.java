package org.cropster.roastingintelligence.sensor;

public interface SensorListener {
   void sensorAttached();

   void sensorDetached();

   void sensorError(Exception var1);

   void sensorValueChanged(int var1, double var2);
}
