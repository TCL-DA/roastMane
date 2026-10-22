package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CapacitiveTouchBase extends Phidget {
   private List<CapacitiveTouchTouchListener> touchListeners = new ArrayList<>();
   private long nativeTouchHandler = 0L;
   private List<CapacitiveTouchTouchEndListener> touchEndListeners = new ArrayList<>();
   private long nativeTouchEndHandler = 0L;

   CapacitiveTouchBase(long var1) {
      super(var1);
   }

   public CapacitiveTouchBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableTouchEvents(var1 && this.touchListeners.size() > 0);
      this.enableTouchEndEvents(var1 && this.touchEndListeners.size() > 0);
   }

   public final void addTouchListener(CapacitiveTouchTouchListener var1) {
      synchronized (this.touchListeners) {
         this.touchListeners.add(var1);
         this.enableTouchEvents(true);
      }
   }

   public final void removeTouchListener(CapacitiveTouchTouchListener var1) {
      synchronized (this.touchListeners) {
         this.touchListeners.remove(var1);
         this.enableTouchEvents(this.touchListeners.size() > 0);
      }
   }

   private void fireTouch(CapacitiveTouchTouchEvent var1) {
      synchronized (this.touchListeners) {
         Iterator var3 = this.touchListeners.iterator();

         while (var3.hasNext()) {
            ((CapacitiveTouchTouchListener)var3.next()).onTouch(var1);
         }
      }
   }

   private native void enableTouchEvents(boolean var1);

   public final void addTouchEndListener(CapacitiveTouchTouchEndListener var1) {
      synchronized (this.touchEndListeners) {
         this.touchEndListeners.add(var1);
         this.enableTouchEndEvents(true);
      }
   }

   public final void removeTouchEndListener(CapacitiveTouchTouchEndListener var1) {
      synchronized (this.touchEndListeners) {
         this.touchEndListeners.remove(var1);
         this.enableTouchEndEvents(this.touchEndListeners.size() > 0);
      }
   }

   private void fireTouchEnd(CapacitiveTouchTouchEndEvent var1) {
      synchronized (this.touchEndListeners) {
         Iterator var3 = this.touchEndListeners.iterator();

         while (var3.hasNext()) {
            ((CapacitiveTouchTouchEndListener)var3.next()).onTouchEnd(var1);
         }
      }
   }

   private native void enableTouchEndEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native boolean getIsTouched() throws PhidgetException;

   public native double getSensitivity() throws PhidgetException;

   public native void setSensitivity(double var1) throws PhidgetException;

   public native double getMinSensitivity() throws PhidgetException;

   public native double getMaxSensitivity() throws PhidgetException;

   public native double getTouchValue() throws PhidgetException;

   public native double getMinTouchValue() throws PhidgetException;

   public native double getMaxTouchValue() throws PhidgetException;

   public native double getTouchValueChangeTrigger() throws PhidgetException;

   public native void setTouchValueChangeTrigger(double var1) throws PhidgetException;

   public native double getMinTouchValueChangeTrigger() throws PhidgetException;

   public native double getMaxTouchValueChangeTrigger() throws PhidgetException;
}
