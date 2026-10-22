package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccelerometerBase extends Phidget {
   private List<AccelerometerAccelerationChangeListener> accelerationChangeListeners = new ArrayList<>();
   private long nativeAccelerationChangeHandler = 0L;

   AccelerometerBase(long var1) {
      super(var1);
   }

   public AccelerometerBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableAccelerationChangeEvents(var1 && this.accelerationChangeListeners.size() > 0);
   }

   public final void addAccelerationChangeListener(AccelerometerAccelerationChangeListener var1) {
      synchronized (this.accelerationChangeListeners) {
         this.accelerationChangeListeners.add(var1);
         this.enableAccelerationChangeEvents(true);
      }
   }

   public final void removeAccelerationChangeListener(AccelerometerAccelerationChangeListener var1) {
      synchronized (this.accelerationChangeListeners) {
         this.accelerationChangeListeners.remove(var1);
         this.enableAccelerationChangeEvents(this.accelerationChangeListeners.size() > 0);
      }
   }

   private void fireAccelerationChange(AccelerometerAccelerationChangeEvent var1) {
      synchronized (this.accelerationChangeListeners) {
         Iterator var3 = this.accelerationChangeListeners.iterator();

         while (var3.hasNext()) {
            ((AccelerometerAccelerationChangeListener)var3.next()).onAccelerationChange(var1);
         }
      }
   }

   private native void enableAccelerationChangeEvents(boolean var1);

   public native double[] getAcceleration() throws PhidgetException;

   public native double[] getMinAcceleration() throws PhidgetException;

   public native double[] getMaxAcceleration() throws PhidgetException;

   public native double getAccelerationChangeTrigger() throws PhidgetException;

   public native void setAccelerationChangeTrigger(double var1) throws PhidgetException;

   public native double getMinAccelerationChangeTrigger() throws PhidgetException;

   public native double getMaxAccelerationChangeTrigger() throws PhidgetException;

   public native int getAxisCount() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native boolean getHeatingEnabled() throws PhidgetException;

   public native void setHeatingEnabled(boolean var1) throws PhidgetException;

   public native double getTimestamp() throws PhidgetException;
}
