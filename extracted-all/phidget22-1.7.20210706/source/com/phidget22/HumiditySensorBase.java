package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HumiditySensorBase extends Phidget {
   private List<HumiditySensorHumidityChangeListener> humidityChangeListeners = new ArrayList<>();
   private long nativeHumidityChangeHandler = 0L;

   HumiditySensorBase(long var1) {
      super(var1);
   }

   public HumiditySensorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableHumidityChangeEvents(var1 && this.humidityChangeListeners.size() > 0);
   }

   public final void addHumidityChangeListener(HumiditySensorHumidityChangeListener var1) {
      synchronized (this.humidityChangeListeners) {
         this.humidityChangeListeners.add(var1);
         this.enableHumidityChangeEvents(true);
      }
   }

   public final void removeHumidityChangeListener(HumiditySensorHumidityChangeListener var1) {
      synchronized (this.humidityChangeListeners) {
         this.humidityChangeListeners.remove(var1);
         this.enableHumidityChangeEvents(this.humidityChangeListeners.size() > 0);
      }
   }

   private void fireHumidityChange(HumiditySensorHumidityChangeEvent var1) {
      synchronized (this.humidityChangeListeners) {
         Iterator var3 = this.humidityChangeListeners.iterator();

         while (var3.hasNext()) {
            ((HumiditySensorHumidityChangeListener)var3.next()).onHumidityChange(var1);
         }
      }
   }

   private native void enableHumidityChangeEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native double getHumidity() throws PhidgetException;

   public native double getMinHumidity() throws PhidgetException;

   public native double getMaxHumidity() throws PhidgetException;

   public native double getHumidityChangeTrigger() throws PhidgetException;

   public native void setHumidityChangeTrigger(double var1) throws PhidgetException;

   public native double getMinHumidityChangeTrigger() throws PhidgetException;

   public native double getMaxHumidityChangeTrigger() throws PhidgetException;
}
