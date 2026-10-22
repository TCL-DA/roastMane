package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EncoderBase extends Phidget {
   private List<EncoderPositionChangeListener> positionChangeListeners = new ArrayList<>();
   private long nativePositionChangeHandler = 0L;

   EncoderBase(long var1) {
      super(var1);
   }

   public EncoderBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enablePositionChangeEvents(var1 && this.positionChangeListeners.size() > 0);
   }

   public final void addPositionChangeListener(EncoderPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.add(var1);
         this.enablePositionChangeEvents(true);
      }
   }

   public final void removePositionChangeListener(EncoderPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.remove(var1);
         this.enablePositionChangeEvents(this.positionChangeListeners.size() > 0);
      }
   }

   private void firePositionChange(EncoderPositionChangeEvent var1) {
      synchronized (this.positionChangeListeners) {
         Iterator var3 = this.positionChangeListeners.iterator();

         while (var3.hasNext()) {
            ((EncoderPositionChangeListener)var3.next()).onPositionChange(var1);
         }
      }
   }

   private native void enablePositionChangeEvents(boolean var1);

   public native void setEnabled(boolean var1) throws PhidgetException;

   public native boolean getEnabled() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native long getIndexPosition() throws PhidgetException;

   public native EncoderIOMode getIOMode() throws PhidgetException;

   public native void setIOMode(EncoderIOMode var1) throws PhidgetException;

   public native long getPosition() throws PhidgetException;

   public native void setPosition(long var1) throws PhidgetException;

   public native int getPositionChangeTrigger() throws PhidgetException;

   public native void setPositionChangeTrigger(int var1) throws PhidgetException;

   public native int getMinPositionChangeTrigger() throws PhidgetException;

   public native int getMaxPositionChangeTrigger() throws PhidgetException;
}
