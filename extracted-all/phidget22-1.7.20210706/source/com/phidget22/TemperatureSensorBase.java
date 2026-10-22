package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TemperatureSensorBase extends Phidget {
   private List<TemperatureSensorTemperatureChangeListener> temperatureChangeListeners = new ArrayList<>();
   private long nativeTemperatureChangeHandler = 0L;

   TemperatureSensorBase(long var1) {
      super(var1);
   }

   public TemperatureSensorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableTemperatureChangeEvents(var1 && this.temperatureChangeListeners.size() > 0);
   }

   public final void addTemperatureChangeListener(TemperatureSensorTemperatureChangeListener var1) {
      synchronized (this.temperatureChangeListeners) {
         this.temperatureChangeListeners.add(var1);
         this.enableTemperatureChangeEvents(true);
      }
   }

   public final void removeTemperatureChangeListener(TemperatureSensorTemperatureChangeListener var1) {
      synchronized (this.temperatureChangeListeners) {
         this.temperatureChangeListeners.remove(var1);
         this.enableTemperatureChangeEvents(this.temperatureChangeListeners.size() > 0);
      }
   }

   private void fireTemperatureChange(TemperatureSensorTemperatureChangeEvent var1) {
      synchronized (this.temperatureChangeListeners) {
         Iterator var3 = this.temperatureChangeListeners.iterator();

         while (var3.hasNext()) {
            ((TemperatureSensorTemperatureChangeListener)var3.next()).onTemperatureChange(var1);
         }
      }
   }

   private native void enableTemperatureChangeEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native RTDType getRTDType() throws PhidgetException;

   public native void setRTDType(RTDType var1) throws PhidgetException;

   public native RTDWireSetup getRTDWireSetup() throws PhidgetException;

   public native void setRTDWireSetup(RTDWireSetup var1) throws PhidgetException;

   public native double getTemperature() throws PhidgetException;

   public native double getMinTemperature() throws PhidgetException;

   public native double getMaxTemperature() throws PhidgetException;

   public native double getTemperatureChangeTrigger() throws PhidgetException;

   public native void setTemperatureChangeTrigger(double var1) throws PhidgetException;

   public native double getMinTemperatureChangeTrigger() throws PhidgetException;

   public native double getMaxTemperatureChangeTrigger() throws PhidgetException;

   public native ThermocoupleType getThermocoupleType() throws PhidgetException;

   public native void setThermocoupleType(ThermocoupleType var1) throws PhidgetException;
}
