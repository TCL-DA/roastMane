package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RCServoBase extends Phidget {
   private List<RCServoPositionChangeListener> positionChangeListeners = new ArrayList<>();
   private long nativePositionChangeHandler = 0L;
   private List<RCServoTargetPositionReachedListener> targetPositionReachedListeners = new ArrayList<>();
   private long nativeTargetPositionReachedHandler = 0L;
   private List<RCServoVelocityChangeListener> velocityChangeListeners = new ArrayList<>();
   private long nativeVelocityChangeHandler = 0L;

   RCServoBase(long var1) {
      super(var1);
   }

   public RCServoBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enablePositionChangeEvents(var1 && this.positionChangeListeners.size() > 0);
      this.enableTargetPositionReachedEvents(var1 && this.targetPositionReachedListeners.size() > 0);
      this.enableVelocityChangeEvents(var1 && this.velocityChangeListeners.size() > 0);
   }

   public final void addPositionChangeListener(RCServoPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.add(var1);
         this.enablePositionChangeEvents(true);
      }
   }

   public final void removePositionChangeListener(RCServoPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.remove(var1);
         this.enablePositionChangeEvents(this.positionChangeListeners.size() > 0);
      }
   }

   private void firePositionChange(RCServoPositionChangeEvent var1) {
      synchronized (this.positionChangeListeners) {
         Iterator var3 = this.positionChangeListeners.iterator();

         while (var3.hasNext()) {
            ((RCServoPositionChangeListener)var3.next()).onPositionChange(var1);
         }
      }
   }

   private native void enablePositionChangeEvents(boolean var1);

   public final void addTargetPositionReachedListener(RCServoTargetPositionReachedListener var1) {
      synchronized (this.targetPositionReachedListeners) {
         this.targetPositionReachedListeners.add(var1);
         this.enableTargetPositionReachedEvents(true);
      }
   }

   public final void removeTargetPositionReachedListener(RCServoTargetPositionReachedListener var1) {
      synchronized (this.targetPositionReachedListeners) {
         this.targetPositionReachedListeners.remove(var1);
         this.enableTargetPositionReachedEvents(this.targetPositionReachedListeners.size() > 0);
      }
   }

   private void fireTargetPositionReached(RCServoTargetPositionReachedEvent var1) {
      synchronized (this.targetPositionReachedListeners) {
         Iterator var3 = this.targetPositionReachedListeners.iterator();

         while (var3.hasNext()) {
            ((RCServoTargetPositionReachedListener)var3.next()).onTargetPositionReached(var1);
         }
      }
   }

   private native void enableTargetPositionReachedEvents(boolean var1);

   public final void addVelocityChangeListener(RCServoVelocityChangeListener var1) {
      synchronized (this.velocityChangeListeners) {
         this.velocityChangeListeners.add(var1);
         this.enableVelocityChangeEvents(true);
      }
   }

   public final void removeVelocityChangeListener(RCServoVelocityChangeListener var1) {
      synchronized (this.velocityChangeListeners) {
         this.velocityChangeListeners.remove(var1);
         this.enableVelocityChangeEvents(this.velocityChangeListeners.size() > 0);
      }
   }

   private void fireVelocityChange(RCServoVelocityChangeEvent var1) {
      synchronized (this.velocityChangeListeners) {
         Iterator var3 = this.velocityChangeListeners.iterator();

         while (var3.hasNext()) {
            ((RCServoVelocityChangeListener)var3.next()).onVelocityChange(var1);
         }
      }
   }

   private native void enableVelocityChangeEvents(boolean var1);

   public native double getAcceleration() throws PhidgetException;

   public native void setAcceleration(double var1) throws PhidgetException;

   public native double getMinAcceleration() throws PhidgetException;

   public native double getMaxAcceleration() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native boolean getEngaged() throws PhidgetException;

   public native void setEngaged(boolean var1) throws PhidgetException;

   public native void enableFailsafe(int var1) throws PhidgetException;

   public native int getMinFailsafeTime() throws PhidgetException;

   public native int getMaxFailsafeTime() throws PhidgetException;

   public native boolean getIsMoving() throws PhidgetException;

   public native double getPosition() throws PhidgetException;

   public native void setMinPosition(double var1) throws PhidgetException;

   public native double getMinPosition() throws PhidgetException;

   public native void setMaxPosition(double var1) throws PhidgetException;

   public native double getMaxPosition() throws PhidgetException;

   public native double getMinPulseWidth() throws PhidgetException;

   public native void setMinPulseWidth(double var1) throws PhidgetException;

   public native double getMaxPulseWidth() throws PhidgetException;

   public native void setMaxPulseWidth(double var1) throws PhidgetException;

   public native double getMinPulseWidthLimit() throws PhidgetException;

   public native double getMaxPulseWidthLimit() throws PhidgetException;

   public native void resetFailsafe() throws PhidgetException;

   public native boolean getSpeedRampingState() throws PhidgetException;

   public native void setSpeedRampingState(boolean var1) throws PhidgetException;

   public native double getTargetPosition() throws PhidgetException;

   public native void setTargetPosition(double var1, AsyncListener var3);

   public native void setTargetPosition(double var1) throws PhidgetException;

   public native double getTorque() throws PhidgetException;

   public native void setTorque(double var1) throws PhidgetException;

   public native double getMinTorque() throws PhidgetException;

   public native double getMaxTorque() throws PhidgetException;

   public native double getVelocity() throws PhidgetException;

   public native double getVelocityLimit() throws PhidgetException;

   public native void setVelocityLimit(double var1) throws PhidgetException;

   public native double getMinVelocityLimit() throws PhidgetException;

   public native double getMaxVelocityLimit() throws PhidgetException;

   public native RCServoVoltage getVoltage() throws PhidgetException;

   public native void setVoltage(RCServoVoltage var1) throws PhidgetException;
}
