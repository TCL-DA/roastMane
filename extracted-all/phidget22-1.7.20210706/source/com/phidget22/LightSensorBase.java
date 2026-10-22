package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LightSensorBase extends Phidget {
   private List<LightSensorIlluminanceChangeListener> illuminanceChangeListeners = new ArrayList<>();
   private long nativeIlluminanceChangeHandler = 0L;

   LightSensorBase(long var1) {
      super(var1);
   }

   public LightSensorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableIlluminanceChangeEvents(var1 && this.illuminanceChangeListeners.size() > 0);
   }

   public final void addIlluminanceChangeListener(LightSensorIlluminanceChangeListener var1) {
      synchronized (this.illuminanceChangeListeners) {
         this.illuminanceChangeListeners.add(var1);
         this.enableIlluminanceChangeEvents(true);
      }
   }

   public final void removeIlluminanceChangeListener(LightSensorIlluminanceChangeListener var1) {
      synchronized (this.illuminanceChangeListeners) {
         this.illuminanceChangeListeners.remove(var1);
         this.enableIlluminanceChangeEvents(this.illuminanceChangeListeners.size() > 0);
      }
   }

   private void fireIlluminanceChange(LightSensorIlluminanceChangeEvent var1) {
      synchronized (this.illuminanceChangeListeners) {
         Iterator var3 = this.illuminanceChangeListeners.iterator();

         while (var3.hasNext()) {
            ((LightSensorIlluminanceChangeListener)var3.next()).onIlluminanceChange(var1);
         }
      }
   }

   private native void enableIlluminanceChangeEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native double getIlluminance() throws PhidgetException;

   public native double getMinIlluminance() throws PhidgetException;

   public native double getMaxIlluminance() throws PhidgetException;

   public native double getIlluminanceChangeTrigger() throws PhidgetException;

   public native void setIlluminanceChangeTrigger(double var1) throws PhidgetException;

   public native double getMinIlluminanceChangeTrigger() throws PhidgetException;

   public native double getMaxIlluminanceChangeTrigger() throws PhidgetException;
}
