package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StepperBase extends Phidget {
   private List<StepperPositionChangeListener> positionChangeListeners = new ArrayList<>();
   private long nativePositionChangeHandler = 0L;
   private List<StepperStoppedListener> stoppedListeners = new ArrayList<>();
   private long nativeStoppedHandler = 0L;
   private List<StepperVelocityChangeListener> velocityChangeListeners = new ArrayList<>();
   private long nativeVelocityChangeHandler = 0L;

   StepperBase(long var1) {
      super(var1);
   }

   public StepperBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enablePositionChangeEvents(var1 && this.positionChangeListeners.size() > 0);
      this.enableStoppedEvents(var1 && this.stoppedListeners.size() > 0);
      this.enableVelocityChangeEvents(var1 && this.velocityChangeListeners.size() > 0);
   }

   public final void addPositionChangeListener(StepperPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.add(var1);
         this.enablePositionChangeEvents(true);
      }
   }

   public final void removePositionChangeListener(StepperPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.remove(var1);
         this.enablePositionChangeEvents(this.positionChangeListeners.size() > 0);
      }
   }

   private void firePositionChange(StepperPositionChangeEvent var1) {
      synchronized (this.positionChangeListeners) {
         Iterator var3 = this.positionChangeListeners.iterator();

         while (var3.hasNext()) {
            ((StepperPositionChangeListener)var3.next()).onPositionChange(var1);
         }
      }
   }

   private native void enablePositionChangeEvents(boolean var1);

   public final void addStoppedListener(StepperStoppedListener var1) {
      synchronized (this.stoppedListeners) {
         this.stoppedListeners.add(var1);
         this.enableStoppedEvents(true);
      }
   }

   public final void removeStoppedListener(StepperStoppedListener var1) {
      synchronized (this.stoppedListeners) {
         this.stoppedListeners.remove(var1);
         this.enableStoppedEvents(this.stoppedListeners.size() > 0);
      }
   }

   private void fireStopped(StepperStoppedEvent var1) {
      synchronized (this.stoppedListeners) {
         Iterator var3 = this.stoppedListeners.iterator();

         while (var3.hasNext()) {
            ((StepperStoppedListener)var3.next()).onStopped(var1);
         }
      }
   }

   private native void enableStoppedEvents(boolean var1);

   public final void addVelocityChangeListener(StepperVelocityChangeListener var1) {
      synchronized (this.velocityChangeListeners) {
         this.velocityChangeListeners.add(var1);
         this.enableVelocityChangeEvents(true);
      }
   }

   public final void removeVelocityChangeListener(StepperVelocityChangeListener var1) {
      synchronized (this.velocityChangeListeners) {
         this.velocityChangeListeners.remove(var1);
         this.enableVelocityChangeEvents(this.velocityChangeListeners.size() > 0);
      }
   }

   private void fireVelocityChange(StepperVelocityChangeEvent var1) {
      synchronized (this.velocityChangeListeners) {
         Iterator var3 = this.velocityChangeListeners.iterator();

         while (var3.hasNext()) {
            ((StepperVelocityChangeListener)var3.next()).onVelocityChange(var1);
         }
      }
   }

   private native void enableVelocityChangeEvents(boolean var1);

   public native double getAcceleration() throws PhidgetException;

   public native void setAcceleration(double var1) throws PhidgetException;

   public native double getMinAcceleration() throws PhidgetException;

   public native double getMaxAcceleration() throws PhidgetException;

   public native StepperControlMode getControlMode() throws PhidgetException;

   public native void setControlMode(StepperControlMode var1) throws PhidgetException;

   public native double getCurrentLimit() throws PhidgetException;

   public native void setCurrentLimit(double var1) throws PhidgetException;

   public native double getMinCurrentLimit() throws PhidgetException;

   public native double getMaxCurrentLimit() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native boolean getEngaged() throws PhidgetException;

   public native void setEngaged(boolean var1) throws PhidgetException;

   public native void enableFailsafe(int var1) throws PhidgetException;

   public native int getMinFailsafeTime() throws PhidgetException;

   public native int getMaxFailsafeTime() throws PhidgetException;

   public native double getHoldingCurrentLimit() throws PhidgetException;

   public native void setHoldingCurrentLimit(double var1) throws PhidgetException;

   public native boolean getIsMoving() throws PhidgetException;

   public native double getPosition() throws PhidgetException;

   public native double getMinPosition() throws PhidgetException;

   public native double getMaxPosition() throws PhidgetException;

   public native void addPositionOffset(double var1) throws PhidgetException;

   public native double getRescaleFactor() throws PhidgetException;

   public native void setRescaleFactor(double var1) throws PhidgetException;

   public native void resetFailsafe() throws PhidgetException;

   public native double getTargetPosition() throws PhidgetException;

   public native void setTargetPosition(double var1) throws PhidgetException;

   public native void setTargetPosition(double var1, AsyncListener var3);

   public native double getVelocity() throws PhidgetException;

   public native double getVelocityLimit() throws PhidgetException;

   public native void setVelocityLimit(double var1) throws PhidgetException;

   public native double getMinVelocityLimit() throws PhidgetException;

   public native double getMaxVelocityLimit() throws PhidgetException;
}
