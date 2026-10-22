package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MotorPositionControllerBase extends Phidget {
   private List<MotorPositionControllerDutyCycleUpdateListener> dutyCycleUpdateListeners = new ArrayList<>();
   private long nativeDutyCycleUpdateHandler = 0L;
   private List<MotorPositionControllerPositionChangeListener> positionChangeListeners = new ArrayList<>();
   private long nativePositionChangeHandler = 0L;

   MotorPositionControllerBase(long var1) {
      super(var1);
   }

   public MotorPositionControllerBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableDutyCycleUpdateEvents(var1 && this.dutyCycleUpdateListeners.size() > 0);
      this.enablePositionChangeEvents(var1 && this.positionChangeListeners.size() > 0);
   }

   public final void addDutyCycleUpdateListener(MotorPositionControllerDutyCycleUpdateListener var1) {
      synchronized (this.dutyCycleUpdateListeners) {
         this.dutyCycleUpdateListeners.add(var1);
         this.enableDutyCycleUpdateEvents(true);
      }
   }

   public final void removeDutyCycleUpdateListener(MotorPositionControllerDutyCycleUpdateListener var1) {
      synchronized (this.dutyCycleUpdateListeners) {
         this.dutyCycleUpdateListeners.remove(var1);
         this.enableDutyCycleUpdateEvents(this.dutyCycleUpdateListeners.size() > 0);
      }
   }

   private void fireDutyCycleUpdate(MotorPositionControllerDutyCycleUpdateEvent var1) {
      synchronized (this.dutyCycleUpdateListeners) {
         Iterator var3 = this.dutyCycleUpdateListeners.iterator();

         while (var3.hasNext()) {
            ((MotorPositionControllerDutyCycleUpdateListener)var3.next()).onDutyCycleUpdate(var1);
         }
      }
   }

   private native void enableDutyCycleUpdateEvents(boolean var1);

   public final void addPositionChangeListener(MotorPositionControllerPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.add(var1);
         this.enablePositionChangeEvents(true);
      }
   }

   public final void removePositionChangeListener(MotorPositionControllerPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.remove(var1);
         this.enablePositionChangeEvents(this.positionChangeListeners.size() > 0);
      }
   }

   private void firePositionChange(MotorPositionControllerPositionChangeEvent var1) {
      synchronized (this.positionChangeListeners) {
         Iterator var3 = this.positionChangeListeners.iterator();

         while (var3.hasNext()) {
            ((MotorPositionControllerPositionChangeListener)var3.next()).onPositionChange(var1);
         }
      }
   }

   private native void enablePositionChangeEvents(boolean var1);

   public native double getAcceleration() throws PhidgetException;

   public native void setAcceleration(double var1) throws PhidgetException;

   public native double getMinAcceleration() throws PhidgetException;

   public native double getMaxAcceleration() throws PhidgetException;

   public native double getCurrentLimit() throws PhidgetException;

   public native void setCurrentLimit(double var1) throws PhidgetException;

   public native double getMinCurrentLimit() throws PhidgetException;

   public native double getMaxCurrentLimit() throws PhidgetException;

   public native double getCurrentRegulatorGain() throws PhidgetException;

   public native void setCurrentRegulatorGain(double var1) throws PhidgetException;

   public native double getMinCurrentRegulatorGain() throws PhidgetException;

   public native double getMaxCurrentRegulatorGain() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native double getDeadBand() throws PhidgetException;

   public native void setDeadBand(double var1) throws PhidgetException;

   public native double getDutyCycle() throws PhidgetException;

   public native boolean getEngaged() throws PhidgetException;

   public native void setEngaged(boolean var1) throws PhidgetException;

   public native void enableFailsafe(int var1) throws PhidgetException;

   public native int getMinFailsafeTime() throws PhidgetException;

   public native int getMaxFailsafeTime() throws PhidgetException;

   public native FanMode getFanMode() throws PhidgetException;

   public native void setFanMode(FanMode var1) throws PhidgetException;

   public native EncoderIOMode getIOMode() throws PhidgetException;

   public native void setIOMode(EncoderIOMode var1) throws PhidgetException;

   public native double getKd() throws PhidgetException;

   public native void setKd(double var1) throws PhidgetException;

   public native double getKi() throws PhidgetException;

   public native void setKi(double var1) throws PhidgetException;

   public native double getKp() throws PhidgetException;

   public native void setKp(double var1) throws PhidgetException;

   public native double getPosition() throws PhidgetException;

   public native double getMinPosition() throws PhidgetException;

   public native double getMaxPosition() throws PhidgetException;

   public native void addPositionOffset(double var1) throws PhidgetException;

   public native double getRescaleFactor() throws PhidgetException;

   public native void setRescaleFactor(double var1) throws PhidgetException;

   public native void resetFailsafe() throws PhidgetException;

   public native double getStallVelocity() throws PhidgetException;

   public native void setStallVelocity(double var1) throws PhidgetException;

   public native double getMinStallVelocity() throws PhidgetException;

   public native double getMaxStallVelocity() throws PhidgetException;

   public native double getTargetPosition() throws PhidgetException;

   public native void setTargetPosition(double var1, AsyncListener var3);

   public native void setTargetPosition(double var1) throws PhidgetException;

   public native double getVelocityLimit() throws PhidgetException;

   public native void setVelocityLimit(double var1) throws PhidgetException;

   public native double getMinVelocityLimit() throws PhidgetException;

   public native double getMaxVelocityLimit() throws PhidgetException;
}
