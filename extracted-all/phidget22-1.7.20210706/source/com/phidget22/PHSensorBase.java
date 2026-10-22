package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PHSensorBase extends Phidget {
   private List<PHSensorPHChangeListener> PHChangeListeners = new ArrayList<>();
   private long nativePHChangeHandler = 0L;

   PHSensorBase(long var1) {
      super(var1);
   }

   public PHSensorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enablePHChangeEvents(var1 && this.PHChangeListeners.size() > 0);
   }

   public final void addPHChangeListener(PHSensorPHChangeListener var1) {
      synchronized (this.PHChangeListeners) {
         this.PHChangeListeners.add(var1);
         this.enablePHChangeEvents(true);
      }
   }

   public final void removePHChangeListener(PHSensorPHChangeListener var1) {
      synchronized (this.PHChangeListeners) {
         this.PHChangeListeners.remove(var1);
         this.enablePHChangeEvents(this.PHChangeListeners.size() > 0);
      }
   }

   private void firePHChange(PHSensorPHChangeEvent var1) {
      synchronized (this.PHChangeListeners) {
         Iterator var3 = this.PHChangeListeners.iterator();

         while (var3.hasNext()) {
            ((PHSensorPHChangeListener)var3.next()).onPHChange(var1);
         }
      }
   }

   private native void enablePHChangeEvents(boolean var1);

   public native double getCorrectionTemperature() throws PhidgetException;

   public native void setCorrectionTemperature(double var1) throws PhidgetException;

   public native double getMinCorrectionTemperature() throws PhidgetException;

   public native double getMaxCorrectionTemperature() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native double getPH() throws PhidgetException;

   public native double getMinPH() throws PhidgetException;

   public native double getMaxPH() throws PhidgetException;

   public native double getPHChangeTrigger() throws PhidgetException;

   public native void setPHChangeTrigger(double var1) throws PhidgetException;

   public native double getMinPHChangeTrigger() throws PhidgetException;

   public native double getMaxPHChangeTrigger() throws PhidgetException;
}
