package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SoundSensorBase extends Phidget {
   private List<SoundSensorSPLChangeListener> SPLChangeListeners = new ArrayList<>();
   private long nativeSPLChangeHandler = 0L;

   SoundSensorBase(long var1) {
      super(var1);
   }

   public SoundSensorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableSPLChangeEvents(var1 && this.SPLChangeListeners.size() > 0);
   }

   public final void addSPLChangeListener(SoundSensorSPLChangeListener var1) {
      synchronized (this.SPLChangeListeners) {
         this.SPLChangeListeners.add(var1);
         this.enableSPLChangeEvents(true);
      }
   }

   public final void removeSPLChangeListener(SoundSensorSPLChangeListener var1) {
      synchronized (this.SPLChangeListeners) {
         this.SPLChangeListeners.remove(var1);
         this.enableSPLChangeEvents(this.SPLChangeListeners.size() > 0);
      }
   }

   private void fireSPLChange(SoundSensorSPLChangeEvent var1) {
      synchronized (this.SPLChangeListeners) {
         Iterator var3 = this.SPLChangeListeners.iterator();

         while (var3.hasNext()) {
            ((SoundSensorSPLChangeListener)var3.next()).onSPLChange(var1);
         }
      }
   }

   private native void enableSPLChangeEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native double getdB() throws PhidgetException;

   public native double getMaxdB() throws PhidgetException;

   public native double getdBA() throws PhidgetException;

   public native double getdBC() throws PhidgetException;

   public native double getNoiseFloor() throws PhidgetException;

   public native double[] getOctaves() throws PhidgetException;

   public native double getSPLChangeTrigger() throws PhidgetException;

   public native void setSPLChangeTrigger(double var1) throws PhidgetException;

   public native double getMinSPLChangeTrigger() throws PhidgetException;

   public native double getMaxSPLChangeTrigger() throws PhidgetException;

   public native SPLRange getSPLRange() throws PhidgetException;

   public native void setSPLRange(SPLRange var1) throws PhidgetException;
}
