package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PressureSensorBase extends Phidget {
   private List<PressureSensorPressureChangeListener> pressureChangeListeners = new ArrayList<>();
   private long nativePressureChangeHandler = 0L;

   PressureSensorBase(long var1) {
      super(var1);
   }

   public PressureSensorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enablePressureChangeEvents(var1 && this.pressureChangeListeners.size() > 0);
   }

   public final void addPressureChangeListener(PressureSensorPressureChangeListener var1) {
      synchronized (this.pressureChangeListeners) {
         this.pressureChangeListeners.add(var1);
         this.enablePressureChangeEvents(true);
      }
   }

   public final void removePressureChangeListener(PressureSensorPressureChangeListener var1) {
      synchronized (this.pressureChangeListeners) {
         this.pressureChangeListeners.remove(var1);
         this.enablePressureChangeEvents(this.pressureChangeListeners.size() > 0);
      }
   }

   private void firePressureChange(PressureSensorPressureChangeEvent var1) {
      synchronized (this.pressureChangeListeners) {
         Iterator var3 = this.pressureChangeListeners.iterator();

         while (var3.hasNext()) {
            ((PressureSensorPressureChangeListener)var3.next()).onPressureChange(var1);
         }
      }
   }

   private native void enablePressureChangeEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native double getPressure() throws PhidgetException;

   public native double getMinPressure() throws PhidgetException;

   public native double getMaxPressure() throws PhidgetException;

   public native double getPressureChangeTrigger() throws PhidgetException;

   public native void setPressureChangeTrigger(double var1) throws PhidgetException;

   public native double getMinPressureChangeTrigger() throws PhidgetException;

   public native double getMaxPressureChangeTrigger() throws PhidgetException;
}
