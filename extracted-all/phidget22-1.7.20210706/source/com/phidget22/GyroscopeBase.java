package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GyroscopeBase extends Phidget {
   private List<GyroscopeAngularRateUpdateListener> angularRateUpdateListeners = new ArrayList<>();
   private long nativeAngularRateUpdateHandler = 0L;

   GyroscopeBase(long var1) {
      super(var1);
   }

   public GyroscopeBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableAngularRateUpdateEvents(var1 && this.angularRateUpdateListeners.size() > 0);
   }

   public final void addAngularRateUpdateListener(GyroscopeAngularRateUpdateListener var1) {
      synchronized (this.angularRateUpdateListeners) {
         this.angularRateUpdateListeners.add(var1);
         this.enableAngularRateUpdateEvents(true);
      }
   }

   public final void removeAngularRateUpdateListener(GyroscopeAngularRateUpdateListener var1) {
      synchronized (this.angularRateUpdateListeners) {
         this.angularRateUpdateListeners.remove(var1);
         this.enableAngularRateUpdateEvents(this.angularRateUpdateListeners.size() > 0);
      }
   }

   private void fireAngularRateUpdate(GyroscopeAngularRateUpdateEvent var1) {
      synchronized (this.angularRateUpdateListeners) {
         Iterator var3 = this.angularRateUpdateListeners.iterator();

         while (var3.hasNext()) {
            ((GyroscopeAngularRateUpdateListener)var3.next()).onAngularRateUpdate(var1);
         }
      }
   }

   private native void enableAngularRateUpdateEvents(boolean var1);

   public native double[] getAngularRate() throws PhidgetException;

   public native double[] getMinAngularRate() throws PhidgetException;

   public native double[] getMaxAngularRate() throws PhidgetException;

   public native int getAxisCount() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native boolean getHeatingEnabled() throws PhidgetException;

   public native void setHeatingEnabled(boolean var1) throws PhidgetException;

   public native double getTimestamp() throws PhidgetException;

   public native void zero() throws PhidgetException;
}
