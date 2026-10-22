package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MagnetometerBase extends Phidget {
   private List<MagnetometerMagneticFieldChangeListener> magneticFieldChangeListeners = new ArrayList<>();
   private long nativeMagneticFieldChangeHandler = 0L;

   MagnetometerBase(long var1) {
      super(var1);
   }

   public MagnetometerBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableMagneticFieldChangeEvents(var1 && this.magneticFieldChangeListeners.size() > 0);
   }

   public final void addMagneticFieldChangeListener(MagnetometerMagneticFieldChangeListener var1) {
      synchronized (this.magneticFieldChangeListeners) {
         this.magneticFieldChangeListeners.add(var1);
         this.enableMagneticFieldChangeEvents(true);
      }
   }

   public final void removeMagneticFieldChangeListener(MagnetometerMagneticFieldChangeListener var1) {
      synchronized (this.magneticFieldChangeListeners) {
         this.magneticFieldChangeListeners.remove(var1);
         this.enableMagneticFieldChangeEvents(this.magneticFieldChangeListeners.size() > 0);
      }
   }

   private void fireMagneticFieldChange(MagnetometerMagneticFieldChangeEvent var1) {
      synchronized (this.magneticFieldChangeListeners) {
         Iterator var3 = this.magneticFieldChangeListeners.iterator();

         while (var3.hasNext()) {
            ((MagnetometerMagneticFieldChangeListener)var3.next()).onMagneticFieldChange(var1);
         }
      }
   }

   private native void enableMagneticFieldChangeEvents(boolean var1);

   public native int getAxisCount() throws PhidgetException;

   public native void setCorrectionParameters(
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      double var25
   ) throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native boolean getHeatingEnabled() throws PhidgetException;

   public native void setHeatingEnabled(boolean var1) throws PhidgetException;

   public native double[] getMagneticField() throws PhidgetException;

   public native double[] getMinMagneticField() throws PhidgetException;

   public native double[] getMaxMagneticField() throws PhidgetException;

   public native double getMagneticFieldChangeTrigger() throws PhidgetException;

   public native void setMagneticFieldChangeTrigger(double var1) throws PhidgetException;

   public native double getMinMagneticFieldChangeTrigger() throws PhidgetException;

   public native double getMaxMagneticFieldChangeTrigger() throws PhidgetException;

   public native void resetCorrectionParameters() throws PhidgetException;

   public native void saveCorrectionParameters() throws PhidgetException;

   public native double getTimestamp() throws PhidgetException;
}
