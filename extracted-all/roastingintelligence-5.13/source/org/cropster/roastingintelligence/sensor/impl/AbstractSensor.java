package org.cropster.roastingintelligence.sensor.impl;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.cropster.roastingintelligence.sensor.Sensor;
import org.cropster.roastingintelligence.sensor.SensorListener;

public abstract class AbstractSensor implements Sensor {
   private final Set<SensorListener> _sensorListeners = new CopyOnWriteArraySet<>();

   @Override
   public void addSensorListener(SensorListener listener) {
      this._sensorListeners.add(listener);
   }

   @Override
   public void removeSensorListener(SensorListener listener) {
      this._sensorListeners.remove(listener);
   }

   protected void fireSensorAttached() {
      for (SensorListener listener : this._sensorListeners) {
         listener.sensorAttached();
      }
   }

   protected void fireSensorDetached() {
      for (SensorListener listener : this._sensorListeners) {
         listener.sensorDetached();
      }
   }

   protected void fireSensorError(Exception exception) {
      for (SensorListener listener : this._sensorListeners) {
         listener.sensorError(exception);
      }
   }

   protected void fireSensorValueChanged(int channelIndex, double value) {
      for (SensorListener listener : this._sensorListeners) {
         listener.sensorValueChanged(channelIndex, value);
      }
   }
}
