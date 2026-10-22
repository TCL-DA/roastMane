package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DCMotorBase extends Phidget {
   private List<DCMotorBackEMFChangeListener> backEMFChangeListeners = new ArrayList<>();
   private long nativeBackEMFChangeHandler = 0L;
   private List<DCMotorBrakingStrengthChangeListener> brakingStrengthChangeListeners = new ArrayList<>();
   private long nativeBrakingStrengthChangeHandler = 0L;
   private List<DCMotorVelocityUpdateListener> velocityUpdateListeners = new ArrayList<>();
   private long nativeVelocityUpdateHandler = 0L;

   DCMotorBase(long var1) {
      super(var1);
   }

   public DCMotorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableBackEMFChangeEvents(var1 && this.backEMFChangeListeners.size() > 0);
      this.enableBrakingStrengthChangeEvents(var1 && this.brakingStrengthChangeListeners.size() > 0);
      this.enableVelocityUpdateEvents(var1 && this.velocityUpdateListeners.size() > 0);
   }

   public final void addBackEMFChangeListener(DCMotorBackEMFChangeListener var1) {
      synchronized (this.backEMFChangeListeners) {
         this.backEMFChangeListeners.add(var1);
         this.enableBackEMFChangeEvents(true);
      }
   }

   public final void removeBackEMFChangeListener(DCMotorBackEMFChangeListener var1) {
      synchronized (this.backEMFChangeListeners) {
         this.backEMFChangeListeners.remove(var1);
         this.enableBackEMFChangeEvents(this.backEMFChangeListeners.size() > 0);
      }
   }

   private void fireBackEMFChange(DCMotorBackEMFChangeEvent var1) {
      synchronized (this.backEMFChangeListeners) {
         Iterator var3 = this.backEMFChangeListeners.iterator();

         while (var3.hasNext()) {
            ((DCMotorBackEMFChangeListener)var3.next()).onBackEMFChange(var1);
         }
      }
   }

   private native void enableBackEMFChangeEvents(boolean var1);

   public final void addBrakingStrengthChangeListener(DCMotorBrakingStrengthChangeListener var1) {
      synchronized (this.brakingStrengthChangeListeners) {
         this.brakingStrengthChangeListeners.add(var1);
         this.enableBrakingStrengthChangeEvents(true);
      }
   }

   public final void removeBrakingStrengthChangeListener(DCMotorBrakingStrengthChangeListener var1) {
      synchronized (this.brakingStrengthChangeListeners) {
         this.brakingStrengthChangeListeners.remove(var1);
         this.enableBrakingStrengthChangeEvents(this.brakingStrengthChangeListeners.size() > 0);
      }
   }

   private void fireBrakingStrengthChange(DCMotorBrakingStrengthChangeEvent var1) {
      synchronized (this.brakingStrengthChangeListeners) {
         Iterator var3 = this.brakingStrengthChangeListeners.iterator();

         while (var3.hasNext()) {
            ((DCMotorBrakingStrengthChangeListener)var3.next()).onBrakingStrengthChange(var1);
         }
      }
   }

   private native void enableBrakingStrengthChangeEvents(boolean var1);

   public final void addVelocityUpdateListener(DCMotorVelocityUpdateListener var1) {
      synchronized (this.velocityUpdateListeners) {
         this.velocityUpdateListeners.add(var1);
         this.enableVelocityUpdateEvents(true);
      }
   }

   public final void removeVelocityUpdateListener(DCMotorVelocityUpdateListener var1) {
      synchronized (this.velocityUpdateListeners) {
         this.velocityUpdateListeners.remove(var1);
         this.enableVelocityUpdateEvents(this.velocityUpdateListeners.size() > 0);
      }
   }

   private void fireVelocityUpdate(DCMotorVelocityUpdateEvent var1) {
      synchronized (this.velocityUpdateListeners) {
         Iterator var3 = this.velocityUpdateListeners.iterator();

         while (var3.hasNext()) {
            ((DCMotorVelocityUpdateListener)var3.next()).onVelocityUpdate(var1);
         }
      }
   }

   private native void enableVelocityUpdateEvents(boolean var1);

   public native double getAcceleration() throws PhidgetException;

   public native void setAcceleration(double var1) throws PhidgetException;

   public native double getMinAcceleration() throws PhidgetException;

   public native double getMaxAcceleration() throws PhidgetException;

   public native double getBackEMF() throws PhidgetException;

   public native boolean getBackEMFSensingState() throws PhidgetException;

   public native void setBackEMFSensingState(boolean var1) throws PhidgetException;

   public native double getBrakingStrength() throws PhidgetException;

   public native double getMinBrakingStrength() throws PhidgetException;

   public native double getMaxBrakingStrength() throws PhidgetException;

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

   public native void enableFailsafe(int var1) throws PhidgetException;

   public native int getMinFailsafeTime() throws PhidgetException;

   public native int getMaxFailsafeTime() throws PhidgetException;

   public native FanMode getFanMode() throws PhidgetException;

   public native void setFanMode(FanMode var1) throws PhidgetException;

   public native void resetFailsafe() throws PhidgetException;

   public native double getTargetBrakingStrength() throws PhidgetException;

   public native void setTargetBrakingStrength(double var1) throws PhidgetException;

   public native double getTargetVelocity() throws PhidgetException;

   public native void setTargetVelocity(double var1) throws PhidgetException;

   public native void setTargetVelocity(double var1, AsyncListener var3);

   public native double getVelocity() throws PhidgetException;

   public native double getMinVelocity() throws PhidgetException;

   public native double getMaxVelocity() throws PhidgetException;
}
